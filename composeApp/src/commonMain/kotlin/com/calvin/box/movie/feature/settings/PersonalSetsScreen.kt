package com.calvin.box.movie.feature.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.settings_clear_cache
import org.jetbrains.compose.resources.stringResource

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/9/4
 */
class PersonalSetsScreen:Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val viewModel: PersonalSetsModel = getScreenModel()
        val personalSetsUiState by viewModel.uiState.collectAsState()
        Napier.d { "uiState: $personalSetsUiState" }
        val uiState = personalSetsUiState ?: return
        val nv = LocalNavigator.currentOrThrow
        var imageSizeIndex by remember { mutableStateOf(uiState.picSize)  }
        val imageSizeMap = mapOf("0" to "小", "1" to "中", "2" to "大")
        var speed by remember { mutableStateOf(uiState.playSpeed)  }
        val speedMap = mapOf("0.5" to "0.5x","0.75" to "0.75x", "1.0" to "1x","1.25" to "1.25x", "1.5" to "1.5x", "2.0" to "2x", "3.0" to "3x")
        var languageIndex by remember { mutableStateOf(uiState.language)  }
        val languageMap = mapOf("0" to "英文", "1" to "简体中文", "2" to "繁体中文")
        var apiCacheIndex  by remember { mutableStateOf(uiState.apiCacheEnabled)  }
        val apiCacheMap = mapOf("0" to "关闭", "1" to "12小时", "2" to "24小时")


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = {
                            nv.pop()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Up",
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
                    PreferenceHeader("Personal Settings")
                }
                item {
                    SingleChoicePreference(
                        selectedKey = imageSizeIndex.toString(),
                        onSelectionChanged = {
                            imageSizeIndex = it.toInt()
                            viewModel.eventSink(PersonalSetsUiEvent.SetImageSize(imageSizeIndex))
                        },
                        title = "图片尺寸",
                        entries = imageSizeMap,
                        summary = imageSizeMap[imageSizeIndex.toString()]
                    )
                }
                item {
                    SingleChoicePreference(
                        selectedKey = speed.toString(),
                        onSelectionChanged = {
                            speed = it.toFloat()
                            viewModel.eventSink(PersonalSetsUiEvent.SetPlaybackSpeed(speed))
                        },
                        title = "倍速",
                        entries = speedMap,
                        summary = speedMap[speed.toString()]
                    )
                }

                item {
                    CheckboxPreference(
                        title = "无痕模式",
                        summaryOff = "无痕模式，开启或者关闭",
                        onCheckClicked = { viewModel.eventSink(PersonalSetsUiEvent.ToggleIncognitoMode) }, // 修正事件
                        checked = uiState.incognitoMode,  // 修正变量
                    )
                }
                item {
                    CheckboxPreference(
                        title =  "聚合搜索",
                        summaryOff = /*strings.settingsDynamicColorSummary*/"关闭/开启",
                        onCheckClicked = { viewModel.eventSink(PersonalSetsUiEvent.ToggleSearchAggregation) },
                        checked = uiState.searchAggregation,
                    )
                }
                item {
                    CheckboxPreference(
                        title =  "首页显示站源",
                        summaryOff = /*strings.settingsDynamicColorSummary*/"弹幕加载，开启或者关闭",
                        onCheckClicked = {
                            viewModel.eventSink(PersonalSetsUiEvent.ToggleShowSourcesOnHome)
                                         },
                        checked = uiState.showSourcesOnHome,
                    )
                }
                item {
                    CheckboxPreference(
                        title = "站源搜索",
                        summaryOff = "开启或关闭站源搜索",
                        onCheckClicked = { viewModel.eventSink(PersonalSetsUiEvent.ToggleSourceSearch) }, // 修正事件
                        checked = uiState.sourceSearchEnabled,  // 修正变量
                    )
                }
                item {
                    CheckboxPreference(
                        title =  "AI去广告",
                        summaryOff = /*strings.settingsDynamicColorSummary*/"AI去广告",
                        onCheckClicked = { viewModel.eventSink(PersonalSetsUiEvent.ToggleAIAdBlocking) },
                        checked = uiState.aiAdBlocking,
                    )
                }
                item {
                    CheckboxPreference(
                        title =  "内置下载",
                        summaryOff = /*strings.settingsDynamicColorSummary*/"内置下载",
                        onCheckClicked = { viewModel.eventSink(PersonalSetsUiEvent.ToggleBuiltInDownload) },
                        checked = uiState.builtInDownload,
                    )
                }

                item {
                    SingleChoicePreference(
                        selectedKey = languageIndex.toString(),
                        onSelectionChanged = {
                            languageIndex = it.toInt()
                            viewModel.eventSink(PersonalSetsUiEvent.SetLanguage(languageIndex))

                        },
                        title = "语言设置",
                        entries = languageMap,
                        summary = languageMap[languageIndex.toString()]
                    )
                }

                item {
                    SingleChoicePreference(
                        title = "接口缓存",
                        selectedKey = apiCacheIndex.toString(),
                        onSelectionChanged = {
                            apiCacheIndex = it.toInt()
                            viewModel.eventSink(PersonalSetsUiEvent.SetApiCache(apiCacheIndex))

                        },
                        entries = apiCacheMap,
                        summary = apiCacheMap[apiCacheIndex.toString()],
                    )
                }

                item {
                    Preference(
                        title =  "重置App",
                        summary = {
                            Text(
                                text =  "清理数据",
                            )
                        },
                        modifier =  Modifier.clickable{
                            uiState.eventSink(PersonalSetsUiEvent.ResetApp)
                        },
                    )
                }
            }
        }

    }
}