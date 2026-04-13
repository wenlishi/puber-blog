package com.puber.blog.controller.api;

import com.puber.blog.dto.CommentDTO;
import com.puber.blog.dto.CommentVO;
import com.puber.blog.entity.Comment;
import com.puber.blog.service.CommentService;
import com.puber.blog.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台公开评论控制器
 * 提供游客评论功能的REST API
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@RestController
@RequestMapping("/api/public/comments")
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    /**
     * 提交评论（游客）
     *
     * @param dto 评论DTO
     * @param request HTTP请求对象
     * @return Result<Comment> 创建的评论
     */
    @PostMapping
    public Result<Comment> createComment(@RequestBody CommentDTO dto, HttpServletRequest request) {
        log.info("游客提交评论：articleId={}, nickname={}", dto.getArticleId(), dto.getNickname());

        Comment comment = commentService.createComment(dto, request);
        return Result.success(comment);
    }

    /**
     * 获取文章的评论列表
     *
     * @param articleId 文章ID
     * @return Result<List<CommentVO>> 评论列表（树形结构）
     */
    @GetMapping("/article/{articleId}")
    public Result<List<CommentVO>> getArticleComments(@PathVariable Long articleId) {
        log.info("获取文章评论列表：articleId={}", articleId);

        List<CommentVO> comments = commentService.getApprovedCommentsByArticle(articleId);
        return Result.success(comments);
    }
}