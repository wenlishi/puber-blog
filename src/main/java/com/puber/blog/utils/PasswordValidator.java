package com.puber.blog.utils;

import com.puber.blog.exception.BusinessException;

/**
 * 密码验证工具类
 * 用于验证密码强度，防止弱密码
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-15
 */
public class PasswordValidator {

    /**
     * 最小密码长度
     */
    private static final int MIN_LENGTH = 8;

    /**
     * 验证密码强度
     * 规则：
     * 1. 长度至少8位
     * 2. 包含至少1个大写字母
     * 3. 包含至少1个小写字母
     * 4. 包含至少1个数字
     *
     * @param password 待验证的密码
     * @throws BusinessException 如果密码不符合要求
     */
    public static void validate(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException(400, "密码不能为空");
        }

        // 检查长度
        if (password.length() < MIN_LENGTH) {
            throw new BusinessException(400, "密码长度至少" + MIN_LENGTH + "位");
        }

        // 检查大写字母
        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessException(400, "密码必须包含至少1个大写字母");
        }

        // 检查小写字母
        if (!password.matches(".*[a-z].*")) {
            throw new BusinessException(400, "密码必须包含至少1个小写字母");
        }

        // 检查数字
        if (!password.matches(".*\\d.*")) {
            throw new BusinessException(400, "密码必须包含至少1个数字");
        }
    }

    /**
     * 验证密码强度（返回布尔值，不抛异常）
     *
     * @param password 待验证的密码
     * @return true: 符合强度要求, false: 不符合
     */
    public static boolean isValid(String password) {
        try {
            validate(password);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * 获取密码强度提示信息
     *
     * @return String 密码强度要求说明
     */
    public static String getRequirementHint() {
        return "密码要求：至少8位，包含大小写字母和数字";
    }
}