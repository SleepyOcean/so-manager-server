package com.sleepy.manager.generation.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.generation.domain.HotSearch;
import com.sleepy.manager.generation.service.IHotSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 热搜Controller
 * 
 * @author sleepyocean
 * @date 2022-02-22
 */
@RestController
@RequestMapping("/generation/search")
public class HotSearchController extends BaseController
{
    @Autowired
    private IHotSearchService hotSearchService;

    /**
     * 查询热搜列表
     */
    @PreAuthorize("@ss.hasPermi('system:search:list')")
    @GetMapping("/list")
    public TableDataInfo list(HotSearch hotSearch)
    {
        startPage();
        List<HotSearch> list = hotSearchService.selectHotSearchList(hotSearch);
        return getDataTable(list);
    }

    /**
     * 导出热搜列表
     */
    @PreAuthorize("@ss.hasPermi('system:search:export')")
    @Log(title = "热搜", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, HotSearch hotSearch)
    {
        List<HotSearch> list = hotSearchService.selectHotSearchList(hotSearch);
        ExcelUtil<HotSearch> util = new ExcelUtil<HotSearch>(HotSearch.class);
        util.exportExcel(response, list, "热搜数据");
    }

    /**
     * 获取热搜详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:search:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(hotSearchService.selectHotSearchById(id));
    }

    /**
     * 新增热搜
     */
    @PreAuthorize("@ss.hasPermi('system:search:add')")
    @Log(title = "热搜", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody HotSearch hotSearch)
    {
        return toAjax(hotSearchService.insertHotSearch(hotSearch));
    }

    /**
     * 修改热搜
     */
    @PreAuthorize("@ss.hasPermi('system:search:edit')")
    @Log(title = "热搜", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody HotSearch hotSearch)
    {
        return toAjax(hotSearchService.updateHotSearch(hotSearch));
    }

    /**
     * 删除热搜
     */
    @PreAuthorize("@ss.hasPermi('system:search:remove')")
    @Log(title = "热搜", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(hotSearchService.deleteHotSearchByIds(StringUtils.join(ids, ",")));
    }
}
