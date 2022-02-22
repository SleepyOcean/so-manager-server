package com.sleepy.manager.blog.processor;

import com.sleepy.manager.common.utils.StringUtils;
import com.sleepy.manager.generation.domain.*;
import com.sleepy.manager.generation.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.sleepy.manager.blog.common.Constant.*;

/**
 * 推荐文章处理器
 *
 * @author gehoubao
 * @create 2021-12-04 19:42
 **/
@Component("recommendProcessor")
public class RecommendProcessor {
    private static final Logger log = LoggerFactory.getLogger(RecommendProcessor.class);

    @Autowired
    private ITopicService topicService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ICommentService commentService;

    @Autowired
    private ISortRelService sortRelService;

    @Autowired
    private IArticleService articleService;

    public void automaticRecommendProcess() {
        log.info("start recommend process...");
        // Category recommend
        List<Category> categories = categoryService.selectCategoryList(new Category());
        categories.forEach(category -> processCategory(category));
        // Topic recommend
        List<Topic> topics = topicService.selectTopicList(new Topic());
        topics.forEach(topic -> processTopic(topic));
    }

    private void processCategory(Category category) {
        // step 1. get all article in the category
        Article categoryCond = new Article();
        if (StringUtils.isNotEmpty(category.getClassId())) {
            String[] classIds = category.getClassId().split(",");
            categoryCond.setClassIds(classIds);
        }
        if (StringUtils.isNotEmpty(category.getTagId())) {
            String[] tagIds = category.getTagId().split(",");
            categoryCond.setTagIds(tagIds);
        }
        List<Article> articleListOfCategory = articleService.selectArticleListForRecommend(categoryCond);

        // step 2. get article recommend index calculate factor
        Long[] articleIds = articleListOfCategory.stream().map(Article::getId).collect(Collectors.toList()).toArray(new Long[0]);
        Comment commentCond = new Comment();
        commentCond.setArticleIds(articleIds);
        List<Comment> commentCount = commentService.countComment(commentCond);
        Map<Long, Long> articleCommentCountMap = commentCount.stream().collect(Collectors.toMap(Comment::getArticleId, Comment::getCommentCount));

        // step 3.0 calculate recommend index
        Map<Long, Long> articleRecommendIndex = new HashMap<>();
        articleListOfCategory.forEach(article -> {
            long recommendIndex = calRecommendIndex(article.getViewed(), article.getLikeNum(), articleCommentCountMap.getOrDefault(article.getId(), 0L), getDaysCount(article.getCreatedAt()));
            articleRecommendIndex.put(article.getId(), recommendIndex);
        });
        // step 3.1 sort recommend index
        List<Long> unSortRecommendArticleIds = new ArrayList<>(articleRecommendIndex.keySet());
        Collections.sort(unSortRecommendArticleIds, new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                if (articleRecommendIndex.get(o1) > articleRecommendIndex.get(o2)) {
                    return 1;
                } else if (articleRecommendIndex.get(o1) < articleRecommendIndex.get(o2)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        // step 3.2 generate automatic sort_rel record
        SortRel categoryManualCond = new SortRel();
        categoryManualCond.setCategoryId(category.getId());
        categoryManualCond.setIsSticky(IS_STICKY);
        categoryManualCond.setIsAutomatic(NO_AUTOMATIC);
        categoryManualCond.setSortType(SORT_TYPE_AS_DEFAULT);
        List<SortRel> categoryManualRecord = sortRelService.selectSortRelList(categoryManualCond);
        int automaticSize = RECOMMEND_SIZE - categoryManualRecord.size();
        if (automaticSize < 0) return;
        List<SortRel> automaticSortRelRecords = new ArrayList<>();
        long manualSortMaxNum = categoryManualRecord.size() > 0 ? categoryManualRecord.get(0).getSort() : 0;
        for (int i = 0; unSortRecommendArticleIds.size() > i; i++) {
            SortRel automaticSortRelRecord = new SortRel();
            automaticSortRelRecord.setIsAutomatic(1L);
            automaticSortRelRecord.setCategoryId(category.getId());
            automaticSortRelRecord.setIsSticky(i < automaticSize ? 1L : 0L);
            automaticSortRelRecord.setArticleId(unSortRecommendArticleIds.get(i));
            automaticSortRelRecord.setSort(++manualSortMaxNum);
            automaticSortRelRecords.add(automaticSortRelRecord);
        }


        // step 4. clear old automatic record
        SortRel categoryAutomaticCond = new SortRel();
        categoryAutomaticCond.setCategoryId(category.getId());
        categoryAutomaticCond.setIsAutomatic(1L);
        String deleteIds = StringUtils.join(sortRelService.selectSortRelList(categoryAutomaticCond).stream().map(SortRel::getId).collect(Collectors.toList()), ",");
        sortRelService.deleteSortRelByIds(deleteIds);

        // step 5. add new automatic
        automaticSortRelRecords.forEach(record -> sortRelService.insertSortRel(record));
    }

    private void processTopic(Topic topic) {
        // step 1. get all article in the topic
        Article topicCond = new Article();
        if (StringUtils.isNotEmpty(topic.getClassId())) {
            String[] classIds = topic.getClassId().split(",");
            topicCond.setClassIds(classIds);
        }
        if (StringUtils.isNotEmpty(topic.getTagId())) {
            String[] tagIds = topic.getTagId().split(",");
            topicCond.setTagIds(tagIds);
        }
        List<Article> articleListOfTopic = articleService.selectArticleListForRecommend(topicCond);

        // step 2. get article recommend index calculate factor
        Long[] articleIds = articleListOfTopic.stream().map(Article::getId).collect(Collectors.toList()).toArray(new Long[0]);
        Comment commentCond = new Comment();
        commentCond.setArticleIds(articleIds);
        List<Comment> commentCount = commentService.countComment(commentCond);
        Map<Long, Long> articleCommentCountMap = commentCount.stream().collect(Collectors.toMap(Comment::getArticleId, Comment::getCommentCount));

        // step 3.0 calculate recommend index
        Map<Long, Long> articleRecommendIndex = new HashMap<>();
        articleListOfTopic.forEach(article -> {
            long recommendIndex = calRecommendIndex(article.getViewed(), article.getLikeNum(), articleCommentCountMap.getOrDefault(article.getId(), 0L), getDaysCount(article.getCreatedAt()));
            articleRecommendIndex.put(article.getId(), recommendIndex);
        });
        // step 3.1 sort recommend index
        List<Long> unSortRecommendArticleIds = new ArrayList<>(articleRecommendIndex.keySet());
        Collections.sort(unSortRecommendArticleIds, new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                if (articleRecommendIndex.get(o1) > articleRecommendIndex.get(o2)) {
                    return 1;
                } else if (articleRecommendIndex.get(o1) < articleRecommendIndex.get(o2)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        // step 3.2 generate automatic sort_rel record
        SortRel topicManualCond = new SortRel();
        topicManualCond.setTopicId(topic.getId());
        topicManualCond.setIsSticky(IS_STICKY);
        topicManualCond.setIsAutomatic(NO_AUTOMATIC);
        topicManualCond.setSortType(SORT_TYPE_AS_DEFAULT);
        List<SortRel> topicManualRecord = sortRelService.selectSortRelList(topicManualCond);
        int automaticSize = RECOMMEND_SIZE - topicManualRecord.size();
        if (automaticSize < 0) return;
        List<SortRel> automaticSortRelRecords = new ArrayList<>();
        long manualSortMaxNum = topicManualRecord.size() > 0 ? topicManualRecord.get(0).getSort() : 0;
        for (int i = 0; unSortRecommendArticleIds.size() > i; i++) {
            SortRel automaticSortRelRecord = new SortRel();
            automaticSortRelRecord.setIsAutomatic(1L);
            automaticSortRelRecord.setTopicId(topic.getId());
            automaticSortRelRecord.setIsSticky(i < automaticSize ? 1L : 0L);
            automaticSortRelRecord.setArticleId(unSortRecommendArticleIds.get(i));
            automaticSortRelRecord.setSort(++manualSortMaxNum);
            automaticSortRelRecords.add(automaticSortRelRecord);
        }


        // step 4. clear old automatic record
        SortRel topicAutomaticCond = new SortRel();
        topicAutomaticCond.setTopicId(topic.getId());
        topicAutomaticCond.setIsAutomatic(1L);
        String deleteIds = StringUtils.join(sortRelService.selectSortRelList(topicAutomaticCond).stream().map(SortRel::getId).collect(Collectors.toList()), ",");
        sortRelService.deleteSortRelByIds(deleteIds);

        // step 5. add new automatic
        automaticSortRelRecords.forEach(record -> sortRelService.insertSortRel(record));
    }

    private Long getDaysCount(Date date) {
        return ((System.currentTimeMillis() - date.getTime()) / 24 / 60 / 60 / 1000);
    }

    private long calRecommendIndex(Long readCount, Long likeCount, Long commentCount, Long daysCount) {
        return readCount / 100 + likeCount + commentCount / 2 + commentCount + daysCount / 20;
    }
}