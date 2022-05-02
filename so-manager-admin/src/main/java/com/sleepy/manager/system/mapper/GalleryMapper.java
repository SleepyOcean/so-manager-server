package com.sleepy.manager.system.mapper;

import com.sleepy.manager.system.domain.Gallery;

import java.util.List;

/**
 * 图床Mapper接口
 *
 * @author sleepyocean
 * @date 2022-05-02
 */
public interface GalleryMapper {
    /**
     * 查询图床
     *
     * @param id 图床主键
     * @return 图床
     */
    Gallery selectGalleryById(String id);

    /**
     * 查询图床列表
     *
     * @param gallery 图床
     * @return 图床集合
     */
    List<Gallery> selectGalleryList(Gallery gallery);

    /**
     * 新增图床
     *
     * @param gallery 图床
     * @return 结果
     */
    int insertGallery(Gallery gallery);

    /**
     * 修改图床
     *
     * @param gallery 图床
     * @return 结果
     */
    int updateGallery(Gallery gallery);

    /**
     * 删除图床
     *
     * @param id 图床主键
     * @return 结果
     */
    int deleteGalleryById(String id);

    /**
     * 批量删除图床
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteGalleryByIds(String[] ids);
}
