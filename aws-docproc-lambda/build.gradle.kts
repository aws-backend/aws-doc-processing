plugins {
    java
    id("io.spring.dependency-management")
}

dependencies {
    // Domain
    implementation(project(":aws-docproc-domain"))
    implementation(platform("software.amazon.awssdk:bom:2.41.1"))
    implementation("software.amazon.awssdk:secretsmanager")
    implementation("com.amazonaws:aws-lambda-java-core:1.4.1")
    implementation("com.amazonaws:aws-lambda-java-events:3.16.1")
    implementation("org.mongodb:mongodb-driver-sync:5.14.0")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
}

tasks.test {
    useJUnitPlatform()
}
