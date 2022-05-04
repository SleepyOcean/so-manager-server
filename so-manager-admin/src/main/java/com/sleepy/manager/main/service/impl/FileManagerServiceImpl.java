package com.sleepy.manager.main.service.impl;

import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.blog.common.UnionResponse;
import com.sleepy.manager.common.utils.file.ImageUtils;
import com.sleepy.manager.main.helper.MediaMetaDataHelper;
import com.sleepy.manager.main.service.FileManagerService;
import com.sleepy.manager.system.domain.Gallery;
import com.sleepy.manager.system.mapper.GalleryMapper;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;

import static com.sleepy.manager.common.utils.DateUtils.*;
import static com.sleepy.manager.common.utils.StringUtils.*;
import static com.sleepy.manager.common.utils.file.FileUtils.constructPath;

/**
 * @author Captain1920
 * @classname FileManagerServiceImpl
 * @description TODO
 * @date 2022/5/2 16:06
 */
@Slf4j
@Service
public class FileManagerServiceImpl implements FileManagerService {

    private static final String IMG_SERVER_URL_PLACEHOLDER = "PLACEHOLDER=IMG_SERVER_URL";
    private static final int THUMBNAIL_THRESHOLD = 300;
    private static final int THUMBNAIL_HEIGHT = 300;
    @Autowired
    GalleryMapper galleryMapper;
    @Value("${so-manager-server.galleryRoot}")
    private String galleryStorageRoot;
    @Value("${so-manager-server.galleryPrefix}")
    private String galleryServerUrlPrefix;

    @Override
    public byte[] getImg(String id) {
        String imgPath = imgIdToPath(id);
        return ImageUtils.getImage(imgPath);
    }

    @Override
    public byte[] getImgThumbnail(String id) {
        String imgPath = imgIdToPath(id);
        File file = new File(imgPath);
        if (!file.exists()) {
            log.warn("image does not exist for id[{}], path=[{}]", id, imgPath);
            return new byte[0];
        }
        // 1. 判断图片大小，默认小于300K的图片不压缩
        if (file.length() < THUMBNAIL_THRESHOLD * 1024) {
            return getImg(id);
        }
        // 2. 压缩到指定高度
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BufferedImage bufferedImage = ImageIO.read(file);
            int srcHeight = bufferedImage.getHeight();
            int srcWidth = bufferedImage.getWidth();

            int destHeight = THUMBNAIL_HEIGHT;
            int destWidth = srcWidth * destHeight / srcHeight;
            Thumbnails.of(imgPath).size(destWidth, destHeight).outputFormat("jpeg").toOutputStream(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getCompressedImg(String ratio, String url) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (url != null) {
                URL path = new URL(formatUrl(url));
                Thumbnails.of(path).scale(Float.parseFloat(ratio)).outputFormat("jpeg").toOutputStream(out);
                return out.toByteArray();
            }
            return new byte[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UnionResponse delete(String id) {
        String[] ids = id.split(",");
        if (ids.length > 0 && ids.length < 2) {
            deleteImageFile(id);
            galleryMapper.deleteGalleryById(id);
        } else {
            for (String imgId : ids) {
                deleteImageFile(imgId);
            }
            galleryMapper.deleteGalleryByIds(ids);
        }
        return new UnionResponse.Builder().status(HttpStatus.OK).message(format("Delete image(s)[{}] successfully", id)).build();
    }

    @Override
    public UnionResponse upload(Gallery gallery) {
        String md5 = md5ForString(gallery.getBase64());

        // 判断文件是否已经存在，若存在则直接返回结果
        if (!ObjectUtils.isEmpty(galleryMapper.selectGalleryById(md5))) {
            return new UnionResponse.Builder()
                    .status(HttpStatus.CONFLICT)
                    .message("The image already existed")
                    .data(new AssembledData.Builder()
                            .put("imageId", md5)
                            .put("url", IMG_SERVER_URL_PLACEHOLDER + md5)
                            .put("imgUrl", galleryServerUrlPrefix + md5)
                            .build())
                    .build();
        }

        String currentDay = dateTimeNow(YYYY_MM_DD);
        String currentTime = dateTimeNow(YYYY_MM_DD_HH_MM_SS);
        // 图片名称的路径： 图片的类型/图片上传日期/图片的UUID， 例如： 封面/2019-10-31/2fc9e266e21f4fe18f92da2fc56567f8
        String relativePath = constructPath("Gallery", currentDay, getRandomUuid(""));
        String absolutePath = null;
        try {
            absolutePath = ImageUtils.base64ToImgFile(gallery.getBase64(), constructPath(galleryStorageRoot, relativePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            AssembledData imgMeta = MediaMetaDataHelper.createImgMetaInfo(absolutePath);
            gallery.setId(md5);
            gallery.setSize(imgMeta.get("图片大小").toString());
            gallery.setFormat(imgMeta.get("图片格式").toString());
            gallery.setResolution(imgMeta.get("宽") + " × " + imgMeta.get("高"));
            gallery.setPath(absolutePath.substring(galleryStorageRoot.length()));
            gallery.setCreateTime(dateTime(YYYY_MM_DD_HH_MM_SS, imgMeta.getOrDefault("创建时间", currentTime).toString()));
            gallery.setUploadTime(new Timestamp(System.currentTimeMillis()));
            galleryMapper.insertGallery(gallery);

            return new UnionResponse.Builder()
                    .status(HttpStatus.OK)
                    .message("The image is uploaded successfully.")
                    .data(new AssembledData.Builder()
                            .put("imageId", md5)
                            .put("url", IMG_SERVER_URL_PLACEHOLDER + md5)
                            .put("imgUrl", galleryServerUrlPrefix + md5)
                            .build())
                    .build();
        } catch (Exception e) {
            File file = new File(absolutePath);
            file.delete();
            log.error("image save error", e);
            throw new RuntimeException(e);
        }
    }

    private void deleteImageFile(String id) {
        String localPath = imgIdToPath(id);
        File file = new File(localPath);
        if (file.exists()) {
            file.delete();
        }
    }

    private String imgIdToPath(String id) {
        Gallery gallery = galleryMapper.selectGalleryById(id);
        return constructPath(galleryStorageRoot, gallery.getPath());
    }
}
