package com.sleepy.manager.generation.mapper;

import com.sleepy.manager.generation.domain.UserLike;

import java.util.List;

/**
 * 用户点赞Mapper接口
 *
 * @author SleepyOcean
 * @date 2021-09-29
 */
public interface UserLikeMapper {
    /**
     * 查询用户点赞
     *
     * @param id 用户点赞主键
     * @return 用户点赞
     */
    UserLike selectUserLikeById(Long id);

    /**
     * 查询用户点赞列表
     *
     * @param userLike 用户点赞
     * @return 用户点赞集合
     */
    List<UserLike> selectUserLikeList(UserLike userLike);

    /**
     * 新增用户点赞
     *
     * @param userLike 用户点赞
     * @return 结果
     */
    int insertUserLike(UserLike userLike);

    /**
     * 修改用户点赞
     *
     * @param userLike 用户点赞
     * @return 结果
     */
    int updateUserLike(UserLike userLike);

    /**
     * 删除用户点赞
     *
     * @param id 用户点赞主键
     * @return 结果
     */
    int deleteUserLikeById(Long id);

    /**
     * 批量删除用户点赞
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteUserLikeByIds(String[] ids);
}
