package com.puber.blog.repository;

import com.puber.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户 Repository 接口
 * 提供用户数据的 CRUD 操作
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return Optional<User> 用户对象
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return Optional<User> 用户对象
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true: 存在, false: 不存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return true: 存在, false: 不存在
     */
    boolean existsByEmail(String email);
}