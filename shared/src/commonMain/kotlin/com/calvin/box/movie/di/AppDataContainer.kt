/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.calvin.box.movie.di

import com.calvin.box.movie.DataFactory
import com.calvin.box.movie.SettingsRepository
import com.calvin.box.movie.FakeDataRepository
import com.calvin.box.movie.MovieDataRepository
import com.calvin.box.movie.api.config.LiveConfig
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.api.config.WallConfig
import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.okhttpSetup
import com.calvin.box.movie.pref.BasePreference
import com.calvin.box.movie.pref.MoivePreferenceImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class AppDataContainer(
    private val factory: Factory,
    private val dataFactory: DataFactory,
    private val vod: VodConfig,
    private val live: LiveConfig,
    private val wallPaper: WallConfig
) {
    private val movieDatabase:MoiveDatabase by lazy {
        dataFactory.createRoomDatabase()
    }

    val settingsRepository:SettingsRepository by lazy {
        SettingsRepository(factory,
            dataFactory,
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        )
    }

    val prefApi:BasePreference by lazy {
        MoivePreferenceImpl(settingsRepository,
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),)
    }

    init {
        MoiveDatabase.set(movieDatabase)
        okhttpSetup(prefApi)
    }

    val fakeRepository: FakeDataRepository by lazy {
        FakeDataRepository(
            api = factory.createApi(),
            database = movieDatabase,
            cartDataStore = factory.createCartDataStore(),
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
        )
    }


    val movieRepository:MovieDataRepository by lazy {
        MovieDataRepository(api = dataFactory.createApi(),
            database = movieDatabase,
            dataStore = dataFactory.createDataStore(),
            scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
            )
    }




    val vodRepository:VodConfig by lazy {
        vod.init(this)
    }
    val liveRepository:LiveConfig by lazy {
        live.init(this)
    }
    val wallRepository:WallConfig by lazy {
        wallPaper.init(this)
    }
}
