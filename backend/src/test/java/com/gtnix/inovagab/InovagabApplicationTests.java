package com.gtnix.inovagab;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class InovagabApplicationTests {

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri",
                () -> System.getenv().getOrDefault("MONGODB_URI", "mongodb://localhost:27017/inovagab-test"));
    }

    @Test
    void contextLoads() {
    }
}
