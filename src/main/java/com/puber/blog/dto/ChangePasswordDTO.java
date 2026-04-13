package com.puber.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改密码DTO
 * 用于修改用户密码
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordDTO {

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认新密码
     */
    private String confirmPassword;
}