package com.sleepy.manager.generation.mapper;

import com.sleepy.manager.generation.domain.Comment;

import java.util.List;

/**
 * 评论Mapper接口
 *
 * @author SleepyOcean
 * @date 2021-09-29
 */
public interface CommentMapper {
    /**
     * 查询评论
     *
     * @param id 评论主键
     * @return 评论
     */
    Comment selectCommentById(Long id);

    /**
     * 查询评论列表
     *
     * @param comment 评论
     * @return 评论集合
     */
    List<Comment> selectCommentList(Comment comment);

    /**
     * 新增评论
     *
     * @param comment 评论
     * @return 结果
     */
    int insertComment(Comment comment);

    /**
     * 修改评论
     *
     * @param comment 评论
     * @return 结果
     */
    int updateComment(Comment comment);

    /**
     * 删除评论
     *
     * @param id 评论主键
     * @return 结果
     */
    int deleteCommentById(Long id);

    /**
     * 批量删除评论
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteCommentByIds(String[] ids);

    /**
     * 统计文章评论数量
     *
     * @param comment
     * @return
     */
    List<Comment> countComment(Comment comment);
}
