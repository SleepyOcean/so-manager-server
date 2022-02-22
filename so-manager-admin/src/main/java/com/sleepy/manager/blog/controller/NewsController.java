package com.sleepy.manager.blog.controller;

import com.sleepy.manager.blog.common.AssembledData;
import com.sleepy.manager.blog.common.CommonVO;
import com.sleepy.manager.blog.common.UnionResponse;
import com.sleepy.manager.blog.processor.DataProcessor;
import com.sleepy.manager.blog.processor.RecommendProcessor;
import com.sleepy.manager.common.core.controller.BaseController;
import com.sleepy.manager.common.core.domain.entity.SysUser;
import com.sleepy.manager.common.core.page.TableDataInfo;
import com.sleepy.manager.common.core.text.Convert;
import com.sleepy.manager.common.enums.UserStatus;
import com.sleepy.manager.common.utils.DateUtils;
import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.generation.domain.*;
import com.sleepy.manager.generation.service.*;
import com.sleepy.manager.system.service.ISysUserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Captain1920
 */
@CrossOrigin
@RestController
@RequestMapping("/api")
public class NewsController extends BaseController {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private IUserFavService userFavService;

    @Autowired
    private IUserLikeService userLikeService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ITopicService topicService;

    @Autowired
    private ITagService tagService;

    @Autowired
    private IClassificationService classificationService;

    private static final String ARTICLE_TYPE_HOTSPOT = "hotspot";

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IShareService shareService;

    @Autowired
    private DataProcessor dataProcessor;

    @Autowired
    private RecommendProcessor recommendProcessor;

    private static final String ARTICLE_TYPE_NORMAL = "normal";
    private static final String ARTICLE_TYPE_NOT_HOTSPOT = "non-hotspot";
    @Autowired
    private ISortRelService sortRelService;


    @GetMapping("/valid/article/{articleId}")
    public UnionResponse checkArticleIfValid(@PathVariable("articleId") Long articleId) {
        if (ObjectUtils.isEmpty(articleId)) {
            return new UnionResponse.Builder().status(HttpStatus.BAD_REQUEST).message("articleId cannot be null!").build();
        }
        Article article = articleService.selectArticleById(articleId);
        if (ObjectUtils.isEmpty(article)) {
            return new UnionResponse.Builder().status(HttpStatus.NOT_FOUND).message("文章不存在!").build();
        }
        if (Long.valueOf(0L).equals(article.getStatus())) {
            return new UnionResponse.Builder().status(HttpStatus.GONE).message("文章已下架!").build();
        }
        return new UnionResponse.Builder().status(HttpStatus.OK).message("文章有效!").build();
    }


    @GetMapping("/article/automaticRecommendTrigger")
    public UnionResponse triggerAutomaticRecommend() {
        recommendProcessor.automaticRecommendProcess();
        return new UnionResponse.Builder().status(HttpStatus.OK).build();
    }

    @GetMapping("/article/banner")
    public UnionResponse getArticleBanner(@RequestParam("bannerArticleListStr") String bannerArticleListStr) {
        Article article = new Article();
        article.setIds(Convert.toStrArray(bannerArticleListStr));
        List<Article> list = articleService.selectArticleList(article);
        return new UnionResponse.Builder().data(dataProcessor.processArticles(list)).status(HttpStatus.OK).build();
    }

    @GetMapping("/article/home-non-hotspot")
    public UnionResponse getHomeArticlesNonHotspot() {
        startPage();
        List<Article> list = articleService.selectHomeNonHotspotArticle();
        TableDataInfo pageData = convertProcess(getDataTable(list));
        return new UnionResponse.Builder().status(HttpStatus.OK).message("getHomeArticlesNonHotspot successfully")
                .data(pageData).build();
    }

