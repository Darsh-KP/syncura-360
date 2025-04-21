package com.syncura360;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Test if Spring can start the app with all its beans (controllers, services, repositories, configs, etc.).
// If there’s any misconfiguration (e.g. circular dependencies, missing beans, bad @Values), it will fail.
@SpringBootTest
class Syncura360SpringApplicationTests {
	// Test passes as long as the application context loads without errors.
	@Test
	void contextLoads() {
	}
}
