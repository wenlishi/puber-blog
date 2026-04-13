package com.puber.blog.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果封装类
 * 所有 API 接口返回的数据都使用此格式统一封装
 *
 * @param <T> 返回的数据类型
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Data
public class Result<T> implements Serializable {

    /**
     * 返回状态码
     * 200: 成功
     * 400: 客户端请求错误
     * 401: 未授权
     * 403: 无权限
     * 404: 资源不存在
     * 500: 服务器内部错误
     */
    private Integer code;

    /**
     * 返回消息
     * 成功时为 "操作成功"
     * 失败时为具体错误信息
     */
    private String message;

    /**
     * 返回数据
     * 成功时携带具体数据
     * 失败时为 null
     */
    private T data;

    /**
     * 时间戳
     * 用于调试和日志记录
     */
    private Long timestamp;

    /**
     * 默认构造函数
     */
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功返回（无数据）
     *
     * @param <T> 数据类型
     * @return Result<T> 统一返回结果
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(null);
        return result;
    }

    /**
     * 成功返回（有数据）
     *
     * @param data 返回的数据
     * @param <T> 数据类型
     * @return Result<T> 统一返回结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功返回（自定义消息和数据）
     *
     * @param message 返回消息
     * @param data 返回的数据
     * @param <T> 数据类型
     * @return Result<T> 统一返回结果
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败返回（默认错误）
     *
     * @param <T> 数据类型
     * @return Result<T> 统一返回结果
     */
    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage("操作失败");
        result.setData(null);
        return result;
    }

    /**
     * 失败返回（自定义错误消息）
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return Result<T> 统一返回结果
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    /**
     * 失败返回（自定义状态码和错误消息）
     *
     * @param code 状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return Result<T> 统一返回结果
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    /**
     * 判断是否成功
     *
     * @return true: 成功, false: 失败
     */
    public boolean isSuccess() {
        return this.code != null && this.code == 200;
    }
}