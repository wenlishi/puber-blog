package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章标签关联实体类
 * 对应数据库表：article_tag
 * 用于实现文章和标签的多对多关系
 * 注意：此表不需要updated_at字段，因此不继承BaseEntity的updatedAt
 * 实现 Serializable 接口以支持缓存序列化
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Entity
@Table(name = "article_tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ArticleTag implements Serializable {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文章ID
     */
    @Column(nullable = false)
    private Long articleId;

    /**
     * 标签ID
     */
    @Column(nullable = false)
    private Long tagId;

    /**
     * 创建时间
     * 自动填充
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 序列化版本号
     * 用于确保序列化/反序列化的兼容性
     */
    private static final long serialVersionUID = 1L;
}