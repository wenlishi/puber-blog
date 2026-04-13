package com.puber.blog.service;

import com.puber.blog.entity.User;
import com.puber.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

/**
 * 自定义用户认证服务
 * 实现 Spring Security 的 UserDetailsService 接口
 * 用于从数据库加载用户信息进行认证
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 根据用户名加载用户信息
     * Spring Security 在认证时会调用此方法
     *
     * @param username 用户名
     * @return UserDetails Spring Security 的用户详情对象
     * @throws UsernameNotFoundException 用户未找到异常
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("正在加载用户: {}", username);

        // 从数据库查询用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("用户不存在: {}", username);
                    return new UsernameNotFoundException("用户不存在: " + username);
                });

        // 检查用户是否启用
        if (!user.getEnabled()) {
            log.warn("用户已被禁用: {}", username);
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }

        // 构建 Spring Security 的 UserDetails 对象
        return buildUserDetails(user);
    }

    /**
     * 构建 Spring Security 的 UserDetails 对象
     *
     * @param user 数据库用户实体
     * @return UserDetails Spring Security 用户详情对象
     */
    private UserDetails buildUserDetails(User user) {
        // 将用户的角色转换为 Spring Security 的权限
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole())
        );

        log.debug("用户 {} 的角色权限: {}", user.getUsername(), authorities);

        // 返回 Spring Security 的 User 对象（注意：这里的 User 是 Spring Security 的类）
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,  // accountNonLocked
                authorities
        );
    }
}