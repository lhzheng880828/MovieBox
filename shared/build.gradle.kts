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
        val commonMain by getting {
            kotlin.srcDir("$buildDir/generated/versioninfo")
        }

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
            implementation(libs.lingver)
            implementation(libs.androidx.room.paging)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.nanohttpd)
            implementation(libs.androidx.media)
            implementation(projects.extproject.catvod)
            implementation(projects.extproject.quickjs)
            implementation(projects.extproject.forcetech)
            implementation(projects.extproject.jianpian)
            implementation(projects.extproject.tvbus)
            implementation(projects.extproject.thunder)
            implementation(projects.extproject.youtube)

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
version = "1.0.112"
extra["versionCode"] = 1
extra["flavorApi"] = "Java"
extra["flavorAbi"] = "arm64"


val generateVersionInfo: Task by tasks.creating {
    val outputDir = "$buildDir/generated/versioninfo"
    val versionName = project.version.toString()
    val versionCode = project.extra["versionCode"].toString()
    val flavorApi = project.extra["flavorApi"].toString()
    val flavorAbi = project.extra["flavorAbi"].toString()


    inputs.property("versionName", versionName)
    inputs.property("versionCode", versionCode)
    inputs.property("flavorApi", flavorApi)
    inputs.property("flavorAbi", flavorAbi)

    outputs.dir(outputDir)

    doLast {
        val versionFile = File(outputDir, "AppVersionInfo.kt")
        versionFile.parentFile.mkdirs()
        versionFile.writeText(
            """
            package com.calvin.box.movie

            object AppVersionInfo {
                const val VERSION_NAME = "$versionName"
                const val VERSION_CODE = $versionCode
                const val FLAVOR_API = "$flavorApi"
                const val FLAVOR_ABI = "$flavorAbi"
            }
            """.trimIndent()
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(generateVersionInfo)
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
