import org.gradle.api.JavaVersion

object ConfigData {
  const val compileSdkVersion = 34
  const val minSdkVersion = 21
  const val targetSdkVersion = 34
  const val versionCode = 1
  const val versionName = "1.1"
  const val versionCodeDev = 1
  const val versionNameDev = "1.1"
  val sourceCompatibilityVersion = JavaVersion.VERSION_17
  val targetCompatibilityVersion = JavaVersion.VERSION_17
  const val jvmTargetVersion = "17"
}
