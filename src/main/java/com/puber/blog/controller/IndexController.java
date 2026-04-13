package com.puber.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 * 处理前台首页和相关公开页面的请求
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Controller
public class IndexController {

    /**
     * 首页
     *
     * @return String 视图名称
     */
    @GetMapping("/")
    public String index() {
        return "front/index";
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