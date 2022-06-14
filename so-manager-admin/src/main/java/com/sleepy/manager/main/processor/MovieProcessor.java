package com.sleepy.manager.main.processor;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sleepy.manager.common.utils.DateUtils;
import com.sleepy.manager.common.utils.LogUtils;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.XmlUtils;
import com.sleepy.manager.main.common.AssembledData;
import com.sleepy.manager.system.domain.Movie;
import com.sleepy.manager.system.mapper.MovieMapper;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static com.sleepy.manager.common.constant.ConfigConstants.*;
import static com.sleepy.manager.common.utils.file.FileUtils.*;

/**
 * @author Captain1920
 * @classname MovieHelper
 * @description TODO
 * @date 2022/4/29 20:36
 */
@Slf4j
@Component
public class MovieProcessor {
    private final static Set<String> VIDEO_FORMAT = Sets.newHashSet("mkv", "mp4", "m2ts", "avi", "MKV", "m4v", "wmv", "rmvb", "flv", "mpg");
    private final static List<String> NAS_MOVIE_PATH = Arrays.asList("/0-Cinema0/Cinema1-Extra", "/0-Cinema1");
    @Autowired
    MovieMapper movieMapper;
    @Value("${so-manager-server.movieRoot}")
    private String movieStorageRoot;
    private TimedCache<Integer, AssembledData> movieTagCache;

    private static void findMovieFileRecursion(Set<String> moviePathList, File file) {
        if (file.isFile()) {
            if (VIDEO_FORMAT.contains(FilenameUtils.getExtension(file.getName()))) {
                moviePathList.add(file.getAbsolutePath());
            }
            return;
        }
        for (File f : file.listFiles()) {
            findMovieFileRecursion(moviePathList, f);
        }
    }

    @PostConstruct
    private void init() {
        // TODO movie tag cache module
        movieTagCache = CacheUtil.newTimedCache(TIME_CACHE_TIMEOUT_MS);
        movieTagCache.schedulePrune(TIME_CACHE_TIMEOUT_MS);
    }

    public String coverCache(long movieId) {
        return constructCachePath(MOVIE_IMG_CACHE_PATH, String.valueOf(movieId), "poster.jpg");
    }

    public String fanartCache(long movieId) {
        return constructCachePath(MOVIE_IMG_CACHE_PATH, String.valueOf(movieId), "fanart.jpg");
    }

    public void clearAllCacheNasImg() {
        FileUtil.del(constructCachePath(MOVIE_IMG_CACHE_PATH));
    }

    public void cacheAllNasMovieImg() {
        List<Movie> movies = movieMapper.selectMovieList(new Movie());
        for (Movie movie : movies) {
            cacheNasMovieImg(movie);
        }
    }

    public void cacheNasMovieImg(Movie movie) {
        File dir = new File(constructPath(movieStorageRoot, movie.getAddress())).getParentFile();
        List<File> images = Arrays.stream(dir.listFiles())
                .filter(f -> f.getName().contains("poster") || f.getName().contains("fanart"))
                .collect(Collectors.toList());
        for (File image : images) {
            String imageName = image.getName();
            String cacheDir = constructCachePath(MOVIE_IMG_CACHE_PATH, movie.getId().toString());
            checkDirExistAndCreate(cacheDir);
            if (imageName.contains("poster")) {
                FileUtil.copy(image, new File(constructPath(cacheDir, "poster.jpg")), true);
            } else {
                FileUtil.copy(image, new File(constructPath(cacheDir, "fanart.jpg")), true);
            }
        }
    }

    public AssembledData readNfo(String path) throws IOException, DocumentException {
        String movieXmlInfo = FileUtils.readFileToString(new File(path), Charset.defaultCharset());
        String jsonStr = XmlUtils.xml2Json(movieXmlInfo);
        return new AssembledData.Builder().putAll(JSON.parseObject(jsonStr)).build();
    }

