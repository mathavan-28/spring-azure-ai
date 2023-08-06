package com.rai.online.aidemo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Disabled
@SpringBootTest
@ActiveProfiles("dev")
class AidemoApplicationTests {

    @Test
    void contextLoads() {
        Assertions.assertNotNull("Empty","String should not be null");
    }
}
