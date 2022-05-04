package com.sleepy.manager.common.utils.file;

import com.sleepy.manager.common.config.SoServerConfig;
import com.sleepy.manager.common.constant.Constants;
import com.sleepy.manager.common.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * 图片处理工具类
 *
 * @author
 */
public class ImageUtils
{
    private static final Logger log = LoggerFactory.getLogger(ImageUtils.class);

    public static byte[] getImage(String imagePath)
    {
        InputStream is = getFile(imagePath);
        try
        {
            return IOUtils.toByteArray(is);
        }
        catch (Exception e)
        {
            log.error("图片加载异常 {}", e);
            return null;
        }
        finally
        {
            IOUtils.closeQuietly(is);
        }
    }

    public static InputStream getFile(String imagePath)
    {
        try
        {
            byte[] result = readFile(imagePath);
            result = Arrays.copyOf(result, result.length);
            return new ByteArrayInputStream(result);
        }
        catch (Exception e)
        {
            log.error("获取图片异常 {}", e);
        }
        return null;
    }

    /**
     * 读取文件为字节数据
     * 
     * @param url 地址
     * @return 字节数据
     */
    public static byte[] readFile(String url)
    {
        InputStream in = null;
        try
        {
            if (url.startsWith("http"))
            {
                // 网络地址
                URL urlObj = new URL(url);
                URLConnection urlConnection = urlObj.openConnection();
                urlConnection.setConnectTimeout(30 * 1000);
                urlConnection.setReadTimeout(60 * 1000);
                urlConnection.setDoInput(true);
                in = urlConnection.getInputStream();
            }
            else
            {
                // 本机地址
                String localPath = SoServerConfig.getProfile();
                String downloadPath = localPath + StringUtils.substringAfter(url, Constants.RESOURCE_PREFIX);
                try {
                    in = new FileInputStream(downloadPath);
                } catch (FileNotFoundException e) {
                    // 非downloadPath文件，默认按绝对路径处理
                    in = new FileInputStream(url);
                }
            }
            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            log.error("获取文件路径异常 {}", e);
            return null;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * base64字符串保存为图片
     *
     * @param base64Str
     * @param destFileName
     * @throws IOException
     */
    public static String base64ToImgFile(String base64Str, String destFileName) throws IOException {
        String format = null;
        if (destFileName.contains(StringUtils.POINT)) {
            format = destFileName.substring(destFileName.indexOf(".") + 1);
            destFileName = destFileName.substring(0, destFileName.indexOf("."));
        }
        if (base64Str.contains(StringUtils.COMMA)) {
            format = base64Str.substring(base64Str.indexOf("/") + 1, base64Str.indexOf(";"));
            base64Str = base64Str.substring(base64Str.indexOf(",") + 1);
        }

        String dest = destFileName + StringUtils.POINT + (StringUtils.isEmpty(format) ? "jpg" : format);
        //对字节数组字符串进行Base64解码并生成图片
        byte[] bs = Base64Utils.decodeFromString(base64Str);
        FileUtils.writeByteArrayToFile(new File(dest), bs);
        return dest;
    }
}
