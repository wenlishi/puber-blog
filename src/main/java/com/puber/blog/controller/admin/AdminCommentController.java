package com.puber.blog.controller.admin;

import com.puber.blog.dto.CommentVO;
import com.puber.blog.entity.Comment;
import com.puber.blog.service.CommentService;
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

    /**
     * 获取所有评论列表（分页）
     *
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @return Result<Page<Comment>> 评论分页列表
     */
    @GetMapping
    public Result<Page<Comment>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("获取所有评论列表：page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> comments = commentService.getAllComments(pageable);

        return Result.success(comments);
    }

    /**
     * 获取待审核评论列表（分页）
     *
     * @param page 页码
     * @param size 每页数量
     * @return Result<Page<Comment>> 待审核评论分页列表
     */
    @GetMapping("/pending")
    public Result<Page<Comment>> getPendingComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("获取待审核评论列表：page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> comments = commentService.getPendingComments(pageable);

        return Result.success(comments);
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
}