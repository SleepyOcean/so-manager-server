package com.sleepy.manager.blog.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sleepy.manager.generation.domain.Category;
import com.sleepy.manager.generation.domain.Classification;
import com.sleepy.manager.generation.domain.Tag;
import lombok.*;

import java.util.Date;

/**
 * @author gehoubao
 * @create 2021-10-28 16:01
 **/
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonVO {
    /**
     * user
     */
    private Long userId;

    /**
     * login
     */
    private String phone;
    private String password;
    private String token;
    private String thirdPartId;
    private String loginType;
    private String unionIdCache;
    private String userCode;
    private String wechatOpenId;

    /**
     * register
     */
    private String verifyCode;
    private String nickName;

    /**
     * profile
     */
    private String avatar;
    private String gender;

    /**
     * category
     */
    private Long id;
    private String title;
    private JSONObject bgColor;
    private JSONObject textColor;
    private String icon;
    private String banner;
    private int sort;
    private String tagId;
    private String classId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    /**
     * constant information
     *
     * @return
     */
    private String appInfo;
    private String homeBanner;
    private String privatePolicy;
    private String servicePolicy;

    public Category toCategory() {
        Category category = JSON.parseObject(JSON.toJSONString(this), Category.class);
        return category;
    }

    public Tag toTag() {
        Tag tag = JSON.parseObject(JSON.toJSONString(this), Tag.class);
        return tag;
    }

    public Classification toClassification() {
        Classification klass = JSON.parseObject(JSON.toJSONString(this), Classification.class);
        return klass;
    }
}