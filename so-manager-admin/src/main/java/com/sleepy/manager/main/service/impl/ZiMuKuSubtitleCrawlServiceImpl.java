package com.sleepy.manager.main.service.impl;

import com.alibaba.fastjson.JSON;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.common.core.redis.RedisCache;
import com.sleepy.manager.common.exception.ServiceException;
import com.sleepy.manager.common.utils.ExceptionUtil;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.main.helper.MovieHelper;
import com.sleepy.manager.main.service.SubtitleCrawlService;
import com.sleepy.manager.system.domain.Movie;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static com.sleepy.manager.common.utils.CommonUtils.generateRandomNum;

/**
 * @author captain1920
 * @classname ZiMuKuSubtitleCrawlServiceImpl
 * @description TODO
 * @date 2022/4/20 18:46
 */
@Slf4j
public class ZiMuKuSubtitleCrawlServiceImpl implements SubtitleCrawlService {

    public static final String ZIMUKU_HOST = "http://zmk.pw";
    private static final String SUBS_MATCH_KEY = "subsMatchMap";
    public static final String SUBTITLE_DOWNLOAD_ROOT = "";
    @Autowired
    private RedisCache redisCache;

    @Override
    public AssembledData rematchNasMovieSub() {
        try {
            List<Movie> movies = MovieHelper.loadNasMovie();
            movies = movies.stream().filter(m -> {
                boolean flag = StringUtils.isNotEmpty(m.getImdbid());
                if (!flag) {
                    log.info("skip subtitle match for movie[{}], because without imdbId", m.getTitle());
                }
                return flag;
            }).collect(Collectors.toList());

            String subMap = redisCache.getCacheObject(SUBS_MATCH_KEY);
            AssembledData subMapData = new AssembledData.Builder()
                    .putAll(JSON.parseObject(subMap)).build();
            if (subMapData.keySet().size() == movies.size()) {
                return new AssembledData.Builder()
                        .put("subsMatchMap", subMapData)
                        .build();
            }

            AssembledData.Builder subsMatchBuilder = new AssembledData.Builder();
            for (Movie movie : movies) {
                subsMatchBuilder
                        .put(movie.getImdbid(), new AssembledData.Builder()
                                .put("sub", listSubtitles(movie))
                                .put("movie", movie)
                                .put("status", 0)
                                .build());
            }
            AssembledData subsMatchMap = subsMatchBuilder.build();
            redisCache.setCacheObject(SUBS_MATCH_KEY, subsMatchMap);

            return new AssembledData.Builder()
                    .put("subsMatchMap", subsMatchMap)
                    .build();
        } catch (Exception e) {
            String msg = StringUtils.format("rematchNasMovieSub failed! because Exception={}, {}",
                    e.getClass().getName(), ExceptionUtil.getRootErrorMessage(e));
            log.error(msg);
            throw new ServiceException(msg);
        }
    }

