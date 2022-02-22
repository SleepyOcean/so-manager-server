package com.sleepy.manager.generation.service;

import com.sleepy.manager.generation.domain.Share;

import java.util.List;

/**
 * 分享Service接口
 *
 * @author SleepyOcean
 * @date 2021-11-30
 */
public interface IShareService {
    /**
     * 查询分享
     *
     * @param id 分享主键
     * @return 分享
     */
    Share selectShareById(Long id);

    /**
     * 查询分享列表
     *
     * @param share 分享
     * @return 分享集合
     */
    List<Share> selectShareList(Share share);

    /**
     * 新增分享
     *
     * @param share 分享
     * @return 结果
     */
    int insertShare(Share share);

    /**
     * 修改分享
     *
     * @param share 分享
     * @return 结果
     */
    int updateShare(Share share);

    /**
     * 批量删除分享
     *
     * @param ids 需要删除的分享主键集合
     * @return 结果
     */
    int deleteShareByIds(String ids);

    /**
     * 删除分享信息
     *
     * @param id 分享主键
     * @return 结果
     */
    int deleteShareById(Long id);
}
