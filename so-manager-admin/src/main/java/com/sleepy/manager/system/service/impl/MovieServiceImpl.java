package com.sleepy.manager.system.service.impl;

import com.sleepy.manager.system.domain.Movie;
import com.sleepy.manager.system.mapper.MovieMapper;
import com.sleepy.manager.system.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 电影库Service业务层处理
 *
 * @author sleepyocean
 * @date 2022-04-30
 */
@Service
public class MovieServiceImpl implements IMovieService {
    @Autowired
    private MovieMapper movieMapper;

    /**
     * 查询电影库
     *
     * @param id 电影库主键
     * @return 电影库
     */
    @Override
    public Movie selectMovieById(Long id) {
        return movieMapper.selectMovieById(id);
    }

    /**
     * 查询电影库列表
     *
     * @param movie 电影库
     * @return 电影库
     */
    @Override
    public List<Movie> selectMovieList(Movie movie) {
        return movieMapper.selectMovieList(movie);
    }

    /**
     * 新增电影库
     *
     * @param movie 电影库
     * @return 结果
     */
    @Override
    public int insertMovie(Movie movie) {
        return movieMapper.insertMovie(movie);
    }

    /**
     * 修改电影库
     *
     * @param movie 电影库
     * @return 结果
     */
    @Override
    public int updateMovie(Movie movie) {
        return movieMapper.updateMovie(movie);
    }

    /**
     * 批量删除电影库
     *
     * @param ids 需要删除的电影库主键
     * @return 结果
     */
    @Override
    public int deleteMovieByIds(Long[] ids) {
        return movieMapper.deleteMovieByIds(ids);
    }

    /**
     * 删除电影库信息
     *
     * @param id 电影库主键
     * @return 结果
     */
    @Override
    public int deleteMovieById(Long id) {
        return movieMapper.deleteMovieById(id);
    }
}
