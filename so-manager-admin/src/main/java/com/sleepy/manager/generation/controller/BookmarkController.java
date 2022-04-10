package com.sleepy.manager.generation.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.generation.domain.Bookmark;
import com.sleepy.manager.generation.service.IBookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 网页书签Controller
 *
 * @author sleepyocean
 * @date 2022-04-10
 */
@RestController
@RequestMapping("/generation/bookmark")
public class BookmarkController extends BaseController {
    @Autowired
    private IBookmarkService bookmarkService;

    /**
     * 查询网页书签列表
     */
    @PreAuthorize("@ss.hasPermi('generation:bookmark:list')")
    @GetMapping("/list")
    public TableDataInfo list(Bookmark bookmark) {
        startPage();
        List<Bookmark> list = bookmarkService.selectBookmarkList(bookmark);
        return getDataTable(list);
    }

    /**
     * 导出网页书签列表
     */
    @PreAuthorize("@ss.hasPermi('generation:bookmark:export')")
    @Log(title = "网页书签", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Bookmark bookmark) {
        List<Bookmark> list = bookmarkService.selectBookmarkList(bookmark);
        ExcelUtil<Bookmark> util = new ExcelUtil<Bookmark>(Bookmark.class);
        util.exportExcel(response, list, "网页书签数据");
    }

    /**
     * 获取网页书签详细信息
     */
    @PreAuthorize("@ss.hasPermi('generation:bookmark:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(bookmarkService.selectBookmarkById(id));
    }

    /**
     * 新增网页书签
     */
    @PreAuthorize("@ss.hasPermi('generation:bookmark:add')")
    @Log(title = "网页书签", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Bookmark bookmark) {
        return toAjax(bookmarkService.insertBookmark(bookmark));
    }

    /**
     * 修改网页书签
     */
    @PreAuthorize("@ss.hasPermi('generation:bookmark:edit')")
    @Log(title = "网页书签", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Bookmark bookmark) {
        return toAjax(bookmarkService.updateBookmark(bookmark));
    }

    /**
     * 删除网页书签
     */
    @PreAuthorize("@ss.hasPermi('generation:bookmark:remove')")
    @Log(title = "网页书签", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(bookmarkService.deleteBookmarkByIds(ids));
    }
}
