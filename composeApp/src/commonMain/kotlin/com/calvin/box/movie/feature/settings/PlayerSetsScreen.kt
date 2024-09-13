package com.calvin.box.movie.feature.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.aakira.napier.Napier

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/9/4
 */
class PlayerSetsScreen: Screen {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: PlayerSetsModel = getScreenModel()
        val playerSetsUiState by viewModel.uiState.collectAsState()
        Napier.d { "uiState: $playerSetsUiState" }
        val uiState = playerSetsUiState ?: return
        val nv = LocalNavigator.currentOrThrow
        var player by remember {  mutableStateOf(uiState.player)  }
        var decoder by remember {  mutableStateOf(uiState.decoder) }
        var render by remember { mutableStateOf(uiState.render) }
        var scale by remember { mutableStateOf(uiState.scale)  }
        var subtitleSize by remember { mutableStateOf(uiState.subtitleSize) }
        var subtitleStyle by remember { mutableStateOf(uiState.subtitleStyle) }
        val danmuLoad by remember { mutableStateOf(uiState.danmuLoad) }
        var background by remember { mutableStateOf(uiState.background) }
        var rtspTunnel by remember { mutableStateOf(uiState.rtspTunnel) }
        var autoLineSwitching by remember { mutableStateOf(uiState.autoLineSwitching) }
        var userAgent by remember { mutableStateOf(uiState.userAgent) }

        val scaleMap = mapOf("0" to "原始", "1" to "16：9", "2" to "4:3", "3" to "填充", "4" to "裁剪")
        val backgroundMap = mapOf("0" to "关闭","1" to "开启","2" to "画中画" )
        val subtitleStyleList = listOf("系统", "预设")

        val playerList = listOf("系统", "EXO", "IJK")

        val decoderList =  listOf("硬解", "软解")
        val renderList = listOf("Surface", "Texture")

        val rtspTunnelList = listOf("UDP", "TCP")

        val autoLineSwitchingList = listOf("自动", "手动")

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = {
                            /*viewModel.eventSink(SettingsUiEvent.NavigateUp)*/
                            nv.pop()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = /*LocalStrings.current.cdNavigateUp*/"Up",
                            )
                        }
                    },
                )
            },
        ){
                contentPadding ->
            LazyColumn(
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxWidth(),
            ) {
                stickyHeader {
                    PreferenceHeader("Player Settings")
                }


                item {
                    ListPreference(
                        title = "播放器",
                        items = playerList,
                        onItemSelected = {
                          player = playerList.indexOf(it)
                            viewModel.eventSink(PlayerSetsUiEvent.SetPlayer(player))
                        },
                        summary = playerList[player]
                    )
                }
                item {
                    ListPreference(
                        title = "解码方式",
                        items = decoderList,
                        onItemSelected = {
                            decoder = decoderList.indexOf(it)
                            viewModel.eventSink(PlayerSetsUiEvent.SetDecoder(decoder))
                        },
                        summary = decoderList[decoder]
                    )
                }
                item {
                    ListPreference(
                        title = "渲染方式",
                        items = renderList,
                        onItemSelected = {
                            render = renderList.indexOf(it)
                            viewModel.eventSink(PlayerSetsUiEvent.SetRender(render))
                        },
                        summary = renderList[render]
                    )
                }
                item {
                    SingleChoicePreference(
                        selectedKey = scale.toString(),
                        onSelectionChanged = {
                            scale = it.toInt()
                            viewModel.eventSink(PlayerSetsUiEvent.SetScale(scale) )

                        },
                        title = "缩放比例",
                        entries = scaleMap,
                        summary = scaleMap[scale.toString()]
                    )
                }

                item {
                    ListPreference(
                        title = "字幕样式",
                        items = subtitleStyleList,
                        onItemSelected = {
                            subtitleStyle = subtitleStyleList.indexOf(it) == 1
                            viewModel.eventSink(PlayerSetsUiEvent.ToggleSubtitleStyle)
                        },

                        summary = subtitleStyleList[ if(subtitleStyle) 1 else 0]
                    )
                }

                item {
                    SliderPreference(
                        value = subtitleSize.toFloat(),
                        onValueChange = {
                            subtitleSize = (it*100).toInt()
                            viewModel.eventSink(PlayerSetsUiEvent.SetSubtitleSize(subtitleSize))

                        },
                        title = "字幕大小",
                        summary = { "当前字幕大小: ${(it * 100).toInt()}%" }
                    )
                }
                item {
                    CheckboxPreference(
                        title =  "弹幕加载",
                        summaryOff = /*strings.settingsDynamicColorSummary*/"弹幕加载，开启或者关闭",
                        onCheckClicked = { viewModel.eventSink(PlayerSetsUiEvent.ToggleDanmuLoad) },
                        checked = danmuLoad,
                    )
                }
                item {
                    SingleChoicePreference(
                        selectedKey = background.toString(),
                        onSelectionChanged = {
                            background = it.toInt()
                            viewModel.eventSink(PlayerSetsUiEvent.SetBackground(background))

                        },
                        title = "后台播放",
                        entries = backgroundMap,
                        summary = backgroundMap[background.toString()]
                    )
                }
                item {
                    ListPreference(
                        title =  "RTSP通道",
                        items = rtspTunnelList,
                        onItemSelected = {
                            rtspTunnel = rtspTunnelList.indexOf(it)
                            viewModel.eventSink(PlayerSetsUiEvent.SetRtspTunnel(rtspTunnel))
                        },
                        summary = rtspTunnelList[rtspTunnel]
                    )
                }
                item {
                    ListPreference(
                        title =  "线路播放",
                        items = autoLineSwitchingList,
                        onItemSelected = {
                            autoLineSwitching = autoLineSwitchingList.indexOf(it)
                            viewModel.eventSink(PlayerSetsUiEvent.SetAutoLineSwitching(autoLineSwitching))
                        },
                        summary = autoLineSwitchingList[autoLineSwitching]
                    )
                }
                item{
                    EditTextPreference(
                        value = userAgent,
                        onValueChange = {
                            userAgent = it
                            viewModel.eventSink(PlayerSetsUiEvent.SetUserAgent(it))
                        },
                        title = "User-Agent",
                        summary = userAgent
                    )
                }
            }
        }

    }
}