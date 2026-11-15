plugins {
	java
	id("org.springframework.boot") version "3.5.16" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
	group = "com.gmail.alexei28.docproc"
	version = "1.0.0"
}

subprojects {
	apply(plugin = "java")

	repositories {
		mavenCentral()
	}

	configure<JavaPluginExtension> {
		toolchain {
			languageVersion = JavaLanguageVersion.of(21)
		}
	}

	tasks.withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		options.release = 21
	}

	tasks.withType<Test>().configureEach {
		useJUnitPlatform()
	}

	dependencies {
		"testImplementation"(
			platform("org.junit:junit-bom:5.12.2")
		)
		"testImplementation"("org.junit.jupiter:junit-jupiter")
		"testImplementation"("org.assertj:assertj-core:3.27.3")
		"testImplementation"("org.mockito:mockito-junit-jupiter:5.17.0")
	}
}
