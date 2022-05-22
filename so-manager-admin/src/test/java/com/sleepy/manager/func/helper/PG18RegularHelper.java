package com.sleepy.manager.func.helper;

import cn.hutool.core.io.FileUtil;
import com.sleepy.manager.common.utils.file.FileUtils;
import com.sleepy.manager.func.utils.FakeFileUtils;
import com.sleepy.manager.main.helper.MovieHelper;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import static com.sleepy.manager.common.utils.file.FileUtils.constructPath;

/**
 * @author Captain1920
 * @classname PG18RegularHelper
 * @description TODO
 * @date 2022/5/20 20:22
 */
public class PG18RegularHelper {
    private final static String FAKE_FILE_ROOT = "E:\\5-Cache\\1-FakeFile";
    private final static String TRUE_FILE_ROOT = "\\\\DS218plus\\0-Cinema0\\Cinema3\\5-Classify(小小篇)\\Collection - page18.area\\2-fresh";


    @Test
    public void extractMovieFile() {
        int index = 3;
        // 参数
        String target = constructPath(TRUE_FILE_ROOT, String.valueOf(index));

        // Step 1. 找出所有视频
        Set<String> allMovieFilePath = MovieHelper.findMovieFilePath(Arrays.asList(target));
        // Step 2. 移动视频到统一目录下
        for (String path : allMovieFilePath) {
            FileUtil.move(new File(path),
                    FileUtils.dirFile(constructPath(TRUE_FILE_ROOT, "Top-" + index)),
                    false);
        }
    }

    @Test
    public void createPG18FakeFile() {
        // 参数
        String sourcePath = "\\\\DS218plus\\0-Cinema0\\Cinema3\\5-Classify(小小篇)\\Collection - page18.area\\2-fresh";
        String destPath = constructPath(FAKE_FILE_ROOT, "PG18");

        // 创建假文件
        FakeFileUtils.writeFakeFile(sourcePath, destPath);
    }
}
