package com.puber.blog.controller;

import com.puber.blog.dto.ArticleListVO;
import com.puber.blog.dto.ArticleVO;
import com.puber.blog.dto.CategoryVO;
import com.puber.blog.dto.TagVO;
import com.puber.blog.entity.Category;
import com.puber.blog.entity.FriendLink;
import com.puber.blog.entity.Tag;
import com.puber.blog.exception.BusinessException;
import com.puber.blog.service.ArticleService;
import com.puber.blog.service.CategoryService;
import com.puber.blog.service.FriendLinkService;
import com.puber.blog.service.SiteSettingService;
import com.puber.blog.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * 首页控制器
 * 处理前台首页和相关公开页面的请求
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class IndexController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final SiteSettingService siteSettingService;
    private final FriendLinkService friendLinkService;

    /**
     * 为所有页面添加通用数据
     * 包括网站配置、分类列表、标签列表、友链列表
     *
     * @param model Spring MVC 模型
     */
    @ModelAttribute
    public void addSiteSettings(Model model) {
        // 获取网站基本信息
        Map<String, String> siteInfo = siteSettingService.getSiteInfo();
        model.addAttribute("siteName", siteInfo.getOrDefault("siteName", "puber-blog"));
        model.addAttribute("siteDescription", siteInfo.getOrDefault("siteDescription", "个人博客系统"));

        // 获取所有分类（用于导航栏）
        List<CategoryVO> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        // 获取所有标签（用于导航栏）
        List<TagVO> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);

        // 获取所有启用的友链（用于侧边栏）
        List<FriendLink> friendLinks = friendLinkService.getActiveFriendLinks();
        model.addAttribute("friendLinks", friendLinks);
    }

    /**
     * 首页（文章列表）
     *
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        log.info("访问首页：page={}, size={}", page, size);

        // 获取已发布文章列表（置顶优先，时间倒序）
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleListVO> articles = articleService.getPublishedArticles(pageable);

        model.addAttribute("articles", articles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        model.addAttribute("totalElements", articles.getTotalElements());

        return "front/index";
    }

    /**
     * 文章详情页
     *
     * @param slug 文章别名
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping("/article/{slug}")
    public String article(@PathVariable String slug, Model model) {
        log.info("访问文章详情：slug={}", slug);

        try {
            // 获取文章详情（自动触发浏览量+1）
            ArticleVO article = articleService.getArticleBySlug(slug);
            model.addAttribute("article", article);
            return "front/article";
        } catch (BusinessException e) {
            if (e.getCode() == 404) {
                log.warn("文章不存在：{}", slug);
                model.addAttribute("error", "文章不存在");
                return "front/error/404";
            }
            throw e;
        }
    }

    /**
     * 分类文章列表
     *
     * @param slug 分类别名
     * @param page 页码
     * @param size 每页数量
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping("/category/{slug}")
    public String category(@PathVariable String slug,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          Model model) {
        log.info("访问分类文章列表：slug={}, page={}", slug, page);

        try {
            // 根据slug获取分类信息
            Category category = categoryService.getCategoryBySlug(slug);
            model.addAttribute("category", category);

            // 获取该分类下的文章列表
            Pageable pageable = PageRequest.of(page, size);
            Page<ArticleListVO> articles = articleService.getArticlesByCategory(category.getId(), pageable);

            model.addAttribute("articles", articles);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", articles.getTotalPages());

            return "front/category";
        } catch (BusinessException e) {
            if (e.getCode() == 404) {
                log.warn("分类不存在：{}", slug);
                model.addAttribute("error", "分类不存在");
                return "front/error/404";
            }
            throw e;
        }
    }

    /**
     * 标签文章列表
     *
     * @param slug 标签别名
     * @param page 页码
     * @param size 每页数量
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping("/tag/{slug}")
    public String tag(@PathVariable String slug,
                     @RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "10") int size,
                     Model model) {
        log.info("访问标签文章列表：slug={}, page={}", slug, page);

        try {
            // 根据slug获取标签信息
            Tag tag = tagService.getTagBySlug(slug);
            model.addAttribute("tag", tag);

            // 获取该标签下的文章列表
            Pageable pageable = PageRequest.of(page, size);
            Page<ArticleListVO> articles = articleService.getArticlesByTag(tag.getId(), pageable);

            model.addAttribute("articles", articles);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", articles.getTotalPages());

            return "front/tag";
        } catch (BusinessException e) {
            if (e.getCode() == 404) {
                log.warn("标签不存在：{}", slug);
                model.addAttribute("error", "标签不存在");
                return "front/error/404";
            }
            throw e;
        }
    }

    /**
     * 文章搜索
     *
     * @param keyword 搜索关键字
     * @param page 页码
     * @param size 每页数量
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String keyword,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {
        log.info("文章搜索：keyword={}, page={}", keyword, page);

        if (keyword == null || keyword.trim().isEmpty()) {
            model.addAttribute("keyword", "");
            model.addAttribute("articles", Page.empty());
            return "front/search";
        }

        // 搜索文章
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleListVO> articles = articleService.searchArticles(keyword.trim(), pageable);

        model.addAttribute("keyword", keyword.trim());
        model.addAttribute("articles", articles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        model.addAttribute("totalElements", articles.getTotalElements());

        return "front/search";
    }

    /**
     * 归档页面
     *
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping("/archive")
    public String archive(Model model) {
        log.info("访问归档页面");

        // 获取归档数据（按年份-月份分组）
        Map<Integer, Map<Integer, List<ArticleListVO>>> archive = articleService.getArchiveArticles();

        // 计算文章总数
        long totalArticles = 0;
        for (Map<Integer, List<ArticleListVO>> monthMap : archive.values()) {
            for (List<ArticleListVO> articleList : monthMap.values()) {
                totalArticles += articleList.size();
            }
        }

        model.addAttribute("archive", archive);
        model.addAttribute("totalArticles", totalArticles);

        return "front/archive";
    }

    /**
     * 登录页面
     *
     * @return String 视图名称
     */
    @GetMapping("/login")
    public String login() {
        return "front/login";
    }
}