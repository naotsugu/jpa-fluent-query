plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("jakarta.persistence:jakarta.persistence-api:3.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testImplementation("jakarta.persistence:jakarta.persistence-api:3.0.0")
    testAnnotationProcessor("org.hibernate:hibernate-jpamodelgen:6.1.1.Final")
    testAnnotationProcessor("com.mammb:jpa-fluent-modelgen:0.6.0")
    testImplementation("com.h2database:h2:2.1.212")
    testImplementation("org.eclipse.persistence:eclipselink:4.0.0-M3")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    // testLogging.showStandardStreams = true
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
    withJavadocJar()
    withSourcesJar()
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

group = "com.mammb"
version = "0.7.0"

val sonatypeUsername: String? by project
val sonatypePassword: String? by project

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "jpa-fluent-query"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("jpa fluent query")
                description.set("JPA fluent query library")
                url.set("https://github.com/naotsugu/jpa-fluent-query")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("naotsugu")
                        name.set("Naotsugu Kobayashi")
                        email.set("naotsugukobayashi@gmail.com")
                    }
                }
                scm {
                    connection.set("git@github.com:naotsugu/jpa-fluent-query.git")
                    developerConnection.set("git@github.com:naotsugu/jpa-fluent-query.git")
                    url.set("https://github.com/naotsugu/jpa-fluent-query")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}


// asciidoctor config
val asciidoctorExtensions: Configuration by configurations.creating
tasks.asciidoctor {
    baseDirFollowsSourceFile()
    sources(delegateClosureOf<PatternSet> {
        include("index.adoc")
    })
    forkOptions {
        jvmArgs("--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED")
        jvmArgs("--add-opens", "java.base/java.io=ALL-UNNAMED")
    }
}
