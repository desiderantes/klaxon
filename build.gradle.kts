plugins {
    java
    `java-library`
    kotlin("jvm") version libs.versions.kotlin apply true
    alias(libs.plugins.benmanes.versions)
}

java {
    sourceCompatibility =  libs.versions.java.map(JavaVersion::toVersion).get()
    targetCompatibility = libs.versions.java.map(JavaVersion::toVersion).get()

    toolchain {
        languageVersion.set(libs.versions.java.map(JavaLanguageVersion::of))
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.framework.datatest)

    listOf("klaxon", "klaxon-jackson").forEach {
        implementation(project(":$it", "default"))
    }
}

subprojects {
    group = KlaxonConfig.groupId
    version = KlaxonConfig.version
}