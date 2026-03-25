package com.nebula.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.nebula.config.result.Result;
import com.nebula.model.dto.BlogNoteCreateDTO;
import com.nebula.model.dto.BlogNoteUpdateDTO;
import com.nebula.model.dto.BlogNoteQueryDTO;
import com.nebula.model.vo.BlogArticleVO;
import com.nebula.service.blog.BlogNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 日常碎碎念控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/blog/note")
@RequiredArgsConstructor
@Tag(name = "日常碎碎念", description = "日常碎碎念相关接口")
public class BlogNoteController {

    private final BlogNoteService blogNoteService;

    /**
     * 创建日常碎碎念
     */
    @PostMapping
    @Operation(summary = "创建日常碎碎念", description = "创建一条新的日常碎碎念")
    public Result<Long> createBlogNote(@Valid @RequestBody BlogNoteCreateDTO dto) {
        try {
            StpUtil.checkLogin();
            Long userId = StpUtil.getLoginIdAsLong();

            Long noteId = blogNoteService.createBlogNote(dto, userId);
            log.info("创建日常碎碎念成功: userId={}, noteId={}", userId, noteId);
            return Result.success(noteId);
        } catch (Exception e) {
            log.error("创建日常碎碎念失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新日常碎碎念
     */
    @PutMapping
    @Operation(summary = "更新日常碎碎念", description = "更新指定的日常碎碎念")
    public Result<Void> updateBlogNote(@Valid @RequestBody BlogNoteUpdateDTO dto) {
        try {
            StpUtil.checkLogin();
            Long userId = StpUtil.getLoginIdAsLong();

            blogNoteService.updateBlogNote(dto, userId);
            log.info("更新日常碎碎念成功: userId={}, noteId={}", userId, dto.getId());
            return Result.success("更新成功");
        } catch (Exception e) {
            log.error("更新日常碎碎念失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我的碎碎念详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取日常碎碎念详情", description = "获取指定ID的日常碎碎念详情")
    public Result<BlogArticleVO> getBlogNote(@PathVariable Long id) {
        try {
            StpUtil.checkLogin();
            Long userId = StpUtil.getLoginIdAsLong();

            BlogArticleVO vo = blogNoteService.getBlogNoteById(id, userId);
            if (vo == null) {
                return Result.error("笔记不存在或无权限");
            }
            return Result.success(vo);
        } catch (Exception e) {
            log.error("获取日常碎碎念详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取我的碎碎念列表
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的碎碎念列表", description = "获取当前用户的日常碎碎念列表")
    public Result<List<BlogArticleVO>> getMyBlogNotes(BlogNoteQueryDTO query) {
        try {
            StpUtil.checkLogin();
            Long userId = StpUtil.getLoginIdAsLong();

            List<BlogArticleVO> list = blogNoteService.getMyBlogNotes(userId, query);
            return Result.success(list);
        } catch (Exception e) {
            log.error("获取我的碎碎念列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取公开碎碎念列表
     */
    @GetMapping("/public")
    @Operation(summary = "获取公开碎碎念列表", description = "获取公开的日常碎碎念列表")
    public Result<List<BlogArticleVO>> getPublicBlogNotes(BlogNoteQueryDTO query) {
        try {
            List<BlogArticleVO> list = blogNoteService.getPublicBlogNotes(query);
            return Result.success(list);
        } catch (Exception e) {
            log.error("获取公开碎碎念列表失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除日常碎碎念
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除日常碎碎念", description = "删除指定的日常碎碎念（逻辑删除）")
    public Result<Void> deleteBlogNote(@PathVariable Long id) {
        try {
            StpUtil.checkLogin();
            Long userId = StpUtil.getLoginIdAsLong();

            blogNoteService.deleteBlogNote(id, userId);
            log.info("删除日常碎碎念成功: userId={}, noteId={}", userId, id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除日常碎碎念失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取碎碎念数量
     */
    @GetMapping("/count")
    @Operation(summary = "获取碎碎念数量", description = "获取当前用户的日常碎碎念数量")
    public Result<Long> countBlogNotes() {
        try {
            StpUtil.checkLogin();
            Long userId = StpUtil.getLoginIdAsLong();

            Long count = blogNoteService.countBlogNotes(userId, null);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取碎碎念数量失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据心情获取碎碎念数量
     */
    @GetMapping("/count/mood/{mood}")
    @Operation(summary = "根据心情获取碎碎念数量", description = "获取当前用户特定心情的日常碎碎念数量")
    public Result<Long> countBlogNotesByMood(@PathVariable String mood) {
        try {
            StpUtil.checkLogin();
            Long userId = StpUtil.getLoginIdAsLong();

            Long count = blogNoteService.countBlogNotes(userId, mood);
            return Result.success(count);
        } catch (Exception e) {
            log.error("获取碎碎念数量失败", e);
            return Result.error(e.getMessage());
        }
    }

}
