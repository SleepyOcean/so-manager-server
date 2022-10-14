package com.sleepy.manager.main.service.impl;

import com.alibaba.fastjson.JSON;
import com.sleepy.manager.common.core.domain.entity.SysUser;
import com.sleepy.manager.common.utils.LogUtils;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.generator.util.VelocityInitializer;
import com.sleepy.manager.main.common.AssembledData;
import com.sleepy.manager.main.common.UnionResponse;
import com.sleepy.manager.main.processor.CrawlerProcessor;
import com.sleepy.manager.main.processor.MovieProcessor;
import com.sleepy.manager.main.service.MainManagerService;
import com.sleepy.manager.system.domain.Movie;
import com.sleepy.manager.system.service.ISysConfigService;
import com.sleepy.manager.system.service.impl.MovieServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static com.sleepy.manager.main.common.BizConst.MENU_ROUTE_CONFIG_KEY;


@Service
@Slf4j
public class MainManagerServiceImpl implements MainManagerService {

    @Autowired
    ISysConfigService configService;

    @Autowired
    CrawlerProcessor crawlerProcessor;

    @Autowired
    MovieProcessor movieProcessor;

    @Autowired
    EmailNotificationServiceImpl emailNotificationService;

    @Autowired
    MovieServiceImpl movieService;

    @Override
    public UnionResponse getFundsStrategyReport(Long strategyId) {
        // todo 对接线上基金数据
        List<AssembledData> fundsList = Arrays.asList(
                new AssembledData.Builder()
                        .put("name", "富国基金")
                        .put("totalAmount", "21021.21")
                        .put("totalReturn", "1021.21")
                        .put("totalReturnRate", "5.1")
                        .put("todayChange", "2.5")
                        .put("todayReturn" , "323")
                        .put("recommendation" , "卧倒不动，继续观望")
                        .build(),
                new AssembledData.Builder()
                        .put("name" , "富国基金")
                        .put("totalAmount" , "21021.21")
                        .put("totalReturn" , "1021.21")
                        .put("totalReturnRate" , "5.1")
                        .put("todayChange" , "2.5")
                        .put("todayReturn" , "323")
                        .put("recommendation" , "卧倒不动，继续观望")
                        .build(),
                new AssembledData.Builder()
                        .put("name" , "富国基金")
                        .put("totalAmount" , "21021.21")
                        .put("totalReturn" , "1021.21")
                        .put("totalReturnRate" , "5.1")
                        .put("todayChange" , "2.5")
                        .put("todayReturn" , "323")
                        .put("recommendation" , "卧倒不动，继续观望")
                        .build(),
                new AssembledData.Builder()
                        .put("name" , "富国基金")
                        .put("totalAmount" , "21021.21")
                        .put("totalReturn" , "1021.21")
                        .put("totalReturnRate" , "5.1")
                        .put("todayChange" , "2.5")
                        .put("todayReturn" , "323")
                        .put("recommendation" , "卧倒不动，继续观望")
                        .build()
        );

        VelocityInitializer.initVelocity();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("fundsList" , fundsList);
        Template template = Velocity.getTemplate("template/mail/FundsMailNotification.html.vm");
        StringWriter sw = new StringWriter();

        template.merge(velocityContext, sw);

        emailNotificationService.send("captain1920@foxmail.com" , "基金日报 - 生成于2022-04-27 14:56" , sw.toString());
        return new UnionResponse.Builder().message("get Funds report successfully.").build();
    }

    @Override
    public UnionResponse getUserRoutes(SysUser user) {
        String userRoutesKey = constructUserRoutesKey(user.getUserId());
        String userRoutes = configService.selectConfigByKey(userRoutesKey);
        return new UnionResponse.Builder().status(HttpStatus.OK)
                .data(new AssembledData.Builder()
                        .put("home", "dashboard_analysis")
                        .put("routes", JSON.parseArray(userRoutes))
                        .build())
                .build();
    }

    @Override
    public UnionResponse getWebPageBaseInfo(String url) {
        AssembledData data = crawlerProcessor.analysisWebPageBaseInfo(url);
        if (ObjectUtils.isEmpty(data.get("error"))) {
            return new UnionResponse.Builder()
                    .status(HttpStatus.OK)
                    .data(data)
                    .build();
        }
        return new UnionResponse.Builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .data(data)
                .message(data.get("error").toString())
                .build();
    }

    @Override
    public UnionResponse syncNasMovieBase() {
        try {
            List<Movie> movieList = movieProcessor.loadNasMovie();
            for (Movie movie : movieList) {
                Movie res = null;
                // 判断电影是否已存在，存在则更新，不存在则插入
                if (StringUtils.isNotEmpty(movie.getImdbid())) {
                    res = movieService.selectMovieByImdbId(movie.getImdbid());
                } else {
                    res = movieService.selectMovieByTitle(movie.getTitle());
                }

                if (ObjectUtils.isEmpty(res)) {
                    movieService.insertMovie(movie);
                } else {
                    movie.setId(res.getId());
                    movie.setHeadCover(null);
                    movie.setCover(null);
                    movieService.updateMovie(movie);
                }
            }
        } catch (Exception e) {
            LogUtils.logError(e, "syncNasMovieBase failed!");
        }
        return new UnionResponse.Builder().build();
    }

    private String constructUserRoutesKey(Long userId) {
        return MENU_ROUTE_CONFIG_KEY + ":" + userId;
    }
}
