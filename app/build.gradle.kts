plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.dagger.hilt.android")
  id("kotlin-kapt")
  id("com.google.gms.google-services")
  id("com.google.firebase.crashlytics")
}

android {
  namespace = "com.dpfht.demofbasemvvm"
  compileSdk = ConfigData.compileSdkVersion

  defaultConfig {
    minSdk = ConfigData.minSdkVersion
    targetSdk = ConfigData.targetSdkVersion
    
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true

      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

      manifestPlaceholders["appNameSuffix"] = ""
      resValue("string", "running_mode", "")
    }
    debug {
      isMinifyEnabled = false
      isShrinkResources = false

      applicationIdSuffix = ".debug"
      versionNameSuffix = "-debug"

      manifestPlaceholders["appNameSuffix"] = "-(debug)"
      resValue("string", "running_mode", "-(debug)")
    }
  }

  flavorDimensions.add("default")

  productFlavors {
    create("prod") {
      applicationId = "com.dpfht.demofbasemvvm"
      versionCode = ConfigData.versionCode
      versionName = ConfigData.versionName

      manifestPlaceholders["appName"] = "Demo FBase MVVM"
      resValue("string", "app_name", "Demo FBase MVVM")
    }
    create("dev") {
      applicationId = "com.dpfht.demofbasemvvm.dev"
      versionCode = ConfigData.versionCodeDev
      versionName = ConfigData.versionNameDev

      manifestPlaceholders["appName"] = "Demo FBase MVVM (DEV)"
      resValue("string", "app_name", "Demo FBase MVVM (DEV)")
    }
  }

  buildFeatures {
    buildConfig = true
    viewBinding = true
    dataBinding = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {

  implementation(project(":framework"))
  implementation(project(":data"))
  implementation(project(":domain"))
  implementation(project(":features:feature_splash"))
  implementation(project(":features:feature_login_register"))
  implementation(project(":features:feature_book_inventory"))
  implementation(project(":features:feature_push_message"))
  implementation(project(":features:feature_user_profile"))

  implementation(Deps.coreKtx)
  implementation(Deps.appCompat)
  implementation(Deps.material)
  implementation(Deps.constraintLayout)
  implementation(Deps.navigationFragment)
  implementation(Deps.navigationUi)
  testImplementation(Deps.jUnit)
  androidTestImplementation(Deps.jUnitExt)
  androidTestImplementation(Deps.espresso)

  implementation(Deps.hilt)
  kapt(Deps.hiltCompiler)

  implementation(platform(Deps.firebaseBom))

  implementation(Deps.firebaseAnalytics)
  implementation(Deps.firebaseCrashlytics)
  implementation(Deps.firebaseConfig)
  implementation(Deps.firebaseAuth)
  implementation(Deps.playServicesAuth)
  implementation(Deps.firebaseMessaging)
  implementation(Deps.firebaseFirestore)
  implementation(Deps.firebaseStorage)

  implementation(Deps.rxKotlin)
}
