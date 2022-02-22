package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.SortRel;
import com.sleepy.manager.generation.mapper.SortRelMapper;
import com.sleepy.manager.generation.service.ISortRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-11-26
 */
@Service
public class SortRelServiceImpl implements ISortRelService {
    @Autowired
    private SortRelMapper sortRelMapper;

    /**
     * 查询分类
     *
     * @param id 分类主键
     * @return 分类
     */
    @Override
    public SortRel selectSortRelById(Long id) {
        return sortRelMapper.selectSortRelById(id);
    }

    /**
     * 查询分类列表
     *
     * @param sortRel 分类
     * @return 分类
     */
    @Override
    public List<SortRel> selectSortRelList(SortRel sortRel) {
        return sortRelMapper.selectSortRelList(sortRel);
    }

    /**
     * 新增分类
     *
     * @param sortRel 分类
     * @return 结果
     */
    @Override
    public int insertSortRel(SortRel sortRel) {
        return sortRelMapper.insertSortRel(sortRel);
    }

    /**
     * 修改分类
     *
     * @param sortRel 分类
     * @return 结果
     */
    @Override
    public int updateSortRel(SortRel sortRel) {
        return sortRelMapper.updateSortRel(sortRel);
    }

    /**
     * 批量删除分类
     *
     * @param ids 需要删除的分类主键
     * @return 结果
     */
    @Override
    public int deleteSortRelByIds(String ids) {
        return sortRelMapper.deleteSortRelByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除分类信息
     *
     * @param id 分类主键
     * @return 结果
     */
    @Override
    public int deleteSortRelById(Long id) {
        return sortRelMapper.deleteSortRelById(id);
    }
}
