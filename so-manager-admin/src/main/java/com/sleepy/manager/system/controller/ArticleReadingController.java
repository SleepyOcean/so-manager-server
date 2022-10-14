package com.sleepy.manager.system.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.main.common.AssembledData;
import com.sleepy.manager.main.processor.CrawlerProcessor;
import com.sleepy.manager.system.domain.ArticleReading;
import com.sleepy.manager.system.service.IArticleReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 稍后阅读Controller
 *
 * @author sleepyocean
 * @date 2022-06-08
 */
@RestController
@RequestMapping("/system/reading")
public class ArticleReadingController extends BaseController {
    @Autowired
    private IArticleReadingService articleReadingService;
    @Autowired
    private CrawlerProcessor crawlerProcessor;

    /**
     * 查询稍后阅读列表
     */
    @PreAuthorize("@ss.hasPermi('system:reading:list')")
    @GetMapping("/list")
    public TableDataInfo list(ArticleReading articleReading) {
        startPage();
        List<ArticleReading> list = articleReadingService.selectArticleReadingList(articleReading);
        TableDataInfo data = getDataTable(list);
        data.setRows(((List<ArticleReading>) data.getRows()).stream().map(r -> new AssembledData.Builder()
                .putAll(r)
                .put("preview", crawlerProcessor.beautifyArticle(r))
                .build()).collect(Collectors.toList()));
        return data;
    }

    /**
     * 导出稍后阅读列表
     */
    @PreAuthorize("@ss.hasPermi('system:reading:export')")
    @Log(title = "稍后阅读", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ArticleReading articleReading) {
        List<ArticleReading> list = articleReadingService.selectArticleReadingList(articleReading);
        ExcelUtil<ArticleReading> util = new ExcelUtil<ArticleReading>(ArticleReading.class);
        util.exportExcel(response, list, "稍后阅读数据");
    }

    /**
     * 获取稍后阅读详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:reading:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(articleReadingService.selectArticleReadingById(id));
    }

    /**
     * 新增稍后阅读
     */
    @PreAuthorize("@ss.hasPermi('system:reading:add')")
    @Log(title = "稍后阅读", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ArticleReading articleReading) {
        return toAjax(articleReadingService.insertArticleReading(articleReading));
    }

    /**
     * 修改稍后阅读
     */
    @PreAuthorize("@ss.hasPermi('system:reading:edit')")
    @Log(title = "稍后阅读", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ArticleReading articleReading) {
        return toAjax(articleReadingService.updateArticleReading(articleReading));
    }

    /**
     * 删除稍后阅读
     */
    @PreAuthorize("@ss.hasPermi('system:reading:remove')")
    @Log(title = "稍后阅读", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(articleReadingService.deleteArticleReadingByIds(ids));
    }
}
