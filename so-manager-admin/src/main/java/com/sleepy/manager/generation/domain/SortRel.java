package com.sleepy.manager.generation.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sleepy.manager.common.annotation.Excel;
import com.sleepy.manager.common.core.domain.BaseEntity;
import com.sleepy.manager.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 分类对象 sort_rel
 *
 * @author SleepyOcean
 * @date 2021-11-26
 */
@Getter
@Setter
public class SortRel extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * custom query params
     */
    private Date createdAtStart = DateUtils.parseDate("1970-01-01");
    private Date createdAtEnd = new Date();
    private long sortType;

    /**
     * id
     */
    private Long id;

    /**
     * 文章
     */
    @Excel(name = "文章")
    private Long articleId;

    /**
     * 栏目
     */
    @Excel(name = "栏目")
    private Long categoryId;

    /**
     * 专题
     */
    @Excel(name = "专题")
    private Long topicId;

    /**
     * 是否置顶， 0-不置顶，1-置顶
     */
    @Excel(name = "是否置顶， 0-不置顶，1-置顶")
    private Long isSticky;

    /**
     * 手动排序，ASC
     */
    @Excel(name = "手动排序，ASC")
    private Long sort;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    /**
     * 状态, 0-Deleted, 1-Normal
     */
    @Excel(name = "状态, 0-Deleted, 1-Normal")
    private Long status;

    /**
     * 自动推荐，0-手动，1-自动
     */
    @Excel(name = "自动推荐，0-手动，1-自动")
    private Long isAutomatic;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("articleId", getArticleId())
                .append("categoryId", getCategoryId())
                .append("topicId", getTopicId())
                .append("isSticky", getIsSticky())
                .append("sort", getSort())
                .append("createdAt", getCreatedAt())
                .append("updatedAt", getUpdatedAt())
                .append("status", getStatus())
                .append("isAutomatic", getIsAutomatic())
                .toString();
    }
}
