import org.gradle.kotlin.dsl.`kotlin-dsl`

plugins {
    `kotlin-dsl`
    java
}

tasks.withType<Javadoc> {
    options {
        quiet()
//            outputLevel = JavadocOutputLevel.QUIET
//            jFlags = listOf("-Xdoclint:none", "foo")
//            "-quiet"
    }
}
