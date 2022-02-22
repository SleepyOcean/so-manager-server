package com.sleepy.manager.generation.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.generation.domain.AppVersion;
import com.sleepy.manager.generation.service.IAppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * app版本Controller
 * 
 * @author sleepyocean
 * @date 2022-02-22
 */
@RestController
@RequestMapping("/generation/version")
public class AppVersionController extends BaseController
{
    @Autowired
    private IAppVersionService appVersionService;

    /**
     * 查询app版本列表
     */
    @PreAuthorize("@ss.hasPermi('system:version:list')")
    @GetMapping("/list")
    public TableDataInfo list(AppVersion appVersion)
    {
        startPage();
        List<AppVersion> list = appVersionService.selectAppVersionList(appVersion);
        return getDataTable(list);
    }

    /**
     * 导出app版本列表
     */
    @PreAuthorize("@ss.hasPermi('system:version:export')")
    @Log(title = "app版本", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AppVersion appVersion)
    {
        List<AppVersion> list = appVersionService.selectAppVersionList(appVersion);
        ExcelUtil<AppVersion> util = new ExcelUtil<AppVersion>(AppVersion.class);
        util.exportExcel(response, list, "app版本数据");
    }

    /**
     * 获取app版本详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:version:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(appVersionService.selectAppVersionById(id));
    }

    /**
     * 新增app版本
     */
    @PreAuthorize("@ss.hasPermi('system:version:add')")
    @Log(title = "app版本", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AppVersion appVersion)
    {
        return toAjax(appVersionService.insertAppVersion(appVersion));
    }

    /**
     * 修改app版本
     */
    @PreAuthorize("@ss.hasPermi('system:version:edit')")
    @Log(title = "app版本", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AppVersion appVersion)
    {
        return toAjax(appVersionService.updateAppVersion(appVersion));
    }

    /**
     * 删除app版本
     */
    @PreAuthorize("@ss.hasPermi('system:version:remove')")
    @Log(title = "app版本", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(appVersionService.deleteAppVersionByIds(StringUtils.join(ids, ",")));
    }
}