    public int searchBestMatch(List<AssembledData> searchList, String originalMovieFileName) {
        int bestMatchIndex = 0;
        int maxDownloadCountIndex = 0;
        int lastMaxDownloadCount = 0;
        int lastBestRatio = 0;
        List<AssembledData> mulLangSubList = searchList.stream()
                .filter(s -> s.getJSONArray("langList").stream().filter(e -> "双语".equals(e)).collect(Collectors.toList()).size() > 0)
                .collect(Collectors.toList());
        List<AssembledData> simpleChineseSubList = searchList.stream()
                .filter(s -> s.getJSONArray("langList").stream().filter(e -> e.toString().contains("简体")).collect(Collectors.toList()).size() > 0)
                .collect(Collectors.toList());
        if (mulLangSubList.size() > 0) {
            searchList = mulLangSubList;
        } else if (simpleChineseSubList.size() > 0) {
            searchList = simpleChineseSubList;
        } else {
            return -1;
        }
        for (int i = 0; i < searchList.size(); i++) {
            AssembledData data = searchList.get(i);
            String downloadCount = data.getString("downloadCount");
            if (StringUtils.isEmpty(downloadCount)) continue;
            int downloadCountVal = 0;
            if (downloadCount.contains("万")) {
                downloadCountVal = (int) (Float.valueOf(downloadCount.substring(0, downloadCount.length() - 1)) * 10000F);
            } else {
                downloadCountVal = Integer.valueOf(downloadCount);
            }
            if (downloadCountVal > lastMaxDownloadCount) {
                lastMaxDownloadCount = downloadCountVal;
                maxDownloadCountIndex = i;
            }
            String subTitle = data.getString("title");

            int ratio = FuzzySearch.partialRatio(originalMovieFileName, subTitle);
            if (ratio > lastBestRatio) {
                lastBestRatio = ratio;
                bestMatchIndex = i;
            }
        }
        if (lastBestRatio < 90) {
            return maxDownloadCountIndex;
        }
        return bestMatchIndex;
    }

    public List<Movie> loadNasMovie() throws DocumentException, IOException {
        Set<String> moviePathList = findMovieFileParentPath(getNasMoviePathList());
        List<Movie> movieList = new ArrayList<>();

        for (String movieDir : moviePathList) {
            File dir = new File(movieDir);
            Movie movie = new Movie();
            String movieFileName = Arrays.stream(dir.list()).filter(n -> MovieProcessor.VIDEO_FORMAT.contains(FilenameUtils.getExtension(n))).collect(Collectors.toList()).get(0);
            String movieFilePrefix = movieFileName.substring(0, movieFileName.lastIndexOf("."));
            for (String name : dir.list()) {
                String absPath = movieDir + File.separator + name;
                if ("nfo".equals(FilenameUtils.getExtension(name)) && name.equals(movieFilePrefix + ".nfo")) {
                    AssembledData nfoData = readNfo(absPath);
                    movie.setTitle(nfoData.getString("title"));
                    movie.setYear(nfoData.getLong("year"));
                    movie.setIntro(nfoData.getString("outline"));
                    movie.setImdbid(nfoData.getString("id"));
                    movie.setCreatedAt(DateUtils.parseDate(nfoData.getString("dateadded")));
                    movie.setDetail(nfoData.toJSONString());
                }
                String relativePath = absPath.replaceFirst(NAS_ROOT_ON_WINDOWS, "");
                if (MovieProcessor.VIDEO_FORMAT.contains(FilenameUtils.getExtension(name))) {
                    movie.setAddress(relativePath);
                }
                if (name.contains("poster")) {
                    movie.setCover(relativePath);
                }
                if (name.contains("fanart")) {
                    movie.setHeadCover(relativePath);
                }
            }
            movieList.add(movie);
        }
        return movieList;
    }

    public Set<String> findMovieFileParentPath(List<String> searchPathList) {
        Set<String> moviePathList = findMovieFilePath(searchPathList).stream()
                .map(f -> new File(f).getParent()).collect(Collectors.toSet());
        return moviePathList;
    }

