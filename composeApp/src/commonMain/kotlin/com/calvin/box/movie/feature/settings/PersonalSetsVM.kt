package com.calvin.box.movie.feature.settings

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.getPlatform
import com.calvin.box.movie.pref.toggle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/9/13
 */
class PersonalSetsModel(appDataContainer: AppDataContainer) : ScreenModel{
    private val _uiState = MutableStateFlow<PersonalSetsUiState?>(null)
    val uiState: StateFlow<PersonalSetsUiState?> = _uiState

    private val preferences  = appDataContainer.prefApi

    init {
        screenModelScope.launch {
            preferences.size.flow.collect { imageSize ->
                val playSpeed = preferences.playSpeed.get()
                val incognito = preferences.incognito.get()
                val aggregatedSearch =preferences.aggregatedSearch.get()
                val homeDisplayName = preferences.homeDisplayName.get()
                val aiAdBlocking = preferences.removeAd.get()
                val configCache = preferences.configCache.get()
                val siteSearch = preferences.siteSearch.get()
                val buildInDownload = preferences.buildInDownload.get()
                val language = preferences.language.get()

                _uiState.value = PersonalSetsUiState(
                    picSize = imageSize,
                    playSpeed = playSpeed,
                    incognitoMode = incognito,
                    searchAggregation = aggregatedSearch,
                    showSourcesOnHome = homeDisplayName,
                    aiAdBlocking = aiAdBlocking,
                    builtInDownload = buildInDownload,
                    sourceSearchEnabled = siteSearch,
                    apiCacheEnabled =  configCache,
                    language = language,
                    eventSink = ::eventSink
                )

            }
        }
    }

    fun eventSink(event: PersonalSetsUiEvent) {
        when (event) {

            PersonalSetsUiEvent.ToggleAIAdBlocking ->  {
                screenModelScope.launch {
                    preferences.removeAd.toggle()
                }
            }

            PersonalSetsUiEvent.ToggleBuiltInDownload -> {
                screenModelScope.launch {
                    preferences.buildInDownload.toggle()
                }
            }
            PersonalSetsUiEvent.ToggleIncognitoMode -> {
                screenModelScope.launch {
                    preferences.incognito.toggle()
                }
            }
            PersonalSetsUiEvent.ToggleSearchAggregation ->{
                screenModelScope.launch {
                    preferences.aggregatedSearch.toggle()
                }
            }
            PersonalSetsUiEvent.ToggleShowSourcesOnHome -> {
                screenModelScope.launch {
                    preferences.homeDisplayName.toggle()
                }
            }
            PersonalSetsUiEvent.ToggleSourceSearch -> {
                screenModelScope.launch {
                    preferences.siteSearch.toggle()
                }
            }
            PersonalSetsUiEvent.ResetApp -> {
                screenModelScope.launch(Dispatchers.IO) {
                    resetApp()
                }
            }
            is PersonalSetsUiEvent.SetLanguage -> {
                screenModelScope.launch {
                    preferences.language.set(event.languageIndex)
                   getPlatform().setLanguage()

                }
            }
            is PersonalSetsUiEvent.SetImageSize -> {
                screenModelScope.launch {
                    preferences.size.set(event.imageIndex)
                }
            }
            is PersonalSetsUiEvent.SetPlaybackSpeed -> {
                screenModelScope.launch {
                    preferences.playSpeed.set(event.speed)
                }
            }
            is PersonalSetsUiEvent.SetApiCache -> {
                screenModelScope.launch {
                    preferences.configCache.set(event.apiCacheIndex)
                }
            }
        }
    }
}

fun resetApp(){
    getPlatform().resetApp()
}

@Immutable
data class PersonalSetsUiState(
    val picSize:Int = 0,
    val playSpeed: Float = 0f,
    val incognitoMode: Boolean = false,
    val searchAggregation: Boolean = false,
    val showSourcesOnHome: Boolean = false,
    val aiAdBlocking: Boolean = false,
    val builtInDownload: Boolean = false,
    val sourceSearchEnabled: Boolean = false,
    val apiCacheEnabled: Int = 0,
    val language: Int = 0,
    val eventSink: (PersonalSetsUiEvent) -> Unit?,
)
sealed interface PersonalSetsUiEvent {
    data object ToggleIncognitoMode: PersonalSetsUiEvent
    data object ToggleSearchAggregation : PersonalSetsUiEvent
    data object ToggleShowSourcesOnHome : PersonalSetsUiEvent
    data object ToggleSourceSearch: PersonalSetsUiEvent
    data object ToggleAIAdBlocking : PersonalSetsUiEvent
    data object ToggleBuiltInDownload : PersonalSetsUiEvent
    data class SetImageSize(val imageIndex: Int): PersonalSetsUiEvent
    data class SetPlaybackSpeed(val speed: Float): PersonalSetsUiEvent
    data class SetLanguage(val languageIndex: Int): PersonalSetsUiEvent
    data class SetApiCache(val apiCacheIndex: Int): PersonalSetsUiEvent
    data object ResetApp: PersonalSetsUiEvent
}