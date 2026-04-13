package com.puber.blog.controller.admin;

import com.puber.blog.entity.SiteSetting;
import com.puber.blog.service.SiteSettingService;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 后台系统设置控制器
 * 提供系统设置页面的访问和 REST API
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Controller
@RequestMapping("/admin/settings")
@RequiredArgsConstructor
public class AdminSettingController {

    private final SiteSettingService siteSettingService;

    /**
     * 系统设置页面
     *
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping
    public String settingsPage(Model model) {
        log.info("访问系统设置页面");

        // 获取所有配置
        List<SiteSetting> settings = siteSettingService.getAllSettings();

        // 将配置列表添加到模型
        model.addAttribute("settings", settings);

        return "admin/settings";
    }

    /**
     * 获取所有网站配置（API）
     *
     * @return Result<List<SiteSetting>> 配置列表
     */
    @GetMapping("/api")
    @ResponseBody
    public Result<List<SiteSetting>> getAllSettings() {
        log.info("获取所有网站配置");

        List<SiteSetting> settings = siteSettingService.getAllSettings();
        return Result.success(settings);
    }

    /**
     * 获取网站基本信息（API）
     *
     * @return Result<Map<String, String>> 网站基本信息
     */
    @GetMapping("/api/site-info")
    @ResponseBody
    public Result<Map<String, String>> getSiteInfo() {
        log.info("获取网站基本信息");

        Map<String, String> siteInfo = siteSettingService.getSiteInfo();
        return Result.success(siteInfo);
    }

    /**
     * 更新单个配置项（API）
     *
     * @param key 配置键
     * @param value 配置值
     * @return Result<SiteSetting> 更新后的配置对象
     */
    @PutMapping("/api/{key}")
    @ResponseBody
    public Result<SiteSetting> updateSetting(
            @PathVariable String key,
            @RequestParam String value) {
        log.info("更新配置项：key={}, value={}", key, value);

        SiteSetting setting = siteSettingService.updateSetting(key, value);
        return Result.success(setting);
    }

    /**
     * 批量更新配置（API）
     *
     * @param settings 配置Map（key-value）
     * @return Result<Void> 操作结果
     */
    @PostMapping("/api/batch")
    @ResponseBody
    public Result<Void> batchUpdateSettings(@RequestBody Map<String, String> settings) {
        log.info("批量更新配置：{}", settings);

        siteSettingService.batchUpdateSettings(settings);
        return Result.success();
    }
}