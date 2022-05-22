package com.sleepy.manager.func.helper;

import cn.hutool.http.HttpUtil;
import com.google.gson.reflect.TypeToken;
import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.main.service.impl.ZiMuKuSubtitleCrawlServiceImpl;
import com.sleepy.manager.system.domain.Movie;
import com.sleepy.manager.utils.DataSnapshotUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sleepy.manager.common.utils.StringUtils.format;
import static com.sleepy.manager.common.utils.file.FileUtils.constructPath;

/**
 * @author Captain1920
 * @classname SubtitleMatchHelper
 * @description TODO
 * @date 2022/5/22 10:43
 */
public class SubtitleMatchHelper {

    private static final String MOVIE_BASE_CACHE_FILE_PATH = "local/DataSnapshot/movie-base-2022-05-09.json";
    ZiMuKuSubtitleCrawlServiceImpl ziMuKuSubtitleCrawlService = new ZiMuKuSubtitleCrawlServiceImpl();

    Map<Long, Movie> movieBaseCache;


    /**
     * Step 1. 手动匹配下载电影字幕
     */
    @Test
    public void manualMatchSubtitles() {
        setMovieBaseCache(MOVIE_BASE_CACHE_FILE_PATH);
        List<String> movieIdList = movieBaseCache.values().stream().map(p -> p.getId().toString()).collect(Collectors.toList());

        List<Movie> movieList = movieIdList.stream().map(id -> {
            if (movieBaseCache.containsKey(Long.valueOf(id))) {
                return movieBaseCache.get(Long.valueOf(id));
            }
            String res = HttpUtil.get("http://api.sleepyocean.cn/resource/movie/" + id);
            Movie movie = DataSnapshotUtils.fromJson(new AssembledData.Builder().putAll(res).build().getString("data"),
                    new TypeToken<Movie>() {
                    }.getType());
            return movie;
        }).collect(Collectors.toList());

        // 过滤已有字幕记录
        movieList = movieList.stream().filter(m -> {
            File file = new File(constructPath("\\\\DS218plus", m.getAddress()));
            for (String name : file.getParentFile().list()) {
                if (name.contains(".ass") || name.contains(".srt") || name.contains(".ssa") || name.contains(".sup")) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        // 过滤无需中文字幕的电影
        movieList = movieList.stream()
                .filter(m -> !(StringUtils.isEmpty(m.getImdbid()) ||
                        new AssembledData.Builder().putAll(m.getDetail()).build().getString("languages").toLowerCase().contains("chinese")))
                .collect(Collectors.toList());

        int failedCount = 0;
        for (Movie movie : movieList) {
            AssembledData matchedSub = null;
            // 获取字幕
            try {
                matchedSub = ziMuKuSubtitleCrawlService.listSubtitles(movie);
            } catch (Exception e) {
                continue;
            }

            if (matchedSub.getJSONArray("subtitles").size() < 1) continue;

            // 下载字幕
            String downloadPageRoute = null;
            try {
                downloadPageRoute = matchedSub.getJSONObject("bestMatch").getString("download");
            } catch (Exception e) {
                System.out.println(format("自动匹配字幕失败，{}", movie.getId()));
                continue;
            }
            try {
                ziMuKuSubtitleCrawlService.downloadSubtitle(String.valueOf(movie.getId()), downloadPageRoute);
            } catch (Exception e) {
                System.out.println(format("下载字幕失败，{}", movie.getId()));
                e.printStackTrace();
                if (failedCount++ > 2) return;
            }
        }

        // 整理字幕
        File downloadDir = new File(ZiMuKuSubtitleCrawlServiceImpl.SUBTITLE_DOWNLOAD_ROOT);
        List<String> downloadedIdList = Arrays.asList(downloadDir.list());
        List<Movie> downloadedList = movieList.stream().filter(movie -> downloadedIdList.contains(String.valueOf(movie.getId()))).collect(Collectors.toList());
        Collections.sort(downloadedList, new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                return (int) (o1.getId() - o2.getId());
            }
        });
        for (int i = 0; i < downloadedList.size(); i++) {
            ziMuKuSubtitleCrawlService.regularSubForMovie(downloadedList.get(i));
        }
    }

    /**
     * Step 2. 符合条件的字幕整理为可迁移到NAS的目录结构
     */
    @Test
    public void cleanUpDownloadSubBatch() {
        setMovieBaseCache(MOVIE_BASE_CACHE_FILE_PATH);
        List<String> movieIdList = Arrays.asList(new File(ZiMuKuSubtitleCrawlServiceImpl.SUBTITLE_ROOT).list());

        List<Movie> movieList = movieIdList.stream().map(id -> {
            if (movieBaseCache.containsKey(Long.valueOf(id))) {
                return movieBaseCache.get(Long.valueOf(id));
            }
            String res = HttpUtil.get("http://api.sleepyocean.cn/resource/movie/" + id);
            Movie movie = DataSnapshotUtils.fromJson(new AssembledData.Builder().putAll(res).build().getString("data"),
                    new TypeToken<Movie>() {
                    }.getType());
            return movie;
        }).collect(Collectors.toList());

        for (Movie movie : movieList) {
            ziMuKuSubtitleCrawlService.renamePathForSub(movie);
        }
    }

    private void setMovieBaseCache(String cachePath) {
        List<Movie> movies = null;
        try {
            movies = DataSnapshotUtils.loadDataSnapshotFromFile(cachePath, new TypeToken<List<Movie>>() {
            }.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        movieBaseCache = movies.stream().collect(Collectors.toMap(Movie::getId, Function.identity()));
    }
}
