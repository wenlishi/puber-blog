package com.puber.blog.service;

import com.puber.blog.dto.CommentDTO;
import com.puber.blog.dto.CommentVO;
import com.puber.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评论业务服务接口
 * 提供评论的业务逻辑处理
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
public interface CommentService {

    /**
     * 创建评论（游客提交）
     * 记录IP地址和UserAgent
     *
     * @param dto 评论DTO
     * @param request HTTP请求对象（用于获取IP和UserAgent）
     * @return Comment 创建的评论实体
     */
    Comment createComment(CommentDTO dto, HttpServletRequest request);

    /**
     * 获取文章的已批准评论列表（包含子评论）
     * 用于前台文章详情页展示
     *
     * @param articleId 文章ID
     * @return List<CommentVO> 评论列表（树形结构）
     */
    List<CommentVO> getApprovedCommentsByArticle(Long articleId);

    /**
     * 获取待审核评论列表（分页）
     * 用于后台管理审核
     *
     * @param pageable 分页参数
     * @return Page<Comment> 待审核评论分页列表
     */
    Page<Comment> getPendingComments(Pageable pageable);

    /**
     * 获取所有评论列表（分页）
     * 用于后台管理查看所有评论
     *
     * @param pageable 分页参数
     * @return Page<Comment> 评论分页列表
     */
    Page<Comment> getAllComments(Pageable pageable);

    /**
     * 批准评论
     * 将评论状态改为APPROVED
     *
     * @param id 评论ID
     */
    void approveComment(Long id);

    /**
     * 拒绝评论
     * 将评论状态改为REJECTED
     *
     * @param id 评论ID
     */
    void rejectComment(Long id);

    /**
     * 删除评论
     *
     * @param id 评论ID
     */
    void deleteComment(Long id);

    /**
     * 统计待审核评论数量
     * 用于后台仪表盘显示
     *
     * @return Long 待审核评论数量
     */
    Long countPendingComments();

    /**
     * 统计文章的评论数量
     *
     * @param articleId 文章ID
     * @return Long 评论数量
     */
    Long countByArticleId(Long articleId);

    /**
     * 获取最新评论列表
     * 用于前台侧边栏展示
     *
     * @param limit 数量限制
     * @return List<CommentVO> 最新评论列表
     */
    List<CommentVO> getRecentComments(int limit);

    /**
     * 根据ID获取评论
     *
     * @param id 评论ID
     * @return Comment 评论实体
     */
    Comment getCommentById(Long id);
}