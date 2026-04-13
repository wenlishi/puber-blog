package com.puber.blog.controller.admin;

import com.puber.blog.dto.ArticleDTO;
import com.puber.blog.dto.ArticleListVO;
import com.puber.blog.dto.ArticleVO;
import com.puber.blog.entity.Article;
import com.puber.blog.service.ArticleService;
import com.puber.blog.utils.FileUploadUtils;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

/**
 * 后台文章管理控制器
 * 提供文章管理的REST API
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleService articleService;

    /**
     * 获取文章列表（分页）
     *
     * @param status 文章状态（PUBLISHED/DRAFT/ALL）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return Result<Page<ArticleListVO>> 文章分页列表
     */
    @GetMapping
    public Result<Page<ArticleListVO>> getAllArticles(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("获取文章列表：status={}, page={}, size={}", status, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ArticleListVO> articles = articleService.getArticlesByStatus(status, pageable);

        return Result.success(articles);
    }

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return Result<ArticleVO> 文章详情
     */
    @GetMapping("/{id}")
    public Result<ArticleVO> getArticleById(@PathVariable Long id) {
        log.info("获取文章详情：{}", id);
        ArticleVO article = articleService.getArticleById(id);
        return Result.success(article);
    }

    /**
     * 创建文章
     *
     * @param dto 文章DTO
     * @param principal 当前登录用户
     * @return Result<Article> 创建的文章
     */
    @PostMapping
    public Result<Article> createArticle(@RequestBody ArticleDTO dto, Principal principal) {
        log.info("创建文章：{}", dto.getTitle());

        // 获取当前登录用户的ID（这里简化处理，实际应该从UserDetailsService获取）
        // TODO: 从SecurityContext获取用户ID
        Long authorId = 1L; // 暂时固定为admin用户

        Article article = articleService.createArticle(dto, authorId);
        return Result.success(article);
    }

    /**
     * 更新文章
     *
     * @param id 文章ID
     * @param dto 文章DTO
     * @return Result<Article> 更新后的文章
     */
    @PutMapping("/{id}")
    public Result<Article> updateArticle(@PathVariable Long id, @RequestBody ArticleDTO dto) {
        log.info("更新文章：{}", id);
        Article article = articleService.updateArticle(id, dto);
        return Result.success(article);
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return Result<Void> 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        log.info("删除文章：{}", id);
        articleService.deleteArticle(id);
        return Result.success();
    }

    /**
     * 置顶/取消置顶文章
     *
     * @param id 文章ID
     * @return Result<Void> 操作结果
     */
    @PutMapping("/{id}/top")
    public Result<Void> toggleTop(@PathVariable Long id) {
        log.info("切换文章置顶状态：{}", id);
        articleService.toggleTop(id);
        return Result.success();
    }

    /**
     * 上传封面图片
     *
     * @param file 图片文件
     * @return Result<String> 图片URL路径
     */
    @PostMapping("/upload-cover")
    public Result<String> uploadCoverImage(@RequestParam("file") MultipartFile file) {
        log.info("上传文章封面图片");

        // 上传文件（使用配置的上传路径）
        String uploadPath = "E:/Desktop/puber-blog/uploads";
        String imageUrl = FileUploadUtils.uploadImage(file, uploadPath);

        return Result.success(imageUrl);
    }

    /**
     * 搜索文章
     *
     * @param keyword 搜索关键字
     * @param page 页码
     * @param size 每页数量
     * @return Result<Page<ArticleListVO>> 搜索结果
     */
    @GetMapping("/search")
    public Result<Page<ArticleListVO>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("搜索文章：keyword={}", keyword);

        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleListVO> articles = articleService.searchArticles(keyword, pageable);

        return Result.success(articles);
    }
}