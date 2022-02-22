package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.Tag;
import com.sleepy.manager.generation.mapper.TagMapper;
import com.sleepy.manager.generation.service.ITagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-11-11
 */
@Service
public class TagServiceImpl implements ITagService {
    @Autowired
    private TagMapper tagMapper;

    /**
     * 查询标签
     *
     * @param id 标签主键
     * @return 标签
     */
    @Override
    public Tag selectTagById(Long id) {
        return tagMapper.selectTagById(id);
    }

    /**
     * 查询标签列表
     *
     * @param tag 标签
     * @return 标签
     */
    @Override
    public List<Tag> selectTagList(Tag tag) {
        return tagMapper.selectTagList(tag);
    }

    /**
     * 新增标签
     *
     * @param tag 标签
     * @return 结果
     */
    @Override
    public int insertTag(Tag tag) {
        return tagMapper.insertTag(tag);
    }

    /**
     * 修改标签
     *
     * @param tag 标签
     * @return 结果
     */
    @Override
    public int updateTag(Tag tag) {
        return tagMapper.updateTag(tag);
    }

    /**
     * 批量删除标签
     *
     * @param ids 需要删除的标签主键
     * @return 结果
     */
    @Override
    public int deleteTagByIds(String ids) {
        return tagMapper.deleteTagByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除标签信息
     *
     * @param id 标签主键
     * @return 结果
     */
    @Override
    public int deleteTagById(Long id) {
        return tagMapper.deleteTagById(id);
    }
}
