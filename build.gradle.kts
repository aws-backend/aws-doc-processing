plugins {
	java
	id("org.springframework.boot") version "3.5.16" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
	group = "com.gmail.alexei28.aws.docproc"
	version = "1.0.0"
}

subprojects {
	apply(plugin = "java")

	extensions.configure<JavaPluginExtension> {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(21))
		}
	}

	repositories {
		mavenCentral()
	}
}