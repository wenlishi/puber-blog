package com.puber.blog.controller.admin;

import com.puber.blog.dto.ChangePasswordDTO;
import com.puber.blog.dto.UserProfileDTO;
import com.puber.blog.entity.User;
import com.puber.blog.service.UserService;
import com.puber.blog.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 后台用户管理控制器
 * 提供用户个人信息管理的页面和 REST API
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Slf4j
@Controller
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * 个人信息管理页面
     *
     * @param model Spring MVC 模型
     * @return String 视图名称
     */
    @GetMapping("/profile")
    public String profilePage(Model model) {
        log.info("访问个人信息管理页面");

        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.getUserByUsername(username);

        // 转换为DTO
        UserProfileDTO profileDTO = UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .role(user.getRole())
                .build();

        model.addAttribute("user", profileDTO);

        return "admin/profile";
    }

    /**
     * 获取当前用户信息（API）
     *
     * @return Result<UserProfileDTO> 用户信息
     */
    @GetMapping("/api/profile")
    @ResponseBody
    public Result<UserProfileDTO> getProfile() {
        log.info("获取当前用户信息");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.getUserByUsername(username);

        UserProfileDTO profileDTO = UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .role(user.getRole())
                .build();

        return Result.success(profileDTO);
    }

    /**
     * 更新个人信息（API）
     *
     * @param profileDTO 个人信息DTO
     * @return Result<User> 更新后的用户对象
     */
    @PostMapping("/api/profile")
    @ResponseBody
    public Result<User> updateProfile(@RequestBody UserProfileDTO profileDTO) {
        log.info("更新个人信息：nickname={}, email={}", profileDTO.getNickname(), profileDTO.getEmail());

        // 获取当前登录用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);

        // 更新个人信息
        User user = userService.updateProfile(
                currentUser.getId(),
                profileDTO.getNickname(),
                profileDTO.getEmail(),
                profileDTO.getBio(),
                profileDTO.getAvatar()
        );

        return Result.success(user);
    }

    /**
     * 修改密码（API）
     *
     * @param passwordDTO 修改密码DTO
     * @return Result<Void> 操作结果
     */
    @PostMapping("/api/change-password")
    @ResponseBody
    public Result<Void> changePassword(@RequestBody ChangePasswordDTO passwordDTO) {
        log.info("修改密码");

        // 验证新密码和确认密码是否一致
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            return Result.error("新密码和确认密码不一致");
        }

        // 获取当前登录用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);

        // 修改密码
        userService.changePassword(
                currentUser.getId(),
                passwordDTO.getOldPassword(),
                passwordDTO.getNewPassword()
        );

        return Result.success();
    }

    /**
     * 更新头像（API）
     *
     * @param avatarUrl 头像URL
     * @return Result<User> 更新后的用户对象
     */
    @PostMapping("/api/avatar")
    @ResponseBody
    public Result<User> updateAvatar(@RequestParam String avatarUrl) {
        log.info("更新头像：avatarUrl={}", avatarUrl);

        // 获取当前登录用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.getUserByUsername(username);

        // 更新头像
        User user = userService.updateAvatar(currentUser.getId(), avatarUrl);

        return Result.success(user);
    }
}