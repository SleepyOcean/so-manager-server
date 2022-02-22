package com.sleepy.manager.generation.mapper;

import com.sleepy.manager.generation.domain.AppVersion;

import java.util.List;

/**
 * app版本Mapper接口
 *
 * @author SleepyOcean
 * @date 2021-12-18
 */
public interface AppVersionMapper {
    /**
     * 查询app版本
     *
     * @param id app版本主键
     * @return app版本
     */
    AppVersion selectAppVersionById(Long id);

    /**
     * 查询app版本列表
     *
     * @param appVersion app版本
     * @return app版本集合
     */
    List<AppVersion> selectAppVersionList(AppVersion appVersion);

    /**
     * 新增app版本
     *
     * @param appVersion app版本
     * @return 结果
     */
    int insertAppVersion(AppVersion appVersion);

    /**
     * 修改app版本
     *
     * @param appVersion app版本
     * @return 结果
     */
    int updateAppVersion(AppVersion appVersion);

    /**
     * 删除app版本
     *
     * @param id app版本主键
     * @return 结果
     */
    int deleteAppVersionById(Long id);

    /**
     * 批量删除app版本
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteAppVersionByIds(String[] ids);
}
