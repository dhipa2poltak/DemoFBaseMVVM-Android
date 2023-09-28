plugins {
  id("java-library")
  id("org.jetbrains.kotlin.jvm")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
  implementation(project(":domain"))

  implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
  implementation("com.google.code.gson:gson:2.9.0")
  implementation("androidx.annotation:annotation:1.7.0")
}
