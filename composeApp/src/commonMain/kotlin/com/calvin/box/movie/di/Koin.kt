package com.calvin.box.movie.di

import com.calvin.box.movie.DiceRoller
import com.calvin.box.movie.api.config.LiveConfig
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.api.config.WallConfig
import com.calvin.box.movie.data.InMemoryMuseumStorage
import com.calvin.box.movie.data.KtorMuseumApi
import com.calvin.box.movie.data.MuseumApi
import com.calvin.box.movie.data.MuseumRepository
import com.calvin.box.movie.data.MuseumStorage
import com.calvin.box.movie.screens.detail.DetailScreenModel
import com.calvin.box.movie.screens.list.ListScreenModel
import com.calvin.box.movie.screens.fruitties.FruitViewModel
import com.calvin.box.movie.screens.dic.DiceViewModel
import com.calvin.box.movie.feature.settings.SettingsViewModel
import com.calvin.box.movie.ui.screens.tabsview.HomeTabViewModel
import com.calvin.box.movie.feature.collection.SearchScreenModel
import com.calvin.box.movie.feature.settings.PlayerSetsModel
import com.calvin.box.movie.feature.settings.PersonalSetsModel
import com.calvin.box.movie.feature.detail.VodDetailScreenModel

import com.calvin.box.movie.HomeScreenModel

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.dsl.KoinAppDeclaration

import com.calvin.box.movie.getDataFactory
import com.calvin.box.movie.feature.videoplayerview.VideoPlayerViewModel

val dataModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                // TODO Fix API so it serves application/json
                json(json, contentType = ContentType.Any)
            }
            install(Logging) {
                level = LogLevel.NONE
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d(tag = "HttpClient", message = message)
                    }
                }
            }
        }
    }

    single<MuseumApi> { KtorMuseumApi(get()) }
    single<MuseumStorage> { InMemoryMuseumStorage() }
    single {
        MuseumRepository(get(), get())
    }
    single {  VodConfig.get() }
    single {  LiveConfig.get() }
    single {  WallConfig.get() }
    single { AppDataContainer(getFactory(), getDataFactory(), get(), get(), get()) }

    single { DiceRoller() }

}

/*val platformModule = module {
    singleOf(::Factory)
    singleOf(::DataFactory)
}*/

val screenModelsModule = module {
    factoryOf(::ListScreenModel)
    factoryOf(::DetailScreenModel)
    factoryOf(::FruitViewModel)
    factoryOf(::DiceViewModel)
    factoryOf(::SettingsViewModel)
    factoryOf(::HomeTabViewModel)
    factoryOf( ::VideoPlayerViewModel)
    factoryOf( ::SearchScreenModel )
    factoryOf( ::HomeScreenModel )
    factoryOf( ::PlayerSetsModel )
    factoryOf( ::PersonalSetsModel)
    factoryOf( ::VodDetailScreenModel)

}



fun initKoin(isDebug: Boolean = false, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            dataModule,
            screenModelsModule,
            //platformModule,
        )
    }

fun initKoin() {
    startKoin {
        modules(
            dataModule,
            screenModelsModule,
            //platformModule,
        )
    }
}
