package com.sleepy.manager.lab;

import cn.hutool.core.util.StrUtil;
import com.github.junrar.Junrar;
import org.junit.Test;

import static com.sleepy.manager.common.utils.LogUtils.logServiceError;

/**
 * @author Captain1920
 * @classname JunrarTest
 * @description TODO
 * @date 2022/7/6 10:27
 */
public class JunrarTest {

    @Test
    public void unrarTest() {
        try {
            Junrar.extract("C:\\Users\\Captain1920\\Downloads\\[zmk.pw]搭错车.Papa.Can.You.Hear.Me.Sing.1983.1080p.WEB-DL.AVC.AC3.2.0-Knight.rar",
                    "C:\\Users\\Captain1920\\Downloads\\test");
        } catch (Exception e) {
            logServiceError(e, StrUtil.format("解压rar({})失败！", "[zmk.pw]搭错车.Papa.Can.You.Hear.Me.Sing.1983.1080p.WEB-DL.AVC.AC3.2.0-Knight.rar"));
        }
    }
}
