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
import com.calvin.box.movie.db.CartDataStore
import com.calvin.box.movie.network.FruittieApi
import com.calvin.box.movie.network.FruittieNetworkApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import okio.Path.Companion.toPath

expect class Factory {

    fun createApi(): FruittieApi

    fun createCartDataStore(): CartDataStore

    fun  createDataStore(): DataStore<Preferences>

}

expect fun getFactory():Factory

internal fun commonCreateApi(): FruittieApi = FruittieNetworkApi(
    client = HttpClient {
        install(ContentNegotiation) {
            json(json, contentType = ContentType.Any)
        }
    },
    apiUrl = "https://yenerm.github.io/frutties/",
)
val json = Json { ignoreUnknownKeys = true }



private lateinit var dataStore: DataStore<Preferences>

private val lock = SynchronizedObject()

/**
 * Gets the singleton DataStore instance, creating it if necessary.
 */
fun getDataStore(producePath: () -> String): DataStore<Preferences> =
    synchronized(lock) {
        if (::dataStore.isInitialized) {
            dataStore
        } else {
            PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
                .also { dataStore = it }
        }
    }

internal const val dataStoreFileName = "dice.preferences_pb"