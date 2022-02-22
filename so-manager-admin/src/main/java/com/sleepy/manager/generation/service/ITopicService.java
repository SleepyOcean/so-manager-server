package com.sleepy.manager.generation.service;

import com.sleepy.manager.generation.domain.Topic;

import java.util.List;

/**
 * 专题Service接口
 *
 * @author SleepyOcean
 * @date 2021-11-10
 */
public interface ITopicService {
    /**
     * 查询专题
     *
     * @param id 专题主键
     * @return 专题
     */
    Topic selectTopicById(Long id);

    /**
     * 查询专题列表
     *
     * @param topic 专题
     * @return 专题集合
     */
    List<Topic> selectTopicList(Topic topic);

    /**
     * 新增专题
     *
     * @param topic 专题
     * @return 结果
     */
    int insertTopic(Topic topic);

    /**
     * 修改专题
     *
     * @param topic 专题
     * @return 结果
     */
    int updateTopic(Topic topic);

    /**
     * 批量删除专题
     *
     * @param ids 需要删除的专题主键集合
     * @return 结果
     */
    int deleteTopicByIds(String ids);

    /**
     * 删除专题信息
     *
     * @param id 专题主键
     * @return 结果
     */
    int deleteTopicById(Long id);

    /**
     * 获取category的topic数量
     *
     * @param topic
     * @return
     */
    int countTopics(Topic topic);

    /**
     * 通过categoryId删除topic
     *
     * @param id
     * @return
     */
    int deleteTopicByCategoryId(Long id);

    /**
     * 移除tag或分类
     *
     * @param topic
     * @return
     */
    int removeTagOrClassification(Topic topic);
}
