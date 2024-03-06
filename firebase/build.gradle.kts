plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  id("kotlin-kapt")
}

android {
  namespace = "com.dpfht.demofbasemvvm.firebase"
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

  flavorDimensions.add("default")

  productFlavors {
    create("prod") {

    }
    create("dev") {

    }
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
  implementation(project(":data"))
  implementation(project(":domain"))

  implementation(Deps.coreKtx)
  implementation(Deps.appCompat)
  implementation(Deps.material)
  testImplementation(Deps.jUnit)
  androidTestImplementation(Deps.jUnitExt)
  androidTestImplementation(Deps.espresso)

  implementation(Deps.hilt)
  kapt(Deps.hiltCompiler)

  implementation(Deps.rxKotlin)

  implementation(platform(Deps.firebaseBom))

  implementation(Deps.firebaseAnalytics)
  implementation(Deps.firebaseCrashlytics)
  implementation(Deps.firebaseConfig)
  implementation(Deps.firebaseAuth)
  implementation(Deps.playServicesAuth)
  implementation(Deps.firebaseMessaging)
  implementation(Deps.firebaseFirestore)
  implementation(Deps.firebaseStorage)
}
