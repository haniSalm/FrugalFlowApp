// Top-level build file
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0") // Updated to Kotlin 2.0
        classpath("com.google.gms:google-services:4.4.1")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}