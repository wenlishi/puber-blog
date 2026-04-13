package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 分类实体类
 * 对应数据库表：category
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Entity
@Table(name = "category")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    /**
     * 分类名称
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 分类别名（URL友好）
     */
    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    /**
     * 分类描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 排序顺序
     */
    @Column(nullable = false)
    private Integer sortOrder;
}