package com.sleepy.manager.generation.service;

import com.sleepy.manager.generation.domain.ThirdPartUser;

import java.util.List;

/**
 * 三方账号Service接口
 *
 * @author SleepyOcean
 * @date 2021-12-08
 */
public interface IThirdPartUserService {
    /**
     * 查询三方账号
     *
     * @param id 三方账号主键
     * @return 三方账号
     */
    ThirdPartUser selectThirdPartUserById(Long id);

    /**
     * 查询三方账号列表
     *
     * @param thirdPartUser 三方账号
     * @return 三方账号集合
     */
    List<ThirdPartUser> selectThirdPartUserList(ThirdPartUser thirdPartUser);

    /**
     * 新增三方账号
     *
     * @param thirdPartUser 三方账号
     * @return 结果
     */
    int insertThirdPartUser(ThirdPartUser thirdPartUser);

    /**
     * 修改三方账号
     *
     * @param thirdPartUser 三方账号
     * @return 结果
     */
    int updateThirdPartUser(ThirdPartUser thirdPartUser);

    /**
     * 批量删除三方账号
     *
     * @param ids 需要删除的三方账号主键集合
     * @return 结果
     */
    int deleteThirdPartUserByIds(String ids);

    /**
     * 删除三方账号信息
     *
     * @param id 三方账号主键
     * @return 结果
     */
    int deleteThirdPartUserById(Long id);
}
