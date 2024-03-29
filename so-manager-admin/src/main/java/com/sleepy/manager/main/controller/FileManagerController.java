package com.sleepy.manager.main.controller;

import com.sleepy.manager.common.utils.ServletUtils;
import com.sleepy.manager.main.common.AssembledData;
import com.sleepy.manager.main.common.UnionResponse;
import com.sleepy.manager.main.processor.CrawlerProcessor;
import com.sleepy.manager.main.processor.MovieProcessor;
import com.sleepy.manager.main.service.FileManagerService;
import com.sleepy.manager.system.domain.ArticleReading;
import com.sleepy.manager.system.domain.Gallery;
import com.sleepy.manager.system.domain.Movie;
import com.sleepy.manager.system.mapper.MovieMapper;
import com.sleepy.manager.system.service.IArticleReadingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sleepy.manager.common.utils.DateUtils.dateTime;
import static com.sleepy.manager.common.utils.StringUtils.format;

/**
 * @author captain1920
 * @classname FileManagerController
 * @description TODO
 * @date 2022/3/26 14:27
 */
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/resource")
public class FileManagerController {
    @Autowired
    FileManagerService fileManagerService;
    @Autowired
    MovieMapper movieMapper;
    @Autowired
    CrawlerProcessor crawlerProcessor;
    @Autowired
    IArticleReadingService articleReadingService;
    @Autowired
    private MovieProcessor movieProcessor;
    @Value("${so-manager-server.galleryPrefix}")
    private String galleryServerUrlPrefix;

    // Image 模块
    @GetMapping(value = "/img/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImage(@PathVariable("id") String id) {
        ServletUtils.getResponse().setHeader("Cache-Control", "max-age=6048000");
        return fileManagerService.getImg(id);
    }

    @GetMapping(value = "/img/thumbnail/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getThumbnailImage(@PathVariable("id") String id) {
        return fileManagerService.getImgThumbnail(id);
    }

    @GetMapping("/img/compress")
    @ResponseBody
    public byte[] getCompressedImage(@RequestParam("ratio") String ratio, @RequestParam("url") String url) {
        return fileManagerService.getCompressedImg(ratio, url);
    }

    @PostMapping("/gallery/upload")
    public UnionResponse saveImage(@RequestBody Gallery gallery) {
        return fileManagerService.upload(gallery);
    }

    @PostMapping("/gallery/delete/{id}")
    public UnionResponse deleteImage(@PathVariable("id") String id) {
        return fileManagerService.delete(id);
    }

    // Movie 模块
    @GetMapping(value = "/img/movie-cover/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getMovieCover(@PathVariable("id") long id) {
        ServletUtils.getResponse().setHeader("Cache-Control", "max-age=6048000");
        return fileManagerService.getMovieCover(id);
    }

    @GetMapping(value = "/img/movie-fanart/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getMovieFanart(@PathVariable("id") long id) {
        ServletUtils.getResponse().setHeader("Cache-Control", "max-age=6048000");
        return fileManagerService.getMovieFanart(id);
    }

    @GetMapping(value = "/movie/{id}")
    public UnionResponse getMovieDetail(@PathVariable("id") long id) {
        Movie movie = movieMapper.selectMovieById(id);
        return new UnionResponse.Builder().status(HttpStatus.OK).data(new AssembledData.Builder()
                .putAll(movie)
                .putAll(movieProcessor.genMovieTag(movie))
                .put("coverUrl", format("{}{}{}", galleryServerUrlPrefix, "movie-cover/", movie.getId()))
                .put("fanartUrl", format("{}{}{}", galleryServerUrlPrefix, "movie-fanart/", movie.getId()))
                .put("updateDate", dateTime(movie.getUpdatedAt()))
                .put("createDate", dateTime(movie.getCreatedAt()))
                .build()).build();
    }

    // 稍后阅读模块
    @GetMapping(value = "/img/article/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getThirdPartArticleImgCache(@PathVariable("id") String id) {
        ServletUtils.getResponse().setHeader("Cache-Control", "max-age=6048000");
        return crawlerProcessor.getThirdPartArticleImgCache(id);
    }

    @GetMapping(value = "/reading-article/{id}", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getThirdPartArticle(@PathVariable("id") String id) {
        ArticleReading articleReading = new ArticleReading();
        articleReading.setMd5(id);
        List<ArticleReading> list = articleReadingService.selectArticleReadingList(articleReading);
        if (list.size() > 0) {
            articleReading = list.get(0);
        }
        return crawlerProcessor.beautifyArticle(articleReading);
    }
}
