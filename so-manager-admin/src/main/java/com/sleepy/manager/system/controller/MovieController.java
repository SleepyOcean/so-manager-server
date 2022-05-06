package com.sleepy.manager.system.controller;

import com.alibaba.fastjson.JSON;
import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.system.domain.Movie;
import com.sleepy.manager.system.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

import static com.sleepy.manager.common.utils.DateUtils.dateTime;
import static com.sleepy.manager.common.utils.StringUtils.format;

/**
 * 电影库Controller
 *
 * @author sleepyocean
 * @date 2022-04-30
 */
@RestController
@RequestMapping("/system/movie")
public class MovieController extends BaseController {
    @Autowired
    private IMovieService movieService;

    @Value("${so-manager-server.galleryPrefix}")
    private String galleryServerUrlPrefix;

    /**
     * 查询电影库列表
     */
    @PreAuthorize("@ss.hasPermi('system:movie:list')")
    @GetMapping("/list")
    public TableDataInfo list(Movie movie) {
        startPage();
        List<Movie> list = movieService.selectMovieList(movie);
        TableDataInfo data = getDataTable(list);
        data.setRows(((List<Movie>) data.getRows()).stream().map(r -> new AssembledData.Builder()
                .putAll(r)
                .put("coverUrl", format("{}{}{}", galleryServerUrlPrefix, "movie-cover/", r.getId()))
                .put("fanartUrl", format("{}{}{}", galleryServerUrlPrefix, "movie-fanart/", r.getId()))
                .put("updateDate", dateTime(r.getUpdatedAt()))
                .put("createDate", dateTime(r.getCreatedAt()))
                .put("detail", new AssembledData.Builder().putAll(JSON.parseObject(r.getDetail())).build())
                .build()).collect(Collectors.toList()));
        return data;
    }

    /**
     * 导出电影库列表
     */
    @PreAuthorize("@ss.hasPermi('system:movie:export')")
    @Log(title = "电影库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Movie movie) {
        List<Movie> list = movieService.selectMovieList(movie);
        ExcelUtil<Movie> util = new ExcelUtil<Movie>(Movie.class);
        util.exportExcel(response, list, "电影库数据");
    }

    /**
     * 获取电影库详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:movie:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(movieService.selectMovieById(id));
    }

    /**
     * 新增电影库
     */
    @PreAuthorize("@ss.hasPermi('system:movie:add')")
    @Log(title = "电影库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Movie movie) {
        return toAjax(movieService.insertMovie(movie));
    }

    /**
     * 修改电影库
     */
    @PreAuthorize("@ss.hasPermi('system:movie:edit')")
    @Log(title = "电影库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Movie movie) {
        return toAjax(movieService.updateMovie(movie));
    }

    /**
     * 删除电影库
     */
    @PreAuthorize("@ss.hasPermi('system:movie:remove')")
    @Log(title = "电影库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(movieService.deleteMovieByIds(ids));
    }
}
