package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.UserFav;
import com.sleepy.manager.generation.mapper.UserFavMapper;
import com.sleepy.manager.generation.service.IUserFavService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户收藏Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-09-29
 */
@Service
public class UserFavServiceImpl implements IUserFavService {
    @Autowired
    private UserFavMapper userFavMapper;

    /**
     * 查询用户收藏
     *
     * @param id 用户收藏主键
     * @return 用户收藏
     */
    @Override
    public UserFav selectUserFavById(Long id) {
        return userFavMapper.selectUserFavById(id);
    }

    /**
     * 查询用户收藏列表
     *
     * @param userFav 用户收藏
     * @return 用户收藏
     */
    @Override
    public List<UserFav> selectUserFavList(UserFav userFav) {
        return userFavMapper.selectUserFavList(userFav);
    }

    /**
     * 新增用户收藏
     *
     * @param userFav 用户收藏
     * @return 结果
     */
    @Override
    public int insertUserFav(UserFav userFav) {
        return userFavMapper.insertUserFav(userFav);
    }

    /**
     * 修改用户收藏
     *
     * @param userFav 用户收藏
     * @return 结果
     */
    @Override
    public int updateUserFav(UserFav userFav) {
        return userFavMapper.updateUserFav(userFav);
    }

    /**
     * 批量删除用户收藏
     *
     * @param ids 需要删除的用户收藏主键
     * @return 结果
     */
    @Override
    public int deleteUserFavByIds(String ids) {
        return userFavMapper.deleteUserFavByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除用户收藏信息
     *
     * @param id 用户收藏主键
     * @return 结果
     */
    @Override
    public int deleteUserFavById(Long id) {
        return userFavMapper.deleteUserFavById(id);
    }
}
