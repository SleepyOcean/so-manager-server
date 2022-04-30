package com.sleepy.manager.main.service;

import com.sleepy.manager.blog.common.AssembledData;

/**
 * @author captain1920
 * @classname CrawlService
 * @description TODO
 * @date 2022/4/20 18:41
 */
public interface SubtitleCrawlService {

    AssembledData listSubtitles(String ttID);

    AssembledData downloadSubtitle(String movieName, String downloadPageRoute);
}
