package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.Topic;
import com.sleepy.manager.generation.mapper.TopicMapper;
import com.sleepy.manager.generation.service.ITopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 专题Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-11-10
 */
@Service
public class TopicServiceImpl implements ITopicService {
    @Autowired
    private TopicMapper topicMapper;

    /**
     * 查询专题
     *
     * @param id 专题主键
     * @return 专题
     */
    @Override
    public Topic selectTopicById(Long id) {
        return topicMapper.selectTopicById(id);
    }

    /**
     * 查询专题列表
     *
     * @param topic 专题
     * @return 专题
     */
    @Override
    public List<Topic> selectTopicList(Topic topic) {
        return topicMapper.selectTopicList(topic);
    }

    /**
     * 新增专题
     *
     * @param topic 专题
     * @return 结果
     */
    @Override
    public int insertTopic(Topic topic) {
        return topicMapper.insertTopic(topic);
    }

    /**
     * 修改专题
     *
     * @param topic 专题
     * @return 结果
     */
    @Override
    public int updateTopic(Topic topic) {
        return topicMapper.updateTopic(topic);
    }

    /**
     * 批量删除专题
     *
     * @param ids 需要删除的专题主键
     * @return 结果
     */
    @Override
    public int deleteTopicByIds(String ids) {
        return topicMapper.deleteTopicByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除专题信息
     *
     * @param id 专题主键
     * @return 结果
     */
    @Override
    public int deleteTopicById(Long id) {
        return topicMapper.deleteTopicById(id);
    }

    @Override
    public int countTopics(Topic topic) {
        return topicMapper.countTopics(topic);
    }

    @Override
    public int deleteTopicByCategoryId(Long id) {
        return topicMapper.deleteTopicByCategoryId(id);
    }

    @Override
    public int removeTagOrClassification(Topic topic) {
        return topicMapper.removeTagOrClassification(topic);
    }
}
