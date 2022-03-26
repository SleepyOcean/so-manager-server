package com.sleepy.manager.main.controller;

import com.sleepy.manager.blog.common.UnionResponse;
import com.sleepy.manager.common.core.domain.entity.SysUser;
import com.sleepy.manager.main.service.MainManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
public class MainManagerController {

    @Autowired
    MainManagerService mainManagerService;

    // funds 基金模块
    @GetMapping("/funds/{strategyId}")
    public UnionResponse getFundsStrategyReport(@PathVariable("strategyId") Long strategyId) {
        return mainManagerService.getFundsStrategyReport(strategyId);
    }

    // config 配置模块
    @PostMapping("/getUserRoutes")
    public UnionResponse getUserRoutes(@RequestBody SysUser user) {
        return mainManagerService.getUserRoutes(user);
    }

}
