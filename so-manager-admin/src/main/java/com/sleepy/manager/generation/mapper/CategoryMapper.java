package com.sleepy.manager.generation.mapper;

import com.sleepy.manager.generation.domain.Category;

import java.util.List;

/**
 * 栏目Mapper接口
 *
 * @author SleepyOcean
 * @date 2021-09-29
 */
public interface CategoryMapper {
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
     * 删除栏目
     *
     * @param id 栏目主键
     * @return 结果
     */
    int deleteCategoryById(Long id);

    /**
     * 批量删除栏目
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteCategoryByIds(String[] ids);

    /**
     * 移除tag或分类
     *
     * @param category
     * @return
     */
    int removeTagOrClassification(Category category);
}
