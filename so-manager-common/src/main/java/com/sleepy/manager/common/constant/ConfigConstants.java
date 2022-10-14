package com.sleepy.manager.common.constant;

/**
 * @author Captain1920
 * @classname ConfigConstants
 * @description TODO
 * @date 2022/5/29 17:49
 */
public class ConfigConstants {
    /**
     * 通用模块
     */
    public static final String SERVER_CACHE_ROOT = "E:\\5-Cache\\4-ManagerServerCache";
    public static final String SERVER_DATA_ROOT = "E:\\5-Cache\\5-ManagerServerData";

    /**
     * 缓存过期时间： 12h
     */
    public static final int TIME_CACHE_TIMEOUT_MS = 12 * 60 * 60 * 1000;

    /**
     * 电影模块
     */
    public static final String NAS_ROOT_ON_WINDOWS = "\\\\DS218plus";
    public static final String MOVIE_IMG_CACHE_PATH = "movie/img";
}
