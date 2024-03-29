package com.sleepy.manager.main.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.compress.CompressUtil;
import cn.hutool.extra.compress.extractor.Extractor;
import com.alibaba.fastjson.JSON;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WaitingRefreshHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.junrar.Junrar;
import com.sleepy.manager.common.core.redis.RedisCache;
import com.sleepy.manager.common.exception.ServiceException;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.file.FileUtils;
import com.sleepy.manager.main.adpater.WebClientAdapter;
import com.sleepy.manager.main.common.AssembledData;
import com.sleepy.manager.main.common.BizConst;
import com.sleepy.manager.main.processor.MovieProcessor;
import com.sleepy.manager.main.service.SubtitleCrawlService;
import com.sleepy.manager.system.domain.Movie;
import com.sleepy.manager.system.mapper.MovieMapper;
import com.sleepy.manager.system.service.ISysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sleepy.manager.common.utils.CommonUtils.generateRandomNum;
import static com.sleepy.manager.common.utils.LogUtils.logError;
import static com.sleepy.manager.common.utils.LogUtils.logServiceError;
import static com.sleepy.manager.common.utils.StringUtils.format;
import static com.sleepy.manager.common.utils.file.FileUtils.checkDirExistAndCreate;
import static com.sleepy.manager.common.utils.file.FileUtils.constructCachePath;

/**
 * Subtitles match step:
 * step 1. search the movie all subtitles and try to automatic match the best, return the best match and all subtitle list
 * step 2. confirm the subtitle download link, and save to the specific file as cache
 * step 3. start the crawl process to download all subtitle and record downloaded subtitle to the specific file as cache for next crawl
 * step 4. read the downloaded subtitle cache and reconstruct the subtitle path with the same construct as movie
 *
 * @author captain1920
 * @classname ZiMuKuSubtitleCrawlServiceImpl
 * @description TODO
 * @date 2022/4/20 18:46
 */
@Slf4j
@Service
public class ZiMuKuSubtitleCrawlServiceImpl implements SubtitleCrawlService {

    public static final String SUBTITLE_DOWNLOAD_ROOT = "subtitle/1-SubtitleDownloadRoot";
    public static final String SUBTITLE_EXTRACT_ROOT = "subtitle/2-SubtitleExtractRoot";
    public static final String SUBTITLE_ROOT = "subtitle/3-SubtitleForNas";
    private static final String ZIMUKU_HOST = "http://zmk.pw";
    private static final String SUBS_MATCH_KEY = "subsMatchMap";
    @Autowired
    ISysConfigService configService;
    private Set<String> chsAndEngSet;
    LRUCache<Long, AssembledData> subtitlesListCache = CacheUtil.newLRUCache(10, 60 * 60 * 1000);

    @Autowired
    private RedisCache redisCache;
    @Autowired
    MovieProcessor movieProcessor;
    @Autowired
    MovieMapper movieMapper;
    @Autowired
    WebClientAdapter webClientAdapter;
    private Set<String> chsSet;

    @Override
    public AssembledData rematchNasMovieSub() {
        try {
            List<Movie> movies = movieProcessor.loadNasMovie();
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
            logServiceError(e, "rematchNasMovieSub failed!");
        }
        return new AssembledData.Builder().build();
    }

    @Override
    public AssembledData listSubtitles(long id) {
        Movie movie = movieMapper.selectMovieById(id);
        if (ObjectUtils.isEmpty(movie)) {
            return new AssembledData.Builder().build();
        }
        return listSubtitles(movie);
    }

    @Override
    public AssembledData listSubtitles(Movie movie) {
        // 先查询 cache 是否存在
        AssembledData cache = subtitlesListCache.get(movie.getId());
        if (ObjectUtil.isNotEmpty(cache)) {
            return cache;
        }
        // 过滤无需中文字幕的电影
        if (new AssembledData.Builder().putAll(movie.getDetail()).build().getString("languages").toLowerCase().contains("chinese")) {
            return new AssembledData.Builder()
                    .put("subtitles", new ArrayList<>())
                    .put("msg", "中文电影，无需字幕")
                    .build();
        }

        try {
            // 获取 search token
            webClientAdapter.getPage(ZIMUKU_HOST, generateRandomNum(1000, 5000));
            String homepage = webClientAdapter.getPage(ZIMUKU_HOST).getWebResponse().getContentAsString();
            Document homeDoc = Jsoup.parse(homepage);
            String token = homeDoc.getElementsByTag("input").get(1).attr("value");

            String searchUrl = format("{}/search?q={}&vertoken={}", ZIMUKU_HOST, movie.getImdbid(), token);
            webClientAdapter.getPage(searchUrl, generateRandomNum(1000, 5000));
            HtmlPage htmlpage = webClientAdapter.getPage(searchUrl);
            String html = htmlpage.asXml();
            Document searchPageDoc = Jsoup.parse(html);

            String subDetailUrl = ZIMUKU_HOST + searchPageDoc.getElementsByClass("litpic").get(0).getElementsByTag("a").get(0).attr("href");
            Thread.sleep(generateRandomNum(1000, 5000));
            HtmlPage subDetailPage = webClientAdapter.getPage(subDetailUrl);
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
            int bestMatchIndex = movieProcessor.searchBestMatch(detailList, originFileName);
            AssembledData bestMatch = detailList.get(bestMatchIndex);
            detailList.remove(bestMatchIndex);
            AssembledData subtitles = new AssembledData.Builder()
                    .put("subtitles", detailList)
                    .put("bestMatch", bestMatch)
                    .build();
            subtitlesListCache.put(movie.getId(), subtitles);
            return subtitles;
        } catch (Exception e) {
            logServiceError(e, format("listSubtitles failed! movie[name={}, ttid={}]", movie.getTitle(), movie.getImdbid()));
        }
        return new AssembledData.Builder().build();
    }

