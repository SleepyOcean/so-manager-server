package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.ThirdPartUser;
import com.sleepy.manager.generation.mapper.ThirdPartUserMapper;
import com.sleepy.manager.generation.service.IThirdPartUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 三方账号Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-12-08
 */
@Service
public class ThirdPartUserServiceImpl implements IThirdPartUserService {
    @Autowired
    private ThirdPartUserMapper thirdPartUserMapper;

    /**
     * 查询三方账号
     *
     * @param id 三方账号主键
     * @return 三方账号
     */
    @Override
    public ThirdPartUser selectThirdPartUserById(Long id) {
        return thirdPartUserMapper.selectThirdPartUserById(id);
    }

    /**
     * 查询三方账号列表
     *
     * @param thirdPartUser 三方账号
     * @return 三方账号
     */
    @Override
    public List<ThirdPartUser> selectThirdPartUserList(ThirdPartUser thirdPartUser) {
        return thirdPartUserMapper.selectThirdPartUserList(thirdPartUser);
    }

    /**
     * 新增三方账号
     *
     * @param thirdPartUser 三方账号
     * @return 结果
     */
    @Override
    public int insertThirdPartUser(ThirdPartUser thirdPartUser) {
        return thirdPartUserMapper.insertThirdPartUser(thirdPartUser);
    }

    /**
     * 修改三方账号
     *
     * @param thirdPartUser 三方账号
     * @return 结果
     */
    @Override
    public int updateThirdPartUser(ThirdPartUser thirdPartUser) {
        return thirdPartUserMapper.updateThirdPartUser(thirdPartUser);
    }

    /**
     * 批量删除三方账号
     *
     * @param ids 需要删除的三方账号主键
     * @return 结果
     */
    @Override
    public int deleteThirdPartUserByIds(String ids) {
        return thirdPartUserMapper.deleteThirdPartUserByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除三方账号信息
     *
     * @param id 三方账号主键
     * @return 结果
     */
    @Override
    public int deleteThirdPartUserById(Long id) {
        return thirdPartUserMapper.deleteThirdPartUserById(id);
    }
}
