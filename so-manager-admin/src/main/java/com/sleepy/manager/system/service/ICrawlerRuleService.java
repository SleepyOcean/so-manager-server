package com.sleepy.manager.system.service;

import com.sleepy.manager.system.domain.CrawlerRule;

import java.util.List;

/**
 * 爬虫规则Service接口
 *
 * @author sleepyocean
 * @date 2022-06-03
 */
public interface ICrawlerRuleService {
    /**
     * 查询爬虫规则
     *
     * @param id 爬虫规则主键
     * @return 爬虫规则
     */
    CrawlerRule selectCrawlerRuleById(Long id);

    /**
     * 查询爬虫规则列表
     *
     * @param crawlerRule 爬虫规则
     * @return 爬虫规则集合
     */
    List<CrawlerRule> selectCrawlerRuleList(CrawlerRule crawlerRule);

    /**
     * 新增爬虫规则
     *
     * @param crawlerRule 爬虫规则
     * @return 结果
     */
    int insertCrawlerRule(CrawlerRule crawlerRule);

    /**
     * 修改爬虫规则
     *
     * @param crawlerRule 爬虫规则
     * @return 结果
     */
    int updateCrawlerRule(CrawlerRule crawlerRule);

    /**
     * 批量删除爬虫规则
     *
     * @param ids 需要删除的爬虫规则主键集合
     * @return 结果
     */
    int deleteCrawlerRuleByIds(Long[] ids);

    /**
     * 删除爬虫规则信息
     *
     * @param id 爬虫规则主键
     * @return 结果
     */
    int deleteCrawlerRuleById(Long id);
}
