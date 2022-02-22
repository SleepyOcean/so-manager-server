package com.sleepy.manager.generation.controller;

import com.sleepy.manager.common.annotation.Log;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.AjaxResult;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.enums.BusinessType;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.common.utils.poi.ExcelUtil;
import com.sleepy.manager.generation.domain.Topic;
import com.sleepy.manager.generation.service.ITopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 专题Controller
 * 
 * @author sleepyocean
 * @date 2022-02-22
 */
@RestController
@RequestMapping("/generation/topic")
public class TopicController extends BaseController
{
    @Autowired
    private ITopicService topicService;

    /**
     * 查询专题列表
     */
    @PreAuthorize("@ss.hasPermi('system:topic:list')")
    @GetMapping("/list")
    public TableDataInfo list(Topic topic)
    {
        startPage();
        List<Topic> list = topicService.selectTopicList(topic);
        return getDataTable(list);
    }

    /**
     * 导出专题列表
     */
    @PreAuthorize("@ss.hasPermi('system:topic:export')")
    @Log(title = "专题", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Topic topic)
    {
        List<Topic> list = topicService.selectTopicList(topic);
        ExcelUtil<Topic> util = new ExcelUtil<Topic>(Topic.class);
        util.exportExcel(response, list, "专题数据");
    }

    /**
     * 获取专题详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:topic:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(topicService.selectTopicById(id));
    }

    /**
     * 新增专题
     */
    @PreAuthorize("@ss.hasPermi('system:topic:add')")
    @Log(title = "专题", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Topic topic)
    {
        return toAjax(topicService.insertTopic(topic));
    }

    /**
     * 修改专题
     */
    @PreAuthorize("@ss.hasPermi('system:topic:edit')")
    @Log(title = "专题", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Topic topic)
    {
        return toAjax(topicService.updateTopic(topic));
    }

    /**
     * 删除专题
     */
    @PreAuthorize("@ss.hasPermi('system:topic:remove')")
    @Log(title = "专题", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(topicService.deleteTopicByIds(StringUtils.join(ids, ",")));
    }
}
