package com.puber.blog.aspect;

import com.puber.blog.annotation.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 业务操作日志记录切面
 * 自动拦截标注了@LogRecord注解的方法，记录操作日志
 *
 * 功能特性：
 * - 自动记录方法名称、参数、执行时间
 * - 自动捕获异常并记录异常信息
 * - 支持配置日志级别、是否记录参数/返回值
 * - 生成结构化的运营数据日志
 *
 * 示例日志输出：
 * INFO - 操作：发布文章 | 类：ArticleServiceImpl | 方法：publishArticle | 参数：[{title=测试文章}] | 状态：开始执行
 * INFO - 操作：发布文章 | 状态：成功 | 耗时：125ms
 * ERROR - 操作：发布文章 | 状态：失败 | 耗时：5000ms | 异常：数据库连接超时
 *
 * @author wenlishi
 * @version 1.0.0
 * @since 2026-04-14
 */
@Slf4j
@Aspect
@Component
public class LogRecordAspect {

    /**
     * 环绕通知：拦截方法调用前后
     * 自动记录操作开始、执行时间、参数、结果、异常
     */
    @Around("@annotation(com.puber.blog.annotation.LogRecord)")
    public Object recordOperationLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // 获取注解配置
        LogRecord annotation = signature.getMethod().getAnnotation(LogRecord.class);
        String operation = annotation.operation();
        boolean recordParams = annotation.recordParams();
        boolean recordResult = annotation.recordResult();
        boolean recordTime = annotation.recordTime();
        LogRecord.LogLevel level = annotation.level();

        // 构建开始日志
        long startTime = System.currentTimeMillis();
        StringBuilder startLog = new StringBuilder();

        startLog.append("操作：").append(operation.isEmpty() ? methodName : operation);
        startLog.append(" | 类：").append(className);
        startLog.append(" | 方法：").append(methodName);

        if (recordParams) {
            Object[] args = joinPoint.getArgs();
            // 过滤掉HttpServletRequest等非业务参数
            Object[] filteredArgs = Arrays.stream(args)
                    .filter(arg -> arg != null && !arg.getClass().getName().contains("HttpServletRequest"))
                    .toArray();
            startLog.append(" | 参数：").append(Arrays.toString(filteredArgs));
        }

        startLog.append(" | 状态：开始执行");
        logAtLevel(level, startLog.toString());

        // 执行目标方法
        Object result = null;
        Throwable exception = null;

        try {
            result = joinPoint.proceed();  // 执行业务方法

            // 记录成功日志
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            StringBuilder successLog = new StringBuilder();
            successLog.append("操作：").append(operation.isEmpty() ? methodName : operation);
            successLog.append(" | 状态：成功");

            if (recordTime) {
                successLog.append(" | 耗时：").append(duration).append("ms");
            }

            if (recordResult && result != null) {
                // 简化返回值显示，避免日志过大
                String resultStr = result.toString();
                if (resultStr.length() > 100) {
                    resultStr = resultStr.substring(0, 100) + "...";
                }
                successLog.append(" | 结果：").append(resultStr);
            }

            logAtLevel(level, successLog.toString());

            return result;

        } catch (Throwable e) {
            exception = e;

            // 记录失败日志
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            StringBuilder errorLog = new StringBuilder();
            errorLog.append("操作：").append(operation.isEmpty() ? methodName : operation);
            errorLog.append(" | 状态：失败");

            if (recordTime) {
                errorLog.append(" | 耗时：").append(duration).append("ms");
            }

            errorLog.append(" | 异常：").append(e.getClass().getSimpleName());
            errorLog.append(" - ").append(e.getMessage());

            // ERROR级别必须记录堆栈
            log.error(errorLog.toString(), e);

            throw e;
        }
    }

    /**
     * 根据配置的日志级别输出日志
     */
    private void logAtLevel(LogRecord.LogLevel level, String message) {
        switch (level) {
            case TRACE:
                log.trace(message);
                break;
            case DEBUG:
                log.debug(message);
                break;
            case INFO:
                log.info(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case ERROR:
                log.error(message);
                break;
            default:
                log.info(message);
        }
    }
}