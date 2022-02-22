package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.Classification;
import com.sleepy.manager.generation.mapper.ClassificationMapper;
import com.sleepy.manager.generation.service.IClassificationService;
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
public class ClassificationServiceImpl implements IClassificationService {
    @Autowired
    private ClassificationMapper classificationMapper;

    /**
     * 查询分类
     *
     * @param id 分类主键
     * @return 分类
     */
    @Override
    public Classification selectClassificationById(Long id) {
        return classificationMapper.selectClassificationById(id);
    }

    /**
     * 查询分类列表
     *
     * @param classification 分类
     * @return 分类
     */
    @Override
    public List<Classification> selectClassificationList(Classification classification) {
        return classificationMapper.selectClassificationList(classification);
    }

    /**
     * 新增分类
     *
     * @param classification 分类
     * @return 结果
     */
    @Override
    public int insertClassification(Classification classification) {
        return classificationMapper.insertClassification(classification);
    }

    /**
     * 修改分类
     *
     * @param classification 分类
     * @return 结果
     */
    @Override
    public int updateClassification(Classification classification) {
        return classificationMapper.updateClassification(classification);
    }

    /**
     * 批量删除分类
     *
     * @param ids 需要删除的分类主键
     * @return 结果
     */
    @Override
    public int deleteClassificationByIds(String ids) {
        return classificationMapper.deleteClassificationByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除分类信息
     *
     * @param id 分类主键
     * @return 结果
     */
    @Override
    public int deleteClassificationById(Long id) {
        return classificationMapper.deleteClassificationById(id);
    }
}
