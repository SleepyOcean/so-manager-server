package com.sleepy.manager.common.utils;

/**
 * @author captain1920
 * @classname CommonUtils
 * @description TODO
 * @date 2022/5/1 20:12
 */
public class CommonUtils {

    /**
     * 生成 [start, end) 区间到随机数
     *
     * @param start
     * @param end
     * @return
     */
    public static long generateRandomNum(long start, long end) {
        long number = (long) (Math.random() * (end - start) + start);
        return number;
    }
}
