package com.sleepy.manager.common.utils;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.sleepy.manager.common.constant.HttpStatus;
import com.sleepy.manager.common.exception.ServiceException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ReUtil.findAll;
import static cn.hutool.core.util.ReUtil.isMatch;
import static cn.hutool.json.JSONUtil.toJsonStr;
import static cn.hutool.log.StaticLog.error;
import static com.sleepy.manager.common.utils.ExceptionUtil.getRootErrorMessage;
import static com.sleepy.manager.common.utils.StringUtils.getOrDefault;

/**
 * 处理并记录日志文件
 *
 * @author
 */
public class LogUtils {
    public static String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg + "]";
    }

    public static void logError(Exception exception, String message) {
        Dict msg = Dict.create()
                .set("msg", getOrDefault(message, ""))
                .set("exception", exception.getClass().getName())
                .set("exceptionMsg", getRootErrorMessage(exception));

        List<String> selfServiceStack = Arrays.stream(exception.getStackTrace())
                .filter(e -> isMatch("com.sleepy.manager.*", e.toString()))
                .map(e -> e.toString())
                .collect(Collectors.toList());
        if (selfServiceStack.size() > 0) {
            List<String> serviceStack = selfServiceStack.stream()
                    .map(e -> findAll("[A-Za-z]+\\(\\S+\\)", e, 0))
                    .filter(e -> ObjectUtil.isNotEmpty(e))
                    .map(es -> es.get(0))
                    .collect(Collectors.toList());
            Collections.reverse(serviceStack);
            msg.set("serviceStack", serviceStack);
        }
        error(toJsonStr(msg));
    }

    public static void logError(Exception exception) {
        logError(exception, null);
    }

    public static void logServiceError(Exception exception, String message) {
        logError(exception, message);
        throw new ServiceException(getOrDefault(message, getRootErrorMessage(exception)), HttpStatus.NO_NEED_LOG);
    }

    public static void logServiceError(Exception exception) {
        logServiceError(exception, null);
    }
}
