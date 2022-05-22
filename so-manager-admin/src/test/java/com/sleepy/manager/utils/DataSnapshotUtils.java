package com.sleepy.manager.utils;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * @author captain1920
 * @classname DataSnapshotUtils
 * @description TODO
 * @date 2022/4/19 20:30
 */
public class DataSnapshotUtils {

    public static void writeDataSnapshotToFile(String filePath, Object data) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        FileUtils.write(new File(filePath), json, Charset.defaultCharset());
    }

    public static <T> T loadDataSnapshotFromFile(String filePath, Type type) throws IOException {
        String json = FileUtils.readFileToString(new File(filePath), Charset.defaultCharset());
        return new Gson().fromJson(json, type);
    }

    public static <T> T loadDataSnapshotFromFile(String filePath, Class<T> clazz) throws IOException {
        String json = FileUtils.readFileToString(new File(filePath), Charset.defaultCharset());
        return new Gson().fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        return new Gson().fromJson(json, type);
    }
}
