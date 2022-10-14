package com.sleepy.manager.main.helper;

import com.sleepy.manager.main.processor.MovieProcessor;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Captain1920
 * @classname MovieHelperTest
 * @description TODO
 * @date 2022/5/22 17:28
 */
public class MovieProcessorTest {
    private static final String MOVIE_BASE_CACHE_FILE_PATH = "local/DataSnapshot/movie-base-2022-05-09.json";

    @Test
    public void cacheNasImgTest() throws IOException {
        new MovieProcessor().cacheAllNasMovieImg();
    }
}
