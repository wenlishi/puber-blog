package com.puber.blog.service;

import com.puber.blog.entity.User;

/**
 * 用户业务服务接口
 * 提供用户的业务逻辑处理
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
public interface UserService {

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return User 用户对象
     */
    User getUserByUsername(String username);

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return User 用户对象
     */
    User getUserById(Long id);

    /**
     * 更新用户个人信息
     * 包括：昵称、邮箱、简介、头像
     *
     * @param id 用户ID
     * @param nickname 昵称
     * @param email 邮箱
     * @param bio 简介
     * @param avatar 头像URL
     * @return User 更新后的用户对象
     */
    User updateProfile(Long id, String nickname, String email, String bio, String avatar);

    /**
     * 修改密码
     *
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return boolean 是否成功
     */
    boolean changePassword(Long id, String oldPassword, String newPassword);

    /**
     * 更新头像
     *
     * @param id 用户ID
     * @param avatarUrl 头像URL
     * @return User 更新后的用户对象
     */
    User updateAvatar(Long id, String avatarUrl);
}