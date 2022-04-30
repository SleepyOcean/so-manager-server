package com.sleepy.manager.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sleepy.manager.common.annotation.Excel;
import com.sleepy.manager.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 电影库对象 movie
 *
 * @author sleepyocean
 * @date 2022-04-30
 */
public class Movie extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    private Long id;

    /**
     * imdb ID
     */
    @Excel(name = "imdb ID")
    private String imdbid;

    /**
     * 标题
     */
    @Excel(name = "标题")
    private String title;

    /**
     * 上映年份
     */
    @Excel(name = "上映年份")
    private Long year;

    /**
     * 视频地址
     */
    @Excel(name = "视频地址")
    private String address;

    /**
     * 介绍
     */
    @Excel(name = "介绍")
    private String intro;

    /**
     * 封面
     */
    @Excel(name = "封面")
    private String cover;

    /**
     * 横板封面
     */
    @Excel(name = "横板封面")
    private String headCover;

    /**
     * 详情数据集
     */
    @Excel(name = "详情数据集")
    private String detail;

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
     * 创建人
     */
    @Excel(name = "创建人")
    private Long createdBy;

    /**
     * 状态, 0-Deleted, 1-Normal
     */
    @Excel(name = "状态, 0-Deleted, 1-Normal")
    private Long status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImdbid() {
        return imdbid;
    }

    public void setImdbid(String imdbid) {
        this.imdbid = imdbid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getHeadCover() {
        return headCover;
    }

    public void setHeadCover(String headCover) {
        this.headCover = headCover;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("imdbid", getImdbid())
                .append("title", getTitle())
                .append("year", getYear())
                .append("address", getAddress())
                .append("intro", getIntro())
                .append("cover", getCover())
                .append("headCover", getHeadCover())
                .append("detail", getDetail())
                .append("createdAt", getCreatedAt())
                .append("updatedAt", getUpdatedAt())
                .append("createdBy", getCreatedBy())
                .append("status", getStatus())
                .toString();
    }
}
