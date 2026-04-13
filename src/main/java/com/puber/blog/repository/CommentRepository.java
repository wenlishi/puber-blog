package com.puber.blog.repository;

import com.puber.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 评论 Repository 接口
 * 提供评论数据的 CRUD 操作
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据文章 ID 查询评论列表（分页）
     *
     * @param articleId 文章 ID
     * @param status 评论状态
     * @param pageable 分页参数
     * @return Page<Comment> 评论分页列表
     */
    Page<Comment> findByArticleIdAndStatus(Long articleId, String status, Pageable pageable);

    /**
     * 根据状态查询评论列表（分页）
     *
     * @param status 评论状态
     * @param pageable 分页参数
     * @return Page<Comment> 评论分页列表
     */
    Page<Comment> findByStatus(String status, Pageable pageable);

    /**
     * 根据父评论 ID 查询子评论列表
     *
     * @param parentId 父评论 ID
     * @param status 评论状态
     * @return List<Comment> 子评论列表
     */
    List<Comment> findByParentIdAndStatus(Long parentId, String status);

    /**
     * 统计评论数量
     *
     * @param status 评论状态
     * @return Long 评论数量
     */
    Long countByStatus(String status);

    /**
     * 统计文章的评论数量
     *
     * @param articleId 文章 ID
     * @param status 评论状态
     * @return Long 评论数量
     */
    Long countByArticleIdAndStatus(Long articleId, String status);
}