package com.sleepy.manager.main.helper;

import com.alibaba.fastjson.JSON;
import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.common.utils.DateUtils;
import com.sleepy.manager.common.utils.XmlUtils;
import com.sleepy.manager.system.domain.Movie;
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
            int ratio = FuzzySearch.ratio(originalMovieFileName, subTitle);
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
}
