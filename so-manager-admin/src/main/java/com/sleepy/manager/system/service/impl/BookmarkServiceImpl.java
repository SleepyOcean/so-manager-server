package com.sleepy.manager.system.service.impl;

import com.sleepy.manager.system.domain.Bookmark;
import com.sleepy.manager.system.mapper.BookmarkMapper;
import com.sleepy.manager.system.service.IBookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 网页书签Service业务层处理
 *
 * @author sleepyocean
 * @date 2022-04-10
 */
@Service
public class BookmarkServiceImpl implements IBookmarkService {
    @Autowired
    private BookmarkMapper bookmarkMapper;

    /**
     * 查询网页书签
     *
     * @param id 网页书签主键
     * @return 网页书签
     */
    @Override
    public Bookmark selectBookmarkById(Long id) {
        return bookmarkMapper.selectBookmarkById(id);
    }

    /**
     * 查询网页书签列表
     *
     * @param bookmark 网页书签
     * @return 网页书签
     */
    @Override
    public List<Bookmark> selectBookmarkList(Bookmark bookmark) {
        return bookmarkMapper.selectBookmarkList(bookmark);
    }

    /**
     * 新增网页书签
     *
     * @param bookmark 网页书签
     * @return 结果
     */
    @Override
    public int insertBookmark(Bookmark bookmark) {
        return bookmarkMapper.insertBookmark(bookmark);
    }

    /**
     * 修改网页书签
     *
     * @param bookmark 网页书签
     * @return 结果
     */
    @Override
    public int updateBookmark(Bookmark bookmark) {
        return bookmarkMapper.updateBookmark(bookmark);
    }

    /**
     * 批量删除网页书签
     *
     * @param ids 需要删除的网页书签主键
     * @return 结果
     */
    @Override
    public int deleteBookmarkByIds(Long[] ids) {
        return bookmarkMapper.deleteBookmarkByIds(ids);
    }

    /**
     * 删除网页书签信息
     *
     * @param id 网页书签主键
     * @return 结果
     */
    @Override
    public int deleteBookmarkById(Long id) {
        return bookmarkMapper.deleteBookmarkById(id);
    }
}
