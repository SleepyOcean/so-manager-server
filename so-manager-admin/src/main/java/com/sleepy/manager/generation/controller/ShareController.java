package com.sleepy.manager.generation.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.generation.domain.Share;
import com.sleepy.manager.generation.service.IShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 分享Controller
 * 
 * @author sleepyocean
 * @date 2022-02-22
 */
@RestController
@RequestMapping("/generation/share")
public class ShareController extends BaseController
{
    @Autowired
    private IShareService shareService;

    /**
     * 查询分享列表
     */
    @PreAuthorize("@ss.hasPermi('system:share:list')")
    @GetMapping("/list")
    public TableDataInfo list(Share share)
    {
        startPage();
        List<Share> list = shareService.selectShareList(share);
        return getDataTable(list);
    }

    /**
     * 导出分享列表
     */
    @PreAuthorize("@ss.hasPermi('system:share:export')")
    @Log(title = "分享", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Share share)
    {
        List<Share> list = shareService.selectShareList(share);
        ExcelUtil<Share> util = new ExcelUtil<Share>(Share.class);
        util.exportExcel(response, list, "分享数据");
    }

    /**
     * 获取分享详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:share:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(shareService.selectShareById(id));
    }

    /**
     * 新增分享
     */
    @PreAuthorize("@ss.hasPermi('system:share:add')")
    @Log(title = "分享", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Share share)
    {
        return toAjax(shareService.insertShare(share));
    }

    /**
     * 修改分享
     */
    @PreAuthorize("@ss.hasPermi('system:share:edit')")
    @Log(title = "分享", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Share share)
    {
        return toAjax(shareService.updateShare(share));
    }

    /**
     * 删除分享
     */
    @PreAuthorize("@ss.hasPermi('system:share:remove')")
    @Log(title = "分享", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(shareService.deleteShareByIds(StringUtils.join(ids, ",")));
    }
}
