package com.sleepy.manager.main.helper;

import com.alibaba.fastjson.JSON;
import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.common.utils.DateUtils;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.XmlUtils;
import com.sleepy.manager.system.domain.Movie;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Captain1920
 * @classname MovieHelper
 * @description TODO
 * @date 2022/4/29 20:36
 */
@Slf4j
public class MovieHelper {

    private final static Set<String> VIDEO_FORMAT = Sets.newHashSet("mkv", "mp4", "m2ts", "avi", "MKV", "m4v", "wmv", "rmvb", "flv", "mpg");
    private final static List<String> NAS_MOVIE_PATH = Arrays.asList("/0-Cinema0/Cinema1-Extra", "/0-Cinema1");

    public static AssembledData readNfo(String path) throws IOException, DocumentException {
        String movieXmlInfo = FileUtils.readFileToString(new File(path), Charset.defaultCharset());
        String jsonStr = XmlUtils.xml2Json(movieXmlInfo);
        return new AssembledData.Builder().putAll(JSON.parseObject(jsonStr)).build();
    }

    public static AssembledData searchBestMatch(List<AssembledData> searchList, String originalMovieFileName) {
        int bestMatchIndex = 0;
        int lastBestRatio = 0;
        for (int i = 0; i < searchList.size(); i++) {
            AssembledData data = searchList.get(i);
            String subTitle = data.getString("title");
            int ratio = FuzzySearch.partialRatio(originalMovieFileName, subTitle);
            if (ratio > lastBestRatio) {
                lastBestRatio = ratio;
                bestMatchIndex = i;
            }
        }
        return searchList.get(bestMatchIndex);
    }

    public static List<Movie> loadNasMovie() throws DocumentException, IOException {
        Set<String> moviePathList = findAllMoviePath();
        List<Movie> movieList = new ArrayList<>();

        for (String movieDir : moviePathList) {
            File dir = new File(movieDir);
            Movie movie = new Movie();
            String movieFileName = Arrays.stream(dir.list()).filter(n -> MovieHelper.VIDEO_FORMAT.contains(FilenameUtils.getExtension(n))).collect(Collectors.toList()).get(0);
            String movieFilePrefix = movieFileName.substring(0, movieFileName.lastIndexOf("."));
            for (String name : dir.list()) {
                String absPath = movieDir + File.separator + name;
                if ("nfo".equals(FilenameUtils.getExtension(name)) && name.equals(movieFilePrefix + ".nfo")) {
                    AssembledData nfoData = MovieHelper.readNfo(absPath);
                    movie.setTitle(nfoData.getString("title"));
                    movie.setYear(nfoData.getLong("year"));
                    movie.setIntro(nfoData.getString("outline"));
                    movie.setImdbid(nfoData.getString("id"));
                    movie.setCreatedAt(DateUtils.parseDate(nfoData.getString("dateadded")));
                    movie.setDetail(nfoData.toJSONString());
                }
                if (MovieHelper.VIDEO_FORMAT.contains(FilenameUtils.getExtension(name))) {
                    movie.setAddress(absPath);
                }
                if (name.contains("poster")) {
                    movie.setCover(absPath);
                }
                if (name.contains("fanart")) {
                    movie.setHeadCover(absPath);
                }
            }
            movieList.add(movie);
        }
        return movieList;
    }


    private static Set<String> findAllMoviePath() {
        Set<String> moviePathList = new HashSet<>();
        for (String path : NAS_MOVIE_PATH) {
            File file = new File(path);
            findMovieRecursion(moviePathList, file);
        }
        return moviePathList;
    }

    private static void findMovieRecursion(Set<String> moviePathList, File file) {
        if (file.isFile()) {
            if (VIDEO_FORMAT.contains(FilenameUtils.getExtension(file.getName()))) {
                moviePathList.add(file.getParent());
            }
            return;
        }
        for (File f : file.listFiles()) {
            findMovieRecursion(moviePathList, f);
        }
    }

    public static AssembledData genMovieTag(Movie movie) {
        AssembledData detail = new AssembledData.Builder().putAll(movie.getDetail()).build();
        AssembledData.Builder dataBuilder = new AssembledData.Builder();
        dataBuilder.put("rating", "N/A");
        dataBuilder.put("ratingNum", "N/A");
        try {
            int movieScreenWidth = detail.getJSONObject("fileinfo").getJSONObject("streamdetails").getJSONObject("video").getInteger("width");
            if (movieScreenWidth == 0) {
                dataBuilder.put("ratio", "N/A");
            } else if (movieScreenWidth < 900) {
                dataBuilder.put("ratio", "720P");
            } else if (movieScreenWidth <= 1920) {
                dataBuilder.put("ratio", "1080P");
            } else {
                dataBuilder.put("ratio", "2160P");
            }
            List<Object> ratings = detail.getJSONObject("ratings").getJSONArray("rating")
                    .stream()
                    .filter(m -> "imdb".equals(JSON.parseObject(m.toString()).getString("@name"))).collect(Collectors.toList());
            if (ratings.size() > 0) {
                AssembledData rating = new AssembledData.Builder().putAll(ratings.get(0)).build();
                dataBuilder.put("rating", StringUtils.getValFormat(rating.getString("value"), 1));
                dataBuilder.put("ratingNum", rating.getInteger("votes"));
            }
        } catch (Exception e) {
            log.warn("解析电影详情内容出错[{}]", e.getMessage());
        }
        return dataBuilder.put("detail", detail).build();
    }
}
