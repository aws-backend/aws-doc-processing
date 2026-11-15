// Здесь намеренно не используется Spring Boot:
// Lambda будет иметь лёгкий Java handler без поднятия полного Spring Application Context при каждой инициализации.
plugins {
    java
}

dependencies {
    implementation(project(":aws-docproc-domain"))
    implementation(project(":aws-docproc-infrastructure"))
    implementation(project(":aws-docproc-contracts"))

    implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
    implementation("com.amazonaws:aws-lambda-java-events:3.16.1")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation("org.junit.jupiter:junit-jupiter")
}
