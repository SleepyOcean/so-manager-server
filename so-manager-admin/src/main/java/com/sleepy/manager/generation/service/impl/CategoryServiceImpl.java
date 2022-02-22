package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.Category;
import com.sleepy.manager.generation.mapper.CategoryMapper;
import com.sleepy.manager.generation.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 栏目Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-09-29
 */
@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询栏目
     *
     * @param id 栏目主键
     * @return 栏目
     */
    @Override
    public Category selectCategoryById(Long id) {
        return categoryMapper.selectCategoryById(id);
    }

    /**
     * 查询栏目列表
     *
     * @param category 栏目
     * @return 栏目
     */
    @Override
    public List<Category> selectCategoryList(Category category) {
        return categoryMapper.selectCategoryList(category);
    }

    /**
     * 新增栏目
     *
     * @param category 栏目
     * @return 结果
     */
    @Override
    public int insertCategory(Category category) {
        return categoryMapper.insertCategory(category);
    }

    /**
     * 修改栏目
     *
     * @param category 栏目
     * @return 结果
     */
    @Override
    public int updateCategory(Category category) {
        return categoryMapper.updateCategory(category);
    }

    /**
     * 批量删除栏目
     *
     * @param ids 需要删除的栏目主键
     * @return 结果
     */
    @Override
    public int deleteCategoryByIds(String ids) {
        return categoryMapper.deleteCategoryByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除栏目信息
     *
     * @param id 栏目主键
     * @return 结果
     */
    @Override
    public int deleteCategoryById(Long id) {
        return categoryMapper.deleteCategoryById(id);
    }

    @Override
    public int removeTagOrClassification(Category category) {
        return categoryMapper.removeTagOrClassification(category);
    }
}
