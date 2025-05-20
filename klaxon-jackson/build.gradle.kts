import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    java
    kotlin("jvm") version libs.versions.kotlin
}

java {
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    compilerOptions {
        languageVersion.set(libs.versions.kotlin.map { it.substringBeforeLast(".") }.map (KotlinVersion::fromVersion))
    }
    jvmToolchain {
        languageVersion.set(libs.versions.java.map (JavaLanguageVersion::of))
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(project(":klaxon", "default"))
    implementation(libs.jackson.databind)
}
