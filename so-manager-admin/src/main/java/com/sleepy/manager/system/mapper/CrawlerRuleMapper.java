package com.sleepy.manager.system.mapper;

import com.sleepy.manager.system.domain.CrawlerRule;

import java.util.List;

/**
 * 爬虫规则Mapper接口
 *
 * @author sleepyocean
 * @date 2022-06-03
 */
public interface CrawlerRuleMapper {
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
     * 删除爬虫规则
     *
     * @param id 爬虫规则主键
     * @return 结果
     */
    int deleteCrawlerRuleById(Long id);

    /**
     * 批量删除爬虫规则
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteCrawlerRuleByIds(Long[] ids);
}