    @Override
    public AssembledData downloadSubtitle(long id, String downloadPageRoute) {
        Movie movie = movieMapper.selectMovieById(id);
        if (ObjectUtils.isEmpty(movie)) {
            return new AssembledData.Builder().build();
        }
        return downloadSubtitle(movie, downloadPageRoute);
    }

    @Override
    public AssembledData downloadSubtitle(Movie movie, String downloadPageRoute) {
        String movieId = movie.getId().toString();
        try (WebClient webClient = WebClientAdapter.create()) {
            webClient.setRefreshHandler(new WaitingRefreshHandler());

            webClient.getPage(ZIMUKU_HOST + downloadPageRoute);
            webClient.waitForBackgroundJavaScript(generateRandomNum(5000, 9000));
            HtmlPage downloadPage = webClient.getPage(ZIMUKU_HOST + downloadPageRoute);
            webClient.waitForBackgroundJavaScript(generateRandomNum(15000, 20000));
            String downloadHtml = downloadPage.asXml();
            List<AssembledData> downloadLinkList = Jsoup.parse(downloadHtml).getElementsByClass("down").get(0).getElementsByTag("a").stream().map(p -> new AssembledData.Builder().put("download", p.attr("href")).put("title", p.text()).build()).collect(Collectors.toList());
            Thread.sleep(generateRandomNum(5000, 10000));

            String downloadLink = ZIMUKU_HOST + downloadLinkList.get(3).getString("download");
            webClient.getPage(downloadLink);
            webClient.waitForBackgroundJavaScript(generateRandomNum(6000, 9000));
            webClient.addRequestHeader("Referer", downloadLink);
            Page page = webClient.getPage(downloadLink + "?security_verify_data=313531332c393832");
            webClient.waitForBackgroundJavaScript(generateRandomNum(15000, 30000));

            String fileNameContent = page.getWebResponse().getResponseHeaderValue("Content-Disposition");
            String fileName = new String(
                    fileNameContent.substring(fileNameContent.indexOf("=") + 2, fileNameContent.length() - 1).getBytes(StandardCharsets.ISO_8859_1),
                    StandardCharsets.UTF_8);
            InputStream contentAsStream = page.getWebResponse().getContentAsStream();
            FileUtils.checkDirExistAndCreate(constructCachePath(SUBTITLE_DOWNLOAD_ROOT, movieId));
            String downloadPath = constructCachePath(SUBTITLE_DOWNLOAD_ROOT, movieId, fileName);
            FileOutputStream fos = new FileOutputStream(downloadPath);
            IOUtils.write(
                    IOUtils.readFully(contentAsStream, (int) page.getWebResponse().getContentLength()),
                    fos);
            fos.close();
            return new AssembledData.Builder()
                    .put("name", movie.getTitle())
                    .put("downloadedPath", downloadPath).build();
        } catch (Exception e) {
            logServiceError(e, format("downloadSubtitle failed! movieID[{}], downloadPageRoute[{}]", movieId, downloadPageRoute));
        }
        return new AssembledData.Builder().build();
    }

    @Override
    public AssembledData downloadBeautifiedSubtitle(long id, String downloadPageRoute) {
        Movie movie = movieMapper.selectMovieById(id);
        if (ObjectUtils.isEmpty(movie)) {
            return new AssembledData.Builder().build();
        }
        return downloadBeautifiedSubtitle(movie, downloadPageRoute);
    }

