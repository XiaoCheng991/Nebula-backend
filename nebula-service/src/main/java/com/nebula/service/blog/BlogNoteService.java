package com.nebula.service.blog;

import com.nebula.model.dto.BlogNoteCreateDTO;
import com.nebula.model.dto.BlogNoteQueryDTO;
import com.nebula.model.dto.BlogNoteUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 日常碎碎念服务
 */
@Service
@RequiredArgsConstructor
public class BlogNoteService {

    private final com.nebula.service.mapper.blog.BlogArticleMapper blogArticleMapper;
    private final com.nebula.service.mapper.SysUserMapper sysUserMapper;

    /**
     * 创建日常碎碎念
     */
    public Long createBlogNote(BlogNoteCreateDTO dto, Long userId) {
        // 这里我们复用BlogArticle的实体和Mapper
        // 因为日常碎碎念就是博客的一种特殊形式
        com.nebula.model.entity.blog.BlogArticle article = new com.nebula.model.entity.blog.BlogArticle();
        article.setAuthorId(userId);
        article.setAuthorName(getUserNickname(userId));
        article.setContent(dto.getContent());
        article.setMood(dto.getMood());
        article.setTags(dto.getTags());
        article.setLocation(dto.getLocation());
        article.setWeather(dto.getWeather());
        article.setIsPublic(dto.getIsPublic() != null && dto.getIsPublic());
        article.setStatus(dto.getStatus() != null ? dto.getStatus() : "PUBLISHED"); // 碎碎念默认直接发布
        article.setIsTop(false);
        article.setIsRecommended(false);
        article.setIsCommentEnabled(true);
        article.setWordCount(dto.getContent() != null ? dto.getContent().length() : 0);
        article.setPublishTime(java.time.OffsetDateTime.now());

        blogArticleMapper.insert(article);
        return article.getId();
    }

    /**
     * 更新日常碎碎念
     */
    public void updateBlogNote(BlogNoteUpdateDTO dto, Long userId) {
        com.nebula.model.entity.blog.BlogArticle article = blogArticleMapper.selectById(dto.getId());
        if (article == null || !article.getAuthorId().equals(userId)) {
            throw new RuntimeException("笔记不存在或无权限");
        }

        article.setContent(dto.getContent());
        article.setMood(dto.getMood());
        article.setTags(dto.getTags());
        article.setLocation(dto.getLocation());
        article.setWeather(dto.getWeather());
        article.setIsPublic(dto.getIsPublic() != null && dto.getIsPublic());
        article.setWordCount(dto.getContent() != null ? dto.getContent().length() : 0);
        article.setUpdateTime(java.time.OffsetDateTime.now());

        blogArticleMapper.updateById(article);
    }

    /**
     * 获取我的碎碎念列表
     */
    public com.nebula.model.vo.BlogArticleVO getBlogNoteById(Long id, Long userId) {
        com.nebula.model.vo.BlogArticleVO vo = blogArticleMapper.selectArticleVOById(id);
        if (vo == null || !vo.getUserId().equals(userId)) {
            return null;
        }
        // 设置是否已点赞（这里简化处理，实际可能需要查询点赞表）
        vo.setIsLiked(false);
        return vo;
    }

    /**
     * 获取我的碎碎念列表
     */
    public List<com.nebula.model.vo.BlogArticleVO> getMyBlogNotes(Long userId, BlogNoteQueryDTO query) {
        return blogArticleMapper.selectArticleVOList(
            userId,
            query.getPageNum(),
            query.getPageSize()
        );
    }

    /**
     * 获取公开碎碎念列表
     */
    public List<com.nebula.model.vo.BlogArticleVO> getPublicBlogNotes(BlogNoteQueryDTO query) {
        return blogArticleMapper.selectPublicArticleVOList(
            query.getPageNum(),
            query.getPageSize(),
            query.getKeyword() != null ? query.getKeyword() : "",
            query.getMood() != null ? query.getMood() : "",
            query.getTag() != null ? query.getTag() : ""
        );
    }

    /**
     * 删除日常碎碎念（逻辑删除）
     */
    public void deleteBlogNote(Long id, Long userId) {
        com.nebula.model.entity.blog.BlogArticle article = blogArticleMapper.selectById(id);
        if (article == null || !article.getAuthorId().equals(userId)) {
            throw new RuntimeException("笔记不存在或无权限");
        }
        article.setDeleted(0);
        article.setUpdateTime(java.time.OffsetDateTime.now());
        blogArticleMapper.updateById(article);
    }

    /**
     * 获取用户昵称
     */
    private String getUserNickname(Long userId) {
        com.nebula.model.entity.SysUser user = sysUserMapper.selectById(userId);
        return user != null ? user.getNickname() : "未知用户";
    }

    /**
     * 获取碎碎念数量
     */
    public Long countBlogNotes(Long userId, String mood) {
        return blogArticleMapper.countArticles(userId,
            "PUBLISHED",
            null,
            mood);
    }
}
