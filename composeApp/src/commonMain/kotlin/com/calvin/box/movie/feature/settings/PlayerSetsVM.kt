package com.calvin.box.movie.feature.settings

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.pref.toggle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/9/13
 */
class PlayerSetsModel(appDataContainer: AppDataContainer) : ScreenModel{
    private val _uiState = MutableStateFlow<PlayerSetsUiState?>(null)
    val uiState: StateFlow<PlayerSetsUiState?> = _uiState

    private val preferences  = appDataContainer.prefApi
    init {
        screenModelScope.launch {
            preferences.player.flow.collect { player ->
                val decoder = preferences.decode(player)
                val render = preferences.render
                val scale = preferences.scale
                val subtitleSize = preferences.subtitle
                val subtitleStyle = preferences.caption
                val danmuLoad = preferences.danmuLoad
                val background = preferences.background
                val rtspTunnel = preferences.rtsp
                // "Automatic Stream Switching" 或 "Auto Line Switching"
                val autoLineSwitching = preferences.flag
                val userAgent = preferences.ua
                _uiState.value = PlayerSetsUiState(
                    player = player,
                    decoder = decoder.get(),
                    render = render.get(),
                    scale = scale.get(),
                    subtitleSize = subtitleSize.get(),
                    subtitleStyle = subtitleStyle.get(),
                    danmuLoad = danmuLoad.get(),
                    background = background.get(),
                    rtspTunnel = rtspTunnel.get(),
                    autoLineSwitching = autoLineSwitching.get(),
                    userAgent = userAgent.get()
                )
            }
        }
    }

    fun eventSink(event: PlayerSetsUiEvent) {
        when (event) {

            is PlayerSetsUiEvent.SetAutoLineSwitching -> {
                screenModelScope.launch {
                    preferences.flag.set(event.autoLineSwitching)
                }
            }
            is PlayerSetsUiEvent.SetBackground ->  {
                screenModelScope.launch {
                    preferences.background.set(event.background)
                }
            }
            is PlayerSetsUiEvent.SetDecoder -> {
                screenModelScope.launch {
                    preferences.decode(preferences.player.get()).set(event.decoder)
                }
            }
            is PlayerSetsUiEvent.SetPlayer -> {
                screenModelScope.launch {
                    preferences.player.set(event.player)
                }
            }
            is PlayerSetsUiEvent.SetRender -> {
                screenModelScope.launch {
                    preferences.render.set(event.render)
                }
            }
            is PlayerSetsUiEvent.SetRtspTunnel -> {
                screenModelScope.launch {
                    preferences.rtsp.set(event.rtspTunnel)
                }
            }
            is PlayerSetsUiEvent.SetScale -> {
                screenModelScope.launch {
                    preferences.scale.set(event.scale)
                }
            }
            is PlayerSetsUiEvent.SetSubtitleSize -> {
                screenModelScope.launch {
                    preferences.subtitle.set(event.subtitleSize)
                }
            }
            is PlayerSetsUiEvent.SetUserAgent ->  {
                screenModelScope.launch {
                    preferences.ua.set(event.userAgent)
                }
            }
            PlayerSetsUiEvent.ToggleDanmuLoad ->  {
                screenModelScope.launch { preferences.danmuLoad.toggle()}
            }
            PlayerSetsUiEvent.ToggleSubtitleStyle ->  {
                screenModelScope.launch { preferences.caption.toggle()}
            }
        }
    }
}

@Immutable
data class PlayerSetsUiState(
    val player: Int = 0,
    val decoder: Int = 0,
    val render: Int = 0,
    val scale: Int = 0,
    val subtitleSize: Int = 0,
    val subtitleStyle: Boolean = false,
    val danmuLoad: Boolean = false,
    val background: Int = 0,
    val rtspTunnel: Int = 0,
    val autoLineSwitching: Int = 0,
    val userAgent:String = "",
)
sealed interface PlayerSetsUiEvent {
    data class SetPlayer(val player: Int): PlayerSetsUiEvent
    data class SetDecoder(val decoder: Int): PlayerSetsUiEvent
    data class SetRender(val render: Int): PlayerSetsUiEvent
    data class SetScale(val scale: Int): PlayerSetsUiEvent
    data class SetSubtitleSize(val subtitleSize: Int): PlayerSetsUiEvent
    data object ToggleSubtitleStyle : PlayerSetsUiEvent
    data object ToggleDanmuLoad : PlayerSetsUiEvent
    data class SetBackground(val background: Int): PlayerSetsUiEvent
    data class SetRtspTunnel(val rtspTunnel: Int): PlayerSetsUiEvent
    data class SetAutoLineSwitching(val autoLineSwitching: Int): PlayerSetsUiEvent
    data class SetUserAgent(val userAgent: String): PlayerSetsUiEvent
}