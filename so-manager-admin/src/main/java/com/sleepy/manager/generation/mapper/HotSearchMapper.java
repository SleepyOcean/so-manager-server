package com.sleepy.manager.generation.mapper;

import com.sleepy.manager.generation.domain.HotSearch;

import java.util.List;

/**
 * 热搜Mapper接口
 *
 * @author SleepyOcean
 * @date 2021-12-18
 */
public interface HotSearchMapper {
    /**
     * 查询热搜
     *
     * @param id 热搜主键
     * @return 热搜
     */
    HotSearch selectHotSearchById(Long id);

    /**
     * 查询热搜列表
     *
     * @param hotSearch 热搜
     * @return 热搜集合
     */
    List<HotSearch> selectHotSearchList(HotSearch hotSearch);

    /**
     * 新增热搜
     *
     * @param hotSearch 热搜
     * @return 结果
     */
    int insertHotSearch(HotSearch hotSearch);

    /**
     * 修改热搜
     *
     * @param hotSearch 热搜
     * @return 结果
     */
    int updateHotSearch(HotSearch hotSearch);

    /**
     * 删除热搜
     *
     * @param id 热搜主键
     * @return 结果
     */
    int deleteHotSearchById(Long id);

    /**
     * 批量删除热搜
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteHotSearchByIds(String[] ids);
}
