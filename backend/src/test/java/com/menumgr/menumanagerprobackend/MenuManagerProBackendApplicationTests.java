package com.menumgr.menumanagerprobackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "security.jwt.secret=test-only-jwt-key-not-for-deployment-1234567890")
class MenuManagerProBackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
