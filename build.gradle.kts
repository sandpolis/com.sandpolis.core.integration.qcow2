plugins {
    id("java-library")
    id("eclipse")
    id("maven-publish")
    id("signing")

    id("org.ajoberstar.grgit") version "4.1.0"
}

import org.ajoberstar.grgit.Grgit

// Set project version according to the latest git tag
val gitDescribe = Grgit.open {
    currentDir = project.getProjectDir().toString()
}.describe { tags = true }

if (gitDescribe == null) {
    project.version = "0.0.0"
} else {
    project.version = gitDescribe.replaceFirst("^v".toRegex(), "")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testRuntimeOnly("ch.qos.logback:logback-core:1.3.0-alpha5")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.3.0-alpha5")

    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
}

java {
    modularity.inferModulePath.set(true)

    withJavadocJar()
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        setExceptionFormat("full")
    }
}

// Configure Eclipse plugin
eclipse {
	project {
		name = project.name
		comment = project.name
	}
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "com.github.cilki"
            artifactId = "qcow4j"
            version = project.version.toString()

            from(components["java"])

            pom {
                name.set(project.name)
                description.set("Qcow2 driver written in Java")
                url.set("https://github.com/cilki/${project.name}")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("cilki")
                        name.set("Tyler Cook")
                        email.set("tcc@sandpolis.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/cilki/${project.name}.git")
                    developerConnection.set("scm:git:ssh://git@github.com/cilki/${project.name}.git")
                    url.set("https://github.com/cilki/${project.name}")
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/cilki/${project.name}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
signing {
    useInMemoryPgpKeys(System.getenv("SIGNING_PGP_KEY") ?: "", System.getenv("SIGNING_PGP_PASSWORD") ?: "")
    sign(publishing.publications["mavenJava"])
}
