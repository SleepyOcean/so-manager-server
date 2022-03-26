package com.sleepy.manager.main.service;

import com.sleepy.manager.blog.common.UnionResponse;
import com.sleepy.manager.common.core.domain.entity.SysUser;

public interface MainManagerService {
    UnionResponse getFundsStrategyReport(Long strategyId);

    UnionResponse getUserRoutes(SysUser user);
}
