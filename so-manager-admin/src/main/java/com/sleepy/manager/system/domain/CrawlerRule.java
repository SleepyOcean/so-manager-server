package com.sleepy.manager.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sleepy.manager.common.annotation.Excel;
import com.sleepy.manager.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 爬虫规则对象 crawler_rule
 *
 * @author sleepyocean
 * @date 2022-06-03
 */
public class CrawlerRule extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    private Long id;

    /**
     * 规则名称
     */
    @Excel(name = "规则名称")
    private String name;

    /**
     * host
     */
    @Excel(name = "host")
    private String host;

    /**
     * 爬取类型, 默认 1-文章
     */
    @Excel(name = "爬取类型, 默认 1-文章")
    private Long type;

    /**
     * 爬取目标规则
     */
    @Excel(name = "爬取目标规则")
    private String targetRule;

    /**
     * 剔除元素规则
     */
    @Excel(name = "剔除元素规则")
    private String excludeRule;

    /**
     * 美化元素规则
     */
    @Excel(name = "美化元素规则")
    private String beautifyRule;

    /**
     * 封面
     */
    @Excel(name = "封面")
    private String cover;

    /**
     * 备注
     */
    @Excel(name = "备注")
    private String note;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getTargetRule() {
        return targetRule;
    }

    public void setTargetRule(String targetRule) {
        this.targetRule = targetRule;
    }

    public String getExcludeRule() {
        return excludeRule;
    }

    public void setExcludeRule(String excludeRule) {
        this.excludeRule = excludeRule;
    }

    public String getBeautifyRule() {
        return beautifyRule;
    }

    public void setBeautifyRule(String beautifyRule) {
        this.beautifyRule = beautifyRule;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
                .append("name", getName())
                .append("host", getHost())
                .append("type", getType())
                .append("targetRule", getTargetRule())
                .append("excludeRule", getExcludeRule())
                .append("beautifyRule", getBeautifyRule())
                .append("cover", getCover())
                .append("note", getNote())
                .append("createdAt", getCreatedAt())
                .append("updatedAt", getUpdatedAt())
                .append("status", getStatus())
                .toString();
    }
}
