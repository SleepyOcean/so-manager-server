package com.sleepy.manager.common.utils.file;

import cn.hutool.core.util.ArrayUtil;
import com.sleepy.manager.common.config.SoServerConfig;
import com.sleepy.manager.common.utils.DateUtils;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.uuid.IdUtils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;

import static com.sleepy.manager.common.constant.ConfigConstants.SERVER_CACHE_ROOT;
import static com.sleepy.manager.common.constant.ConfigConstants.SERVER_DATA_ROOT;

/**
 * 文件处理工具类
 *
 * @author
 */
public class FileUtils {
    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            IOUtils.close(os);
            IOUtils.close(fis);
        }
    }

    /**
     * 写数据到文件中
     *
     * @param data 数据
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static String writeImportBytes(byte[] data) throws IOException {
        return writeBytes(data, SoServerConfig.getImportPath());
    }

    /**
     * 写数据到文件中
     *
     * @param data      数据
     * @param uploadDir 目标文件
     * @return 目标文件
     * @throws IOException IO异常
     */
    public static String writeBytes(byte[] data, String uploadDir) throws IOException
    {
        FileOutputStream fos = null;
        String pathName = "";
        try
        {
            String extension = getFileExtendName(data);
            pathName = DateUtils.datePath() + "/" + IdUtils.fastUUID() + "." + extension;
            File file = FileUploadUtils.getAbsoluteFile(uploadDir, pathName);
            fos = new FileOutputStream(file);
            fos.write(data);
        }
        finally
        {
            IOUtils.close(fos);
        }
        return FileUploadUtils.getPathFileName(uploadDir, pathName);
    }

    /**
     * 删除文件
     * 
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath)
    {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 递归删除文件
     *
     * @param file
     * @return
     */
    public static boolean deleteFileRecursion(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFileRecursion(f);
            }
        }
        return file.delete();
    }

    /**
     * 文件名称验证
     *
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 检查文件是否可下载
     * 
     * @param resource 需要下载的文件
     * @return true 正常 false 非法
     */
    public static boolean checkAllowDownload(String resource)
    {
        // 禁止目录上跳级别
        if (StringUtils.contains(resource, ".."))
        {
            return false;
        }

        // 检查允许下载的文件规则
        return ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource));

        // 不在允许下载的文件规则
    }

    /**
     * 下载文件名重新编码
     * 
     * @param request 请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException
    {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE"))
        {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        }
        else if (agent.contains("Firefox"))
        {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        }
        else if (agent.contains("Chrome"))
        {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        else
        {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 下载文件名重新编码
     *
     * @param response 响应对象
     * @param realFileName 真实文件名
     * @return
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException
    {
        String percentEncodedFileName = percentEncode(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=")
                .append(percentEncodedFileName)
                .append(";")
                .append("filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-disposition", contentDispositionValue.toString());
        response.setHeader("download-filename", percentEncodedFileName);
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException
    {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }

    /**
     * 获取图像后缀
     *
     * @param photoByte 图像数据
     * @return 后缀名
     */
    public static String getFileExtendName(byte[] photoByte) {
        String strFileExtendName = "jpg";
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56)
                && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97)) {
            strFileExtendName = "gif";
        } else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70)) {
            strFileExtendName = "jpg";
        } else if ((photoByte[0] == 66) && (photoByte[1] == 77)) {
            strFileExtendName = "bmp";
        } else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71)) {
            strFileExtendName = "png";
        }
        return strFileExtendName;
    }

    /**
     * 获取文件MD5值
     *
     * @param file
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(File file) throws IOException, NoSuchAlgorithmException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        }
    }

    /**
     * 构建普通文件路径
     *
     * @param path
     * @return
     */
    public static String constructPath(String... path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.length; i++) {
            // 特殊处理： 处理相对路径的的开头包含文件分割符，导致路径错误
            if (i > 0 && (path[i].indexOf("\\") == 0 || path[i].indexOf("/") == 0)) {
                path[i] = path[i].substring(1);
            }
            // 特殊处理： 处理相对路径中包含 "/"、"\"导致的路径错误
            if (path[i].contains("\\")) {
                path[i] = path[i].replaceAll("\\\\", Matcher.quoteReplacement(File.separator));
            }
            if (path[i].contains("/")) {
                path[i] = path[i].replaceAll("/", Matcher.quoteReplacement(File.separator));
            }

            sb.append(path[i]);
            if (i != path.length - 1) {
                sb.append(File.separatorChar);
            }
        }
        return sb.toString();
    }

    /**
     * 构建 service 缓存路径下的路径。service创建的 “临时文件” 均使用该方法创建路径 <br/>
     * service临时存储目录 -> /ManagerServerCache （!!! 临时存储目录 非必须请勿修改）
     *
     * @param path
     * @return
     */
    public static String constructCachePath(String... path) {
        return constructPath(ArrayUtil.insert(path, 0, SERVER_CACHE_ROOT));
    }

    /**
     * 构建 service 持久化数据的路径。service创建的 “持久化文件” 均使用该方法创建路径 <br/>
     * service持久化数据存储根目录 -> /ManagerServerData （!!! 持久化数据存储根目录 非必须请勿修改）
     *
     * @param path
     * @return
     */
    public static String constructDataPath(String... path) {
        return constructPath(ArrayUtil.insert(path, 0, SERVER_DATA_ROOT));
    }

    /**
     * 检测目录是否存在，不存在则创建
     *
     * @param dirPath
     * @return
     */
    public static boolean checkDirExistAndCreate(String dirPath) {
        File file = new File(dirPath);
        return checkDirExistAndCreate(file);
    }

    /**
     * 检测目录是否存在，不存在则创建
     *
     * @param dir
     * @return
     */
    public static boolean checkDirExistAndCreate(File dir) {
        if (dir.exists()) {
            return true;
        }
        dir.mkdirs();
        return false;
    }

    public static File dirFile(String dirPath) {
        File dir = new File(dirPath);
        checkDirExistAndCreate(dir);
        return dir;
    }
}
