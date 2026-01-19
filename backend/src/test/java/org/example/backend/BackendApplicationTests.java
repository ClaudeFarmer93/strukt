package org.example.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires MongoDB connection - enable when test database is configured")
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
