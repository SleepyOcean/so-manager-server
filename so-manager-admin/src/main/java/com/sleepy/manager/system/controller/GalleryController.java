package com.sleepy.manager.system.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.main.common.AssembledData;
import com.sleepy.manager.system.domain.Gallery;
import com.sleepy.manager.system.service.IGalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static com.sleepy.manager.common.utils.DateUtils.dateTime;

/**
 * 图床Controller
 *
 * @author sleepyocean
 * @date 2022-05-02
 */
@RestController
@RequestMapping("/system/gallery")
public class GalleryController extends BaseController {
    @Autowired
    private IGalleryService galleryService;

    @Value("${so-manager-server.galleryPrefix}")
    private String galleryServerUrlPrefix;

    /**
     * 查询图床列表
     */
    @PreAuthorize("@ss.hasPermi('system:gallery:list')")
    @GetMapping("/list")
    public TableDataInfo list(Gallery gallery) {
        startPage();
        List<Gallery> list = galleryService.selectGalleryList(gallery);
        TableDataInfo data = getDataTable(list);
        data.setRows(((List<Gallery>) data.getRows()).stream().map(r -> new AssembledData.Builder()
                .putAll(r)
                .put("url", galleryServerUrlPrefix + r.getId())
                .put("uploadDate", dateTime(r.getUploadTime()))
                .put("createDate", dateTime(r.getCreateTime()))
                .build()).collect(Collectors.toList()));
        return data;
    }

    /**
     * 导出图床列表
     */
    @PreAuthorize("@ss.hasPermi('system:gallery:export')")
    @Log(title = "图床", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Gallery gallery) {
        List<Gallery> list = galleryService.selectGalleryList(gallery);
        ExcelUtil<Gallery> util = new ExcelUtil<Gallery>(Gallery.class);
        util.exportExcel(response, list, "图床数据");
    }

    /**
     * 获取图床详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:gallery:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return AjaxResult.success(galleryService.selectGalleryById(id));
    }

    /**
     * 新增图床
     */
    @PreAuthorize("@ss.hasPermi('system:gallery:add')")
    @Log(title = "图床", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Gallery gallery) {
        return toAjax(galleryService.insertGallery(gallery));
    }

    /**
     * 修改图床
     */
    @PreAuthorize("@ss.hasPermi('system:gallery:edit')")
    @Log(title = "图床", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Gallery gallery) {
        return toAjax(galleryService.updateGallery(gallery));
    }

    /**
     * 删除图床
     */
    @PreAuthorize("@ss.hasPermi('system:gallery:remove')")
    @Log(title = "图床", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(galleryService.deleteGalleryByIds(ids));
    }
}
