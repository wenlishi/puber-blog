package com.puber.blog.controller;

import com.puber.blog.dto.CategoryVO;
import com.puber.blog.dto.DemoVO;
import com.puber.blog.dto.TagVO;
import com.puber.blog.entity.FriendLink;
import com.puber.blog.exception.BusinessException;
import com.puber.blog.service.CategoryService;
import com.puber.blog.service.DemoService;
import com.puber.blog.service.FriendLinkService;
import com.puber.blog.service.SiteSettingService;
import com.puber.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
 * 演示控制器（前台展示）
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-16
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class DemoController {

    private final DemoService demoService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final SiteSettingService siteSettingService;
    private final FriendLinkService friendLinkService;

    /**
     * 为所有页面添加通用数据
     *
     * @param model Spring MVC 模型
     */
    @ModelAttribute
    public void addSiteSettings(Model model) {
        Map<String, String> siteInfo = siteSettingService.getSiteInfo();
        model.addAttribute("siteName", siteInfo.getOrDefault("siteName", "puber-blog"));
        model.addAttribute("siteDescription", siteInfo.getOrDefault("siteDescription", "个人博客系统"));

        List<CategoryVO> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        List<TagVO> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);

        List<FriendLink> friendLinks = friendLinkService.getActiveFriendLinks();
        model.addAttribute("friendLinks", friendLinks);
    }

    /**
     * 演示详情页面
     *
     * @param slug 演示别名
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping("/demo/{slug}")
    public String viewDemo(@PathVariable String slug, Model model) {
        log.info("访问演示页面：slug={}", slug);

        try {
            DemoVO demo = demoService.getDemoBySlug(slug);
            model.addAttribute("demo", demo);
            return "demo/view";
        } catch (BusinessException e) {
            if (e.getCode() == 404) {
                log.warn("演示不存在：{}", slug);
                model.addAttribute("error", "演示不存在");
                return "front/error/404";
            }
            throw e;
        }
    }

    /**
     * 演示嵌入页面（iframe专用，无框架）
     * 不增加浏览量统计，避免重复计数
     *
     * @param slug 演示别名
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping("/demo/{slug}/embed")
    public String embedDemo(@PathVariable String slug, Model model) {
        log.info("访问演示嵌入页面（iframe）：slug={}", slug);

        try {
            DemoVO demo = demoService.getDemoBySlugWithoutViewCount(slug);
            model.addAttribute("demo", demo);
            return "demo/embed";
        } catch (BusinessException e) {
            if (e.getCode() == 404) {
                log.warn("演示不存在：{}", slug);
                return "front/error/404";
            }
            throw e;
        }
    }
}