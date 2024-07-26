package com.calvin.box.movie.feature.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.aakira.napier.Napier


class SettingsScreen:Screen {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: SettingsViewModel = getScreenModel()
        val settingsUiState by viewModel.uiState.collectAsState()
        Napier.d { "uiState: $settingsUiState" }
        val uiState = settingsUiState ?: return

        val nv = LocalNavigator.currentOrThrow
        var theme by remember { mutableStateOf(uiState.theme) }
        var vodUrl by remember{mutableStateOf(uiState.vodUrl)}
        var vodName by remember{mutableStateOf(uiState.vodName)}

        val vodhistory by remember { mutableStateOf(uiState.vodUrls) }
        var liveUrl by remember{mutableStateOf(uiState.liveUrl)}
        var wallPaperUrl by remember{mutableStateOf(uiState.wallPaperUrl)}

        var volume by remember { mutableStateOf(uiState.volume) }

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
        ) {contentPadding ->
            LazyColumn(
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxWidth(),
            ) {
                stickyHeader {
                    PreferenceHeader(/*LocalStrings.current.settingsUiCategoryTitle*/"User interface")
                }

                item {
                    ThemePreference(
                        title = /*strings.settingsThemeTitle*/"Theme",
                        selected = theme,
                        onThemeSelected = {
                            theme = it
                            viewModel.eventSink(SettingsUiEvent.SetTheme(theme))
                                          },
                    )
                }

                item { PreferenceDivider() }

                if (uiState.dynamicColorsAvailable) {
                    item {
                        CheckboxPreference(
                            title = /*strings.settingsDynamicColorTitle*/"Dynamic Colors",
                            summaryOff = /*strings.settingsDynamicColorSummary*/"use colors derived from your wallpaper",
                            onCheckClicked = { viewModel.eventSink(SettingsUiEvent.ToggleUseDynamicColors) },
                            checked = uiState.useDynamicColors,
                        )
                    }

                    item { PreferenceDivider() }
                }

                item {

                    val vodsites = remember { listOf("Site 1", "Site 2", "Site 3") }

                    VodPreference(
                        vodAddress = vodUrl,
                        onVodAddressChange = { vodUrl = it },
                        vodName = vodName,
                        onVodNameChange = {vodName = it},
                        sites = vodsites,
                        onSiteSelected = { selectedSite ->
                           // vodUrl = "http://$selectedSite"
                        },
                        history = vodhistory,
                        onHistorySelected = { selectedHistory ->
                            vodUrl = selectedHistory
                        },
                        onHistoryDeleted = { itemToDelete ->
                            viewModel.eventSink(SettingsUiEvent.DelVodUrl(itemToDelete))
                        },
                        onSaved = {
                            viewModel.eventSink(SettingsUiEvent.SetVodUrl(vodUrl, vodName))
                        }
                    )
                }

                item {
                    PreferenceScreen()
                }

                item{
                    EditTextPreference(
                        value = vodUrl,
                        onValueChange = {
                            vodUrl = it
                            viewModel.eventSink(SettingsUiEvent.SetLiveUrl(vodUrl))
                        },
                        title = "点播地址",
                        summary = vodUrl
                    )
                }
                item{
                    EditTextPreference(
                        value = liveUrl,
                        onValueChange = {
                            liveUrl = it
                            viewModel.eventSink(SettingsUiEvent.SetLiveUrl(liveUrl))
                        },
                        title = "直播地址",
                        summary = liveUrl
                    )
                }
                item{
                    EditTextPreference(
                        value = wallPaperUrl,
                        onValueChange = {
                            wallPaperUrl = it
                            viewModel.eventSink(SettingsUiEvent.SetWallPaperUrl(wallPaperUrl))
                        },
                        title = "壁纸地址",
                        summary = wallPaperUrl
                    )
                }

                item {
                    SingleChoicePreference(
                        selectedKey = "dark",
                        onSelectionChanged = { /* 处理主题更改 */ },
                        title = "选择主题",
                        entries = mapOf("light" to "浅色", "dark" to "深色", "system" to "跟随系统"),
                        summary = "选择应用主题"
                    )
                }

                item {
                    SliderPreference(
                        value = volume/100f,
                        onValueChange = {
                            volume = (it*100).toInt()
                            viewModel.eventSink(SettingsUiEvent.SetVolume(volume))

                        },
                        title = "音量",
                        summary = { "当前音量: ${(it * 100).toInt()}%" }
                    )
                }

               item {
                   ListPreference(
                       title = "选择语言",
                       items = listOf("简体中文", "English", "日本語"),
                       onItemSelected = { /* 处理语言选择 */ },
                       summary = "选择应用界面语言"
                   )
               }

            }
        }
    }

}