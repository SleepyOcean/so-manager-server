package com.sleepy.manager.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sleepy.manager.common.annotation.Excel;
import com.sleepy.manager.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 图床对象 so_gallery
 *
 * @author sleepyocean
 * @date 2022-05-02
 */
public class Gallery extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private String id;

    /**
     * 图片大小
     */
    @Excel(name = "图片大小")
    private String size;

    /**
     * 图片格式
     */
    @Excel(name = "图片格式")
    private String format;

    /**
     * 图片相对路径
     */
    @Excel(name = "图片相对路径")
    private String path;

    /**
     * 图片分辨率
     */
    @Excel(name = "图片分辨率")
    private String resolution;

    /**
     * 图片标题
     */
    @Excel(name = "图片标题")
    private String title;

    /**
     * 图片简述
     */
    @Excel(name = "图片简述")
    private String description;

    /**
     * 图片标签
     */
    @Excel(name = "图片标签")
    private String tag;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date uploadTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("size", getSize())
                .append("format", getFormat())
                .append("path", getPath())
                .append("resolution", getResolution())
                .append("title", getTitle())
                .append("description", getDescription())
                .append("tag", getTag())
                .append("createTime", getCreateTime())
                .append("uploadTime", getUploadTime())
                .toString();
    }
}
