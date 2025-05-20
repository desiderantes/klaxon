import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    `maven-publish`
    signing
    kotlin("jvm")
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

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.framework.datatest)
}


with(publishing) {
    publications {
        register<MavenPublication>("custom") {
            groupId = KlaxonConfig.groupId
            artifactId = KlaxonConfig.artifactId
            version = project.version.toString()
            pom {
                name.set(KlaxonConfig.artifactId)
                description.set(KlaxonConfig.description)
                url.set(KlaxonConfig.url)
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                issueManagement {
                    system.set("Github")
                    url.set(KlaxonConfig.issueManagementUrl)
                }
                developers {

                    KlaxonConfig.developers.forEach { developer ->
                        developer {
                            id.set(developer.id)
                            name.set(developer.name)
                            email.set(developer.email)
                            roles.set(listOf(developer.role))
                        }
                    }
                }
                scm {
                    connection.set("scm:git:git://${KlaxonConfig.scm}.git")
                    url.set("https://${KlaxonConfig.scm}")
                }
            }
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "sonatype"
            url = if (KlaxonConfig.isSnapshot)
                uri("https://oss.sonatype.org/content/repositories/snapshots/") else
                uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("sonatypeUser")?.toString() ?: System.getenv("SONATYPE_USER")
                password = project.findProperty("sonatypePassword")?.toString() ?: System.getenv("SONATYPE_PASSWORD")
            }
        }
        maven {
            name = "myRepo"
            url = uri("file://$buildDir/repo")
        }
    }
}

