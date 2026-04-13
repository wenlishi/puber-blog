package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户个人信息DTO
 * 用于更新用户个人信息
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名（登录名，不可修改）
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 用户角色
     */
    private String role;
}