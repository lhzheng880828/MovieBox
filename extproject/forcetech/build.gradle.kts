
plugins {
    alias(libs.plugins.androidLibrary)

}

android {
    namespace = "com.forcetech"

    compileSdk=libs.versions.android.compileSdk.get().toInt()


    defaultConfig {
        minSdk=libs.versions.android.minSdk.get().toInt()
        targetSdk=libs.versions.android.targetSdk.get().toInt()
        ndk {
            abiFilters += "armeabi-v7a"
        }

    }

    lint {
        //disable('UnsafeOptInUsageError')
    }

}

dependencies {
    implementation(projects.extproject.catvod)

}
