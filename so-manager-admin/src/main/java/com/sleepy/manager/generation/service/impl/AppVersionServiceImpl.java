package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.AppVersion;
import com.sleepy.manager.generation.mapper.AppVersionMapper;
import com.sleepy.manager.generation.service.IAppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * app版本Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-12-18
 */
@Service
public class AppVersionServiceImpl implements IAppVersionService {
    @Autowired
    private AppVersionMapper appVersionMapper;

    /**
     * 查询app版本
     *
     * @param id app版本主键
     * @return app版本
     */
    @Override
    public AppVersion selectAppVersionById(Long id) {
        return appVersionMapper.selectAppVersionById(id);
    }

    /**
     * 查询app版本列表
     *
     * @param appVersion app版本
     * @return app版本
     */
    @Override
    public List<AppVersion> selectAppVersionList(AppVersion appVersion) {
        return appVersionMapper.selectAppVersionList(appVersion);
    }

    /**
     * 新增app版本
     *
     * @param appVersion app版本
     * @return 结果
     */
    @Override
    public int insertAppVersion(AppVersion appVersion) {
        return appVersionMapper.insertAppVersion(appVersion);
    }

    /**
     * 修改app版本
     *
     * @param appVersion app版本
     * @return 结果
     */
    @Override
    public int updateAppVersion(AppVersion appVersion) {
        return appVersionMapper.updateAppVersion(appVersion);
    }

    /**
     * 批量删除app版本
     *
     * @param ids 需要删除的app版本主键
     * @return 结果
     */
    @Override
    public int deleteAppVersionByIds(String ids) {
        return appVersionMapper.deleteAppVersionByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除app版本信息
     *
     * @param id app版本主键
     * @return 结果
     */
    @Override
    public int deleteAppVersionById(Long id) {
        return appVersionMapper.deleteAppVersionById(id);
    }
}
