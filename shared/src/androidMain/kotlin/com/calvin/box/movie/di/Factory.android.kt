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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.calvin.box.movie.appContext
import com.calvin.box.movie.db.CartDataStore
import com.calvin.box.movie.network.FruittieApi

actual class Factory {
   /* actual fun createRoomDatabase(): AppDatabase {
        val dbFile = appContext.getDatabasePath(dbFileName)
        return Room.databaseBuilder<AppDatabase>(appContext, dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }*/

    actual fun createCartDataStore(): CartDataStore {
        return CartDataStore {
            appContext.filesDir.resolve(
                "cart.json",
            ).absolutePath
        }
    }

    actual fun createApi(): FruittieApi = commonCreateApi()

    actual fun createDataStore(): DataStore<Preferences> {
        return getDataStore(
            producePath = { appContext.filesDir.resolve(dataStoreFileName).absolutePath }
        )
    }
}

actual fun getFactory() = Factory()
