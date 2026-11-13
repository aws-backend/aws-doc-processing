plugins {
	java
	id("org.springframework.boot") version "4.1.2" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
	group = "com.gmail.alexei28.docproc"
	version = "1.0.0"
}

subprojects {
	apply(plugin = "java")

	extensions.configure<JavaPluginExtension> {
		toolchain {
			languageVersion = JavaLanguageVersion.of(21)
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	repositories {
		mavenCentral()
	}
}