package com.sleepy.manager.system.mapper;

import com.sleepy.manager.system.domain.Movie;

import java.util.List;

/**
 * 电影库Mapper接口
 *
 * @author sleepyocean
 * @date 2022-04-30
 */
public interface MovieMapper {
    /**
     * 查询电影库
     *
     * @param id 电影库主键
     * @return 电影库
     */
    Movie selectMovieById(Long id);

    /**
     * 查询电影库列表
     *
     * @param movie 电影库
     * @return 电影库集合
     */
    List<Movie> selectMovieList(Movie movie);

    /**
     * 新增电影库
     *
     * @param movie 电影库
     * @return 结果
     */
    int insertMovie(Movie movie);

    /**
     * 修改电影库
     *
     * @param movie 电影库
     * @return 结果
     */
    int updateMovie(Movie movie);

    /**
     * 删除电影库
     *
     * @param id 电影库主键
     * @return 结果
     */
    int deleteMovieById(Long id);

    /**
     * 批量删除电影库
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    int deleteMovieByIds(Long[] ids);
}
