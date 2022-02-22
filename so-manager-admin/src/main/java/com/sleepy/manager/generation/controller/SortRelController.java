package com.sleepy.manager.generation.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.generation.domain.SortRel;
import com.sleepy.manager.generation.service.ISortRelService;
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
@RequestMapping("/generation/rel")
public class SortRelController extends BaseController
{
    @Autowired
    private ISortRelService sortRelService;

    /**
     * 查询分类列表
     */
    @PreAuthorize("@ss.hasPermi('system:rel:list')")
    @GetMapping("/list")
    public TableDataInfo list(SortRel sortRel)
    {
        startPage();
        List<SortRel> list = sortRelService.selectSortRelList(sortRel);
        return getDataTable(list);
    }

    /**
     * 导出分类列表
     */
    @PreAuthorize("@ss.hasPermi('system:rel:export')")
    @Log(title = "分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SortRel sortRel)
    {
        List<SortRel> list = sortRelService.selectSortRelList(sortRel);
        ExcelUtil<SortRel> util = new ExcelUtil<SortRel>(SortRel.class);
        util.exportExcel(response, list, "分类数据");
    }

    /**
     * 获取分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:rel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(sortRelService.selectSortRelById(id));
    }

    /**
     * 新增分类
     */
    @PreAuthorize("@ss.hasPermi('system:rel:add')")
    @Log(title = "分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SortRel sortRel)
    {
        return toAjax(sortRelService.insertSortRel(sortRel));
    }

    /**
     * 修改分类
     */
    @PreAuthorize("@ss.hasPermi('system:rel:edit')")
    @Log(title = "分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SortRel sortRel)
    {
        return toAjax(sortRelService.updateSortRel(sortRel));
    }

    /**
     * 删除分类
     */
    @PreAuthorize("@ss.hasPermi('system:rel:remove')")
    @Log(title = "分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sortRelService.deleteSortRelByIds(StringUtils.join(ids, ",")));
    }
}
