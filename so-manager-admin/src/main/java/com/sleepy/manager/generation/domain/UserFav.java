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
 * 用户收藏对象 user_fav
 *
 * @author SleepyOcean
 * @date 2021-11-26
 */
@Getter
@Setter
public class UserFav extends BaseEntity {
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
     * 用户id
     */
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 文章id
     */
    @Excel(name = "文章id")
    private Long articleId;

    /**
     * 状态, 0-Deleted, 1-Normal
     */
    @Excel(name = "状态, 0-Deleted, 1-Normal")
    private Long status;

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
    @Excel(name = "更新时间" , width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    private Date updatedAt;

    public UserFav() {
    }

    public UserFav(Long userId) {
        this.userId = userId;
    }

    public UserFav(Long articleId, Long userId) {
        this.articleId = articleId;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("articleId", getArticleId())
                .append("status", getStatus())
                .append("createdAt", getCreatedAt())
                .append("updatedAt", getUpdatedAt())
                .toString();
    }
}
