package com.calvin.box.movie.feature.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.aakira.napier.Napier
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.settings_about_category_title
import moviebox.composeapp.generated.resources.settings_analytics_data_collection_summary
import moviebox.composeapp.generated.resources.settings_analytics_data_collection_title
import moviebox.composeapp.generated.resources.settings_app_version
import moviebox.composeapp.generated.resources.settings_app_version_summary
import moviebox.composeapp.generated.resources.settings_crash_data_collection_summary
import moviebox.composeapp.generated.resources.settings_crash_data_collection_title
import moviebox.composeapp.generated.resources.settings_open_source
import moviebox.composeapp.generated.resources.settings_open_source_summary
import moviebox.composeapp.generated.resources.settings_privacy_category_title
import moviebox.composeapp.generated.resources.view_privacy_policy
import moviebox.composeapp.generated.resources.developer_settings_title
import moviebox.composeapp.generated.resources.settings_personalization_title
import moviebox.composeapp.generated.resources.settings_player_title


import org.jetbrains.compose.resources.stringResource


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

        val dohList = uiState.dohList
        val dohMap by remember { mutableStateOf(uiState.dohList.withIndex().associate { it.index.toString() to it.value.name }) }
        var dohIndex by remember { mutableStateOf(uiState.dohIndex) }

        var proxy by remember { mutableStateOf(uiState.proxy) }

        var liveAddress by remember { mutableStateOf("") }
        val sites = remember { listOf("Site 1", "Site 2", "Site 3") }
        val history = remember { mutableStateListOf("History 1", "History 2", "History 3") }
        var wallpaperAddress by remember { mutableStateOf("") }


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
                item { PreferenceDivider() }

                item {
                    LivePreference(
                        liveAddress = liveAddress,
                        onLiveAddressChange = { liveAddress = it },
                        sites = sites,
                        onSiteSelected = { selectedSite ->
                            liveAddress = "http://$selectedSite"
                        },
                        history = history,
                        onHistorySelected = { selectedHistory ->
                            liveAddress = selectedHistory
                        },
                        onHistoryDeleted = { itemToDelete ->
                            history.remove(itemToDelete)
                        },
                        onFileSelected = { filePath ->
                            // TODO: Implement file import logic
                            liveAddress = filePath
                        }
                    )
                }
                item { PreferenceDivider() }

                item {
                    WallPaperPreference(
                        wallpaperAddress = wallpaperAddress,
                        onWallpaperAddressChange = { wallpaperAddress = it },
                        onSwitchWallpaper = {
                            // 实现切换壁纸的逻辑
                            println("Switching wallpaper")
                        },
                        onRefreshWallpaper = {
                            // 实现刷新壁纸的逻辑
                            println("Refreshing wallpaper")
                        },
                        onFileSelected = { filePath ->
                            // 实现文件选择逻辑
                            println("Selected file: $filePath")
                            wallpaperAddress = filePath
                        }
                    )
                }
                item { PreferenceDivider() }

                item {
                    Preference(
                        title = stringResource(Res.string.settings_player_title),
                        modifier = Modifier.clickable{
                            nv.push(PlayerSettingsScreen())
                        },
                    )
                }
                item { PreferenceDivider() }
                item {
                    Preference(
                        title = stringResource(Res.string.settings_personalization_title),
                        modifier = Modifier.clickable{
                            nv.push(PersonalizationSettingsScreen())
                        },
                    )
                }
                item { PreferenceDivider() }

                item {
                    SingleChoicePreference(
                        selectedKey = dohIndex.toString(),
                        onSelectionChanged = {
                            dohIndex = it.toUInt()
                            viewModel.eventSink(SettingsUiEvent.SetDoh(dohList[dohIndex.toInt()]))

                        },
                        title = "Doh",
                        entries = dohMap,
                        summary = dohList[dohIndex.toInt()].name
                    )
                }
                item { PreferenceDivider() }
                item{
                    EditTextPreference(
                        value = proxy,
                        onValueChange = {
                            proxy = it
                            viewModel.eventSink(SettingsUiEvent.SetProxy(proxy))
                        },
                        title = "Proxy",
                        summary = proxy
                    )
                }
                item { PreferenceDivider() }
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

                stickyHeader {
                    PreferenceHeader(stringResource(Res.string.settings_privacy_category_title))
                }

                item {
                    Preference(
                        title = stringResource(Res.string.view_privacy_policy),
                        //onClick = { viewModel.eventSink(SettingsUiEvent.NavigatePrivacyPolicy) },
                    )
                }

                item { PreferenceDivider() }
                item {
                    CheckboxPreference(
                        title = stringResource(Res.string.settings_crash_data_collection_title),
                        summaryOff = stringResource(Res.string.settings_crash_data_collection_summary),
                        onCheckClicked = { viewModel.eventSink(SettingsUiEvent.ToggleCrashDataReporting) },
                        checked = uiState.crashDataReportingEnabled,
                    )
                }

                item { PreferenceDivider() }

                item {
                    CheckboxPreference(
                        title = stringResource(Res.string.settings_analytics_data_collection_title),
                        summaryOff = stringResource(Res.string.settings_analytics_data_collection_summary),
                        onCheckClicked = { viewModel.eventSink(SettingsUiEvent.ToggleAnalyticsDataReporting) },
                        checked = uiState.analyticsDataReportingEnabled,
                    )
                }

                itemSpacer(24.dp)
                stickyHeader {
                    PreferenceHeader(stringResource(Res.string.settings_about_category_title))
                }

                item {
                    Preference(
                        title = stringResource(Res.string.settings_app_version),
                        summary = {
                            Text(
                                text = stringResource(
                                    Res.string.settings_app_version_summary,
                                    uiState./*applicationInfo.*/versionName,
                                    uiState./*applicationInfo.*/versionCode,
                                ),
                            )
                        },
                    )
                }

                if (uiState.openSourceLicenseAvailable) {
                    item { PreferenceDivider() }

                    item {
                        Preference(
                            title = stringResource(Res.string.settings_open_source),
                            summary = {
                                Text(stringResource(Res.string.settings_open_source_summary))
                            },
                            //onClick = { viewModel.eventSink(SettingsUiEvent.NavigateOpenSource) },
                        )
                    }
                }

                if (uiState.showDeveloperSettings) {
                    item { PreferenceDivider() }

                    item {
                        Preference(
                            title = stringResource(Res.string.developer_settings_title),
                            //onClick = { viewModel.eventSink(SettingsUiEvent.NavigateDeveloperSettings) },
                        )
                    }
                }
                itemSpacer(16.dp)

            }
        }
    }

}