    @GetMapping("/article/search")
    public UnionResponse getAllArticles(@RequestParam(value = "title", required = false) String title,
                                        @RequestParam(value = "classId", required = false) Long classId,
                                        @RequestParam(value = "tagId", required = false) Long tagId,
                                        @RequestParam(value = "exclude", required = false) String excludeArticleIds,
                                        @RequestParam(value = "startAt", required = false) String startAt,
                                        @RequestParam(value = "endAt", required = false) String endAt,
                                        @RequestParam(value = "status", required = false) Long status) {
        startPage();
        Article article = new Article();
        if (!StringUtils.isEmpty(title)) {
            // 如果title不为空，则视为模糊搜索
            article.setTitle("%" + title + "%");
        }
        if (!StringUtils.isEmpty(excludeArticleIds)) {
            article.setExcludeIds(Convert.toStrArray(excludeArticleIds));
        }
        if (!StringUtils.isEmpty(startAt)) {
            article.setCreatedAtStart(DateUtils.parseDate(startAt));
        }
        if (!StringUtils.isEmpty(endAt)) {
            article.setCreatedAtEnd(DateUtils.parseDate(endAt));
        }
        if (null != tagId) {
            article.setTagId(tagId.toString());
        }
        if (null != classId) {
            article.setClassId(classId);
        }
        if (null != status) {
            article.setStatus(status);
        }

        List<Article> list = articleService.selectArticleList(article);
        TableDataInfo pageData = convertProcess(getDataTable(list));
        return new UnionResponse.Builder().status(HttpStatus.OK).message("getArticles successfully").data(pageData)
                .build();
    }

    private TableDataInfo convertProcess(TableDataInfo pageData) {
        List list = pageData.getRows();
        if(ObjectUtils.isEmpty(list)) {
            return pageData;
        }

        String beanName = list.get(0).getClass().getSimpleName();
        switch (beanName) {
            case "Article": pageData.setRows(dataProcessor.processArticles((List<Article>) pageData.getRows()));
            case "Comment": pageData.setRows(dataProcessor.processComments((List<Comment>) pageData.getRows()));
            default: break;
        }

        return pageData;
    }

    @GetMapping("/article")
    public UnionResponse getArticles(@RequestParam(value = "title", required = false) String title,
                                     @RequestParam(value = "categoryId", required = false) Long categoryId,
                                     @RequestParam(value = "topicId", required = false) Long topicId,
                                     @RequestParam(value = "type", required = false) String type,
                                     @RequestParam(value = "exclude", required = false) String excludeArticleIds,
                                     @RequestParam(value = "startAt", required = false) String startAt,
                                     @RequestParam(value = "endAt", required = false) String endAt,
                                     @RequestParam(value = "status", required = false) Long status) {
        Article article = new Article();
        if (!StringUtils.isEmpty(title)) {
            // 如果title不为空，则视为模糊搜索
            article.setTitle("%" + title + "%");
        }
        if (!StringUtils.isEmpty(excludeArticleIds)) {
            article.setExcludeIds(Convert.toStrArray(excludeArticleIds));
        }
        if (!StringUtils.isEmpty(startAt)) {
            article.setCreatedAtStart(DateUtils.parseDate(startAt));
        }
        if (!StringUtils.isEmpty(endAt)) {
            article.setCreatedAtEnd(DateUtils.parseDate(endAt));
        }
        if (null != status) {
            article.setStatus(status);
        }
        if (null != categoryId) {
            article.setCategoryId(categoryId);
            if (!categoryId.equals(1L)) {
                Category category = categoryService.selectCategoryById(categoryId);
                if (StringUtils.isNotEmpty(category.getTagId())) {
                    article.setTagIds(category.getTagId().split(","));
                }
                if (StringUtils.isNotEmpty(category.getClassId())) {
                    article.setClassIds(category.getClassId().split(","));
                }
                if (type != null && type != ARTICLE_TYPE_NORMAL && article.getTagIds() == null
                        && article.getClassIds() == null) {
                    List<Article> list = Collections.emptyList();
                    TableDataInfo pageData = convertProcess(getDataTable(list));
                    return new UnionResponse.Builder().status(HttpStatus.OK).message("getArticles successfully")
                            .data(pageData).build();
                }
            }
        }
        if (null != topicId) {
            article.setTopicId(topicId);
            Topic topic = topicService.selectTopicById(topicId);
            if (StringUtils.isNotEmpty(topic.getTagId())) {
                article.setTagIds(topic.getTagId().split(","));
            }
            if(StringUtils.isNotEmpty(topic.getClassId())) {
                article.setClassIds(topic.getClassId().split(","));
            }
        }
        if (type == null) {
            startPage();
        } else {
            switch (type) {
                case ARTICLE_TYPE_NORMAL:
                    startPage();
                    break;
                case ARTICLE_TYPE_HOTSPOT:
                    article.setIsSticky(1L);
                    break;
                case ARTICLE_TYPE_NOT_HOTSPOT:
                    startPage();
                    article.setIsSticky(0L);
                    break;
                default:
                    return new UnionResponse.Builder().status(HttpStatus.NOT_IMPLEMENTED)
                            .message("wrong 'type', name should be " + Arrays.asList(ARTICLE_TYPE_NORMAL,
                                    ARTICLE_TYPE_HOTSPOT, ARTICLE_TYPE_NOT_HOTSPOT))
                            .build();
            }
        }
        List<Article> list = articleService.selectArticleByCategoryOrTopicId(article);
        TableDataInfo pageData = convertProcess(getDataTable(list));
        return new UnionResponse.Builder().status(HttpStatus.OK).message("getArticles successfully").data(pageData)
                .build();
    }

