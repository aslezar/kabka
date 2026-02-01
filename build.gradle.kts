// Root project configuration for multi-module Kabka project
import com.diffplug.gradle.spotless.SpotlessExtension
plugins {
	java
	id("org.springframework.boot") version "4.0.1" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
	id("com.diffplug.spotless") version "6.25.0" apply false
}

group = "dev.kabka"
version = "0.0.1-SNAPSHOT"
description = "Kafka ka bhai - A distributed messaging system"

// Common configuration for all subprojects
subprojects {
	apply(plugin = "java")
	apply(plugin = "com.diffplug.spotless")
	
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

	configure<SpotlessExtension> {
		java {
			eclipse()
			importOrder()
			removeUnusedImports()
			endWithNewline()
		}
	}
}
