package com.sleepy.manager.system.service.impl;

import com.sleepy.manager.common.utils.DateUtils;
import com.sleepy.manager.system.domain.Gallery;
import com.sleepy.manager.system.mapper.GalleryMapper;
import com.sleepy.manager.system.service.IGalleryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 图床Service业务层处理
 *
 * @author sleepyocean
 * @date 2022-05-02
 */
@Service
public class GalleryServiceImpl implements IGalleryService {
    @Autowired
    private GalleryMapper galleryMapper;

    /**
     * 查询图床
     *
     * @param id 图床主键
     * @return 图床
     */
    @Override
    public Gallery selectGalleryById(String id) {
        return galleryMapper.selectGalleryById(id);
    }

    /**
     * 查询图床列表
     *
     * @param gallery 图床
     * @return 图床
     */
    @Override
    public List<Gallery> selectGalleryList(Gallery gallery) {
        return galleryMapper.selectGalleryList(gallery);
    }

    /**
     * 新增图床
     *
     * @param gallery 图床
     * @return 结果
     */
    @Override
    public int insertGallery(Gallery gallery) {
        gallery.setCreateTime(DateUtils.getNowDate());
        return galleryMapper.insertGallery(gallery);
    }

    /**
     * 修改图床
     *
     * @param gallery 图床
     * @return 结果
     */
    @Override
    public int updateGallery(Gallery gallery) {
        return galleryMapper.updateGallery(gallery);
    }

    /**
     * 批量删除图床
     *
     * @param ids 需要删除的图床主键
     * @return 结果
     */
    @Override
    public int deleteGalleryByIds(String[] ids) {
        return galleryMapper.deleteGalleryByIds(ids);
    }

    /**
     * 删除图床信息
     *
     * @param id 图床主键
     * @return 结果
     */
    @Override
    public int deleteGalleryById(String id) {
        return galleryMapper.deleteGalleryById(id);
    }
}
