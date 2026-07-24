package com.example.springboot.config;

import cn.hutool.crypto.digest.BCrypt;
import com.example.springboot.common.constant.RoleConstant;
import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        User existing = userService.findByUsername("admin");
        if (existing == null) {
            String hashedPassword = BCrypt.hashpw("admin123");
            User admin = User.builder()
                    .id("1")
                    .username("admin")
                    .password(hashedPassword)
                    .nickname("博主")
                    .email("admin@blog.com")
                    .avatar("https://cdn.com/a.jpg")
                    .bio("个人简介")
                    .role(RoleConstant.ADMIN)
                    .build();
            userService.save(admin);
            log.info("默认管理员用户已创建: username=admin, password=admin123");
        }
    }
}