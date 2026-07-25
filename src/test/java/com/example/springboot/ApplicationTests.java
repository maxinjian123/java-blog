package com.example.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestRedisConfig.class)
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}