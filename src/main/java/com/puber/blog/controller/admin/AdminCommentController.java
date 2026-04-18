package com.puber.blog.controller.admin;

import com.puber.blog.dto.AdminCommentVO;
import com.puber.blog.dto.CommentVO;
import com.puber.blog.entity.Article;
import com.puber.blog.entity.Comment;
import com.puber.blog.repository.ArticleRepository;
import com.puber.blog.service.CommentService;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台评论管理控制器
 * 提供评论审核和管理的REST API
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;
    private final ArticleRepository articleRepository;

    /**
     * 获取所有评论列表（分页）
     *
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return Result<Page<AdminCommentVO>> 评论分页列表
     */
    @GetMapping
    public Result<Page<AdminCommentVO>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("获取所有评论列表：page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> comments = commentService.getAllComments(pageable);

        Page<AdminCommentVO> commentVOs = comments.map(this::convertToAdminCommentVO);

        return Result.success(commentVOs);
    }

    /**
     * 获取待审核评论列表（分页）
     *
     * @param page 页码
     * @param size 每页数量
     * @return Result<Page<AdminCommentVO>> 待审核评论分页列表
     */
    @GetMapping("/pending")
    public Result<Page<AdminCommentVO>> getPendingComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("获取待审核评论列表：page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> comments = commentService.getPendingComments(pageable);

        Page<AdminCommentVO> commentVOs = comments.map(this::convertToAdminCommentVO);

        return Result.success(commentVOs);
    }

    /**
     * 批准评论
     *
     * @param id 评论ID
     * @return Result<Void> 操作结果
     */
    @PutMapping("/{id}/approve")
    public Result<Void> approveComment(@PathVariable Long id) {
        log.info("批准评论：id={}", id);

        commentService.approveComment(id);
        return Result.success();
    }

    /**
     * 拒绝评论
     *
     * @param id 评论ID
     * @return Result<Void> 操作结果
     */
    @PutMapping("/{id}/reject")
    public Result<Void> rejectComment(@PathVariable Long id) {
        log.info("拒绝评论：id={}", id);

        commentService.rejectComment(id);
        return Result.success();
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     * @return Result<Void> 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        log.info("删除评论：id={}", id);

        commentService.deleteComment(id);
        return Result.success();
    }

    /**
     * 统计待审核评论数量
     *
     * @return Result<Long> 待审核评论数量
     */
    @GetMapping("/count/pending")
    public Result<Long> countPendingComments() {
        log.info("统计待审核评论数量");

        Long count = commentService.countPendingComments();
        return Result.success(count);
    }

    /**
     * 批量批准评论
     *
     * @param ids 评论ID列表
     * @return Result<Void> 操作结果
     */
    @PutMapping("/batch-approve")
    public Result<Void> batchApproveComments(@RequestBody List<Long> ids) {
        log.info("批量批准评论：ids={}", ids);

        for (Long id : ids) {
            commentService.approveComment(id);
        }

        return Result.success();
    }

    /**
     * 批量拒绝评论
     *
     * @param ids 评论ID列表
     * @return Result<Void> 操作结果
     */
    @PutMapping("/batch-reject")
    public Result<Void> batchRejectComments(@RequestBody List<Long> ids) {
        log.info("批量拒绝评论：ids={}", ids);

        for (Long id : ids) {
            commentService.rejectComment(id);
        }

        return Result.success();
    }

    /**
     * 管理员回复评论
     *
     * @param id 评论ID
     * @param requestBody 回复内容请求体
     * @return Result<Void> 操作结果
     */
    @PostMapping("/{id}/reply")
    public Result<Void> replyComment(@PathVariable Long id, @RequestBody Map<String, String> requestBody) {
        log.info("管理员回复评论：id={}", id);

        String content = requestBody.get("content");
        if (content == null || content.trim().isEmpty()) {
            return Result.error(400, "回复内容不能为空");
        }

        commentService.replyComment(id, content);
        return Result.success();
    }

    /**
     * 将Comment实体转换为AdminCommentVO
     *
     * @param comment 评论实体
     * @return AdminCommentVO 后台评论视图对象
     */
    private AdminCommentVO convertToAdminCommentVO(Comment comment) {
        String articleTitle = null;
        if (comment.getArticleId() != null) {
            Article article = articleRepository.findById(comment.getArticleId()).orElse(null);
            if (article != null) {
                articleTitle = article.getTitle();
            }
        }

        return AdminCommentVO.builder()
                .id(comment.getId())
                .nickname(comment.getNickname())
                .email(comment.getEmail())
                .website(comment.getWebsite())
                .content(comment.getContent())
                .status(comment.getStatus())
                .articleId(comment.getArticleId())
                .articleTitle(articleTitle)
                .replyContent(comment.getReplyContent())
                .replyTime(comment.getReplyTime())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}