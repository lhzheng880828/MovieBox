
plugins {
    alias(libs.plugins.androidLibrary)

}

android {
    namespace = "com.p2p"

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

}