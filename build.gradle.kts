// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "8.1.1" apply false
  id("org.jetbrains.kotlin.android") version "1.8.0" apply false
  id("org.jetbrains.kotlin.jvm") version "1.8.0" apply false
  id("com.android.library") version "8.1.1" apply false
  id("com.google.dagger.hilt.android") version "2.44" apply false
  id("com.google.gms.google-services") version "4.3.15" apply false
}

buildscript {
  repositories {
    // Make sure that you have the following two repositories
    google()  // Google's Maven repository
    mavenCentral()  // Maven Central repository
  }

  dependencies {
    // Add the dependency for the Crashlytics Gradle plugin
    classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
  }
}
