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
 * 分类对象 classification
 *
 * @author SleepyOcean
 * @date 2021-11-26
 */
@Getter
@Setter
public class Classification extends BaseEntity {
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
     * 标题
     */
    @Excel(name = "标题")
    private String title;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("title", getTitle())
                .append("createdAt", getCreatedAt())
                .append("updatedAt", getUpdatedAt())
                .append("status", getStatus())
                .toString();
    }
}
