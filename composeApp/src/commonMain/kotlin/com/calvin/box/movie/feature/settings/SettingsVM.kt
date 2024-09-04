package com.calvin.box.movie.feature.settings

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

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.AppVersionInfo
import com.calvin.box.movie.DiceRoller
import com.calvin.box.movie.DiceSettings
import com.calvin.box.movie.bean.Config
import com.calvin.box.movie.bean.Doh
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.getPlatform
import com.calvin.box.movie.pref.toggle
import com.calvin.box.movie.theme.DynamicColorsAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

class SettingsViewModel(
    private val roller: DiceRoller,
    private val appDataContainer: AppDataContainer
) : ScreenModel {
    private val _result = MutableStateFlow<DiceRollResult>(DiceRollResult.Initial)
    val result: StateFlow<DiceRollResult> = _result.asStateFlow()

    val settings: StateFlow<DiceSettings?> = appDataContainer.settingsRepository
        .settings
        .stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(5000L),
            null
        )

    private val preferences  = appDataContainer.prefApi
    private val movieRepo = appDataContainer.movieRepository
    private val vodRepo = appDataContainer.vodRepository

    val uiState: StateFlow<SettingsUiState?> = preferences.theme.flow.map {
        val use = preferences.useDynamicColors.get()
        val vodConfig = movieRepo.loadFirstConfig(Config.TYPE.VOD).flowOn(Dispatchers.IO).firstOrNull()
        var vodUrl = ""
        var vodName = ""
        if(vodConfig != null){
            vodUrl =  vodConfig.url
            vodName = vodConfig.name
        }
        val voidConfigList = movieRepo.loadAllConfig(Config.TYPE.VOD).flowOn(Dispatchers.IO).firstOrNull()
        var vodUrlList:List<String> = emptyList()
        if(voidConfigList!=null){
           val urls = voidConfigList.map {
                it.url
            }
            vodUrlList = urls
        }
        val liveUrl = preferences.liveUrl.get()
        val wallPaperUrl = preferences.wallpaperUrl.get()

        val dohList = vodRepo.getDoh()
        val dohIndex =max(
            0u,
            dohList.indexOf( Doh.objectFrom(preferences.doh.get())).toUInt()
        )
        val proxy = preferences.proxy.get()


        val volume = preferences.volume.get()
        SettingsUiState(theme = it, dynamicColorsAvailable = DynamicColorsAvailable,
            useDynamicColors =  use, vodUrl = vodUrl, vodName = vodName, vodUrls = vodUrlList,
            liveUrl = liveUrl, wallPaperUrl =  wallPaperUrl, volume =  volume, dohList = dohList,
            dohIndex = dohIndex, proxy = proxy)
    }.stateIn(
        screenModelScope,
        SharingStarted.WhileSubscribed(5000L),
        null
    )



    fun saveSettings(
        number: Int,
        sides: Int,
        unique: Boolean,
    ) = screenModelScope.launch { appDataContainer.settingsRepository.saveSettings(number, sides, unique) }

    fun rollDice() {
        // Ignore attempted rolls before settings are available
        val settings = settings.value ?: return

        _result.value = try {
            DiceRollResult.Success(roller.rollDice(settings))
        } catch (e: IllegalArgumentException) {
            DiceRollResult.Error
        }

        screenModelScope.launch{
            withContext(Dispatchers.Main){

            }
        }
    }

    fun eventSink(event: SettingsUiEvent) {
        when (event) {
            SettingsUiEvent.NavigateUp -> {
                //navigator.pop()
            }

            is SettingsUiEvent.SetTheme -> {
                screenModelScope.launch { preferences.theme.set(event.theme) }
            }

            SettingsUiEvent.ToggleUseDynamicColors -> {
                screenModelScope.launch { preferences.useDynamicColors.toggle()}
            }

            SettingsUiEvent.NavigateDeveloperSettings -> {

            }

            is SettingsUiEvent.SetVodUrl ->  {
                screenModelScope.launch{
                    movieRepo.saveConfig(Config.TYPE.VOD, event.vodName, event.vodUrl)
                }
            }
            is SettingsUiEvent.DelVodUrl -> {
                screenModelScope.launch{
                    movieRepo.delConfig(event.vodUrl)
                }
            }

            is SettingsUiEvent.SetVolume ->  {
                screenModelScope.launch{
                    preferences.volume.set(event.volume)
                }
            }

            is SettingsUiEvent.SetLiveUrl ->  {
                screenModelScope.launch{
                    preferences.liveUrl.set(event.liveUrl)

                }
            }
            is SettingsUiEvent.SetWallPaperUrl -> {
                screenModelScope.launch{
                    preferences.wallpaperUrl.set(event.wallPaperUrl)
                }
            }

            SettingsUiEvent.NavigateOpenSource ->  {

            }
            SettingsUiEvent.NavigatePrivacyPolicy ->  {

            }
            SettingsUiEvent.ToggleAnalyticsDataReporting -> {

            }
            SettingsUiEvent.ToggleCrashDataReporting ->  {

            }
            SettingsUiEvent.NavigatePersonalizationSettings -> {

            }
            SettingsUiEvent.NavigatePlayerSettings -> {

            }

            is SettingsUiEvent.SetDoh ->  {
                screenModelScope.launch{
                    preferences.doh.set(Doh.encodeToString(event.doh))
                    getPlatform().setDoh(event.doh)
                }
            }
            is SettingsUiEvent.SetProxy ->  {
                screenModelScope.launch{
                    preferences.proxy.set(event.proxy)
                    getPlatform().setProxy(event.proxy)
                }
            }
        }
    }

}

sealed interface DiceRollResult {
    object Initial : DiceRollResult
    class Success(val values: List<Int>) : DiceRollResult
    object Error : DiceRollResult
}
