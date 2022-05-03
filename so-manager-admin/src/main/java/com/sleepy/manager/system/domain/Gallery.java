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
 * 图床对象 so_gallery
 *
 * @author sleepyocean
 * @date 2022-05-02
 */
@Getter
@Setter
public class Gallery extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private String base64;

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
