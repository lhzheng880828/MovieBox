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

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.AppVersionInfo
import com.calvin.box.movie.DiceRoller
import com.calvin.box.movie.DiceSettings
import com.calvin.box.movie.Theme
import com.calvin.box.movie.bean.Config
import com.calvin.box.movie.bean.Doh
import com.calvin.box.movie.bean.DownloadStatus
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.bean.UpdateStatus
import com.calvin.box.movie.bean.VersionCheckStatus
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.getPlatform
import com.calvin.box.movie.pref.toggle
import com.calvin.box.movie.theme.DynamicColorsAvailable
import io.github.aakira.napier.Napier
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

    private val platformApi = getPlatform()

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

    private val _uiState = MutableStateFlow<SettingsUiState?>(null)
    val uiState: StateFlow<SettingsUiState?> = _uiState

    private val _updateStatus = MutableStateFlow(UpdateStatus())
    val updateStatus: StateFlow<UpdateStatus> = _updateStatus

    init {

        screenModelScope.launch {
            preferences.theme.flow.collect { theme ->
                Napier.d(tag = TAG) { "xbox.settings, theme flow changed" }
                val use = preferences.useDynamicColors.get()
                val vodConfig = movieRepo.loadFirstConfig(Config.TYPE.VOD).flowOn(Dispatchers.IO).firstOrNull()
                var vodUrl = ""
                var vodName = ""
                if (vodConfig != null) {
                    vodUrl = vodConfig.url
                    vodName = vodConfig.name
                }

                val voidConfigList = movieRepo.loadAllConfig(Config.TYPE.VOD).flowOn(Dispatchers.IO).firstOrNull()
                var vodUrlList: List<String> = emptyList()
                if (voidConfigList != null) {
                    val urls = voidConfigList.map { it.url }
                    vodUrlList = urls
                }

                val liveUrl = preferences.liveUrl.get()
                val wallPaperUrl = preferences.wallpaperUrl.get()

                val dohList = vodRepo.getDoh()
                val dohIndex = max(
                    0,
                    dohList.indexOf(Doh.objectFrom(preferences.doh.get()))
                )



                val proxy = preferences.proxy.get()
                val volume = preferences.volume.get()

                val cacheSize = platformApi.getCacheSize()
                val versionName = AppVersionInfo.VERSION_NAME
                val versionCode = AppVersionInfo.VERSION_CODE

                val about = "mobile" + AppVersionInfo.FLAVOR_ABI + AppVersionInfo.FLAVOR_API

                val sites = appDataContainer.vodRepository.getSites()

                // 更新 _uiState
                _uiState.value = SettingsUiState(
                    theme = theme,
                    dynamicColorsAvailable = DynamicColorsAvailable,
                    useDynamicColors = use,
                    vodUrl = vodUrl,
                    vodName = vodName,
                    vodUrls = vodUrlList,
                    liveUrl = liveUrl,
                    wallPaperUrl = wallPaperUrl,
                    dohList = dohList,
                    dohIndex = dohIndex,
                    proxy = proxy,
                    cacheSize = cacheSize,
                    versionName = versionName,
                    versionCode = versionCode,
                    sites = sites,
                    eventSink = ::eventSink
                )
            }
        }
    }

    fun forceUpdate() {
        screenModelScope.launch{
            flow {
                emit(VersionCheckStatus.Checking)
                val apkVersion = movieRepo.getApkVersion(false,  "mobile")
                if (needUpdate(apkVersion.code, apkVersion.name)) {
                    emit(VersionCheckStatus.NeedUpdate(apkVersion.name, apkVersion.desc))
                } else {
                    emit(VersionCheckStatus.NoUpdate)
                }
            }.flowOn(Dispatchers.IO).collectLatest {
                if(it is VersionCheckStatus.Checking) {

                } else if(it is VersionCheckStatus.NeedUpdate){
                    _updateStatus.value = _updateStatus.value.copy(updateAvailable = true)
                } else if(it is VersionCheckStatus.NoUpdate){
                    _updateStatus.value = _updateStatus.value.copy(updateAvailable = false)
                }

            }
        }

    }

    private fun needUpdate(code: Int, name: String): Boolean {
         Napier.d { "network code: $code, name: $name" }
        return ( name != AppVersionInfo.VERSION_NAME && code >= AppVersionInfo.VERSION_CODE)
                || code > AppVersionInfo.VERSION_CODE
    }

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

            SettingsUiEvent.ClearCache ->  {
                _uiState.value  =  _uiState.value?.copy(cacheSize = "0 KB")
                platformApi.clearCache()
            }

            SettingsUiEvent.DownloadApp -> {
                val dev = false
                val name = "mobile-java-arm64_v8a"
               screenModelScope.launch {
                   movieRepo.download(dev, name).collectLatest {
                       if(it is DownloadStatus.Started){
                           Napier.i { "dowload>>> started" }
                       } else if (it is DownloadStatus.Progress){
                          val percent = it.percentage
                           Napier.i { "dowload>>> percent: $percent" }
                           _updateStatus.value = _updateStatus.value.copy(downloadProgress = percent.toFloat())
                       } else if(it is DownloadStatus.Success){
                           val filePath = it.filePath
                           Napier.i { "dowload>>> success filePath: $filePath" }
                           _updateStatus.value = _updateStatus.value.copy(downloadComplete =  true)
                       } else if(it is DownloadStatus.Error){
                           val err = it.message
                           Napier.i { "dowload>>> err: $err" }


                       }
                   }
               }

            }

            is SettingsUiEvent.SetVodHomeSite ->  {
                vodRepo.setHome(event.site)
            }
        }
    }

}

