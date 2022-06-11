package com.sleepy.manager.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sleepy.manager.common.annotation.Excel;
import com.sleepy.manager.common.core.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 稍后阅读对象 article_reading
 *
 * @author sleepyocean
 * @date 2022-06-08
 */
@Getter
@Setter
public class ArticleReading extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    private Long id;

    /**
     * url的MD5值
     */
    @Excel(name = "url的MD5值")
    private String md5;

    /**
     * 文章标题
     */
    @Excel(name = "文章标题")
    private String title;

    /**
     * host
     */
    @Excel(name = "host")
    private String host;

    /**
     * 文章源地址
     */
    @Excel(name = "文章源地址")
    private String source;

    /**
     * 文章内容
     */
    @Excel(name = "文章内容")
    private String content;

    /**
     * 文章简介
     */
    @Excel(name = "文章简介")
    private String intro;

    /**
     * 文章封面
     */
    @Excel(name = "文章封面")
    private String cover;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String note;

    /**
     * 阅读状态, 0-未阅读, 1-正在阅读, 2-已阅读
     */
    @Excel(name = "阅读状态, 0-未阅读, 1-正在阅读, 2-已阅读")
    private Long readingStatus;

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
                .append("md5", getMd5())
                .append("title", getTitle())
                .append("host", getHost())
                .append("source", getSource())
                .append("content", getContent())
                .append("intro", getIntro())
                .append("cover", getCover())
                .append("note", getNote())
                .append("readingStatus", getReadingStatus())
                .append("createdAt", getCreatedAt())
                .append("updatedAt", getUpdatedAt())
                .append("status", getStatus())
                .toString();
    }
}
