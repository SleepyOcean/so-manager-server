package com.sleepy.manager.generation.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.generation.domain.UserLike;
import com.sleepy.manager.generation.service.IUserLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户点赞Controller
 * 
 * @author sleepyocean
 * @date 2022-02-22
 */
@RestController
@RequestMapping("/generation/like")
public class UserLikeController extends BaseController
{
    @Autowired
    private IUserLikeService userLikeService;

    /**
     * 查询用户点赞列表
     */
    @PreAuthorize("@ss.hasPermi('system:like:list')")
    @GetMapping("/list")
    public TableDataInfo list(UserLike userLike)
    {
        startPage();
        List<UserLike> list = userLikeService.selectUserLikeList(userLike);
        return getDataTable(list);
    }

    /**
     * 导出用户点赞列表
     */
    @PreAuthorize("@ss.hasPermi('system:like:export')")
    @Log(title = "用户点赞", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserLike userLike)
    {
        List<UserLike> list = userLikeService.selectUserLikeList(userLike);
        ExcelUtil<UserLike> util = new ExcelUtil<UserLike>(UserLike.class);
        util.exportExcel(response, list, "用户点赞数据");
    }

    /**
     * 获取用户点赞详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:like:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(userLikeService.selectUserLikeById(id));
    }

    /**
     * 新增用户点赞
     */
    @PreAuthorize("@ss.hasPermi('system:like:add')")
    @Log(title = "用户点赞", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody UserLike userLike)
    {
        return toAjax(userLikeService.insertUserLike(userLike));
    }

    /**
     * 修改用户点赞
     */
    @PreAuthorize("@ss.hasPermi('system:like:edit')")
    @Log(title = "用户点赞", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody UserLike userLike)
    {
        return toAjax(userLikeService.updateUserLike(userLike));
    }

    /**
     * 删除用户点赞
     */
    @PreAuthorize("@ss.hasPermi('system:like:remove')")
    @Log(title = "用户点赞", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(userLikeService.deleteUserLikeByIds(StringUtils.join(ids, ",")));
    }
}
