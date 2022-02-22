package com.sleepy.manager.generation.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sleepy.manager.common.annotation.Excel;
import com.sleepy.manager.common.core.domain.BaseEntity;
import com.sleepy.manager.common.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 文章对象 article
 *
 * @author SleepyOcean
 * @date 2021-11-26
 */
@Getter
@Setter
@JsonIgnoreProperties({"params", "createdAtStart", "createdAtEnd"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Article extends BaseEntity {
    private static final long serialVersionUID = 1L;
    /**
     * custom query params
     */
    private Date createdAtStart = DateUtils.parseDate("1970-01-01");
    private Date createdAtEnd = new Date();
    private String[] tagIds;
    private String[] classIds;
    private Long categoryId;
    private Long topicId;
    private Long isSticky;
    private Long isAutomatic;

    /**
     * custom result property
     * order number
     */
    private Long orderNum;

    /**
     * custom result property
     * sort id
     */
    private Long sortId;

    /**
     * 自增id
     */
    private Long id;

    /**
     * 标题
     */
    @Excel(name = "标题")
    private String title;

    /**
     * 内容
     */
    @Excel(name = "内容")
    private String content;

    /**
     * 头图路径
     */
    @Excel(name = "头图路径")
    private String cover;

    /**
     * 视频路径
     */
    @Excel(name = "视频路径")
    private String video;

    /**
     * 文章来源
     */
    @Excel(name = "文章来源")
    private String source;

    /**
     * 文章类型，0-text，1-media，2-video
     */
    @Excel(name = "文章类型，0-text，1-media，2-video")
    private Long type;

    /**
     * 分类ID
     */
    @Excel(name = "分类ID")
    private Long classId;

    /**
     * 标签ID组，ID使用英文逗号分隔
     */
    @Excel(name = "标签ID组，ID使用英文逗号分隔")
    private String tagId;

    /**
     * 浏览量
     */
    @Excel(name = "浏览量")
    private Long viewed;

    /**
     * 点赞数量
     */
    @Excel(name = "点赞数量")
    private Long likeNum;

    /**
     * 收藏数量
     */
    @Excel(name = "收藏数量")
    private Long favoriteNum;

    /**
     * 简介
     */
    @Excel(name = "简介")
    private String shortText;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "创建时间" , width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    private Date createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "更新时间" , width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    private Date updatedAt;

    /**
     * 创建人
     */
    @Excel(name = "创建人")
    private Long createdBy;

    /**
     * 文章状态, 0-Deleted, 1-Normal
     */
    @Excel(name = "文章状态, 0-Deleted, 1-Normal")
    private Long status;

    /**
     * 手动排序，ASC
     */
    @Excel(name = "手动排序，ASC")
    private Long homeOrder;

    /**
     * 手动排序，ASC
     */
    @Excel(name = "手动排序，ASC")
    private Long topicHotspotOrder;

    /**
     * 手动排序，ASC
     */
    @Excel(name = "手动排序，ASC")
    private Long cateHotspotOrder;

    /**
     * 手动排序，ASC
     */
    @Excel(name = "手动排序，ASC")
    private Long topicOrder;

    /**
     * 手动排序，ASC
     */
    @Excel(name = "手动排序，ASC")
    private Long cateOrder;

    private String[] ids;
    private String[] excludeIds;

    private Integer sortType;

    public Article() {
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("title", getTitle())
                .append("content", getContent())
                .append("cover", getCover())
                .append("video", getVideo())
                .append("source", getSource())
                .append("type", getType())
                .append("classId", getClassId())
                .append("tagIds", getTagIds())
                .append("tagId", getTagId())
                .append("viewed", getViewed())
                .append("likeNum", getLikeNum())
                .append("favoriteNum", getFavoriteNum())
                .append("shortText", getShortText())
                .append("createdAt", getCreatedAt())
                .append("updatedAt", getUpdatedAt())
                .append("createdBy", getCreatedBy())
                .append("status", getStatus())
                .append("orderNum", getOrderNum())
                .append("sortId", getSortId())
                .append("isSticky", getIsSticky())
                .toString();
    }
}
