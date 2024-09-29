rootProject.name = "MovieBox"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        /*maven {
            setUrl("https://maven.aliyun.com/repository/public")
        }*/
        maven {
            setUrl("https://maven.aliyun.com/repository/gradle-plugin")
        }

        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
       /* maven {
            setUrl("https://maven.aliyun.com/repository/public")
        }*/

        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}

include(":composeApp")
include(":server")
include(":shared")
include(":extproject:catvod")
include(":extproject:quickjs")
include(":extproject:forcetech")
include(":extproject:jianpian")
include(":extproject:thunder")
include(":extproject:tvbus")
include(":extproject:youtube")

include(":extproject:CatVodSpider:app")
include(":extproject:CatVodSpider:tools")
