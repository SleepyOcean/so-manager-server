package com.sleepy.manager.generation.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.generation.domain.Classification;
import com.sleepy.manager.generation.service.IClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 分类Controller
 * 
 * @author sleepyocean
 * @date 2022-02-22
 */
@RestController
@RequestMapping("/generation/classification")
public class ClassificationController extends BaseController
{
    @Autowired
    private IClassificationService classificationService;

    /**
     * 查询分类列表
     */
    @PreAuthorize("@ss.hasPermi('system:classification:list')")
    @GetMapping("/list")
    public TableDataInfo list(Classification classification)
    {
        startPage();
        List<Classification> list = classificationService.selectClassificationList(classification);
        return getDataTable(list);
    }

    /**
     * 导出分类列表
     */
    @PreAuthorize("@ss.hasPermi('system:classification:export')")
    @Log(title = "分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Classification classification)
    {
        List<Classification> list = classificationService.selectClassificationList(classification);
        ExcelUtil<Classification> util = new ExcelUtil<Classification>(Classification.class);
        util.exportExcel(response, list, "分类数据");
    }

    /**
     * 获取分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:classification:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(classificationService.selectClassificationById(id));
    }

    /**
     * 新增分类
     */
    @PreAuthorize("@ss.hasPermi('system:classification:add')")
    @Log(title = "分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Classification classification)
    {
        return toAjax(classificationService.insertClassification(classification));
    }

    /**
     * 修改分类
     */
    @PreAuthorize("@ss.hasPermi('system:classification:edit')")
    @Log(title = "分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Classification classification)
    {
        return toAjax(classificationService.updateClassification(classification));
    }

    /**
     * 删除分类
     */
    @PreAuthorize("@ss.hasPermi('system:classification:remove')")
    @Log(title = "分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(classificationService.deleteClassificationByIds(StringUtils.join(ids, ",")));
    }
}
