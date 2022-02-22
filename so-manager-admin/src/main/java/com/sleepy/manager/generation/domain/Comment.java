package com.sleepy.manager.generation.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sleepy.manager.common.annotation.Excel;
import com.sleepy.manager.common.core.domain.BaseEntity;
import com.sleepy.manager.common.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 评论对象 comment
 *
 * @author SleepyOcean
 * @date 2021-11-26
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"params"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comment extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * custom query params
     */
    private Date createdAtStart = DateUtils.parseDate("1970-01-01");
    private Date createdAtEnd = new Date();
    private Long[] articleIds;

    /**
     * custom return
     */
    private Long commentCount;

    /**
     * id
     */
    private Long id;

    /**
     * 评论内容
     */
    @Excel(name = "评论内容")
    private String content;

    /**
     * 评论用户id
     */
    @Excel(name = "评论用户id")
    private Long createdBy;

    /**
     * 回复用户id
     */
    @Excel(name = "回复用户id")
    private Long replyTo;

    /**
     * 评论文章id
     */
    @Excel(name = "评论文章id")
    private Long articleId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "创建时间" , width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    private Date createdAt;

    /**
     * 状态, 0-Deleted, 1-Normal
     */
    @Excel(name = "状态, 0-Deleted, 1-Normal")
    private Long status;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    private Date updatedAt;

    public Comment(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("content", getContent())
                .append("createdBy", getCreatedBy())
                .append("replyTo", getReplyTo())
                .append("articleId", getArticleId())
                .append("createdAt", getCreatedAt())
                .append("status", getStatus())
                .append("updatedAt", getUpdatedAt())
                .toString();
    }
}
