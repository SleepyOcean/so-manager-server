package com.sleepy.manager.main.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.sleepy.manager.common.core.domain.entity.SysUser;
import com.sleepy.manager.main.common.AssembledData;
import com.sleepy.manager.main.common.UnionResponse;
import com.sleepy.manager.main.processor.CrawlerProcessor;
import com.sleepy.manager.main.processor.MovieProcessor;
import com.sleepy.manager.main.service.MainManagerService;
import com.sleepy.manager.system.domain.ArticleReading;
import com.sleepy.manager.system.service.IArticleReadingService;
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
    @Autowired
    IArticleReadingService articleReadingService;

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
        ArticleReading articleReading = crawlerProcessor.analysisWebArticle(url, type, target);
        return new UnionResponse.Builder().status(HttpStatus.OK).data(new AssembledData.Builder()
                .putAll(articleReading)
                .put("preview", crawlerProcessor.beautifyArticle(articleReading))
                .build()).build();
    }

    @GetMapping("/article-reading/add")
    public UnionResponse addWebArticle(@RequestParam("url") String url,
                                       @RequestParam(value = "type", required = false) String type,
                                       @RequestParam(value = "target", required = false) String target) {
        ArticleReading articleReading = crawlerProcessor.analysisWebArticle(url, type, target);
        if (ObjectUtil.isNotEmpty(articleReading) && StrUtil.isNotEmpty(articleReading.getContent())) {
            ArticleReading old = articleReadingService.selectArticleReadingByUrlMD5(articleReading.getMd5());
            if (ObjectUtil.isNotEmpty(old)) {
                articleReading.setId(old.getId());
                articleReadingService.updateArticleReading(articleReading);
            } else {
                articleReadingService.insertArticleReading(articleReading);
            }
        }
        return new UnionResponse.Builder().status(HttpStatus.OK).data(new AssembledData.Builder()
                .putAll(articleReading)
                .put("preview", crawlerProcessor.beautifyArticle(articleReading))
                .build()).build();
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
