plugins {
    `java-library`
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.16")
    }
}

dependencies {
    api(project(":aws-docproc-domain"))

    implementation(platform("software.amazon.awssdk:bom:2.41.1"))

    implementation("org.springframework:spring-context")
    implementation("software.amazon.awssdk:sqs")
    implementation("org.mongodb:mongodb-driver-sync")
    implementation("com.fasterxml.jackson.core:jackson-databind")
}