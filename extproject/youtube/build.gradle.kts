plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.github.kiulian.downloader"

    compileSdk=libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk=libs.versions.android.minSdk.get().toInt()
        targetSdk=libs.versions.android.targetSdk.get().toInt()
    }
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.gson)
}
