package com.calvin.box.movie

import android.app.Application
import com.calvin.box.movie.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class MovieApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Napier.d { "onCreate invoke" }
        ContextProvider.initialize(this)
        Napier.base(DebugAntilog())
        getNanoServer().start()
        initKoin(isDebug = false, appDeclaration = {
            /*androidContext(this@movieApplication)
            module {
                single { Factory() }
            }*/
        })
    }

}