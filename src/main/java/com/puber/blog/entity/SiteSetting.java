package com.puber.blog.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 网站配置实体类
 * 对应数据库表：site_setting
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Entity
@Table(name = "site_setting")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteSetting extends BaseEntity {

    /**
     * 配置键
     */
    @Column(nullable = false, unique = true, length = 100)
    private String settingKey;

    /**
     * 配置值
     */
    @Column(columnDefinition = "TEXT")
    private String settingValue;

    /**
     * 配置类型
     * STRING: 字符串
     * INTEGER: 整数
     * BOOLEAN: 布尔值
     * JSON: JSON 对象
     */
    @Column(nullable = false, length = 20)
    private String settingType;

    /**
     * 配置描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;
}