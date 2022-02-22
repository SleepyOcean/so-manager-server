package com.sleepy.manager.generation.service;

import com.sleepy.manager.generation.domain.Tag;

import java.util.List;

/**
 * 标签Service接口
 *
 * @author SleepyOcean
 * @date 2021-11-11
 */
public interface ITagService {
    /**
     * 查询标签
     *
     * @param id 标签主键
     * @return 标签
     */
    Tag selectTagById(Long id);

    /**
     * 查询标签列表
     *
     * @param tag 标签
     * @return 标签集合
     */
    List<Tag> selectTagList(Tag tag);

    /**
     * 新增标签
     *
     * @param tag 标签
     * @return 结果
     */
    int insertTag(Tag tag);

    /**
     * 修改标签
     *
     * @param tag 标签
     * @return 结果
     */
    int updateTag(Tag tag);

    /**
     * 批量删除标签
     *
     * @param ids 需要删除的标签主键集合
     * @return 结果
     */
    int deleteTagByIds(String ids);

    /**
     * 删除标签信息
     *
     * @param id 标签主键
     * @return 结果
     */
    int deleteTagById(Long id);
}
