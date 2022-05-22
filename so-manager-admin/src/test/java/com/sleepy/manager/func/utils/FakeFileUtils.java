package com.sleepy.manager.func.utils;


import com.sleepy.manager.func.pojo.FakeFilePOJO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 假文件创建器
 *
 * @author gehoubao
 * @create 2021-01-20 13:58
 **/
public class FakeFileUtils {

    /**
     * 写入假文件到指定目录
     *
     * @param source 源目录
     * @param target 输出路径
     */
    public static void writeFakeFile(String source, String target) {
        List<FakeFilePOJO> tree = readFileTree(source);
        try {
            writeFakeFile(tree, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取参数path文件夹目录下的文件树
     *
     * @param path 文件夹绝对路径
     * @return
     */
    public static List<FakeFilePOJO> readFileTree(String path) {
        File root = new File(path);

        if (root.isFile()) {
            return null;
        }
        List<FakeFilePOJO> list = new ArrayList<>();

        for (File file : root.listFiles()) {
            FakeFilePOJO pojo = new FakeFilePOJO();

            if (file.isDirectory()) {
                pojo.setType("DIR");
                pojo.setSub(readFileTree(file.getAbsolutePath()));
            } else {
                pojo.setType("FILE");
            }
            pojo.setName(file.getName());
            list.add(pojo);
        }
        return list;
    }

    /**
     * 写入假文件到指定目录
     *
     * @param filePOJOList 待写入的文件目录树
     * @param targetPath   假文件输出文件目录
     * @throws IOException
     */
    public static void writeFakeFile(List<FakeFilePOJO> filePOJOList, String targetPath) throws IOException {
        File target = new File(targetPath);
        if (!target.exists()) {
            target.mkdirs();
        }
        for (FakeFilePOJO pojo : filePOJOList) {
            File file = new File(targetPath + File.separator + pojo.getName());
            if ("FILE".equals(pojo.getType())) {
                file.createNewFile();
            } else if ("DIR".equals(pojo.getType())) {
                file.mkdirs();
                writeFakeFile(pojo.getSub(), targetPath + File.separator + pojo.getName());
            }
        }
    }
}