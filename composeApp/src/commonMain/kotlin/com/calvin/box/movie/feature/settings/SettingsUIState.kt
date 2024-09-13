package com.calvin.box.movie.feature.settings

import androidx.compose.runtime.Immutable
import com.calvin.box.movie.Theme
import com.calvin.box.movie.bean.Doh

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

    val dohIndex: UInt = 0u,
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

    val eventSink: (SettingsUiEvent) -> Unit?,
)

sealed interface SettingsUiEvent  {
    data object NavigateUp : SettingsUiEvent
    data class SetTheme(val theme: Theme) : SettingsUiEvent
    data object NavigateDeveloperSettings : SettingsUiEvent
    data object ToggleUseDynamicColors : SettingsUiEvent
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