    @Override
    public AssembledData downloadBeautifiedSubtitle(Movie movie, String downloadPageRoute) {
        String downloadedPath = downloadSubtitle(movie, downloadPageRoute).getString("downloadedPath");
        try {
            AssembledData destData = regularSubForMovie(movie);
            return destData;
        } catch (Exception e) {
            clearSub(movie.getId());
            logServiceError(e, StrUtil.format("download automatically failed! downloaded file detail: {}", FileNameUtil.getName(downloadedPath)));
            return AssembledData.empty();
        }
    }

    @Override
    public void clearSub(Long movieId) {
        FileUtils.delQuietly(constructCachePath(SUBTITLE_DOWNLOAD_ROOT, movieId.toString()));
        FileUtils.delQuietly(constructCachePath(SUBTITLE_EXTRACT_ROOT, movieId.toString()));
        FileUtils.delQuietly(constructCachePath(SUBTITLE_ROOT, movieId.toString()));
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

    @Override
    public AssembledData regularSubForMovie(Movie movie) {
        if (extractSub(movie)) {
            moveExtractedSub(movie);
            cleanupCompressFile(movie);
        }
        renameSub(movie);
        String destPath = moveToSubRoot(movie);
        return new AssembledData.Builder()
                .put("movieName", movie.getTitle())
                .put("destSubPath", destPath)
                .build();
    }

    /**
     * 复制字幕到输出目录
     *
     * @param movie
     * @return
     */
    private String moveToSubRoot(Movie movie) {
        File downloadSubFolder = new File(constructCachePath(SUBTITLE_DOWNLOAD_ROOT, movie.getId().toString()));
        if (!downloadSubFolder.exists() || downloadSubFolder.list().length < 1) {
            clearSub(movie.getId());
            throw new ServiceException("no suitable sub for automatically download");
        }
        File subFolder = new File(constructCachePath(SUBTITLE_ROOT, movie.getId().toString()));
        FileUtil.moveContent(downloadSubFolder, subFolder, true);
        return subFolder.getAbsolutePath();
    }

    /**
     * 删除压缩文件
     *
     * @param movie
     */
    private void cleanupCompressFile(Movie movie) {
        File downloadSubFolder = new File(constructCachePath(SUBTITLE_DOWNLOAD_ROOT, movie.getId().toString()));
        for (File file : downloadSubFolder.listFiles()) {
            String fileSuffix = FileNameUtil.getSuffix(file);
            if ("rar".equalsIgnoreCase(fileSuffix) || "zip".equalsIgnoreCase(fileSuffix) || "7z".equalsIgnoreCase(fileSuffix)) {
                FileUtil.del(file);
            }
        }
        File extractedSubFolder = new File(constructCachePath(SUBTITLE_EXTRACT_ROOT, movie.getId().toString()));
        FileUtil.del(extractedSubFolder);
    }

    /**
     * 复制符合条件的字幕到目标目录
     *
     * @param movie
     */
    private void moveExtractedSub(Movie movie) {
        File extractedSubFolder = new File(constructCachePath(SUBTITLE_EXTRACT_ROOT, movie.getId().toString()));
        checkDirExistAndCreate(extractedSubFolder);
        // 获取解压文件夹中包含字幕的子目录
        File subFolder = recursionSearchSub(extractedSubFolder);
        if (ObjectUtils.isEmpty(subFolder)) {
            String extractedSubFolderDetail = Arrays.toString(extractedSubFolder.list());
            FileUtil.del(constructCachePath(SUBTITLE_DOWNLOAD_ROOT, movie.getId().toString()));
            FileUtil.del(extractedSubFolder);
            throw new ServiceException(StrUtil.format("no suitable sub found, compress dir detail: {}", extractedSubFolderDetail));
        }
        // 过滤目录文件夹中非srt、ass类型字幕的文件
        List<File> filteredSubList = Arrays.stream(subFolder.listFiles()).filter(f -> {
            String suffix = FileNameUtil.getSuffix(f);
            boolean isSubType = !StringUtils.isEmpty(suffix) &&
                    (suffix.equalsIgnoreCase("srt") ||
                            suffix.equalsIgnoreCase("ass"));
            if (!isSubType) {
                FileUtil.del(f);
            }
            return isSubType;
        }).collect(Collectors.toList());
        // 移动字幕文件到 download 目录
        for (File listFile : filteredSubList) {
            String fileName = listFile.getName();
            if (isChsSub(fileName) || isChsAndEngSub(fileName) || subFolder.list().length == 1 ||
                    (filteredSubList.size() == 2 && filteredSubList.stream().filter(f -> {
                        String suffix = FileNameUtil.getSuffix(f);
                        return !StringUtils.isEmpty(suffix) && suffix.equalsIgnoreCase("srt");
                    }).collect(Collectors.toList()).size() == 1)) {
                String destDir = constructCachePath(SUBTITLE_DOWNLOAD_ROOT, movie.getId().toString());
                String srcFile = listFile.getAbsolutePath();
                FileUtil.copy(srcFile, destDir, true);
            }
        }
    }

    /**
     * 解压文件到缓存目录
     *
     * @param movie
     * @return false代表无需解压
     */
    private boolean extractSub(Movie movie) {
        File downloadSubFolder = new File(constructCachePath(SUBTITLE_DOWNLOAD_ROOT, movie.getId().toString()));
        List<String> compressionList = Arrays.stream(downloadSubFolder.list())
                .filter(e -> e.contains(".rar") || e.contains(".zip") || e.contains(".7z")).collect(Collectors.toList());

        if (compressionList.size() != 1) {
            if (compressionList.size() > 1) {
                log.warn("compressionList irregular in movieId({}), compressionList={}", movie.getId(), compressionList);
            }
            return false;
        }

        File compressFile = downloadSubFolder.listFiles()[0];
        String compressFileName = compressFile.getAbsolutePath();
        File extractDir = new File(constructCachePath(SUBTITLE_EXTRACT_ROOT, movie.getId().toString(), ""));
        if (checkDirExistAndCreate(extractDir)) {
            if (extractDir.list().length > 0) {
                FileUtil.del(extractDir);
                checkDirExistAndCreate(extractDir);
            }
        }

        if (compressFileName.contains(".rar")) {
            try {
                Junrar.extract(compressFile, extractDir);
            } catch (Exception e) {
                logServiceError(e, StrUtil.format("解压rar({})失败！", compressFile.getName()));
            }
        } else {
            Extractor extractor = CompressUtil.createExtractor(
                    CharsetUtil.defaultCharset(),
                    FileUtil.file(compressFileName));

            extractor.extract(FileUtil.file(extractDir));
        }
        return true;
    }

    /**
     * 字幕重命名，与电影文件匹配
     *
     * @param movie
     */
    private void renameSub(Movie movie) {
        File subfolder = new File(constructCachePath(SUBTITLE_DOWNLOAD_ROOT, movie.getId().toString()));
        if (subfolder.list().length == 0) {
            FileUtil.del(subfolder);
            return;
        }

        for (File sub : subfolder.listFiles()) {
            String subName = sub.getName();
            String movieFileName = FileNameUtil.getName(movie.getAddress());
            String newName = movieFileName.substring(0, movieFileName.lastIndexOf("."));
            if (isChsAndEngSub(subName)) {
                newName += ".chs&eng";
            } else if (isChsSub(subName)) {
                newName += ".chs";
            }
            try {
                FileUtil.rename(sub, newName, true, false);
            } catch (Exception e) {
                logError(e, format("sub={}, newName={}", sub, newName));
            }
        }
    }

    /**
     * 字幕路径按照电影路径重构，然后可以直接复制到NAS
     *
     * @param movie
     */
    public void renamePathForSub(Movie movie) {
        File subfolder = new File(constructCachePath(SUBTITLE_ROOT, movie.getId().toString()));
        String movieDirPath = movie.getAddress().substring(0, movie.getAddress().lastIndexOf("\\"));
        FileUtil.rename(subfolder, movieDirPath, true, false);
    }

    /**
     * 递归搜索目录下的字幕
     *
     * @param file 返回包含字幕的子目录对象
     * @return
     */
    private File recursionSearchSub(File file) {
        if (file.isFile()) return null;
        if (Arrays.stream(file.list()).filter(s -> s.contains(".ass") || s.contains(".srt")).collect(Collectors.toList()).size() > 0) {
            return file;
        }
        for (File listFile : file.listFiles()) {
            File tmp = recursionSearchSub(listFile);
            if (ObjectUtils.isEmpty(tmp)) continue;
            return tmp;
        }
        return null;
    }

    private boolean isChsAndEngSub(String subName) {
        if (ObjectUtil.isEmpty(chsAndEngSet)) {
            setFilterSet();
        }
        return chsAndEngSet.stream().filter(f -> subName.toLowerCase().contains(f)).collect(Collectors.toList()).size() > 0;
    }

    private boolean isChsSub(String subName) {
        if (ObjectUtil.isEmpty(chsSet)) {
            setFilterSet();
        }
        return chsSet.stream().filter(f -> subName.toLowerCase().contains(f)).collect(Collectors.toList()).size() > 0;
    }

    private void setFilterSet() {
        String value = configService.selectConfigByKey(BizConst.SUB_FILTER_CONFIG_KEY);
        AssembledData filter = new AssembledData.Builder().putAll(value).build();
        chsSet = filter.getJSONArray("chs").stream().map(o -> o.toString()).collect(Collectors.toSet());
        chsAndEngSet = filter.getJSONArray("chsAndEng").stream().map(o -> o.toString()).collect(Collectors.toSet());
    }
}
