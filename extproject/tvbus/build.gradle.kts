plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.tvbus.engine"

    compileSdk=libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk=libs.versions.android.minSdk.get().toInt()
        targetSdk=libs.versions.android.targetSdk.get().toInt()
    }
}

dependencies {
    implementation(projects.extproject.catvod)

}