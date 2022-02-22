package com.sleepy.manager.generation.mapper;

import com.sleepy.manager.generation.domain.Classification;

import java.util.List;

/**
 * 分类Mapper接口
 *
 * @author SleepyOcean
 * @date 2021-11-26
 */
public interface ClassificationMapper {
    /**
     * 查询分类
     *
     * @param id 分类主键
     * @return 分类
     */
    Classification selectClassificationById(Long id);

    /**
     * 查询分类列表
     *
     * @param classification 分类
     * @return 分类集合
     */
    List<Classification> selectClassificationList(Classification classification);

    /**
     * 新增分类
     *
     * @param classification 分类
     * @return 结果
     */
    int insertClassification(Classification classification);

    /**
     * 修改分类
     *
     * @param classification 分类
     * @return 结果
     */
    int updateClassification(Classification classification);

    /**
     * 删除分类
     *
     * @param id 分类主键
     * @return 结果
     */
    int deleteClassificationById(Long id);

    /**
     * 批量删除分类
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteClassificationByIds(String[] ids);
}
