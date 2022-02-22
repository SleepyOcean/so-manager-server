package com.sleepy.manager.generation.service.impl;

import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.generation.domain.Article;
import com.sleepy.manager.generation.mapper.ArticleMapper;
import com.sleepy.manager.generation.service.IArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章Service业务层处理
 *
 * @author SleepyOcean
 * @date 2021-09-29
 */
@Service
public class ArticleServiceImpl implements IArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 查询文章
     *
     * @param id 文章主键
     * @return 文章
     */
    @Override
    public Article selectArticleById(Long id) {
        return articleMapper.selectArticleById(id);
    }

    /**
     * 查询文章列表
     *
     * @param article 文章
     * @return 文章
     */
    @Override
    public List<Article> selectArticleList(Article article) {
        return articleMapper.selectArticleList(article);
    }

    /**
     * 新增文章
     *
     * @param article 文章
     * @return 结果
     */
    @Override
    public int insertArticle(Article article) {
        return articleMapper.insertArticle(article);
    }

    /**
     * 修改文章
     *
     * @param article 文章
     * @return 结果
     */
    @Override
    public int updateArticle(Article article) {
        return articleMapper.updateArticle(article);
    }

    /**
     * 批量删除文章
     *
     * @param ids 需要删除的文章主键
     * @return 结果
     */
    @Override
    public int deleteArticleByIds(String ids) {
        return articleMapper.deleteArticleByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除文章信息
     *
     * @param id 文章主键
     * @return 结果
     */
    @Override
    public int deleteArticleById(Long id) {
        return articleMapper.deleteArticleById(id);
    }

    @Override
    public int countArticles(Article article) {
        return articleMapper.countArticles(article);
    }

    @Override
    public List<Article> selectArticleListBySortType(Article article) {
        return articleMapper.selectArticleListBySortType(article);
    }

    @Override
    public List<Article> selectArticleByCategoryOrTopicId(Article article) {
        return articleMapper.selectArticleByCategoryOrTopicId(article);
    }

    @Override
    public List<Article> selectHomeNonHotspotArticle() {
        return articleMapper.selectHomeNonHotspotArticle();
    }

    @Override
    public List<Article> selectArticleListForRecommend(Article article) {
        return articleMapper.selectArticleListForRecommend(article);
    }

    @Override
    public int removeTagOrClassification(Article article) {
        return articleMapper.removeTagOrClassification(article);
    }
}
