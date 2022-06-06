package com.sleepy.manager.system.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.system.domain.CrawlerRule;
import com.sleepy.manager.system.service.ICrawlerRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 爬虫规则Controller
 *
 * @author sleepyocean
 * @date 2022-06-03
 */
@RestController
@RequestMapping("/system/crawler-rule")
public class CrawlerRuleController extends BaseController {
    @Autowired
    private ICrawlerRuleService crawlerRuleService;

    /**
     * 查询爬虫规则列表
     */
    @PreAuthorize("@ss.hasPermi('system:rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(CrawlerRule crawlerRule) {
        startPage();
        List<CrawlerRule> list = crawlerRuleService.selectCrawlerRuleList(crawlerRule);
        return getDataTable(list);
    }

    /**
     * 导出爬虫规则列表
     */
    @PreAuthorize("@ss.hasPermi('system:rule:export')")
    @Log(title = "爬虫规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CrawlerRule crawlerRule) {
        List<CrawlerRule> list = crawlerRuleService.selectCrawlerRuleList(crawlerRule);
        ExcelUtil<CrawlerRule> util = new ExcelUtil<CrawlerRule>(CrawlerRule.class);
        util.exportExcel(response, list, "爬虫规则数据");
    }

    /**
     * 获取爬虫规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:rule:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(crawlerRuleService.selectCrawlerRuleById(id));
    }

    /**
     * 新增爬虫规则
     */
    @PreAuthorize("@ss.hasPermi('system:rule:add')")
    @Log(title = "爬虫规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CrawlerRule crawlerRule) {
        return toAjax(crawlerRuleService.insertCrawlerRule(crawlerRule));
    }

    /**
     * 修改爬虫规则
     */
    @PreAuthorize("@ss.hasPermi('system:rule:edit')")
    @Log(title = "爬虫规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CrawlerRule crawlerRule) {
        return toAjax(crawlerRuleService.updateCrawlerRule(crawlerRule));
    }

    /**
     * 删除爬虫规则
     */
    @PreAuthorize("@ss.hasPermi('system:rule:remove')")
    @Log(title = "爬虫规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(crawlerRuleService.deleteCrawlerRuleByIds(ids));
    }
}
