package com.sleepy.manager.main.helper;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.common.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.sleepy.manager.common.utils.DateUtils.YYYY_MM_DD_HH_MM_SS;
import static com.sleepy.manager.common.utils.DateUtils.parseDateToStr;

/**
 * 图片 视频 EXIF 信息获取
 *
 * @author Captain1920
 * @classname MediaMetaDataHelper
 * @description TODO
 * @date 2022/5/3 11:49
 */
public class MediaMetaDataHelper {

    public static AssembledData createImgMetaInfo(String imgPath) throws ImageProcessingException, IOException, ParseException, MetadataException {
        File imageFile = new File(imgPath);

        Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
        AssembledData.Builder metaInfo = new AssembledData.Builder();
        for (Directory directory : metadata.getDirectories()) {
            if (directory == null) {
                continue;
            }
            for (Tag tag : directory.getTags()) {
                String tagName = tag.getTagName();
                String desc = tag.getDescription();
                String directName = tag.getDirectoryName();
                if ("GPS Latitude".equals(tagName)) {
                    metaInfo.put("纬度 ", desc);
                } else if ("GPS Longitude".equals(tagName)) {
                    metaInfo.put("经度", desc);
                } else if ("GPS Altitude".equals(tagName)) {
                    metaInfo.put("海拔", desc);
                } else if ("Date/Time Original".equals(tagName)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                    Date date = sdf.parse(desc);
                    metaInfo.put("拍照时间", parseDateToStr(YYYY_MM_DD_HH_MM_SS, date));
                } else if ("Date/Time".equals(tagName)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                    Date date = sdf.parse(desc);
                    metaInfo.put("创建时间", parseDateToStr(YYYY_MM_DD_HH_MM_SS, date));
                } else if ("Expected File Name Extension".equals(tagName)) {
                    metaInfo.put("图片格式", desc);
                } else if ("File Size".equals(tagName)) {
                    metaInfo.put("图片大小", StringUtils.getFormatFileSize(Double.parseDouble(desc.replaceAll("[^0-9]", "").trim())));
                } else {
                    if ("Image Width".equals(tagName)) {
                        metaInfo.put("宽", StringUtils.getIntegerNumFromString(desc));
                    } else if ("Image Height".equals(tagName)) {
                        metaInfo.put("高", StringUtils.getIntegerNumFromString(desc));
                    } else if ("X Resolution".equals(tagName)) {
                        metaInfo.put("水平分辨率", StringUtils.getIntegerNumFromString(desc));
                    } else if ("Y Resolution".equals(tagName)) {
                        metaInfo.put("垂直分辨率", StringUtils.getIntegerNumFromString(desc));
                    }
                }
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_FNUMBER)) {
                //光圈F值=镜头的焦距/镜头光圈的直径
                metaInfo.put("光圈值", directory.getDescription(ExifSubIFDDirectory.TAG_FNUMBER));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_EXPOSURE_TIME)) {
                metaInfo.put("曝光时间", directory.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME) + "秒");
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT)) {
                metaInfo.put("ISO速度", directory.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_FOCAL_LENGTH)) {
                metaInfo.put("焦距", directory.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH) + "毫米");
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_MAX_APERTURE)) {
                metaInfo.put("最大光圈", directory.getDouble(ExifSubIFDDirectory.TAG_MAX_APERTURE));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH)) {
                metaInfo.put("宽", directory.getString(ExifIFD0Directory.TAG_EXIF_IMAGE_WIDTH));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT)) {
                metaInfo.put("高", directory.getString(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_MAKE)) {
                metaInfo.put("照相机制造商", directory.getString(ExifSubIFDDirectory.TAG_MAKE));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_MODEL)) {
                metaInfo.put("照相机型号", directory.getString(ExifSubIFDDirectory.TAG_MODEL));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_X_RESOLUTION)) {
                metaInfo.put("水平分辨率(X方向分辨率)", directory.getString(ExifSubIFDDirectory.TAG_X_RESOLUTION));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_Y_RESOLUTION)) {
                metaInfo.put("垂直分辨率(Y方向分辨率)", directory.getString(ExifSubIFDDirectory.TAG_Y_RESOLUTION));
            }
            //其他参数测试开始
            if (directory.containsTag(ExifSubIFDDirectory.TAG_SOFTWARE)) {
                //Software软件 显示固件Firmware版本
                metaInfo.put("显示固件Firmware版本(图片详细信息的程序名称)", directory.getString(ExifSubIFDDirectory.TAG_SOFTWARE));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH)) {
                metaInfo.put("35mm焦距", directory.getString(ExifSubIFDDirectory.TAG_35MM_FILM_EQUIV_FOCAL_LENGTH));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_APERTURE)) {
                metaInfo.put("孔径(图片分辨率单位)", directory.getString(ExifSubIFDDirectory.TAG_APERTURE));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_APPLICATION_NOTES)) {
                //一般无
                metaInfo.put("应用程序记录", directory.getString(ExifSubIFDDirectory.TAG_APPLICATION_NOTES));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_ARTIST)) {
                //作者
                metaInfo.put("作者", directory.getString(ExifSubIFDDirectory.TAG_ARTIST));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_BODY_SERIAL_NUMBER)) {
                metaInfo.put("TAG_BODY_SERIAL_NUMBER", directory.getString(ExifSubIFDDirectory.TAG_BODY_SERIAL_NUMBER));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_METERING_MODE)) {
                //MeteringMode测光方式， 平均式测光、中央重点测光、点测光等
                metaInfo.put("点测光值", directory.getString(ExifSubIFDDirectory.TAG_METERING_MODE));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_RESOLUTION_UNIT)) {
                //XResolution/YResolution X/Y方向分辨率
                metaInfo.put("分辨率单位", directory.getString(ExifSubIFDDirectory.TAG_RESOLUTION_UNIT));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_EXPOSURE_BIAS)) {
                metaInfo.put("曝光补偿", directory.getDouble(ExifSubIFDDirectory.TAG_EXPOSURE_BIAS));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_COLOR_SPACE)) {
                metaInfo.put("色域、色彩空间", directory.getString(ExifSubIFDDirectory.TAG_COLOR_SPACE));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_YCBCR_COEFFICIENTS)) {
                metaInfo.put("色相系数", directory.getString(ExifSubIFDDirectory.TAG_YCBCR_COEFFICIENTS));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_YCBCR_POSITIONING)) {
                metaInfo.put("色相定位", directory.getString(ExifSubIFDDirectory.TAG_YCBCR_POSITIONING));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_YCBCR_SUBSAMPLING)) {
                metaInfo.put("色相抽样", directory.getString(ExifSubIFDDirectory.TAG_YCBCR_SUBSAMPLING));
            }
            if (directory.containsTag(ExifSubIFDDirectory.TAG_EXIF_VERSION)) {
                metaInfo.put("exif版本号", directory.getString(ExifSubIFDDirectory.TAG_EXIF_VERSION));
            }
        }
        return metaInfo.build();
    }
}
