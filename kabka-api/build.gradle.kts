// Kabka API - REST API layer for managing the messaging system
plugins {
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

dependencies {
	// Depend on kabka-core
	implementation(project(":kabka-core"))
	
	// Spring Boot for REST API
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	
	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-resttestclient")
	testImplementation("org.springframework.boot:spring-boot-restclient")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
