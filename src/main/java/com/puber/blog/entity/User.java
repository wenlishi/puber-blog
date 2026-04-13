package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 用户实体类
 * 对应数据库表：blog_user
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Entity
@Table(name = "blog_user")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    /**
     * 用户名（登录名）
     * 唯一，不可重复
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 密码
     * 使用 BCrypt 加密存储
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 昵称
     * 用于显示的名称
     */
    @Column(length = 50)
    private String nickname;

    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;

    /**
     * 头像 URL
     */
    @Column(length = 255)
    private String avatar;

    /**
     * 个人简介
     */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * 用户角色
     * ROLE_ADMIN: 管理员
     * ROLE_USER: 普通用户
     */
    @Column(nullable = false, length = 20)
    private String role;

    /**
     * 是否启用
     * true: 启用, false: 禁用
     */
    @Column(nullable = false)
    private Boolean enabled;
}