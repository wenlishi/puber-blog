package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 标签实体类
 * 对应数据库表：tag
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Entity
@Table(name = "tag")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag extends BaseEntity {

    /**
     * 标签名称
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 标签别名（URL友好）
     */
    @Column(nullable = false, unique = true, length = 50)
    private String slug;
}