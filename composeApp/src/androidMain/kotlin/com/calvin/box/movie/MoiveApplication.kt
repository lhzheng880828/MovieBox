package com.calvin.box.movie

import android.app.Application
import com.calvin.box.movie.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class movieApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextProvider.initialize(this)
        Napier.base(DebugAntilog())
        initKoin(isDebug = false, appDeclaration = {
            /*androidContext(this@movieApplication)
            module {
                single { Factory() }
            }*/
        })

    }

}