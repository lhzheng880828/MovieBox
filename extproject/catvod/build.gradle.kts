plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace="com.github.catvod.crawler"
    compileSdk=libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk=libs.versions.android.minSdk.get().toInt()
        targetSdk=libs.versions.android.targetSdk.get().toInt()
    }


}


dependencies {
    api(libs.androidx.annotation)
    api(libs.gson)
    api(libs.okhttp)
    api(libs.okhttp.dnsoverhttps)
    api(libs.media3.common)
    api(libs.androidx.annotation)
    api(libs.preference)
    api(libs.juniversalchardet)
    api("com.orhanobut:logger:2.2.0")

}