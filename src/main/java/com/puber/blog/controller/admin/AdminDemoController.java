package com.puber.blog.controller.admin;

import com.puber.blog.dto.DemoDTO;
import com.puber.blog.dto.DemoVO;
import com.puber.blog.entity.Demo;
import com.puber.blog.entity.User;
import com.puber.blog.service.DemoService;
import com.puber.blog.service.UserService;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 演示管理控制器（后台管理）
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-16
 */
@Slf4j
@Controller
@RequestMapping("/admin/demos")
@RequiredArgsConstructor
public class AdminDemoController {

    private final DemoService demoService;
    private final UserService userService;

    /**
     * 演示管理页面
     *
     * @return String 视图名称
     */
    @GetMapping
    public String demosPage(Model model) {
        log.info("访问演示管理页面");
        return "admin/demos";
    }

    /**
     * 新增演示页面
     *
     * @return String 视图名称
     */
    @GetMapping("/new")
    public String newDemo() {
        log.info("访问新增演示页面");
        return "admin/demo-form";
    }

    /**
     * 编辑演示页面
     *
     * @return String 视图名称
     */
    @GetMapping("/{id}/edit")
    public String editDemo() {
        log.info("访问编辑演示页面");
        return "admin/demo-form";
    }
}

/**
 * 演示管理API控制器（后台管理）
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-16
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/demos")
@RequiredArgsConstructor
class AdminDemoApiController {

    private final DemoService demoService;
    private final UserService userService;

    /**
     * 获取演示列表（分页）
     *
     * @param status 状态（ALL/PUBLISHED/DRAFT）
     * @param page 页码
     * @param size 每页数量
     * @return Result<Page<DemoVO>> 演示分页列表
     */
    @GetMapping
    public Result<Page<DemoVO>> getAllDemos(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("获取演示列表：status={}, page={}, size={}", status, page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DemoVO> demos = demoService.getDemosByStatus(status, pageable);

        return Result.success(demos);
    }

    /**
     * 获取演示详情
     *
     * @param id 演示ID
     * @return Result<DemoVO> 演示详情
     */
    @GetMapping("/{id}")
    public Result<DemoVO> getDemoById(@PathVariable Long id) {
        log.info("获取演示详情：{}", id);

        DemoVO demo = demoService.getDemoById(id);
        return Result.success(demo);
    }

    /**
     * 创建演示
     *
     * @param dto 演示DTO
     * @return Result<Demo> 创建结果
     */
    @PostMapping
    public Result<Demo> createDemo(@RequestBody DemoDTO dto) {
        log.info("创建演示：{}", dto.getName());

        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);

        Demo demo = demoService.createDemo(dto, currentUser.getId());
        return Result.success(demo);
    }

    /**
     * 更新演示
     *
     * @param id 演示ID
     * @param dto 演示DTO
     * @return Result<Demo> 更新结果
     */
    @PutMapping("/{id}")
    public Result<Demo> updateDemo(@PathVariable Long id, @RequestBody DemoDTO dto) {
        log.info("更新演示：{}", id);

        Demo demo = demoService.updateDemo(id, dto);
        return Result.success(demo);
    }

    /**
     * 删除演示
     *
     * @param id 演示ID
     * @return Result<Void> 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteDemo(@PathVariable Long id) {
        log.info("删除演示：{}", id);

        demoService.deleteDemo(id);
        return Result.success();
    }
}