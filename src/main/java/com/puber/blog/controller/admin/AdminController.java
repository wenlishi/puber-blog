package com.puber.blog.controller.admin;

import com.puber.blog.service.CategoryService;
import com.puber.blog.service.DashboardService;
import com.puber.blog.service.TagService;
import com.puber.blog.vo.DashboardVO;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 后台管理控制器
 * 处理后台管理页面的请求
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;
    private final CategoryService categoryService;
    private final TagService tagService;

    /**
     * 后台仪表盘
     *
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        log.info("访问后台仪表盘");

        // 获取仪表盘统计数据
        DashboardVO dashboardData = dashboardService.getDashboardData();

        // 将统计数据添加到模型（方便模板直接访问）
        model.addAttribute("dashboard", dashboardData);
        model.addAttribute("totalViews", dashboardData.getArticleStats().getTotalViewCount());
        model.addAttribute("articleCount", dashboardData.getArticleStats().getPublishedCount());
        model.addAttribute("commentCount", dashboardData.getCommentStats().getTotalCount());
        model.addAttribute("pendingComments", dashboardData.getCommentStats().getPendingCount());
        model.addAttribute("topArticles", dashboardData.getHotArticles());
        model.addAttribute("weeklyViews", dashboardData.getArticleStats().getTotalViewCount());

        return "admin/dashboard";
    }

    /**
     * 分类管理页面
     *
     * @return String 视图名称
     */
    @GetMapping("/admin/categories")
    public String categories() {
        return "admin/categories";
    }

    /**
     * 标签管理页面
     *
     * @return String 视图名称
     */
    @GetMapping("/admin/tags")
    public String tags() {
        return "admin/tags";
    }

    /**
     * 文章管理页面
     *
     * @return String 视图名称
     */
    @GetMapping("/admin/articles")
    public String articles() {
        return "admin/articles";
    }

    /**
     * 新增文章页面
     *
     * @return String 视图名称
     */
    @GetMapping("/admin/articles/new")
    public String newArticle() {
        return "admin/article-form";
    }

    /**
     * 编辑文章页面
     *
     * @return String 视图名称
     */
    @GetMapping("/admin/articles/{id}/edit")
    public String editArticle() {
        return "admin/article-form";
    }

    /**
     * 评论管理页面
     *
     * @return String 视图名称
     */
    @GetMapping("/admin/comments")
    public String comments() {
        return "admin/comments";
    }
}

/**
 * 后台 API 控制器
 * 提供后台页面的数据 API
 */
@Slf4j
@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
class AdminApiController {

    private final CategoryService categoryService;
    private final TagService tagService;

    /**
     * 获取所有分类列表
     *
     * @return Result<List> 分类列表
     */
    @GetMapping("/categories")
    public Result<List<?>> getCategories() {
        log.debug("获取所有分类列表");
        return Result.success((List<?>) categoryService.getAllCategories());
    }

    /**
     * 获取所有标签列表
     *
     * @return Result<List> 标签列表
     */
    @GetMapping("/tags")
    public Result<List<?>> getTags() {
        log.debug("获取所有标签列表");
        return Result.success((List<?>) tagService.getAllTags());
    }
}
