package com.sleepy.manager.generation.service;

import com.sleepy.manager.generation.domain.SortRel;

import java.util.List;

/**
 * 分类Service接口
 *
 * @author SleepyOcean
 * @date 2021-11-26
 */
public interface ISortRelService {
    /**
     * 查询分类
     *
     * @param id 分类主键
     * @return 分类
     */
    SortRel selectSortRelById(Long id);

    /**
     * 查询分类列表
     *
     * @param sortRel 分类
     * @return 分类集合
     */
    List<SortRel> selectSortRelList(SortRel sortRel);

    /**
     * 新增分类
     *
     * @param sortRel 分类
     * @return 结果
     */
    int insertSortRel(SortRel sortRel);

    /**
     * 修改分类
     *
     * @param sortRel 分类
     * @return 结果
     */
    int updateSortRel(SortRel sortRel);

    /**
     * 批量删除分类
     *
     * @param ids 需要删除的分类主键集合
     * @return 结果
     */
    int deleteSortRelByIds(String ids);

    /**
     * 删除分类信息
     *
     * @param id 分类主键
     * @return 结果
     */
    int deleteSortRelById(Long id);
}
