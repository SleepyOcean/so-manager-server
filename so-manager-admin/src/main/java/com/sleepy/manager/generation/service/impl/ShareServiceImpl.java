package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.Share;
import com.sleepy.manager.generation.mapper.ShareMapper;
import com.sleepy.manager.generation.service.IShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分享Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-11-30
 */
@Service
public class ShareServiceImpl implements IShareService {
    @Autowired
    private ShareMapper shareMapper;

    /**
     * 查询分享
     *
     * @param id 分享主键
     * @return 分享
     */
    @Override
    public Share selectShareById(Long id) {
        return shareMapper.selectShareById(id);
    }

    /**
     * 查询分享列表
     *
     * @param share 分享
     * @return 分享
     */
    @Override
    public List<Share> selectShareList(Share share) {
        return shareMapper.selectShareList(share);
    }

    /**
     * 新增分享
     *
     * @param share 分享
     * @return 结果
     */
    @Override
    public int insertShare(Share share) {
        return shareMapper.insertShare(share);
    }

    /**
     * 修改分享
     *
     * @param share 分享
     * @return 结果
     */
    @Override
    public int updateShare(Share share) {
        return shareMapper.updateShare(share);
    }

    /**
     * 批量删除分享
     *
     * @param ids 需要删除的分享主键
     * @return 结果
     */
    @Override
    public int deleteShareByIds(String ids) {
        return shareMapper.deleteShareByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除分享信息
     *
     * @param id 分享主键
     * @return 结果
     */
    @Override
    public int deleteShareById(Long id) {
        return shareMapper.deleteShareById(id);
    }
}
