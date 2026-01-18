// Kabka Core - The messaging engine (no web dependencies)
dependencies {
	// Core Java utilities
	implementation("org.slf4j:slf4j-api:2.0.17")
	implementation("ch.qos.logback:logback-classic:1.5.22")
	
	// For future: serialization, compression, etc.
	// implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
	
	// Testing
	testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
	testImplementation("org.mockito:mockito-core:5.15.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