    @GetMapping("/article/{id}")
    public UnionResponse getArticle(HttpServletRequest request, @PathVariable("id") Long id) {
        Article article = articleService.selectArticleById(id);

        Comment comment = new Comment();
        comment.setArticleId(id);
        List<Comment> comments = commentService.selectCommentList(comment);

        AssembledData.Builder dataBuilder = dataProcessor.process(article);

        String token = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(token)) {
            SysUser user = userService.selectUserById(getUserId());
            if (user != null) {
                UserLike userLike = new UserLike(id, user.getUserId());
                int isLike = userLikeService.selectUserLikeList(userLike).size();
                
                UserFav userFav = new UserFav(id, user.getUserId());
                int isCollect = userFavService.selectUserFavList(userFav).size();
                dataBuilder.put("isLike", isLike).put("isCollect", isCollect);
            }
        }
        AssembledData data = dataBuilder.put("commentCount", comments.size()).build();
        if (StringUtils.isEmpty(request.getHeader("resources"))) {
            // 浏览数+1
            article.setViewed(article.getViewed() + 1);
        }
        articleService.updateArticle(article);

        return new UnionResponse.Builder().status(HttpStatus.OK).message("getArticle successfully").data(data).build();
    }

    @PutMapping("/article")
    public UnionResponse updateArticleWithAuth(@RequestBody Article article) {
        if (null != article.getCreatedBy() || null != article.getCreatedAt() || null != article.getUpdatedAt()) {
            return new UnionResponse.Builder().status(HttpStatus.FORBIDDEN).message(
                    "Failed to update article, because createBy, createdAt and updatedAt column forbid to change!")
                    .build();
        }
        article.setUpdatedAt(new Date());
        int result = articleService.updateArticle(article);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("updateArticle successfully").data(result)
                .build();
    }

    @PostMapping("/article")
    public UnionResponse addArticleWithAuth(@RequestBody Article article) {
        int result = articleService.insertArticle(article);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("addArticle successfully").data(article.getId())
                .build();
    }

    @DeleteMapping("/article/{id}")
    public UnionResponse deleteArticleWithAuth(@PathVariable("id") Long id) {
        Article article = new Article();
        article.setId(id);
        article.setStatus(0L);
        int result = articleService.updateArticle(article);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("deleteArticle successfully").data(result)
                .build();
    }

    @GetMapping("/article/{id}/comment")
    public UnionResponse getComment(@PathVariable("id") Long id) {
        startPage();
        Comment comment = new Comment();
        comment.setArticleId(id);
        List<Comment> list = commentService.selectCommentList(comment);

        TableDataInfo pageData = convertProcess(getDataTable(list));
        return new UnionResponse.Builder().status(HttpStatus.OK).message("getComment successfully").data(pageData)
                .build();
    }

    @PostMapping("/article/{id}/comment")
    public UnionResponse addComment(@PathVariable("id") Long id, @RequestBody Comment comment) {
        if (checkArticleValid(id)) {
            return new UnionResponse.Builder().status(HttpStatus.BAD_REQUEST).message("文章无效!").build();
        }
        if (checkUserIsBlocked()) {
            return new UnionResponse.Builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message("用户已封禁，请联系管理员!").build();
        }
        comment.setArticleId(id);
        comment.setCreatedBy(getUserId());
        comment.setCreatedAt(new Date());
        comment.setUpdatedAt(new Date());
        comment.setStatus(1L);
        int result = commentService.insertComment(comment);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("评论成功").data(result)
                .build();
    }

    @PostMapping("/article/{id}/like")
    public UnionResponse addLike(@PathVariable("id") Long id, UserLike userLike) {
        if (checkArticleValid(id)) {
            return new UnionResponse.Builder().status(HttpStatus.BAD_REQUEST).message("文章无效!").build();
        }
        if (checkUserIsBlocked()) {
            return new UnionResponse.Builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message("用户已封禁，请联系管理员!").build();
        }
        userLike.setArticleId(id);
        userLike.setUserId(getUserId());
        userLike.setCreatedAt(new Date());
        userLike.setUpdatedAt(new Date());
        try {
            userLikeService.insertUserLike(userLike);
        } catch (DuplicateKeyException e) {
            return new UnionResponse.Builder().status(HttpStatus.CREATED)
                    .message("").build();
        }

        Article article = articleService.selectArticleById(id);
        article.setLikeNum(article.getLikeNum() + 1L);
        articleService.updateArticle(article);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("addLike successfully").data(article).build();
    }

    @GetMapping("/comment")
    public UnionResponse searchCommentWithAuth(@RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "articleId", required = false) Long articleId,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "startAt", required = false) String startAt,
            @RequestParam(value = "endAt", required = false) String endAt) {
        startPage();
        Comment comment = new Comment();
        if (null != userId) {
            comment.setCreatedBy(userId);
        }
        if (null != articleId) {
            comment.setArticleId(articleId);
        }
        if (!StringUtils.isEmpty(content)) {
            comment.setContent("%" + content + "%");
        }
        if (!StringUtils.isEmpty(startAt)) {
            comment.setCreatedAtStart(DateUtils.parseDate(startAt));
        }
        if (!StringUtils.isEmpty(endAt)) {
            comment.setCreatedAtEnd(DateUtils.parseDate(endAt));
        }
        List<Comment> list = commentService.selectCommentList(comment);
        TableDataInfo pageData = convertProcess(getDataTable(list));
        return new UnionResponse.Builder().status(HttpStatus.OK).message("getComment successfully").data(pageData)
                .build();
    }

    @PutMapping("/comment")
    public UnionResponse updateCommentWithAuth(@RequestBody Comment comment) {
        int result = commentService.updateComment(comment);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("updateComment successfully").data(result)
                .build();
    }

    @DeleteMapping("/comment/{id}")
    public UnionResponse deleteCommentWithAuth(@PathVariable("id") Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(0L);
        int result = commentService.updateComment(comment);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("deleteComment successfully").data(result)
                .build();
    }

    @PostMapping("/article/{id}/unlike")
    public UnionResponse unlike(@PathVariable("id") Long id, UserLike userLike) {
        userLike.setArticleId(id);
        userLike.setUserId(getUserId());
        List<UserLike> list = userLikeService.selectUserLikeList(userLike);
        if (list.isEmpty()) {
            return new UnionResponse.Builder().status(HttpStatus.CREATED)
                    .message("").build();
        }
        userLikeService.deleteUserLikeById(list.get(0).getId());

        Article article = articleService.selectArticleById(id);
        article.setLikeNum(article.getLikeNum() - 1L);
        articleService.updateArticle(article);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("unlike successfully").data(article).build();
    }

    @GetMapping("/like")
    public UnionResponse getLike(@RequestParam(value = "userId", required = false) Long userId) {
        UserLike like = new UserLike();
        if (null != userId) {
            like.setUserId(userId);
        }
        List<UserLike> userLikeList = userLikeService.selectUserLikeList(like);
        Article article = new Article();
        String[] articleIds = userLikeList.stream().map(userLike -> {
            return userLike.getArticleId().toString();
        }).collect(Collectors.toList()).toArray(new String[0]);

        if (articleIds.length == 0) {
            return new UnionResponse.Builder().status(HttpStatus.OK).build();
        }

        startPage();
        article.setIds(articleIds);
        List<Article> articleList = articleService.selectArticleList(article);
        TableDataInfo pageData = convertProcess(getDataTable(articleList));
        AssembledData result = new AssembledData.Builder().put("likeArticles", pageData)
                .put("currentUser", dataProcessor.enrich(userService.selectUserById(userId)).build()).build();
        return new UnionResponse.Builder().data(result).status(HttpStatus.OK).build();
    }

    @GetMapping("/fav")
    public UnionResponse getFav(@RequestParam(value = "userId", required = false) Long userId) {
        UserFav fav = new UserFav();
        if (null != userId) {
            fav.setUserId(userId);
        }
        List<UserFav> userFavList = userFavService.selectUserFavList(fav);
        Article article = new Article();
        String[] articleIds = userFavList.stream().map(userFav -> {
            return userFav.getArticleId().toString();
        }).collect(Collectors.toList()).toArray(new String[0]);

        if (articleIds.length == 0) {
            return new UnionResponse.Builder().status(HttpStatus.OK).build();
        }

        startPage();
        article.setIds(articleIds);
        List<Article> articleList = articleService.selectArticleList(article);
        TableDataInfo pageData = convertProcess(getDataTable(articleList));
        AssembledData result = new AssembledData.Builder().put("favArticles", pageData)
                .put("currentUser", dataProcessor.enrich(userService.selectUserById(userId)).build()).build();
        return new UnionResponse.Builder().data(result).status(HttpStatus.OK).build();
    }

    @PostMapping("/article/{id}/fav")
    public UnionResponse addFav(@PathVariable("id") Long id, UserFav userFav) {
        if (checkArticleValid(id)) {
            return new UnionResponse.Builder().status(HttpStatus.BAD_REQUEST).message("文章无效!").build();
        }
        if (checkUserIsBlocked()) {
            return new UnionResponse.Builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message("用户已封禁，请联系管理员!").build();
        }
        userFav.setArticleId(id);
        userFav.setUserId(getUserId());
        userFav.setCreatedAt(new Date());
        userFav.setUpdatedAt(new Date());
        try {
            userFavService.insertUserFav(userFav);
        } catch (DuplicateKeyException e) {
            return new UnionResponse.Builder().status(HttpStatus.CREATED)
                    .message("").build();
        }

        Article article = articleService.selectArticleById(id);
        article.setFavoriteNum(article.getFavoriteNum() + 1);
        articleService.updateArticle(article);

        return new UnionResponse.Builder().status(HttpStatus.OK).message("addFav successfully").data(article).build();
    }

    @PostMapping("/article/{id}/unfav")
    public UnionResponse unFav(@PathVariable("id") Long id, UserFav userFav) {
        userFav.setArticleId(id);
        userFav.setUserId(getUserId());
        List<UserFav> list = userFavService.selectUserFavList(userFav);
        if (list.isEmpty()) {
            return new UnionResponse.Builder().status(HttpStatus.CREATED)
                    .message("").build();
        }
        userFavService.deleteUserFavById(list.get(0).getId());
        Article article = articleService.selectArticleById(id);
        article.setFavoriteNum(article.getFavoriteNum() - 1);
        articleService.updateArticle(article);

        return new UnionResponse.Builder().status(HttpStatus.OK).message("unFav successfully").data(article).build();
    }

    @GetMapping("/category")
    public UnionResponse getCategory(@RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "startAt", required = false) String startAt,
            @RequestParam(value = "endAt", required = false) String endAt) {
        Category category = new Category();
        if (null != id) {
            category.setId(id);
        }
        if (!StringUtils.isEmpty(startAt)) {
            category.setCreatedAtStart(DateUtils.parseDate(startAt));
        }
        if (!StringUtils.isEmpty(endAt)) {
            category.setCreatedAtEnd(DateUtils.parseDate(endAt));
        }
        List<Category> list = categoryService.selectCategoryList(category);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("getCategory successfully")
                .data(dataProcessor.convertCategoryForResponse(list)).build();
    }

    @PutMapping("/category")
    public UnionResponse updateCategoryWithAuth(@RequestBody CommonVO vo) {
        int result = categoryService.updateCategory(dataProcessor.convertCategoryForDB(vo));
        return new UnionResponse.Builder().status(HttpStatus.OK).message("updateCategory successfully").data(result)
                .build();
    }

    @PostMapping("/category")
    public UnionResponse addCategoryWithAuth(@RequestBody CommonVO vo) {
        Category category = dataProcessor.convertCategoryForDB(vo);
        int result = categoryService.insertCategory(category);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("addCategory successfully").data(category.getId())
                .build();
    }

    @DeleteMapping("/category/{id}")
    public UnionResponse deleteCategoryWithAuth(@PathVariable("id") Long id) {
        Category category = new Category();
        category.setId(id);
        category.setStatus(0L);
        Topic topic = new Topic();
        topic.setCategoryId(id);
        topic.setStatus(0L);
        int result = categoryService.updateCategory(category);
        topicService.updateTopic(topic);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("deleteCategory successfully").data(result)
                .build();
    }

    @GetMapping("/topic")
    public UnionResponse getTopic(@RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "startAt", required = false) String startAt,
            @RequestParam(value = "endAt", required = false) String endAt) {
        Topic topic = new Topic();
        if (null != id) {
            topic.setId(id);
        }
        if (null != categoryId) {
            topic.setCategoryId(categoryId);
        }
        if (!StringUtils.isEmpty(startAt)) {
            topic.setCreatedAtStart(DateUtils.parseDate(startAt));
        }
        if (!StringUtils.isEmpty(endAt)) {
            topic.setCreatedAtEnd(DateUtils.parseDate(endAt));
        }
        List<AssembledData> list = topicService.selectTopicList(topic).stream().map(item -> {
            return dataProcessor.process(item).build();
        }).collect(Collectors.toList());
        return new UnionResponse.Builder().status(HttpStatus.OK).message("getTopic successfully").data(list).build();
    }

    @PutMapping("/topic")
    public UnionResponse updateTopicWithAuth(@RequestBody Topic vo) {
        int result = topicService.updateTopic(vo);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("updateTopic successfully").data(result)
                .build();
    }

    @PostMapping("/topic")
    public UnionResponse addTopicWithAuth(@RequestBody Topic vo) {
        int result = topicService.insertTopic(vo);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("addTopic successfully").data(result).build();
    }

    @DeleteMapping("/topic/{id}")
    public UnionResponse deleteTopicWithAuth(@PathVariable("id") Long id) {
        Topic topic = new Topic();
        topic.setId(id);
        topic.setStatus(0L);
        int result = topicService.updateTopic(topic);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("deleteTopic successfully").data(result)
                .build();
    }

    @GetMapping("/tag")
    public UnionResponse getTag(@RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "startAt", required = false) String startAt,
            @RequestParam(value = "endAt", required = false) String endAt) {
        Tag tag = new Tag();
        if (null != id) {
            tag.setId(id);
        }
        if (!StringUtils.isEmpty(startAt)) {
            tag.setCreatedAtStart(DateUtils.parseDate(startAt));
        }
        if (!StringUtils.isEmpty(endAt)) {
            tag.setCreatedAtEnd(DateUtils.parseDate(endAt));
        }
        List<Tag> list = tagService.selectTagList(tag);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("getTag successfully")
                .data(dataProcessor.convertTagForResponse(list)).build();
    }

    @PutMapping("/tag")
    public UnionResponse updateTagWithAuth(@RequestBody CommonVO vo) {
        int result = tagService.updateTag(dataProcessor.convertTagForDB(vo));
        return new UnionResponse.Builder().status(HttpStatus.OK).message("updateTag successfully").data(result).build();
    }

    @PostMapping("/tag")
    public UnionResponse addTagWithAuth(@RequestBody CommonVO vo) {
        int result = tagService.insertTag(dataProcessor.convertTagForDB(vo));
        return new UnionResponse.Builder().status(HttpStatus.OK).message("addTag successfully").data(result).build();
    }

    @DeleteMapping("/tag/{id}")
    public UnionResponse deleteTagWithAuth(@PathVariable("id") Long id) {
        Article article = new Article();
        article.setTagId(id.toString());
        articleService.removeTagOrClassification(article);

        Category category = new Category();
        category.setTagId(id.toString());
        categoryService.removeTagOrClassification(category);

        Topic topic = new Topic();
        topic.setTagId(id.toString());
        topicService.removeTagOrClassification(topic);

        tagService.deleteTagById(id);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("deleteTag successfully").build();
    }

    @GetMapping("/classification")
    public UnionResponse getClassification(@RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "startAt", required = false) String startAt,
            @RequestParam(value = "endAt", required = false) String endAt) {
        Classification klass = new Classification();
        if (null != id) {
            klass.setId(id);
        }
        if (!StringUtils.isEmpty(startAt)) {
            klass.setCreatedAtStart(DateUtils.parseDate(startAt));
        }
        if (!StringUtils.isEmpty(endAt)) {
            klass.setCreatedAtEnd(DateUtils.parseDate(endAt));
        }
        List<Classification> list = classificationService.selectClassificationList(klass);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("getClassification successfully")
                .data(dataProcessor.convertClassificationForResponse(list)).build();
    }

    @PutMapping("/classification")
    public UnionResponse updateClassificationWithAuth(@RequestBody CommonVO vo) {
        int result = classificationService.updateClassification(dataProcessor.convertClassificationForDB(vo));
        return new UnionResponse.Builder().status(HttpStatus.OK).message("updateClassification successfully")
                .data(result).build();
    }

    @PostMapping("/classification")
    public UnionResponse addClassificationWithAuth(@RequestBody CommonVO vo) {
        int result = classificationService.insertClassification(dataProcessor.convertClassificationForDB(vo));
        return new UnionResponse.Builder().status(HttpStatus.OK).message("addClassification successfully").data(result)
                .build();
    }

    @DeleteMapping("/classification/{id}")
    public UnionResponse deleteClassificationWithAuth(@PathVariable("id") Long id) {
        Article article = new Article();
        article.setClassId(id);
        articleService.removeTagOrClassification(article);

        Category category = new Category();
        category.setClassId(id.toString());
        categoryService.removeTagOrClassification(category);

        Topic topic = new Topic();
        topic.setClassId(id.toString());
        topicService.removeTagOrClassification(topic);

        classificationService.deleteClassificationById(id);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("deleteClassification successfully").build();
    }

    @GetMapping("/share")
    public UnionResponse getShare(@RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "userId", required = false) Long userId) {
        Share cond = new Share();
        if (null != id) {
            cond.setId(id);
        }
        if (null != userId) {
            cond.setUserId(userId);
        }
        List<Share> shareList = shareService.selectShareList(cond);
        Article article = new Article();
        String[] articleIds = shareList.stream().map(share -> share.getArticleId().toString()).collect(Collectors.toList())
                .toArray(new String[0]);
        if (articleIds.length == 0) {
            return new UnionResponse.Builder().status(HttpStatus.OK).build();
        }

        startPage();
        article.setIds(articleIds);
        List<Article> articleList = articleService.selectArticleList(article);
        TableDataInfo pageData = convertProcess(getDataTable(articleList));
        AssembledData result = new AssembledData.Builder().put("shareArticles", pageData)
                .put("currentUser", dataProcessor.enrich(userService.selectUserById(userId)).build()).build();
        return new UnionResponse.Builder().data(result).status(HttpStatus.OK).build();
    }

    @PutMapping("/share")
    public UnionResponse updateShareWithAuth(@RequestBody Share vo) {
        int result = shareService.updateShare(vo);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("updateShare successfully").data(result)
                .build();
    }

    @PostMapping("/share")
    public UnionResponse addShareWithAuth(@RequestBody Share vo) {
        int result = shareService.insertShare(vo);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("addShare successfully").data(result).build();
    }

    @DeleteMapping("/share/{id}")
    public UnionResponse deleteShareWithAuth(@PathVariable("id") Long id) {
        int result = shareService.deleteShareById(id);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("deleteShare successfully").data(result)
                .build();
    }

    @PutMapping("/sortRel")
    public UnionResponse updateSortRelWithAuth(@RequestBody SortRel sortRel) {
        // 相同的category或topic的排序字段sort值与待更新的sort值相同，则将原记录删除
        if (sortRel.getSort() != 99999) {
            SortRel condition = new SortRel();
            condition.setCategoryId(sortRel.getCategoryId());
            condition.setTopicId(sortRel.getTopicId());
            condition.setSort(sortRel.getSort());
            condition.setIsSticky(sortRel.getIsSticky());
            List<SortRel> conflictRecords = sortRelService.selectSortRelList(condition);
            if (conflictRecords.size() > 0) {
                String ids = StringUtils.join(conflictRecords.stream().map(SortRel::getId).toArray(), ",");
                sortRelService.deleteSortRelByIds(ids);
            }
        }
        int result = sortRelService.updateSortRel(sortRel);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("updateTag successfully").data(result).build();
    }

    @PostMapping("/sortRel")
    public UnionResponse addSortRelWithAuth(@RequestBody SortRel sortRel) {
        // 相同的category或topic的排序字段sort值与待更新的sort值相同，则将原记录删除
        if (sortRel.getSort() != 99999) {
            SortRel condition = new SortRel();
            condition.setCategoryId(sortRel.getCategoryId());
            condition.setTopicId(sortRel.getTopicId());
            condition.setSort(sortRel.getSort());
            condition.setIsSticky(sortRel.getIsSticky());
            List<SortRel> conflictRecords = sortRelService.selectSortRelList(condition);
            if (conflictRecords.size() > 0) {
                String ids = StringUtils.join(conflictRecords.stream().map(SortRel::getId).toArray(), ",");
                sortRelService.deleteSortRelByIds(ids);
            }
        }
        int result = sortRelService.insertSortRel(sortRel);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("addTag successfully").data(result).build();
    }

    @DeleteMapping("/sortRel/{id}")
    public UnionResponse deleteSortRelWithAuth(@PathVariable("id") Long id) {
        // SortRel sortRel = new SortRel();
        // sortRel.setId(id);
        // sortRel.setStatus(0L);
        // int result = sortRelService.updateSortRel(sortRel);
        int result = sortRelService.deleteSortRelById(id);
        return new UnionResponse.Builder().status(HttpStatus.OK).message("deleteTag successfully").data(result).build();
    }

    private boolean checkUserIsBlocked() {
        return UserStatus.DISABLE.getCode().equals(getLoginUser().getUser().getStatus());
    }

    private boolean checkArticleValid(Long articleId) {
        Article article = articleService.selectArticleById(articleId);
        return !ObjectUtils.isEmpty(article) & Long.valueOf(0L).equals(article.getStatus());
    }
}
