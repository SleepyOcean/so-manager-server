package com.sleepy.manager.generation.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sleepy.manager.common.annotation.Excel;
import com.sleepy.manager.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * app版本对象 app_version
 *
 * @author SleepyOcean
 * @date 2021-12-18
 */
public class AppVersion extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    private Long id;

    /**
     * app名称
     */
    @Excel(name = "app名称")
    private String appName;

    /**
     * app版本
     */
    @Excel(name = "app版本")
    private Long appVersion;

    /**
     * 强制更新
     */
    @Excel(name = "强制更新")
    private Long forceUpdate;

    /**
     * 兼容版本
     */
    @Excel(name = "兼容版本")
    private Long compatibleVersion;

    /**
     * 描述
     */
    @Excel(name = "描述")
    private String description;

    /**
     * 下载链接
     */
    @Excel(name = "下载链接")
    private String downloadLink;

    /**
     * 状态, 0-Deleted, 1-Normal
     */
    @Excel(name = "状态, 0-Deleted, 1-Normal")
    private Long status;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Long getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(Long appVersion) {
        this.appVersion = appVersion;
    }

    public Long getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Long forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public Long getCompatibleVersion() {
        return compatibleVersion;
    }

    public void setCompatibleVersion(Long compatibleVersion) {
        this.compatibleVersion = compatibleVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("appName", getAppName())
                .append("appVersion", getAppVersion())
                .append("forceUpdate", getForceUpdate())
                .append("compatibleVersion", getCompatibleVersion())
                .append("description", getDescription())
                .append("downloadLink", getDownloadLink())
                .append("createBy", getCreateBy())
                .append("status", getStatus())
                .append("createdAt", getCreatedAt())
                .append("updatedAt", getUpdatedAt())
                .toString();
    }
}