    // step 1. search the movie all subtitles and try to automatic match the best, return the best match and all subtitle list
    // step 2. confirm the subtitle download link, and save to the specific file as cache
    // step 3. start the crawl process to download all subtitle and record downloaded subtitle to the specific file as cache for next crawl
    // step 4. read the downloaded subtitle cache and reconstruct the subtitle path with the same construct as movie
    @Override
    public AssembledData listSubtitles(Movie movie) {

        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            String searchUrl = ZIMUKU_HOST + "/search?q=" + movie.getImdbid();
            webClient.getPage(searchUrl);
            webClient.waitForBackgroundJavaScript(1000);
            HtmlPage htmlpage = webClient.getPage(searchUrl);
            String html = htmlpage.asXml();
            webClient.waitForBackgroundJavaScript(1000);
            Document searchPageDoc = Jsoup.parse(html);

            String subDetailUrl = ZIMUKU_HOST + searchPageDoc.getElementsByClass("litpic").get(0).getElementsByTag("a").get(0).attr("href");
            Thread.sleep(generateRandomNum(1000, 5000));
            HtmlPage subDetailPage = webClient.getPage(subDetailUrl);
            String subDetailHtml = subDetailPage.asXml();

            List<AssembledData> detailList = Jsoup.parse(subDetailHtml).getElementsByTag("tbody").get(0).getElementsByTag("tr")
                    .stream().map(p -> {

                        Element first = p.getElementsByClass("first").get(0);
                        String title = first.getElementsByTag("a").get(0).attr("title");
                        String detail = first.getElementsByTag("a").get(0).attr("href");
                        String download = first.getElementsByTag("a").get(0).attr("href").replace("detail", "dld");
                        AssembledData.Builder builder = new AssembledData.Builder()
                                .put("title", title)
                                .put("detail", detail)
                                .put("download", download);
                        List<Element> spanList = first.getElementsByTag("span");
                        if (spanList.size() > 4) {
                            String subType = spanList.get(0).text();
                            String subSource = spanList.get(4).text().replace("来源： ", "");
                            builder.put("subType", subType)
                                    .put("subSource", subSource);
                        }
                        List<Element> tdList = p.getElementsByTag("td");
                        if (tdList.size() > 4) {
                            List<String> langList = tdList.get(1).getElementsByTag("img").stream().map(a -> a.attr("alt")).collect(Collectors.toList());
                            String downloadCount = tdList.get(3).text();
                            builder.put("langList", langList)
                                    .put("downloadCount", downloadCount);
                        }
                        return builder.build();
                    })
                    .collect(Collectors.toList());

            String originFileName = JSON.parseObject(movie.getDetail()).getString("original_filename");
            AssembledData bestMatch = MovieHelper.searchBestMatch(detailList, originFileName);

            return new AssembledData.Builder()
                    .put("subtitles", detailList)
                    .put("bestMatch", bestMatch)
                    .build();
        } catch (Exception e) {
            String msg = StringUtils.format("downloadSubtitle failed! because Exception={}, {} [ttID={}]",
                    e.getClass().getName(), ExceptionUtil.getRootErrorMessage(e), movie.getImdbid());
            log.error(msg);
            throw new ServiceException(msg);
        }
    }

    @Override
    public AssembledData downloadSubtitle(String movieName, String downloadPageRoute) {
        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);

            HtmlPage downloadPage = webClient.getPage(ZIMUKU_HOST + downloadPageRoute);
            String downloadHtml = downloadPage.asXml();
            List<AssembledData> downloadLinkList = Jsoup.parse(downloadHtml).getElementsByClass("down").get(0).getElementsByTag("a").stream().map(p -> new AssembledData.Builder().put("download", p.attr("href")).put("title", p.text()).build()).collect(Collectors.toList());
            Thread.sleep(generateRandomNum(5000, 10000));

            String downloadLink = ZIMUKU_HOST + downloadLinkList.get(0).getString("download");
            webClient.getPage(downloadLink);
            webClient.waitForBackgroundJavaScript(10000);
            webClient.addRequestHeader("Referer", downloadLink);
            Page page = webClient.getPage(downloadLink + "?security_verify_data=313531322c393832");
            webClient.waitForBackgroundJavaScript(10000);

            String fileNameContent = page.getWebResponse().getResponseHeaderValue("Content-Disposition");
            String fileName = fileNameContent.substring(fileNameContent.indexOf("=") + 2, fileNameContent.length() - 1);
            InputStream contentAsStream = page.getWebResponse().getContentAsStream();
            String downloadPath = SUBTITLE_DOWNLOAD_ROOT + movieName + File.separator + fileName;
            IOUtils.write(
                    IOUtils.readFully(contentAsStream, (int) page.getWebResponse().getContentLength()),
                    new FileOutputStream(downloadPath));
            return new AssembledData.Builder().put("downloadedPath", downloadPath).build();
        } catch (Exception e) {
            String msg = StringUtils.format("downloadSubtitle failed! because Exception={}, {} [movieName={}, downloadPageRoute={}]",
                    e.getClass().getName(), ExceptionUtil.getRootErrorMessage(e), movieName, downloadPageRoute);
            log.error(msg);
            throw new ServiceException(msg);
        }
    }

    /**
     * subConfirmedMap结构
     * {
     * "imdbID": {
     * "status": unconfirmed(0) | confirmed(1),
     * "movie": movie,
     * "sub": subDetail
     * }
     * ...
     * }
     *
     * @param map
     * @return
     */
    @Override
    public AssembledData confirmSubMap(AssembledData map) {
        String subMap = redisCache.getCacheObject(SUBS_MATCH_KEY);

        AssembledData subMapData = new AssembledData.Builder()
                .putAll(JSON.parseObject(subMap)).build();

        for (String imdbID : map.keySet()) {
            subMapData.getJSONObject(imdbID).put("status", 1);
        }

        redisCache.setCacheObject(SUBS_MATCH_KEY, subMapData);

        return subMapData;
    }
}
