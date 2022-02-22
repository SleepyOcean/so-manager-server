package com.sleepy.manager.generation.mapper;

import com.sleepy.manager.generation.domain.Article;

import java.util.List;

/**
 * 文章Mapper接口
 *
 * @author SleepyOcean
 * @date 2021-09-29
 */
public interface ArticleMapper {
    /**
     * 查询文章
     *
     * @param id 文章主键
     * @return 文章
     */
    Article selectArticleById(Long id);

    /**
     * 查询文章列表
     *
     * @param article 文章
     * @return 文章集合
     */
    List<Article> selectArticleList(Article article);

    /**
     * 新增文章
     *
     * @param article 文章
     * @return 结果
     */
    int insertArticle(Article article);

    /**
     * 修改文章
     *
     * @param article 文章
     * @return 结果
     */
    int updateArticle(Article article);

    /**
     * 删除文章
     *
     * @param id 文章主键
     * @return 结果
     */
    int deleteArticleById(Long id);

    /**
     * 批量删除文章
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteArticleByIds(String[] ids);

    /**
     * count文章数
     *
     * @param article
     * @return
     */
    int countArticles(Article article);

    /**
     * 通过sortType获取文章列表
     *
     * @param article
     * @return
     */
    List<Article> selectArticleListBySortType(Article article);

    /**
     * 通过category或topic来获取文章列表
     *
     * @param article
     * @return
     */
    List<Article> selectArticleByCategoryOrTopicId(Article article);

    /**
     * 获取首页非置顶文章
     *
     * @return
     */
    List<Article> selectHomeNonHotspotArticle();

    /**
     * 通过 tagIds 或 classIds 获取文章
     *
     * @param article
     * @return
     */
    List<Article> selectArticleListForRecommend(Article article);

    /**
     * 移除tag或分类
     *
     * @param article
     * @return
     */
    int removeTagOrClassification(Article article);
}
