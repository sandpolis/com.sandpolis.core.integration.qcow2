plugins {
    id("java-library")
    id("eclipse")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
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
