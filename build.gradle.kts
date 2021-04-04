import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
}

repositories {
    mavenCentral()
}

group = "de.drbunsen.common"
version = "0.5.1-SNAPSHOT"

val isSnapshot = (version as String).endsWith("-SNAPSHOT")

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = tasks.jar.get().archiveBaseName.get()
            from(components["java"])
            pom {
                name.set("fluentxmlwrapper")
                description.set("a simple wrapper for XML Processing with a fluent interface")
                url.set("http://github.com/askeron/fluentxmlwrapper")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        id.set("bkuhlen")
                        name.set("Bjoern Kuhlen")
                        email.set("mavencentral@drbunsen.de")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/askeron/fluentxmlwrapper.git")
                    developerConnection.set("scm:git:ssh://github.com:askeron/fluentxmlwrapper.git")
                    url.set("http://github.com/askeron/fluentxmlwrapper/tree/master")
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        System.getenv("SONATYPE_SIGNING_KEY_ID") ?: project.properties["sonatype.signing.keyId"] as? String,
        System.getenv("SONATYPE_SIGNING_SECRING_GPG_BASE64") ?: project.properties["sonatype.signing.secringGpgBase64"] as? String,
        System.getenv("SONATYPE_SIGNING_PASSWORD") ?: project.properties["sonatype.signing.password"] as? String
    )
    sign(publishing.publications["mavenJava"])
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(System.getenv("SONATYPE_USERNAME") ?: project.properties["sonatype.username"] as? String)
            password.set(System.getenv("SONATYPE_PASSWORD") ?: project.properties["sonatype.password"] as? String)
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
}
