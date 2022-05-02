package com.sleepy.manager.main.service;

import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.system.domain.Movie;

/**
 * @author captain1920
 * @classname CrawlService
 * @description TODO
 * @date 2022/4/20 18:41
 */
public interface SubtitleCrawlService {

    AssembledData rematchNasMovieSub();

    AssembledData listSubtitles(Movie movie);

    AssembledData downloadSubtitle(String movieName, String downloadPageRoute);

    AssembledData confirmSubMap(AssembledData map);
}
