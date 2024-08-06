package com.calvin.box.movie.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.calvin.box.movie.MyMovieApp
import com.calvin.box.movie.di.Factory
import com.calvin.box.movie.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.dsl.module


fun main(){
   initKoin(isDebug = false){

       module {
           single { Factory() }
       }
   }

    Napier.base(DebugAntilog())
    return application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "movieBox",
        ) {
           // movieApp()
           // ThemeApp()
            MyMovieApp()
        }
    }
}