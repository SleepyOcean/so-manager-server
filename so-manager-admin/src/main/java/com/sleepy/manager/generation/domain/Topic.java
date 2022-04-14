package com.sleepy.manager.generation.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sleepy.manager.common.annotation.Excel;
import com.sleepy.manager.common.core.domain.BaseEntity;
import com.sleepy.manager.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 专题对象 topic
 *
 * @author SleepyOcean
 * @date 2021-11-26
 */
@Getter
@Setter
@JsonIgnoreProperties({"params"})
public class Topic extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * custom query params
     */
    private Date createdAtStart = DateUtils.parseDate("1970-01-01");
    private Date createdAtEnd = new Date();

    /**
     * id
     */
    private Long id;

    /**
     * 栏目
     */
    @Excel(name = "栏目")
    private Long categoryId;

    /**
     * 标题
     */
    @Excel(name = "标题")
    private String title;

    /**
     * 栏目图标
     */
    @Excel(name = "栏目图标")
    private String icon;

    /**
     * 栏目封面
     */
    @Excel(name = "栏目封面")
    private String cover;

    /**
     * 标签ID组，ID使用英文逗号分隔
     */
    @Excel(name = "标签ID组，ID使用英文逗号分隔")
    private String tagId;

    /**
     * 分类ID组，ID使用英文逗号分隔
     */
    @Excel(name = "分类ID组，ID使用英文逗号分隔")
    private String classId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    private Date createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    private Date updatedAt;

    /**
     * 手动排序，DESC
     */
    @Excel(name = "手动排序，DESC")
    private Long sort;

    /**
     * 状态, 0-Deleted, 1-Normal
     */
    @Excel(name = "状态, 0-Deleted, 1-Normal")
    private Long status;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("categoryId", getCategoryId())
                .append("title", getTitle())
                .append("icon", getIcon())
                .append("cover", getCover())
                .append("tagId", getTagId())
                .append("classId", getClassId())
                .append("createdAt", getCreatedAt())
                .append("updatedAt", getUpdatedAt())
                .append("sort", getSort())
                .append("status", getStatus())
                .toString();
    }
}
