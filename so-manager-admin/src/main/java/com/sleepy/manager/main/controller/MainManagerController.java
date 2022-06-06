package com.sleepy.manager.main.controller;

import com.sleepy.manager.common.core.domain.entity.SysUser;
import com.sleepy.manager.main.common.UnionResponse;
import com.sleepy.manager.main.processor.CrawlerProcessor;
import com.sleepy.manager.main.processor.MovieProcessor;
import com.sleepy.manager.main.service.MainManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class MainManagerController {

    @Autowired
    MainManagerService mainManagerService;
    @Autowired
    MovieProcessor movieProcessor;
    @Autowired
    CrawlerProcessor crawlerProcessor;

    // funds 基金模块
    @GetMapping("/funds/{strategyId}")
    public UnionResponse getFundsStrategyReport(@PathVariable("strategyId") Long strategyId) {
        return mainManagerService.getFundsStrategyReport(strategyId);
    }

    // config 配置模块
    @PostMapping("/getUserRoutes")
    public UnionResponse getUserRoutes(@RequestBody SysUser user) {
        return mainManagerService.getUserRoutes(user);
    }

    // bookmark 书签模块
    @GetMapping("/analysis/url/base-info")
    public UnionResponse getWebPageBaseInfo(@RequestParam("url") String url) {
        return mainManagerService.getWebPageBaseInfo(url);
    }

    // 稍后阅读模块
    @GetMapping("/analysis/url/article")
    public UnionResponse analysisWebArticle(@RequestParam("url") String url,
                                            @RequestParam(value = "type", required = false) String type,
                                            @RequestParam(value = "target", required = false) String target) {
        return new UnionResponse.Builder().status(HttpStatus.OK).data(crawlerProcessor.analysisWebArticle(url, type, target)).build();
    }

    // movie 电影库模块
    @GetMapping("/movie/sync-nas-movie-base")
    public UnionResponse syncNasMovieBase() {
        return mainManagerService.syncNasMovieBase();
    }

    @GetMapping("/movie/clear-movie-img-cache")
    public UnionResponse clearMovieImgCache() {
        movieProcessor.clearAllCacheNasImg();
        return new UnionResponse.Builder().status(HttpStatus.OK).build();
    }

    @GetMapping("/movie/cache-movie-img")
    public UnionResponse cacheMovieImg() {
        movieProcessor.cacheAllNasMovieImg();
        return new UnionResponse.Builder().status(HttpStatus.OK).build();
    }

    @GetMapping("/movie/rebuild-movie-img-cache")
    public UnionResponse rebuildMovieImgCache() {
        movieProcessor.clearAllCacheNasImg();
        movieProcessor.cacheAllNasMovieImg();
        return new UnionResponse.Builder().status(HttpStatus.OK).build();
    }
}
