package com.sleepy.manager.system.service;

import com.sleepy.manager.system.domain.ArticleReading;

import java.util.List;

/**
 * 稍后阅读Service接口
 *
 * @author sleepyocean
 * @date 2022-06-08
 */
public interface IArticleReadingService {
    /**
     * 查询稍后阅读
     *
     * @param id 稍后阅读主键
     * @return 稍后阅读
     */
    ArticleReading selectArticleReadingById(Long id);

    /**
     * 通过md5查询稍后阅读
     *
     * @param md5 稍后阅读源网址的MD5值
     * @return 稍后阅读
     */
    ArticleReading selectArticleReadingByUrlMD5(String md5);

    /**
     * 查询稍后阅读列表
     *
     * @param articleReading 稍后阅读
     * @return 稍后阅读集合
     */
    List<ArticleReading> selectArticleReadingList(ArticleReading articleReading);

    /**
     * 新增稍后阅读
     *
     * @param articleReading 稍后阅读
     * @return 结果
     */
    int insertArticleReading(ArticleReading articleReading);

    /**
     * 修改稍后阅读
     *
     * @param articleReading 稍后阅读
     * @return 结果
     */
    int updateArticleReading(ArticleReading articleReading);

    /**
     * 批量删除稍后阅读
     *
     * @param ids 需要删除的稍后阅读主键集合
     * @return 结果
     */
    int deleteArticleReadingByIds(Long[] ids);

    /**
     * 删除稍后阅读信息
     *
     * @param id 稍后阅读主键
     * @return 结果
     */
    int deleteArticleReadingById(Long id);
}
