/* This file was generated by the Gradle 'init' task.
*
* This generated file contains a sample Kotlin application project to get you started.
* For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.12.1/userguide/building_java_projects.html in the Gradle documentation.
*/

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
//    alias(libs.plugins.kotlin.jvm)

    // Apply the application plugin to add support for building a CLI application in Java.
    application
    id("com.github.ben-manes.versions") version "0.52.0"
    id("com.dorongold.task-tree") version "4.0.0"
    kotlin("jvm") version "2.1.20-Beta2"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.12.0-RC1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:33.4.0-jre")

}

kotlin {
    jvmToolchain(11)
}

application {
    // Define the main class for the application.
    mainClass = "com.nurflugel.hyperdoodle.DoodleFrame"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
