package com.sleepy.manager.main.processor;

import com.sleepy.manager.blog.common.AssembledData;
import org.junit.Test;

/**
 * @author captain1920
 * @classname CrawlerProcessorTest
 * @description TODO
 * @date 2022/4/11 15:53
 */
public class CrawlerProcessorTest {

    CrawlerProcessor processor = new CrawlerProcessor();

    @Test
    public void analysisWebPageBaseInfoTest() {
        String url = "https://www.toutiao.com/article/7085239586711945760/?log_from=10c57feb84045_1649666137771";
        AssembledData data = processor.analysisWebPageBaseInfo(url);
        data.get("title");
    }
}
