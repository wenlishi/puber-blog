package com.puber.blog.service.impl;

import com.puber.blog.entity.SiteSetting;
import com.puber.blog.exception.BusinessException;
import com.puber.blog.repository.SiteSettingRepository;
import com.puber.blog.service.SiteSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网站配置业务服务实现类
 * 实现网站配置的业务逻辑处理
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SiteSettingServiceImpl implements SiteSettingService {

    private final SiteSettingRepository siteSettingRepository;

    /**
     * 获取所有网站配置
     *
     * @return List<SiteSetting> 网站配置列表
     */
    @Override
    public List<SiteSetting> getAllSettings() {
        log.debug("获取所有网站配置");

        return siteSettingRepository.findAll();
    }

    /**
     * 根据配置键获取配置值
     *
     * @param key 配置键
     * @return String 配置值
     */
    @Override
    public String getSettingValue(String key) {
        log.debug("获取配置值：key={}", key);

        SiteSetting setting = siteSettingRepository.findBySettingKey(key)
                .orElse(null);

        return setting != null ? setting.getSettingValue() : null;
    }

    /**
     * 根据配置键获取配置值（带默认值）
     *
     * @param key 配置键
     * @param defaultValue 默认值
     * @return String 配置值
     */
    @Override
    public String getSettingValue(String key, String defaultValue) {
        log.debug("获取配置值：key={}, defaultValue={}", key, defaultValue);

        String value = getSettingValue(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 更新网站配置
     * 更新后清除缓存，确保下次查询获取最新数据
     *
     * @param key 配置键
     * @param value 配置值
     * @return SiteSetting 更新后的配置对象
     */
    @Override
    @Transactional
    @CacheEvict(value = "siteInfo", allEntries = true)
    public SiteSetting updateSetting(String key, String value) {
        log.info("更新网站配置：key={}, value={}", key, value);

        SiteSetting setting = siteSettingRepository.findBySettingKey(key)
                .orElseThrow(() -> new BusinessException("配置项不存在：" + key));

        setting.setSettingValue(value);
        return siteSettingRepository.save(setting);
    }

    /**
     * 批量更新网站配置
     * 更新后清除缓存，确保下次查询获取最新数据
     *
     * @param settings 配置Map（key-value）
     * @return void
     */
    @Override
    @Transactional
    @CacheEvict(value = "siteInfo", allEntries = true)
    public void batchUpdateSettings(Map<String, String> settings) {
        log.info("批量更新网站配置：{}", settings);

        for (Map.Entry<String, String> entry : settings.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            SiteSetting setting = siteSettingRepository.findBySettingKey(key).orElse(null);

            if (setting != null) {
                setting.setSettingValue(value);
                siteSettingRepository.save(setting);
            } else {
                log.info("创建新配置项：key={}", key);
                SiteSetting newSetting = SiteSetting.builder()
                        .settingKey(key)
                        .settingValue(value)
                        .settingType("STRING")
                        .build();
                siteSettingRepository.save(newSetting);
            }
        }
    }

    /**
     * 获取网站基本信息（用于前端展示）
     * 包括：网站名称、描述、关键词、页脚文字等
     * 使用缓存减少数据库查询频率
     *
     * @return Map<String, String> 网站基本信息Map
     */
    @Override
    @Cacheable(value = "siteInfo", key = "'siteInfo'")
    public Map<String, String> getSiteInfo() {
        log.debug("获取网站基本信息");

        Map<String, String> siteInfo = new HashMap<>();

        // 厷取主要配置项
        siteInfo.put("site_name", getSettingValue("site_name", "My Blog"));
        siteInfo.put("site_description", getSettingValue("site_description", "A personal blog built with Spring Boot"));
        siteInfo.put("site_keywords", getSettingValue("site_keywords", "blog, spring boot, java"));
        siteInfo.put("footer_text", getSettingValue("footer_text", "Powered by Spring Boot"));
        siteInfo.put("posts_per_page", getSettingValue("posts_per_page", "10"));

        // 获取邮件配置项
        siteInfo.put("mail_enabled", getSettingValue("mail_enabled", "false"));
        siteInfo.put("mail_smtp_host", getSettingValue("mail_smtp_host", ""));
        siteInfo.put("mail_smtp_port", getSettingValue("mail_smtp_port", "465"));
        siteInfo.put("mail_smtp_username", getSettingValue("mail_smtp_username", ""));
        siteInfo.put("mail_smtp_password", getSettingValue("mail_smtp_password", ""));
        siteInfo.put("mail_admin_email", getSettingValue("mail_admin_email", ""));
        siteInfo.put("site_url", getSettingValue("site_url", "http://localhost:8080"));

        return siteInfo;
    }
}