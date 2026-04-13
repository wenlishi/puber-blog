package com.puber.blog;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PasswordTest {

    @Test
    public void generatePasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("========================================");
        System.out.println("原始密码: " + rawPassword);
        System.out.println("BCrypt 哈希: " + encodedPassword);
        System.out.println("========================================");
        System.out.println();
        System.out.println("请执行以下 SQL 更新数据库：");
        System.out.println("UPDATE blog_user SET password = '" + encodedPassword + "' WHERE username = 'admin';");
        System.out.println();

        // 验证数据库中的当前哈希
        String currentHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH";
        boolean matches = encoder.matches(rawPassword, currentHash);
        System.out.println("当前数据库哈希验证结果: " + matches);
    }
}