    public Set<String> findMovieFilePath(List<String> searchPathList) {
        Set<String> moviePathList = new HashSet<>();
        for (String path : searchPathList) {
            File file = new File(path);
            findMovieFileRecursion(moviePathList, file);
        }
        return moviePathList;
    }

    public AssembledData genMovieTag(Movie movie) {
        AssembledData detail = new AssembledData.Builder().putAll(movie.getDetail()).build();
        AssembledData.Builder dataBuilder = new AssembledData.Builder();
        try {
            JSONObject fileInfo = detail.getJSONObject("fileinfo").getJSONObject("streamdetails");
            // 视频信息 - 分辨率
            int movieScreenWidth = fileInfo.getJSONObject("video").getInteger("width");
            if (movieScreenWidth == 0) {
                dataBuilder.put("ratio", "N/A");
            } else if (movieScreenWidth < 900) {
                dataBuilder.put("ratio", "720P");
            } else if (movieScreenWidth <= 1920) {
                dataBuilder.put("ratio", "1080P");
            } else {
                dataBuilder.put("ratio", "2160P");
            }
            // 视频信息 - 编码
            String videoCodec = fileInfo.getJSONObject("video").getString("codec");
            dataBuilder.put("videoCodec", videoCodec.toUpperCase());

            // 视频信息 - 宽幅比
            String aspect = fileInfo.getJSONObject("video").getString("aspect");
            dataBuilder.put("aspect", aspect);

            // 音频信息 - 音效
            if (fileInfo.get("audio") != null) {
                if (fileInfo.get("audio").getClass().getSimpleName().equals("JSONArray")) {
                    JSONArray array = fileInfo.getJSONArray("audio");
                    List<AssembledData> audios = new ArrayList<>();
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject audio = array.getJSONObject(i);
                        String audioCodec = audio.getString("codec");
                        int audioChannels = audio.getInteger("channels");
                        audios.add(new AssembledData.Builder()
                                .put("codec", audioCodec.toUpperCase())
                                .put("channels", audioChannels)
                                .build());
                    }
                    dataBuilder.put("audios", audios);
                } else {
                    JSONObject audio = fileInfo.getJSONObject("audio");
                    String audioCodec = audio.getString("codec");
                    int audioChannels = audio.getInteger("channels");
                    dataBuilder.put("audios", Arrays.asList(new AssembledData.Builder()
                            .put("codec", audioCodec.toUpperCase())
                            .put("channels", audioChannels)
                            .build()));
                }
            }

            // 评分
            dataBuilder.put("rating", "N/A");
            dataBuilder.put("ratingNum", "N/A");
            if (detail.getJSONObject("ratings").get("rating").getClass().getSimpleName().equals("JSONArray")) {
                List<Object> ratings = detail.getJSONObject("ratings").getJSONArray("rating")
                        .stream()
                        .filter(m -> "imdb".equals(JSON.parseObject(m.toString()).getString("@name"))).collect(Collectors.toList());
                if (ratings.size() > 0) {
                    AssembledData rating = new AssembledData.Builder().putAll(ratings.get(0)).build();
                    dataBuilder.put("rating", StringUtils.getValFormat(rating.getString("value"), 1));
                    dataBuilder.put("ratingNum", rating.getInteger("votes"));
                }
            } else {
                AssembledData rating = new AssembledData.Builder().putAll(detail.getJSONObject("ratings").getJSONObject("rating")).build();
                dataBuilder.put("rating", StringUtils.getValFormat(rating.getString("value"), 1));
                dataBuilder.put("ratingNum", rating.getInteger("votes"));
            }
        } catch (Exception e) {
            LogUtils.logError(e, "解析电影详情内容出错 movieID=" + movie.getId());
        }
        return dataBuilder.put("detail", detail).build();
    }

    private List<String> getNasMoviePathList() {
        return NAS_MOVIE_PATH.stream().map(p -> constructPath(movieStorageRoot, p)).collect(Collectors.toList());
    }
}
