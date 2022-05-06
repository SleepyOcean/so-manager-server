package com.sleepy.manager.main.service;

import com.sleepy.manager.blog.common.UnionResponse;
import com.sleepy.manager.system.domain.Gallery;

/**
 * @author Captain1920
 * @classname FileManagerService
 * @description TODO
 * @date 2022/5/2 15:41
 */
public interface FileManagerService {
    /**
     * 通过 id 获取 img 流
     *
     * @param id
     * @return
     */
    byte[] getImg(String id);

    /**
     * 通过 id 获取 img 缩略图流
     *
     * @param id
     * @return
     */
    byte[] getImgThumbnail(String id);

    /**
     * 通过图片 url 获取 img 压缩流
     *
     * @param ratio
     * @param url
     * @return
     */
    byte[] getCompressedImg(String ratio, String url);

    /**
     * 通过 id(s) 删除图片和图片记录
     *
     * @param id
     * @return
     */
    UnionResponse delete(String id);

    /**
     * 上传图片
     *
     * @param gallery
     * @return
     */
    UnionResponse upload(Gallery gallery);

    /**
     * 获取电影海报
     *
     * @param id
     * @return
     */
    byte[] getMovieCover(long id);

    /**
     * 获取电影横板海报
     *
     * @param id
     * @return
     */
    byte[] getMovieFanart(long id);
}
