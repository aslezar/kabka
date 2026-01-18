// Root project configuration for multi-module Kabka project
plugins {
	java
	id("org.springframework.boot") version "4.0.1" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "dev.kabka"
version = "0.0.1-SNAPSHOT"
description = "Kafka ka bhai - A distributed messaging system"

// Common configuration for all subprojects
subprojects {
	apply(plugin = "java")
	
	group = "dev.kabka"
	version = rootProject.version
	
	java {
		toolchain {
			languageVersion = JavaLanguageVersion.of(25)
		}
	}
	
	repositories {
		mavenCentral()
	}
	
	tasks.withType<Test> {
		useJUnitPlatform()
	}
}
