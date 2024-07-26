/*
 * Copyright 2022 The Android Open Source Project
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
package com.calvin.box.movie

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.calvin.box.movie.di.Factory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class SettingsRepository(private val factory: Factory,
                         private val dataFactory: DataFactory,
                         private val scope: CoroutineScope): MoiveSettings {

    private val dataStore: DataStore<Preferences> = factory.createDataStore()
    companion object {
        const val DEFAULT_DICE_COUNT = 2
        const val DEFAULT_SIDES_COUNT = 6
        const val DEFAULT_UNIQUE_ROLLS_ONLY = false
    }

    private val diceCountKey = intPreferencesKey("dice_count")
    private val sideCountKey = intPreferencesKey("side_count")
    private val uniqueRollsOnlyKey = booleanPreferencesKey("unique_rolls_only")

    val settings: Flow<DiceSettings> = dataStore.data.map {
        DiceSettings(
            it[diceCountKey] ?: DEFAULT_DICE_COUNT,
            it[sideCountKey] ?: DEFAULT_SIDES_COUNT,
            it[uniqueRollsOnlyKey] ?: DEFAULT_UNIQUE_ROLLS_ONLY

        )
    }




    suspend fun saveSettings(
        diceCount: Int,
        sideCount: Int,
        uniqueRollsOnly: Boolean,
    ) {
        dataStore.edit {
            it[diceCountKey] = diceCount
            it[sideCountKey] = sideCount
            it[uniqueRollsOnlyKey] = uniqueRollsOnly
        }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        val prefKey = booleanPreferencesKey(key)
        dataStore.edit {
            it[prefKey] = value
        }
    }

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val settings: Flow<Boolean> = getBooleanFlow(key, defaultValue)
        return settings.first()
    }

    override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> {
        val prefKey = booleanPreferencesKey(key)
        return dataStore.data.map {
            it[prefKey] ?: defaultValue
        }
    }

    override suspend fun putInt(key: String, value: Int) {
        val prefKey = intPreferencesKey(key)
        dataStore.edit {
            it[prefKey] = value
        }
    }

    override suspend fun getInt(key: String, defaultValue: Int): Int {
        val settings: Flow<Int> = getIntFlow(key, defaultValue)
        return settings.first()
    }

    override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> {
        val prefKey = intPreferencesKey(key)
        return dataStore.data.map {
            it[prefKey] ?: defaultValue
        }
    }

    override suspend fun putFloat(key: String, value: Float) {
        val prefKey = floatPreferencesKey(key)
        dataStore.edit {
            it[prefKey] = value
        }
    }

    override suspend fun getFloat(key: String, defaultValue: Float): Float {
        val settings: Flow<Float> = getFloatFlow(key, defaultValue)
        return settings.first()
    }

    override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> {
        val prefKey = floatPreferencesKey(key)
        return dataStore.data.map {
            it[prefKey] ?: defaultValue
        }
    }

    override suspend fun putString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        dataStore.edit {
            it[prefKey] = value
        }
    }

    override suspend fun getStringOrNull(key: String):String? {
        val settings: Flow<String?> = getStringOrNullFlow(key)
        return settings.firstOrNull()
    }

    override fun getStringOrNullFlow(key: String):Flow<String?> {
        val prefKey = stringPreferencesKey(key)
        return dataStore.data.map {
            it[prefKey]
        }
    }
}

interface MoiveSettings {
    /**
     * Stores the `Boolean` [value] at [key].
     */
    suspend fun putBoolean(key: String, value: Boolean)

    /**
     * Returns the `Boolean` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean

    /**
     * Returns the `Boolean` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    //suspend fun getBooleanOrNull(key: String): Boolean?


    fun  getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean>


    suspend fun putInt(key: String, value: Int)

    suspend fun getInt(key: String, defaultValue: Int): Int

    fun  getIntFlow(key: String, defaultValue: Int): Flow<Int>

    suspend fun putFloat(key: String, value: Float)

    suspend fun getFloat(key: String, defaultValue: Float): Float

    fun  getFloatFlow(key: String, defaultValue: Float): Flow<Float>


    suspend fun putString(key: String, value: String)

    suspend fun getStringOrNull(key: String):String?

    fun getStringOrNullFlow(key: String):Flow<String?>
}
