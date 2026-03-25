package com.nebula.service.mapper.blog;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nebula.model.entity.blog.BlogArticle;
import com.nebula.model.vo.BlogArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 博客文章Mapper
 */
@Mapper
public interface BlogArticleMapper extends BaseMapper<BlogArticle> {

    /**
     * 获取文章列表（带用户信息）
     */
    BlogArticleVO selectArticleVOById(@Param("id") Long id);

    /**
     * 获取用户的文章列表
     */
    List<BlogArticleVO> selectArticleVOList(@Param("userId") Long userId,
                                            @Param("pageNum") Integer pageNum,
                                            @Param("pageSize") Integer pageSize);

    /**
     * 获取公开文章列表
     */
    List<BlogArticleVO> selectPublicArticleVOList(@Param("pageNum") Integer pageNum,
                                                  @Param("pageSize") Integer pageSize,
                                                  @Param("keyword") String keyword,
                                                  @Param("mood") String mood,
                                                  @Param("tag") String tag);

    /**
     * 获取文章数量
     */
    Long countArticles(@Param("userId") Long userId,
                       @Param("status") String status,
                       @Param("keyword") String keyword,
                       @Param("mood") String mood);

}