package com.sleepy.manager.generation.service;

import com.sleepy.manager.generation.domain.UserFav;

import java.util.List;

/**
 * 用户收藏Service接口
 *
 * @author SleepyOcean
 * @date 2021-09-29
 */
public interface IUserFavService {
    /**
     * 查询用户收藏
     *
     * @param id 用户收藏主键
     * @return 用户收藏
     */
    UserFav selectUserFavById(Long id);

    /**
     * 查询用户收藏列表
     *
     * @param userFav 用户收藏
     * @return 用户收藏集合
     */
    List<UserFav> selectUserFavList(UserFav userFav);

    /**
     * 新增用户收藏
     *
     * @param userFav 用户收藏
     * @return 结果
     */
    int insertUserFav(UserFav userFav);

    /**
     * 修改用户收藏
     *
     * @param userFav 用户收藏
     * @return 结果
     */
    int updateUserFav(UserFav userFav);

    /**
     * 批量删除用户收藏
     *
     * @param ids 需要删除的用户收藏主键集合
     * @return 结果
     */
    int deleteUserFavByIds(String ids);

    /**
     * 删除用户收藏信息
     *
     * @param id 用户收藏主键
     * @return 结果
     */
    int deleteUserFavById(Long id);
}
