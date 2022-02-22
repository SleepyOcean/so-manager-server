package com.sleepy.manager.generation.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.generation.domain.ThirdPartUser;
import com.sleepy.manager.generation.service.IThirdPartUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 三方账号Controller
 * 
 * @author sleepyocean
 * @date 2022-02-22
 */
@RestController
@RequestMapping("/generation/third-part-user")
public class ThirdPartUserController extends BaseController
{
    @Autowired
    private IThirdPartUserService thirdPartUserService;

    /**
     * 查询三方账号列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(ThirdPartUser thirdPartUser)
    {
        startPage();
        List<ThirdPartUser> list = thirdPartUserService.selectThirdPartUserList(thirdPartUser);
        return getDataTable(list);
    }

    /**
     * 导出三方账号列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @Log(title = "三方账号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ThirdPartUser thirdPartUser)
    {
        List<ThirdPartUser> list = thirdPartUserService.selectThirdPartUserList(thirdPartUser);
        ExcelUtil<ThirdPartUser> util = new ExcelUtil<ThirdPartUser>(ThirdPartUser.class);
        util.exportExcel(response, list, "三方账号数据");
    }

    /**
     * 获取三方账号详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(thirdPartUserService.selectThirdPartUserById(id));
    }

    /**
     * 新增三方账号
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "三方账号", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ThirdPartUser thirdPartUser)
    {
        return toAjax(thirdPartUserService.insertThirdPartUser(thirdPartUser));
    }

    /**
     * 修改三方账号
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "三方账号", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ThirdPartUser thirdPartUser)
    {
        return toAjax(thirdPartUserService.updateThirdPartUser(thirdPartUser));
    }

    /**
     * 删除三方账号
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "三方账号", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(thirdPartUserService.deleteThirdPartUserByIds(StringUtils.join(ids, ",")));
    }
}
