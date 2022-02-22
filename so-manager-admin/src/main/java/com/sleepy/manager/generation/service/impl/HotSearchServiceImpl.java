package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.HotSearch;
import com.sleepy.manager.generation.mapper.HotSearchMapper;
import com.sleepy.manager.generation.service.IHotSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 热搜Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-12-18
 */
@Service
public class HotSearchServiceImpl implements IHotSearchService {
    @Autowired
    private HotSearchMapper hotSearchMapper;

    /**
     * 查询热搜
     *
     * @param id 热搜主键
     * @return 热搜
     */
    @Override
    public HotSearch selectHotSearchById(Long id) {
        return hotSearchMapper.selectHotSearchById(id);
    }

    /**
     * 查询热搜列表
     *
     * @param hotSearch 热搜
     * @return 热搜
     */
    @Override
    public List<HotSearch> selectHotSearchList(HotSearch hotSearch) {
        return hotSearchMapper.selectHotSearchList(hotSearch);
    }

    /**
     * 新增热搜
     *
     * @param hotSearch 热搜
     * @return 结果
     */
    @Override
    public int insertHotSearch(HotSearch hotSearch) {
        return hotSearchMapper.insertHotSearch(hotSearch);
    }

    /**
     * 修改热搜
     *
     * @param hotSearch 热搜
     * @return 结果
     */
    @Override
    public int updateHotSearch(HotSearch hotSearch) {
        return hotSearchMapper.updateHotSearch(hotSearch);
    }

    /**
     * 批量删除热搜
     *
     * @param ids 需要删除的热搜主键
     * @return 结果
     */
    @Override
    public int deleteHotSearchByIds(String ids) {
        return hotSearchMapper.deleteHotSearchByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除热搜信息
     *
     * @param id 热搜主键
     * @return 结果
     */
    @Override
    public int deleteHotSearchById(Long id) {
        return hotSearchMapper.deleteHotSearchById(id);
    }
}
