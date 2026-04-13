package com.puber.blog.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 后台管理控制器
 * 处理后台管理页面的请求
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Controller
public class AdminController {

    /**
     * 后台仪表盘
     *
     * @return String 视图名称
     */
    @GetMapping("/admin/dashboard")
    public String dashboard() {
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