package com.puber.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 安全配置类
 * 配置认证授权规则、密码加密器等
 *
 * @author puber
 * @version 1.0.0
 * @since 2026-04-13
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 密码加密器
     * 使用 BCrypt 加密算法
     *
     * @return PasswordEncoder 密码加密器实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤链配置
     * 定义哪些 URL 需要认证，哪些可以公开访问
     *
     * @param http HttpSecurity 配置对象
     * @return SecurityFilterChain 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 配置授权规则
            .authorizeHttpRequests(auth -> auth
                // 公开访问的路径（前台页面、静态资源）
                .requestMatchers("/", "/index", "/article/**", "/category/**", "/tag/**", "/archive", "/about", "/search")
                .permitAll()
                // 静态资源
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/uploads/**")
                .permitAll()
                // 登录、注册相关页面
                .requestMatchers("/login", "/register", "/perform_login", "/perform_logout")
                .permitAll()
                // 后台管理页面需要认证
                .requestMatchers("/admin/**")
                .hasRole("ADMIN")
                // API 接口权限
                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")
                .requestMatchers("/api/public/**")
                .permitAll()
                // 其他所有请求都需要认证
                .anyRequest()
                .authenticated()
            )
            // 配置表单登录
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            // 配置登出
            .logout(logout -> logout
                .logoutUrl("/perform_logout")
                .logoutSuccessUrl("/login?logout=true")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
            )
            // 禁用 CSRF（开发阶段，生产环境建议启用）
            .csrf(csrf -> csrf.disable())
            // 配置 Session 管理
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
            );

        return http.build();
    }
}