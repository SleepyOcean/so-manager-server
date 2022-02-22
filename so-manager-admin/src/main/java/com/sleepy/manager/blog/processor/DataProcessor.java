package com.sleepy.manager.blog.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.blog.common.CommonVO;
import com.sleepy.manager.common.core.domain.entity.SysUser;
import com.sleepy.manager.common.utils.DateUtils;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.generation.domain.*;
import com.sleepy.manager.generation.service.*;
import com.sleepy.manager.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据处理类
 *
 * @author gehoubao
 * @create 2021-11-03 11:50
 **/
@Component
public class DataProcessor {

    @Autowired
    IArticleService articleService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    ITopicService topicService;

    @Autowired
    ITagService tagService;

    @Autowired
    ICommentService commentService;

    @Autowired
    IThirdPartUserService thirdPartUserService;

    @Autowired
    IClassificationService classificationService;

    public AssembledData.Builder process(Article article) {
        AssembledData.Builder builder = createAssembledDataBuilder(article);

        Long articleCreatorId = article.getCreatedBy();
        SysUser user = userService.selectUserById(articleCreatorId);
        if (null != user) {
            builder.put("createdBy", simplify(user));
        }

        Comment comment = new Comment();
        comment.setArticleIds(new Long[]{article.getId()});
        List<Comment> comments = commentService.countComment(comment);
        builder.put("commentCount", null != comments && comments.size() > 0 ? comments.get(0).getCommentCount() : 0);

        String tagIdStr = article.getTagId();
        if (!StringUtils.isEmpty(tagIdStr)) {
            String[] tagIds = tagIdStr.split(",");
            JSONArray array = new JSONArray();
            Arrays.stream(tagIds).forEach(tagId -> {
                Tag tag = tagService.selectTagById(Long.parseLong(tagId));
                if (tag != null) {
                    array.add(convertTagForResponse(tag).build());
                }
            });
            builder.put("tags", array);
        }

        if (null != article.getClassId()) {
            Classification classification = classificationService.selectClassificationById(article.getClassId());
            builder.put("class", classification);
        }
        builder.source().remove("classId");

        return builder;
    }

    public List<AssembledData> processArticles(List<Article> articles) {
        return articles.stream().map(article -> {
            AssembledData.Builder builder = process(article);
            builder.source().remove("content");
            return builder.build();
        }).collect(Collectors.toList());
    }

    public AssembledData.Builder process(Topic topic) {
        AssembledData.Builder builder = createAssembledDataBuilder(topic);

        if (StringUtils.isNotEmpty(topic.getClassId())) {
            builder.put("class", Arrays.asList(topic.getClassId().split(","))
                    .stream().map(classId -> classificationService.selectClassificationById(Long.valueOf(classId))).collect(Collectors.toList()));
        }
        builder.source().remove("classId");

        return builder;
    }

    public AssembledData.Builder process(Comment comment) {
        AssembledData.Builder builder = createAssembledDataBuilder(comment);

        Long commentCreatorId = comment.getCreatedBy();
        SysUser creatorUser = userService.selectUserById(commentCreatorId);
        if (null != creatorUser) {
            builder.put("createdBy", simplify(creatorUser));
        }

        Long commentReplyToId = comment.getReplyTo();
        SysUser replyToUser = userService.selectUserById(commentReplyToId);
        if (null != replyToUser) {
            builder.put("replyTo", simplify(replyToUser));
        }
        Long articleId = comment.getArticleId();
        Article article = articleService.selectArticleById(articleId);
        if (null != article) {
            builder.put("article", process(article).build());
            builder.source().remove("articleId");
        }
        Long userId = comment.getCreatedBy();
        SysUser user = userService.selectUserById(userId);
        builder.put("user", enrich(user).build());
        return builder;
    }

    public AssembledData.Builder process(UserLike like) {
        AssembledData.Builder builder = createAssembledDataBuilder(like);

        Long userId = like.getUserId();
        SysUser user = userService.selectUserById(userId);
        builder.put("user", enrich(user).build());
        return builder;
    }

