package com.sleepy.manager.main.service;

import com.sleepy.manager.main.common.AssembledData;
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

    AssembledData downloadSubtitle(String movieName, String downloadPageRoute) throws Exception;

    AssembledData confirmSubMap(AssembledData map);

    AssembledData regularSubForMovie(Movie movie);
}
