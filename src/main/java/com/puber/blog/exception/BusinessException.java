package com.puber.blog.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于封装业务逻辑中出现的异常情况
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 异常状态码
     */
    private final Integer code;

    /**
     * 异常消息
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param message 异常消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param code 异常状态码
     * @param message 异常消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param code 异常状态码
     * @param message 异常消息
     * @param cause 异常原因
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}