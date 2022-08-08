plugins {
    id("org.springframework.boot") version "3.0.0-M4"
    java
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.spring.io/milestone")
    }
}

dependencies {

    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))

    implementation("org.springframework:spring-orm")
    implementation("org.hibernate.orm:hibernate-core")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:6.1.0.Final")
    annotationProcessor("com.mammb:jpa-fluent-modelgen:0.9.0")
    implementation("com.mammb:jpa-fluent-query:0.9.0")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("org.webjars:webjars-locator-core")
    runtimeOnly("org.webjars.npm:bootstrap:5.1.3")
    runtimeOnly("org.webjars.npm:font-awesome:4.7.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
