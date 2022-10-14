package com.sleepy.manager.main.service;

import com.sleepy.manager.common.core.domain.entity.SysUser;
import com.sleepy.manager.main.common.UnionResponse;

public interface MainManagerService {
    UnionResponse getFundsStrategyReport(Long strategyId);

    UnionResponse getUserRoutes(SysUser user);

    UnionResponse getWebPageBaseInfo(String url);

    UnionResponse syncNasMovieBase();
}
