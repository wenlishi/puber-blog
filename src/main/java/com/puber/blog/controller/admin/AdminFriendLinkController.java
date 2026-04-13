package com.puber.blog.controller.admin;

import com.puber.blog.entity.FriendLink;
import com.puber.blog.service.FriendLinkService;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台友链管理控制器
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Controller
@RequestMapping("/admin/friend-links")
@RequiredArgsConstructor
public class AdminFriendLinkController {

    private final FriendLinkService friendLinkService;

    /**
     * 友链管理页面
     *
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping
    public String friendLinksPage(Model model) {
        log.info("访问友链管理页面");

        List<FriendLink> friendLinks = friendLinkService.getAllFriendLinks();
        model.addAttribute("friendLinks", friendLinks);

        return "admin/friend-links";
    }

    /**
     * 获取所有友链（API）
     *
     * @return Result<List<FriendLink>> 友链列表
     */
    @GetMapping("/api")
    @ResponseBody
    public Result<List<FriendLink>> getAllFriendLinks() {
        log.info("获取所有友链");

        List<FriendLink> friendLinks = friendLinkService.getAllFriendLinks();
        return Result.success(friendLinks);
    }

    /**
     * 根据ID获取友链（API）
     *
     * @param id 友链ID
     * @return Result<FriendLink> 友链实体
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public Result<FriendLink> getFriendLinkById(@PathVariable Long id) {
        log.info("根据ID获取友链：{}", id);

        FriendLink friendLink = friendLinkService.getFriendLinkById(id);
        return Result.success(friendLink);
    }

    /**
     * 创建友链（API）
     *
     * @param friendLink 友链实体
     * @return Result<FriendLink> 创建的友链
     */
    @PostMapping("/api")
    @ResponseBody
    public Result<FriendLink> createFriendLink(@RequestBody FriendLink friendLink) {
        log.info("创建友链：name={}, url={}", friendLink.getName(), friendLink.getUrl());

        FriendLink createdFriendLink = friendLinkService.createFriendLink(friendLink);
        return Result.success(createdFriendLink);
    }

    /**
     * 更新友链（API）
     *
     * @param id 友链ID
     * @param friendLink 友链实体
     * @return Result<FriendLink> 更新后的友链
     */
    @PutMapping("/api/{id}")
    @ResponseBody
    public Result<FriendLink> updateFriendLink(@PathVariable Long id, @RequestBody FriendLink friendLink) {
        log.info("更新友链：id={}", id);

        FriendLink updatedFriendLink = friendLinkService.updateFriendLink(id, friendLink);
        return Result.success(updatedFriendLink);
    }

    /**
     * 删除友链（API）
     *
     * @param id 友链ID
     * @return Result<Void> 操作结果
     */
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public Result<Void> deleteFriendLink(@PathVariable Long id) {
        log.info("删除友链：id={}", id);

        friendLinkService.deleteFriendLink(id);
        return Result.success();
    }

    /**
     * 启用友链（API）
     *
     * @param id 友链ID
     * @return Result<Void> 操作结果
     */
    @PutMapping("/api/{id}/activate")
    @ResponseBody
    public Result<Void> activateFriendLink(@PathVariable Long id) {
        log.info("启用友链：id={}", id);

        friendLinkService.activateFriendLink(id);
        return Result.success();
    }

    /**
     * 禁用友链（API）
     *
     * @param id 友链ID
     * @return Result<Void> 操作结果
     */
    @PutMapping("/api/{id}/deactivate")
    @ResponseBody
    public Result<Void> deactivateFriendLink(@PathVariable Long id) {
        log.info("禁用友链：id={}", id);

        friendLinkService.deactivateFriendLink(id);
        return Result.success();
    }
}