    public AssembledData.Builder process(UserFav fav) {
        AssembledData.Builder builder = createAssembledDataBuilder(fav);

        Long userId = fav.getUserId();
        SysUser user = userService.selectUserById(userId);
        builder.put("user", enrich(user).build());
        return builder;
    }

    public AssembledData.Builder process(Share share) {
        AssembledData.Builder builder = createAssembledDataBuilder(share);

        Long userId = share.getUserId();
        SysUser user = userService.selectUserById(userId);
        builder.put("user", enrich(user).build());
        return builder;
    }

    public List<AssembledData> processComments(List<Comment> comments) {
        return comments.stream().map(comment -> {
            return process(comment).build();
        }).collect(Collectors.toList());
    }

    public SysUser simplify(SysUser user) {
        SysUser simpleUser = new SysUser();
        simpleUser.setUserName(user.getUserName());
        simpleUser.setAvatar(user.getAvatar());
        simpleUser.setSex(user.getSex());
        simpleUser.setPhonenumber(user.getPhonenumber());
        simpleUser.setEmail(user.getEmail());
        return simpleUser;
    }

    public AssembledData.Builder enrich(SysUser user) {
        AssembledData.Builder builder = new AssembledData.Builder().putAll(user);

        if (null != builder.source().getLong("loginDate")) {
            builder.put("loginDate", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_mm, new Date(builder.source().getLong("loginDate"))));
        }
        if (null != builder.source().getLong("pwdUpdateDate")) {
            builder.put("pwdUpdateDate", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_mm, new Date(builder.source().getLong("pwdUpdateDate"))));
        }
        builder.put("createTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_mm, new Date(builder.source().getLong("createTime"))));

        String[] filterCols = new String[]{"params", "deptId", "parentId", "roleId", "salt", "delFlag", "dept", "roles", "roles", "postIds", "admin"};
        Arrays.asList(filterCols).forEach(col -> {
            if (null != builder.source().get(col)) {
                builder.source().remove(col);
            }
        });
        List<ThirdPartUser> thirdPartUsers = thirdPartUserService.selectThirdPartUserList(ThirdPartUser.builder().userId(user.getUserId()).build());
        if (!thirdPartUsers.isEmpty()) {
            builder.put("thirdPartInfoList", thirdPartUsers);
        }
        return builder;
    }

    public List<AssembledData> enrich(List<SysUser> users) {
        return users.stream().map(user -> {
            return enrich(user).build();
        }).collect(Collectors.toList());
    }

    public Category convertCategoryForDB(CommonVO vo) {
        Category category = vo.toCategory();
        if (!StringUtils.isEmpty(category.getBgColor())) {
            category.setBgColor(convertColorFromJsonToHex(category.getBgColor()));
        }
        if (!StringUtils.isEmpty(category.getTextColor())) {
            category.setTextColor(convertColorFromJsonToHex(category.getTextColor()));
        }
        return category;
    }

    public AssembledData.Builder convertCategoryForResponse(Category category) {
        AssembledData.Builder builder = createAssembledDataBuilder(category);

        if (!StringUtils.isEmpty(category.getBgColor())) {
            builder.put("bgColor", convertColorFromHexToJson(category.getBgColor()));
        }
        if (!StringUtils.isEmpty(category.getTextColor())) {
            builder.put("textColor", convertColorFromHexToJson(category.getTextColor()));
        }

        Topic topic = new Topic();
        topic.setCategoryId(category.getId());
        int topicCount = topicService.countTopics(topic);
        builder.put("topicCount", topicCount);

        if (StringUtils.isNotEmpty(category.getClassId())) {
            builder.put("class", Arrays.asList(category.getClassId().split(","))
                    .stream().map(classId -> classificationService.selectClassificationById(Long.valueOf(classId))).collect(Collectors.toList()));
        }
        builder.source().remove("classId");

        return builder;
    }

    public List<AssembledData> convertCategoryForResponse(List<Category> categories) {
        return categories.stream().map(category -> {
            return convertCategoryForResponse(category).build();
        }).collect(Collectors.toList());
    }

    /**
     * json construction example:
     * {
     * "a": 50,
     * "r": 0,
     * "g": 86,
     * "b": 255
     * }
     *
     * @param bgColor argb json object
     * @return rgba HEX string
     */
    private String convertColorFromJsonToHex(String bgColor) {
        JSONObject colorObj = JSON.parseObject(bgColor);
        StringBuilder colorHex = new StringBuilder();
        colorHex.append("#");
        colorHex.append(String.format("%02X", colorObj.getInteger("r")));
        colorHex.append(String.format("%02X", colorObj.getInteger("g")));
        colorHex.append(String.format("%02X", colorObj.getInteger("b")));
        colorHex.append(String.format("%02X", Double.valueOf(colorObj.getDouble("a") * 100).intValue()));

        return colorHex.toString();
    }

    /**
     * reverse HEX to JSON
     *
     * @param hex
     * @return
     */
    private JSONObject convertColorFromHexToJson(String hex) {
        JSONObject colorObj = new JSONObject();
        hex = hex.substring(1);
        colorObj.put("r", new BigInteger(hex.substring(0, 2), 16).intValue());
        colorObj.put("g", new BigInteger(hex.substring(2, 4), 16).intValue());
        colorObj.put("b", new BigInteger(hex.substring(4, 6), 16).intValue());
        colorObj.put("a", new BigInteger(hex.substring(6, 8), 16).doubleValue() / 100d);
        return colorObj;
    }

    public Tag convertTagForDB(CommonVO vo) {
        Tag tag = vo.toTag();
        if (!StringUtils.isEmpty(tag.getBgColor())) {
            tag.setBgColor(convertColorFromJsonToHex(tag.getBgColor()));
        }
        if (!StringUtils.isEmpty(tag.getTextColor())) {
            tag.setTextColor(convertColorFromJsonToHex(tag.getTextColor()));
        }
        return tag;
    }

    public Object convertTagForResponse(List<Tag> tags) {
        return tags.stream().map(tag -> {
            return convertTagForResponse(tag).build();
        }).collect(Collectors.toList());
    }

    private AssembledData.Builder convertTagForResponse(Tag tag) {
        AssembledData.Builder builder = createAssembledDataBuilder(tag);

        if (!StringUtils.isEmpty(tag.getBgColor())) {
            builder.put("bgColor", convertColorFromHexToJson(tag.getBgColor()));
        }
        if (!StringUtils.isEmpty(tag.getTextColor())) {
            builder.put("textColor", convertColorFromHexToJson(tag.getTextColor()));
        }
        return builder;
    }

    private AssembledData.Builder createAssembledDataBuilder(Object object) {
        AssembledData.Builder builder = new AssembledData.Builder().putAll(object);
        builder.put("createdAt", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_mm, new Date(builder.source().getLong("createdAt"))));
        builder.put("updatedAt", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_mm, new Date(builder.source().getLong("updatedAt"))));
        if (null != builder.source().get("createdAtStart")) {
            builder.source().remove("createdAtStart");
        }
        if (null != builder.source().get("createdAtEnd")) {
            builder.source().remove("createdAtEnd");
        }
        return builder;
    }

    public Classification convertClassificationForDB(CommonVO vo) {
        Classification Classification = vo.toClassification();
        return Classification;
    }

    public Object convertClassificationForResponse(List<Classification> klasses) {
        return klasses.stream().map(tag -> {
            return convertClassificationForResponse(tag).build();
        }).collect(Collectors.toList());
    }

    private AssembledData.Builder convertClassificationForResponse(Classification klass) {
        AssembledData.Builder builder = createAssembledDataBuilder(klass);
        return builder;
    }
}