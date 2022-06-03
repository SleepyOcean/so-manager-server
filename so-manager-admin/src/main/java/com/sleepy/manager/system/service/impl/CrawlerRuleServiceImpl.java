package com.sleepy.manager.system.service.impl;

import com.sleepy.manager.system.domain.CrawlerRule;
import com.sleepy.manager.system.mapper.CrawlerRuleMapper;
import com.sleepy.manager.system.service.ICrawlerRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 爬虫规则Service业务层处理
 *
 * @author sleepyocean
 * @date 2022-06-03
 */
@Service
public class CrawlerRuleServiceImpl implements ICrawlerRuleService {
    @Autowired
    private CrawlerRuleMapper crawlerRuleMapper;

    /**
     * 查询爬虫规则
     *
     * @param id 爬虫规则主键
     * @return 爬虫规则
     */
    @Override
    public CrawlerRule selectCrawlerRuleById(Long id) {
        return crawlerRuleMapper.selectCrawlerRuleById(id);
    }

    /**
     * 查询爬虫规则列表
     *
     * @param crawlerRule 爬虫规则
     * @return 爬虫规则
     */
    @Override
    public List<CrawlerRule> selectCrawlerRuleList(CrawlerRule crawlerRule) {
        return crawlerRuleMapper.selectCrawlerRuleList(crawlerRule);
    }

    /**
     * 新增爬虫规则
     *
     * @param crawlerRule 爬虫规则
     * @return 结果
     */
    @Override
    public int insertCrawlerRule(CrawlerRule crawlerRule) {
        return crawlerRuleMapper.insertCrawlerRule(crawlerRule);
    }

    /**
     * 修改爬虫规则
     *
     * @param crawlerRule 爬虫规则
     * @return 结果
     */
    @Override
    public int updateCrawlerRule(CrawlerRule crawlerRule) {
        return crawlerRuleMapper.updateCrawlerRule(crawlerRule);
    }

    /**
     * 批量删除爬虫规则
     *
     * @param ids 需要删除的爬虫规则主键
     * @return 结果
     */
    @Override
    public int deleteCrawlerRuleByIds(Long[] ids) {
        return crawlerRuleMapper.deleteCrawlerRuleByIds(ids);
    }

    /**
     * 删除爬虫规则信息
     *
     * @param id 爬虫规则主键
     * @return 结果
     */
    @Override
    public int deleteCrawlerRuleById(Long id) {
        return crawlerRuleMapper.deleteCrawlerRuleById(id);
    }
}
