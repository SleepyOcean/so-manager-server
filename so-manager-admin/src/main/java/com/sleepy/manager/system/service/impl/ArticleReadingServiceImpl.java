package com.sleepy.manager.system.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.sleepy.manager.main.processor.CrawlerProcessor;
import com.sleepy.manager.system.domain.ArticleReading;
import com.sleepy.manager.system.mapper.ArticleReadingMapper;
import com.sleepy.manager.system.service.IArticleReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 稍后阅读Service业务层处理
 *
 * @author sleepyocean
 * @date 2022-06-08
 */
@Service
public class ArticleReadingServiceImpl implements IArticleReadingService {
    @Autowired
    private ArticleReadingMapper articleReadingMapper;
    @Autowired
    private CrawlerProcessor crawlerProcessor;

    /**
     * 查询稍后阅读
     *
     * @param id 稍后阅读主键
     * @return 稍后阅读
     */
    @Override
    public ArticleReading selectArticleReadingById(Long id) {
        return articleReadingMapper.selectArticleReadingById(id);
    }

    @Override
    public ArticleReading selectArticleReadingByUrlMD5(String md5) {
        return articleReadingMapper.selectArticleReadingByUrlMD5(md5);
    }

    /**
     * 查询稍后阅读列表
     *
     * @param articleReading 稍后阅读
     * @return 稍后阅读
     */
    @Override
    public List<ArticleReading> selectArticleReadingList(ArticleReading articleReading) {
        return articleReadingMapper.selectArticleReadingList(articleReading);
    }

    /**
     * 新增稍后阅读
     *
     * @param articleReading 稍后阅读
     * @return 结果
     */
    @Override
    public int insertArticleReading(ArticleReading articleReading) {
        return articleReadingMapper.insertArticleReading(articleReading);
    }

    /**
     * 修改稍后阅读
     *
     * @param articleReading 稍后阅读
     * @return 结果
     */
    @Override
    public int updateArticleReading(ArticleReading articleReading) {
        return articleReadingMapper.updateArticleReading(articleReading);
    }

    /**
     * 批量删除稍后阅读
     *
     * @param ids 需要删除的稍后阅读主键
     * @return 结果
     */
    @Override
    public int deleteArticleReadingByIds(Long[] ids) {
        for (Long id : ids) {
            ArticleReading articleReading = articleReadingMapper.selectArticleReadingById(id);
            if (ObjectUtil.isNotEmpty(articleReading)) {
                String articleCachePath = crawlerProcessor.constructArticleReadingImgCachePath(articleReading.getMd5());
                FileUtil.del(articleCachePath);
            }
        }
        return articleReadingMapper.deleteArticleReadingByIds(ids);
    }

    /**
     * 删除稍后阅读信息
     *
     * @param id 稍后阅读主键
     * @return 结果
     */
    @Override
    public int deleteArticleReadingById(Long id) {
        return articleReadingMapper.deleteArticleReadingById(id);
    }
}
