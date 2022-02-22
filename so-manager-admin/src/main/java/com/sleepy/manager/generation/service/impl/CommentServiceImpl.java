package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.Comment;
import com.sleepy.manager.generation.mapper.CommentMapper;
import com.sleepy.manager.generation.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-09-29
 */
@Service
public class CommentServiceImpl implements ICommentService {
    @Autowired
    private CommentMapper commentMapper;

    /**
     * 查询评论
     *
     * @param id 评论主键
     * @return 评论
     */
    @Override
    public Comment selectCommentById(Long id) {
        return commentMapper.selectCommentById(id);
    }

    /**
     * 查询评论列表
     *
     * @param comment 评论
     * @return 评论
     */
    @Override
    public List<Comment> selectCommentList(Comment comment) {
        return commentMapper.selectCommentList(comment);
    }

    /**
     * 新增评论
     *
     * @param comment 评论
     * @return 结果
     */
    @Override
    public int insertComment(Comment comment) {
        return commentMapper.insertComment(comment);
    }

    /**
     * 修改评论
     *
     * @param comment 评论
     * @return 结果
     */
    @Override
    public int updateComment(Comment comment) {
        return commentMapper.updateComment(comment);
    }

    /**
     * 批量删除评论
     *
     * @param ids 需要删除的评论主键
     * @return 结果
     */
    @Override
    public int deleteCommentByIds(String ids) {
        return commentMapper.deleteCommentByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除评论信息
     *
     * @param id 评论主键
     * @return 结果
     */
    @Override
    public int deleteCommentById(Long id) {
        return commentMapper.deleteCommentById(id);
    }

    @Override
    public List<Comment> countComment(Comment comment) {
        return commentMapper.countComment(comment);
    }
}
