package com.sleepy.manager.system.service;

import com.sleepy.manager.system.domain.Bookmark;

import java.util.List;

/**
 * 网页书签Service接口
 *
 * @author sleepyocean
 * @date 2022-04-10
 */
public interface IBookmarkService {
    /**
     * 查询网页书签
     *
     * @param id 网页书签主键
     * @return 网页书签
     */
    Bookmark selectBookmarkById(Long id);

    /**
     * 查询网页书签列表
     *
     * @param bookmark 网页书签
     * @return 网页书签集合
     */
    List<Bookmark> selectBookmarkList(Bookmark bookmark);

    /**
     * 新增网页书签
     *
     * @param bookmark 网页书签
     * @return 结果
     */
    int insertBookmark(Bookmark bookmark);

    /**
     * 修改网页书签
     *
     * @param bookmark 网页书签
     * @return 结果
     */
    int updateBookmark(Bookmark bookmark);

    /**
     * 批量删除网页书签
     *
     * @param ids 需要删除的网页书签主键集合
     * @return 结果
     */
    int deleteBookmarkByIds(Long[] ids);

    /**
     * 删除网页书签信息
     *
     * @param id 网页书签主键
     * @return 结果
     */
    int deleteBookmarkById(Long id);
}
