package com.sleepy.manager.main.service.impl;

import com.alibaba.fastjson.JSON;
import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.blog.common.UnionResponse;
import com.sleepy.manager.common.core.domain.entity.SysUser;
import com.sleepy.manager.main.service.MainManagerService;
import com.sleepy.manager.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.sleepy.manager.blog.common.Constant.USER_ROUTES_KEY_PREFIX;


@Service
public class MainManagerServiceImpl implements MainManagerService {

    @Autowired
    ISysConfigService configService;

    @Override
    public UnionResponse getFundsStrategyReport(Long strategyId) {
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

    private String constructUserRoutesKey(Long userId) {
        return USER_ROUTES_KEY_PREFIX + userId;
    }
}
