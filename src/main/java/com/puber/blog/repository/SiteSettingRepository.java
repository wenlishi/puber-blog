package com.puber.blog.repository;

import com.puber.blog.entity.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 网站配置 Repository 接口
 * 提供网站配置数据的 CRUD 操作
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Repository
public interface SiteSettingRepository extends JpaRepository<SiteSetting, Long> {

    /**
     * 根据配置键查询配置
     *
     * @param settingKey 配置键
     * @return Optional<SiteSetting> 配置对象
     */
    Optional<SiteSetting> findBySettingKey(String settingKey);

    /**
     * 检查配置键是否存在
     *
     * @param settingKey 配置键
     * @return true: 存在, false: 不存在
     */
    boolean existsBySettingKey(String settingKey);
}