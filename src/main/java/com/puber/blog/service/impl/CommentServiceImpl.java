package com.puber.blog.service.impl;

import com.puber.blog.annotation.LogRecord;
import com.puber.blog.annotation.LogRecord.LogLevel;
import com.puber.blog.dto.CommentDTO;
import com.puber.blog.dto.CommentVO;
import com.puber.blog.entity.Article;
import com.puber.blog.entity.Comment;
import com.puber.blog.exception.BusinessException;
import com.puber.blog.repository.ArticleRepository;
import com.puber.blog.repository.CommentRepository;
import com.puber.blog.service.CommentService;
import com.puber.blog.service.MailService;
import com.puber.blog.utils.IpUtils;
import com.puber.blog.utils.MarkdownUtils;
import com.puber.blog.utils.XssPolicy;
import com.puber.blog.utils.XssUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论业务服务实现类
 * 实现评论的业务逻辑处理
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final MailService mailService;

    /**
     * 创建评论（游客提交）
     *
     * @param dto 评论DTO
     * @param request HTTP请求对象
     * @return Comment 创建的评论实体
     */
    @Override
    @Transactional
    @LogRecord(operation = "提交评论", level = LogLevel.INFO, recordParams = true, recordTime = true)
    public Comment createComment(CommentDTO dto, HttpServletRequest request) {

        // 验证必填字段
        if (dto.getNickname() == null || dto.getNickname().trim().isEmpty()) {
            throw new BusinessException(400, "昵称不能为空");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new BusinessException(400, "邮箱不能为空");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new BusinessException(400, "评论内容不能为空");
        }
        if (dto.getArticleId() == null) {
            throw new BusinessException(400, "文章ID不能为空");
        }

        // 获取IP地址和UserAgent
        String ipAddress = IpUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // XSS 过滤：使用严格策略过滤评论内容（移除危险标签，保留纯文本）
        // 注意：评论内容是纯文本，不需要转换为 HTML
        String safeContent = XssUtils.sanitizeStrict(dto.getContent().trim());

        // 创建评论实体
        Comment comment = Comment.builder()
                .nickname(dto.getNickname().trim())
                .email(dto.getEmail().trim())
                .website(dto.getWebsite() != null ? dto.getWebsite().trim() : null)
                .content(safeContent)
                .articleId(dto.getArticleId())
                .parentId(dto.getParentId())
                .replyToId(dto.getReplyToId())
                .status("PENDING")  // 默认待审核
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        // 保存评论
        Comment savedComment = commentRepository.save(comment);

        // 发送新评论通知邮件给站长
        try {
            // 获取文章标题
            Article article = articleRepository.findById(dto.getArticleId()).orElse(null);
            if (article != null) {
                String articleTitle = article.getTitle();
                mailService.sendNewCommentNotification(dto, articleTitle);
            }
        } catch (Exception e) {
            // 邮件发送失败不影响评论提交
            log.warn("发送评论通知邮件失败：{}", e.getMessage());
        }

        return savedComment;
    }

    /**
     * 获取文章的已批准评论列表（树形结构）
     *
     * @param articleId 文章ID
     * @return List<CommentVO> 评论列表
     */
    @Override
    public List<CommentVO> getApprovedCommentsByArticle(Long articleId) {
        log.debug("获取文章已批准评论：articleId={}", articleId);

        // 查询该文章的所有已批准评论
        List<Comment> comments = commentRepository.findByArticleIdAndStatus(articleId, "APPROVED");

        // 构建树形结构
        return buildCommentTree(comments);
    }

    /**
     * 获取待审核评论列表（分页）
     *
     * @param pageable 分页参数
     * @return Page<Comment> 待审核评论分页列表
     */
    @Override
    public Page<Comment> getPendingComments(Pageable pageable) {
        log.debug("获取待审核评论列表");

        return commentRepository.findByStatus("PENDING", pageable);
    }

    /**
     * 获取所有评论列表（分页）
     *
     * @param pageable 分页参数
     * @return Page<Comment> 评论分页列表
     */
    @Override
    public Page<Comment> getAllComments(Pageable pageable) {
        log.debug("获取所有评论列表");

        return commentRepository.findAll(pageable);
    }

    /**
     * 批准评论
     *
     * @param id 评论ID
     */
    @Override
    @Transactional
    @LogRecord(operation = "批准评论", level = LogLevel.INFO, recordParams = true, recordTime = true)
    public void approveComment(Long id) {
        Comment comment = getCommentById(id);
        comment.setStatus("APPROVED");
        commentRepository.save(comment);
    }

    /**
     * 拒绝评论
     *
     * @param id 评论ID
     */
    @Override
    @Transactional
    @LogRecord(operation = "拒绝评论", level = LogLevel.WARN, recordParams = true, recordTime = true)
    public void rejectComment(Long id) {

        Comment comment = getCommentById(id);
        comment.setStatus("REJECTED");
        commentRepository.save(comment);
    }

    /**
     * 删除评论
     *
     * @param id 评论ID
     */
    @Override
    @Transactional
    @LogRecord(operation = "删除评论", level = LogLevel.WARN, recordParams = true, recordTime = true)
    public void deleteComment(Long id) {
        log.info("删除评论：id={}", id);

        Comment comment = getCommentById(id);

        // 删除该评论的所有子评论
        List<Comment> childComments = commentRepository.findByParentIdAndStatus(id, null);
        if (!childComments.isEmpty()) {
            commentRepository.deleteAll(childComments);
        }

        // 删除评论本身
        commentRepository.delete(comment);
    }

    /**
     * 统计待审核评论数量
     *
     * @return Long 待审核评论数量
     */
    @Override
    public Long countPendingComments() {
        return commentRepository.countByStatus("PENDING");
    }

    /**
     * 统计文章的评论数量
     *
     * @param articleId 文章ID
     * @return Long 评论数量
     */
    @Override
    public Long countByArticleId(Long articleId) {
        return commentRepository.countByArticleIdAndStatus(articleId, "APPROVED");
    }

    /**
     * 获取最新评论列表
     *
     * @param limit 数量限制
     * @return List<CommentVO> 最新评论列表
     */
    @Override
    public List<CommentVO> getRecentComments(int limit) {
        log.debug("获取最新评论：limit={}", limit);

        // 查询最新的已批准评论
        Pageable pageable = Pageable.ofSize(limit);
        List<Comment> comments = commentRepository.findByStatusOrderByCreatedAtDesc("APPROVED", pageable);

        return comments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取评论
     *
     * @param id 评论ID
     * @return Comment 评论实体
     */
    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "评论不存在：" + id));
    }

    /**
     * 管理员回复评论
     * 添加回复内容并设置回复时间
     *
     * @param id 评论ID
     * @param content 回复内容
     */
    @Override
    @Transactional
    @LogRecord(operation = "回复评论", level = LogLevel.INFO, recordParams = true, recordTime = true)
    public void replyComment(Long id, String content) {
        log.info("管理员回复评论：id={}, content={}", id, content);

        // 验证回复内容
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(400, "回复内容不能为空");
        }

        // 获取评论
        Comment comment = getCommentById(id);

        // XSS过滤回复内容
        String safeContent = XssUtils.sanitizeStrict(content.trim());

        // 设置回复内容和时间
        comment.setReplyContent(safeContent);
        comment.setReplyTime(java.time.LocalDateTime.now());

        // 保存评论
        commentRepository.save(comment);
    }

    /**
     * 构建评论树形结构
     * 将评论按照父子关系组织成树形结构
     *
     * @param comments 评论列表
     * @return List<CommentVO> 树形评论列表
     */
    private List<CommentVO> buildCommentTree(List<Comment> comments) {
        // 分离出顶级评论（没有父评论的评论）
        List<Comment> topLevelComments = comments.stream()
                .filter(c -> c.getParentId() == null)
                .collect(Collectors.toList());

        // 按父评论ID分组所有子评论
        Map<Long, List<Comment>> childCommentsMap = comments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentId));

        // 构建树形结构
        return topLevelComments.stream()
                .map(comment -> {
                    CommentVO vo = convertToVO(comment);

                    // 添加子评论
                    List<Comment> children = childCommentsMap.getOrDefault(comment.getId(), new ArrayList<>());
                    List<CommentVO> childVOs = children.stream()
                            .map(child -> {
                                CommentVO childVO = convertToVO(child);

                                // 如果有回复目标ID，设置回复目标昵称
                                if (child.getReplyToId() != null) {
                                    Comment replyToComment = commentRepository.findById(child.getReplyToId()).orElse(null);
                                    if (replyToComment != null) {
                                        childVO.setReplyToNickname(replyToComment.getNickname());
                                    }
                                }

                                return childVO;
                            })
                            .collect(Collectors.toList());

                    vo.setReplies(childVOs);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 将Comment实体转换为CommentVO
     *
     * @param comment 评论实体
     * @return CommentVO 评论视图对象
     */
    private CommentVO convertToVO(Comment comment) {
        return CommentVO.builder()
                .id(comment.getId())
                .nickname(comment.getNickname())
                .email(comment.getEmail())
                .website(comment.getWebsite())
                .content(comment.getContent())
                .status(comment.getStatus())
                .createdAt(comment.getCreatedAt())
                .replies(new ArrayList<>())  // 默认空列表
                .build();
    }
}