sealed interface DiceRollResult {
    object Initial : DiceRollResult
    class Success(val values: List<Int>) : DiceRollResult
    object Error : DiceRollResult
}
@Immutable
data class SettingsUiState(
    val theme: Theme = Theme.SYSTEM,
    val dynamicColorsAvailable: Boolean = false,
    val useDynamicColors: Boolean = false,

    val vodUrl: String = "",
    val vodName: String = "站点名称",
    val vodUrls: List<String> = emptyList(),

    val liveUrl: String = "",
    val wallPaperUrl: String = "",

    val dohIndex: Int = 0,
    val dohList: List<Doh> = emptyList(),

    val proxy: String = "",

    var cacheSize: String = "0 KB",
    val versionName: String = "1.0.0",
    val versionCode: Int = 0,
    val about: String = "公众号：虎哥LoveOpenSource",

    val  crashDataReportingEnabled: Boolean = false,
    val analyticsDataReportingEnabled: Boolean = false,

    val showDeveloperSettings: Boolean = false,
    val openSourceLicenseAvailable: Boolean = false,

    val sites: List<Site> = emptyList(),

    val eventSink: (SettingsUiEvent) -> Unit?,
)

sealed interface SettingsUiEvent  {
    data object NavigateUp : SettingsUiEvent
    data class SetTheme(val theme: Theme) : SettingsUiEvent
    data object NavigateDeveloperSettings : SettingsUiEvent
    data object ToggleUseDynamicColors : SettingsUiEvent
    data class SetVodHomeSite(val site: Site): SettingsUiEvent
    data class SetVodUrl(val vodUrl: String, val vodName:String): SettingsUiEvent
    data class SetLiveUrl(val liveUrl: String): SettingsUiEvent
    data class SetWallPaperUrl(val wallPaperUrl: String): SettingsUiEvent
    data class SetDoh(val doh: Doh): SettingsUiEvent
    data class SetProxy(val proxy:String): SettingsUiEvent
    data object NavigatePrivacyPolicy: SettingsUiEvent
    data object NavigatePlayerSettings: SettingsUiEvent
    data object NavigatePersonalizationSettings: SettingsUiEvent

    data object ToggleCrashDataReporting: SettingsUiEvent
    data object ToggleAnalyticsDataReporting: SettingsUiEvent
    data object NavigateOpenSource: SettingsUiEvent
    data object ClearCache: SettingsUiEvent

    data object DownloadApp: SettingsUiEvent

    data class DelVodUrl(val vodUrl: String):SettingsUiEvent
}

const val TAG = "xbox.settings"