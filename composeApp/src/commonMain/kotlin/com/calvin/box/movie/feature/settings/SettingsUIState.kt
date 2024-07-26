package com.calvin.box.movie.feature.settings

import androidx.compose.runtime.Immutable
import com.calvin.box.movie.Theme

@Immutable
data class SettingsUiState(
    val theme: Theme = Theme.SYSTEM,
    val dynamicColorsAvailable: Boolean = false,
    //val openSourceLicenseAvailable: Boolean,
    val useDynamicColors: Boolean = false,
    val vodUrl: String = "",
    val vodName: String = "站点名称",

    val vodUrls: List<String> = emptyList(),
    val liveUrl: String = "",
    val wallPaperUrl: String = "",
    val volume: Int = 0
    //val eventSink: (SettingsUiEvent) -> Unit?,
)

sealed interface SettingsUiEvent  {
    data object NavigateUp : SettingsUiEvent
    data class SetTheme(val theme: Theme) : SettingsUiEvent
    data object NavigateDeveloperSettings : SettingsUiEvent
    data object ToggleUseDynamicColors : SettingsUiEvent
    data class SetVodUrl(val vodUrl: String, val vodName:String): SettingsUiEvent
    data class SetLiveUrl(val liveUrl: String): SettingsUiEvent
    data class SetWallPaperUrl(val wallPaperUrl: String): SettingsUiEvent

    data class SetVolume(val volume: Int): SettingsUiEvent
    data class DelVodUrl(val vodUrl: String):SettingsUiEvent
}