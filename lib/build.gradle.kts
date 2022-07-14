plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.persistence:jakarta.persistence-api:3.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testImplementation("jakarta.persistence:jakarta.persistence-api:3.0.0")
    testAnnotationProcessor("org.hibernate:hibernate-jpamodelgen:6.1.1.Final")
    testAnnotationProcessor(project(":lib", "archives"))
    //testAnnotationProcessor(fileTree("libs") { include("*.jar") })
    testImplementation("com.h2database:h2:2.1.212")
    //testImplementation("org.hibernate:hibernate-core:6.1.1.Final")
    //testImplementation("org.glassfish.jaxb:jaxb-runtime:3.0.0")
    implementation("org.eclipse.persistence:eclipselink:3.0.2")

}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
