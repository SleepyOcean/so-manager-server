package com.sleepy.manager.generation.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.generation.domain.UserFav;
import com.sleepy.manager.generation.service.IUserFavService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户收藏Controller
 * 
 * @author sleepyocean
 * @date 2022-02-22
 */
@RestController
@RequestMapping("/generation/fav")
public class UserFavController extends BaseController
{
    @Autowired
    private IUserFavService userFavService;

    /**
     * 查询用户收藏列表
     */
    @PreAuthorize("@ss.hasPermi('system:fav:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserFav userFav)
    {
        startPage();
        List<UserFav> list = userFavService.selectUserFavList(userFav);
        return getDataTable(list);
    }

    /**
     * 导出用户收藏列表
     */
    @PreAuthorize("@ss.hasPermi('system:fav:export')")
    @Log(title = "用户收藏", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserFav userFav)
    {
        List<UserFav> list = userFavService.selectUserFavList(userFav);
        ExcelUtil<UserFav> util = new ExcelUtil<UserFav>(UserFav.class);
        util.exportExcel(response, list, "用户收藏数据");
    }

    /**
     * 获取用户收藏详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:fav:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(userFavService.selectUserFavById(id));
    }

    /**
     * 新增用户收藏
     */
    @PreAuthorize("@ss.hasPermi('system:fav:add')")
    @Log(title = "用户收藏", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserFav userFav)
    {
        return toAjax(userFavService.insertUserFav(userFav));
    }

    /**
     * 修改用户收藏
     */
    @PreAuthorize("@ss.hasPermi('system:fav:edit')")
    @Log(title = "用户收藏", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserFav userFav)
    {
        return toAjax(userFavService.updateUserFav(userFav));
    }

    /**
     * 删除用户收藏
     */
    @PreAuthorize("@ss.hasPermi('system:fav:remove')")
    @Log(title = "用户收藏", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(userFavService.deleteUserFavByIds(StringUtils.join(ids, ",")));
    }
}
