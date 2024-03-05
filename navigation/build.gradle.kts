plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("kotlin-kapt")
}

android {
  namespace = "com.dpfht.demofbasemvvm.navigation"
  compileSdk = ConfigData.compileSdkVersion

  defaultConfig {
    minSdk = ConfigData.minSdkVersion

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  buildFeatures {
    viewBinding = true
    dataBinding = true
  }
  compileOptions {
    sourceCompatibility = ConfigData.sourceCompatibilityVersion
    targetCompatibility = ConfigData.targetCompatibilityVersion
  }
  kotlinOptions {
    jvmTarget = ConfigData.jvmTargetVersion
  }
}

dependencies {

  implementation(project(":framework"))
  implementation(project(":domain"))
  implementation(project(":features:feature_splash"))
  implementation(project(":features:feature_login_register"))
  implementation(project(":features:feature_book_inventory"))
  implementation(project(":features:feature_push_message"))
  implementation(project(":features:feature_user_profile"))

  implementation(Deps.coreKtx)
  implementation(Deps.appCompat)
  implementation(Deps.material)
  testImplementation(Deps.jUnit)
  androidTestImplementation(Deps.jUnitExt)
  androidTestImplementation(Deps.espresso)

  implementation(Deps.hilt)
  kapt(Deps.hiltCompiler)

  implementation(Deps.navigationFragment)
  implementation(Deps.navigationUi)
}
