package com.puber.blog.service.impl;

import com.puber.blog.entity.User;
import com.puber.blog.exception.BusinessException;
import com.puber.blog.repository.UserRepository;
import com.puber.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户业务服务实现类
 * 实现用户的业务逻辑处理
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return User 用户对象
     */
    @Override
    public User getUserByUsername(String username) {
        log.debug("根据用户名获取用户：username={}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在：" + username));
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return User 用户对象
     */
    @Override
    public User getUserById(Long id) {
        log.debug("根据ID获取用户：id={}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
    }

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
    @Override
    @Transactional
    public User updateProfile(Long id, String nickname, String email, String bio, String avatar) {
        log.info("更新用户个人信息：id={}, nickname={}, email={}", id, nickname, email);

        User user = getUserById(id);

        // 检查邮箱是否已被其他用户使用
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new BusinessException("邮箱已被使用");
            }
            user.setEmail(email);
        }

        // 更新信息
        if (nickname != null) {
            user.setNickname(nickname);
        }
        if (bio != null) {
            user.setBio(bio);
        }
        if (avatar != null) {
            user.setAvatar(avatar);
        }

        return userRepository.save(user);
    }

    /**
     * 修改密码
     *
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return boolean 是否成功
     */
    @Override
    @Transactional
    public boolean changePassword(Long id, String oldPassword, String newPassword) {
        log.info("修改密码：id={}", id);

        User user = getUserById(id);

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }

        // 加密新密码并更新
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        userRepository.save(user);

        log.info("密码修改成功");
        return true;
    }

    /**
     * 更新头像
     *
     * @param id 用户ID
     * @param avatarUrl 头像URL
     * @return User 更新后的用户对象
     */
    @Override
    @Transactional
    public User updateAvatar(Long id, String avatarUrl) {
        log.info("更新头像：id={}, avatarUrl={}", id, avatarUrl);

        User user = getUserById(id);
        user.setAvatar(avatarUrl);

        return userRepository.save(user);
    }
}