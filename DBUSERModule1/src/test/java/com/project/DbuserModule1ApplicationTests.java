package com.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DbuserModule1ApplicationTests {

    @Test
    void contextLoads() {
        // This method is intentionally empty.
        // It's used by Spring Boot to verify that the application context loads successfully.
        // If the context fails to load, this test will fail.
    }

  

    @Test
    void test_main() {
        DbuserModule1Application.main(new String[] {});
        assertTrue(true);
    }
}