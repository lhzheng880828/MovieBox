import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    /*@OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.projectDir.path)
                    }
                }
            }
        }
    }*/

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    jvm()
    
    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here
            api(libs.androidx.datastore.preferences.core)
            api(libs.androidx.datastore.core.okio)

            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.okio)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.napier)
            implementation(libs.kotlinx.atomicfu)

            implementation(libs.androidx.collection)
            implementation(libs.androidx.annotation)
            implementation(libs.sqlite.bundled)
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.androidx.room.paging)
            implementation(libs.ktor.client.okhttp)
            implementation(projects.extproject.catvod)
            implementation(projects.extproject.quickjs)


        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            //implementation(libs.compose.ui.unit.darwin)
            //implementation(libs.compose.runtime.darwin)
        }
       /* desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            api(libs.ktor.client.okhttp)
           implementation(libs.kotlinx.coroutines.swing)
        }*/
        jvmMain.dependencies {
            implementation(libs.logback)
            implementation(libs.androidx.collection.jvm)
            /*implementation(projects.extproject.catvod)
            implementation(projects.extproject.quickjs)*/


        }
    }
}

android {
    namespace = "com.calvin.box.moive.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    dependencies{
        implementation(projects.extproject.catvod)
        implementation(projects.extproject.quickjs)
    }
}


dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
