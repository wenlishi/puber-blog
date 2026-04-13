package com.puber.blog.service;

import com.puber.blog.entity.SiteSetting;

import java.util.List;
import java.util.Map;

/**
 * 网站配置业务服务接口
 * 提供网站配置的业务逻辑处理
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
public interface SiteSettingService {

    /**
     * 获取所有网站配置
     *
     * @return List<SiteSetting> 网站配置列表
     */
    List<SiteSetting> getAllSettings();

    /**
     * 根据配置键获取配置值
     *
     * @param key 配置键
     * @return String 配置值
     */
    String getSettingValue(String key);

    /**
     * 根据配置键获取配置值（带默认值）
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return String 配置值
     */
    String getSettingValue(String key, String defaultValue);

    /**
     * 更新网站配置
     *
     * @param key 配置键
     * @param value 配置值
     * @return SiteSetting 更新后的配置对象
     */
    SiteSetting updateSetting(String key, String value);

    /**
     * 批量更新网站配置
     *
     * @param settings 配置Map（key-value）
     * @return void
     */
    void batchUpdateSettings(Map<String, String> settings);

    /**
     * 获取网站基本信息（用于前端展示）
     * 包括：网站名称、描述、关键词、页脚文字等
     *
     * @return Map<String, String> 网站基本信息Map
     */
    Map<String, String> getSiteInfo();
}