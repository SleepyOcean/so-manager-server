package com.sleepy.manager.generation.service;

import com.sleepy.manager.generation.domain.Category;

import java.util.List;

/**
 * 栏目Service接口
 *
 * @author SleepyOcean
 * @date 2021-09-29
 */
public interface ICategoryService {
    /**
     * 查询栏目
     *
     * @param id 栏目主键
     * @return 栏目
     */
    Category selectCategoryById(Long id);

    /**
     * 查询栏目列表
     *
     * @param category 栏目
     * @return 栏目集合
     */
    List<Category> selectCategoryList(Category category);

    /**
     * 新增栏目
     *
     * @param category 栏目
     * @return 结果
     */
    int insertCategory(Category category);

    /**
     * 修改栏目
     *
     * @param category 栏目
     * @return 结果
     */
    int updateCategory(Category category);

    /**
     * 批量删除栏目
     *
     * @param ids 需要删除的栏目主键集合
     * @return 结果
     */
    int deleteCategoryByIds(String ids);

    /**
     * 删除栏目信息
     *
     * @param id 栏目主键
     * @return 结果
     */
    int deleteCategoryById(Long id);

    /**
     * 移除tag或分类
     *
     * @param category
     * @return
     */
    int removeTagOrClassification(Category category);
}
