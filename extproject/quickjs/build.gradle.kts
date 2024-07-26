plugins {
    alias(libs.plugins.androidLibrary)

}

android {
    namespace = "com.fongmi.android.tv.quickjs"

    compileSdk=libs.versions.android.compileSdk.get().toInt()


    defaultConfig {
        minSdk=libs.versions.android.minSdk.get().toInt()
        targetSdk=libs.versions.android.targetSdk.get().toInt()
    }

    lint {
        //disable('UnsafeOptInUsageError')
    }

}

dependencies {
    implementation(projects.extproject.catvod)
    implementation(libs.jsoup)
    implementation(libs.wrapper.java)
    implementation(libs.android.retrofuture)
    //implementation(ext: 'aar', name: 'quickjs', group: 'fongmi', version: 'release')
    implementation(files("libs/quickjs.aar"))
}
