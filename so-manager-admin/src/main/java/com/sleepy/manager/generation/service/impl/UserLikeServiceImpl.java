package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.UserLike;
import com.sleepy.manager.generation.mapper.UserLikeMapper;
import com.sleepy.manager.generation.service.IUserLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户点赞Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-09-29
 */
@Service
public class UserLikeServiceImpl implements IUserLikeService {
    @Autowired
    private UserLikeMapper userLikeMapper;

    /**
     * 查询用户点赞
     *
     * @param id 用户点赞主键
     * @return 用户点赞
     */
    @Override
    public UserLike selectUserLikeById(Long id) {
        return userLikeMapper.selectUserLikeById(id);
    }

    /**
     * 查询用户点赞列表
     *
     * @param userLike 用户点赞
     * @return 用户点赞
     */
    @Override
    public List<UserLike> selectUserLikeList(UserLike userLike) {
        return userLikeMapper.selectUserLikeList(userLike);
    }

    /**
     * 新增用户点赞
     *
     * @param userLike 用户点赞
     * @return 结果
     */
    @Override
    public int insertUserLike(UserLike userLike) {
        return userLikeMapper.insertUserLike(userLike);
    }

    /**
     * 修改用户点赞
     *
     * @param userLike 用户点赞
     * @return 结果
     */
    @Override
    public int updateUserLike(UserLike userLike) {
        return userLikeMapper.updateUserLike(userLike);
    }

    /**
     * 批量删除用户点赞
     *
     * @param ids 需要删除的用户点赞主键
     * @return 结果
     */
    @Override
    public int deleteUserLikeByIds(String ids) {
        return userLikeMapper.deleteUserLikeByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除用户点赞信息
     *
     * @param id 用户点赞主键
     * @return 结果
     */
    @Override
    public int deleteUserLikeById(Long id) {
        return userLikeMapper.deleteUserLikeById(id);
    }
}
