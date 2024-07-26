package com.calvin.box.movie.feature.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.calvin.box.movie.Theme
import io.github.aakira.napier.Napier

@Composable
fun CheckboxPreference(
    checked: Boolean,
    onCheckClicked: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    summaryOff: String? = null,
    summaryOn: String? = null,
) {
    Preference(
        title = title,
        summary = {
            if (summaryOff != null && summaryOn != null) {
                AnimatedContent(checked) { target ->
                    Text(text = if (target) summaryOn else summaryOff)
                }
            } else if (summaryOff != null) {
                Text(text = summaryOff)
            }
        },
        control = {
            Switch(
                checked = checked,
                onCheckedChange = { onCheckClicked() },
            )
        },
        modifier = modifier,
    )
}

@Composable
fun EditTextPreference(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    placeholder: String = "",
) {
    var showDialog by remember { mutableStateOf(false) }
    var tempValue by remember { mutableStateOf(value) }

    Preference(
        title = title,
        summary = { if (summary != null) Text(summary) else Text(value) },
        modifier = modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(title) },
            text = {
                OutlinedTextField(
                    value = tempValue,
                    onValueChange = { tempValue = it },
                    placeholder = { Text(placeholder) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onValueChange(tempValue)
                    showDialog = false
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}



@Composable
fun SingleChoicePreference(
    selectedKey: String,
    onSelectionChanged: (String) -> Unit,
    title: String,
    entries: Map<String, String>,
    modifier: Modifier = Modifier,
    summary: String? = null,
) {
    var showDialog by remember { mutableStateOf(false) }
    var tempSelection by remember { mutableStateOf(selectedKey) }

    Preference(
        title = title,
        summary = {
            if (summary != null) Text(summary)
            else Text(entries[selectedKey] ?: "")
        },
        modifier = modifier.clickable { showDialog = true }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(title) },
            text = {
                Column {
                    entries.forEach { (key, value) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (key == tempSelection),
                                    onClick = { tempSelection = key }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (key == tempSelection),
                                onClick = null
                            )
                            Text(
                                text = value,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onSelectionChanged(tempSelection)
                    showDialog = false
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun SliderPreference(
    value: Float,
    onValueChange: (Float) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    summary: (Float) -> String = { it.toString() }
) {
    Preference(
        title = title,
        summary = { Text(summary(value)) },
        control = {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier.width(150.dp)
            )
        },
        modifier = modifier
    )
}

@Composable
fun ListPreference(
    title: String,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Preference(
        title = title,
        summary = { if (summary != null) Text(summary) },
        modifier = modifier.clickable { expanded = true }
    )

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        items.forEach { item ->
            DropdownMenuItem(
                text = { Text(item) },
                onClick = {
                    onItemSelected(item)
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun Preference(
    title: String,
    modifier: Modifier = Modifier,
    summary: (@Composable () -> Unit)? = null,
    control: (@Composable () -> Unit)? = null,
) {
    Surface(modifier = modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                )

                if (summary != null) {
                    ProvideTextStyle(
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    ) {
                        summary()
                    }
                }
            }

            control?.invoke()
        }
    }
}

@Composable
fun PreferenceHeader(
    title: String,
    modifier: Modifier = Modifier,
    tonalElevation: Dp = 0.dp,
) {
    Surface(modifier = modifier, tonalElevation = tonalElevation) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp),
        )
    }
}

@Composable
fun PreferenceDivider() {
    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
}



@Composable
fun ThemePreference(
    selected: Theme,
    onThemeSelected: (Theme) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
) {
    Napier.d { "ThemePreference: $selected" }
    Preference(
        title = title,
        control = {
            Row(Modifier.selectableGroup()) {
                ThemeButton(
                    icon = Icons.Default.AutoMode,
                    onClick = { onThemeSelected(Theme.SYSTEM) },
                    isSelected = selected == Theme.SYSTEM,
                )

                ThemeButton(
                    icon = Icons.Default.LightMode,
                    onClick = { onThemeSelected(Theme.LIGHT) },
                    isSelected = selected == Theme.LIGHT,
                )

                ThemeButton(
                    icon = Icons.Default.DarkMode,
                    onClick = { onThemeSelected(Theme.DARK) },
                    isSelected = selected == Theme.DARK,
                )
            }
        },
        modifier = modifier,
    )
}



@Composable
private fun ThemeButton(
    isSelected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    FilledIconToggleButton(
        checked = isSelected,
        onCheckedChange = { onClick() },
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
        )
    }
}



@Composable
fun EditablePreference(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onEditClick: () -> Unit,
    controls: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Preference(
        title = title,
        summary = { Text(value) },
        control = {
            Row {
                controls()
            }
        },
        modifier = modifier.clickable(onClick = onEditClick)
    )
}

@Composable
fun EditDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    extraContent: @Composable (() -> Unit)? = null
) {
    var editingValue by remember { mutableStateOf(value) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                TextField(
                    value = editingValue,
                    onValueChange = { editingValue = it },
                    modifier = Modifier.fillMaxWidth()
                )
                extraContent?.invoke()
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onValueChange(editingValue)
                onConfirm()
            }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun VodPreference(
    vodAddress: String,
    onVodAddressChange: (String) -> Unit,
    vodName: String,
    onVodNameChange: (String) -> Unit,
    sites: List<String>,
    onSiteSelected: (String) -> Unit,
    history: List<String>,
    onHistorySelected: (String) -> Unit,
    onHistoryDeleted: (String) -> Unit,
    onSaved:() -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showSitesDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    EditablePreference(
        title = "VOD Address",
        value = vodAddress,
        onValueChange = onVodAddressChange,
        onEditClick = { showEditDialog = true },
        controls = {
            IconButton(onClick = { showSitesDialog = true }) {
                Icon(Icons.Default.Home, contentDescription = "Home")
            }
            IconButton(onClick = { showHistoryDialog = true }) {
                Icon(Icons.Default.History, contentDescription = "History")
            }
        },
        modifier = modifier
    )

    if (showEditDialog) {
        EditDialog(
            title = "Edit VOD Address",
            value = vodAddress,
            onValueChange = onVodAddressChange,
            onDismiss = { showEditDialog = false },
            onConfirm = {
                showEditDialog = false
                onSaved()
            }
        ){
            TextField(
                value = vodName,
                onValueChange = onVodNameChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showSitesDialog) {
        SitesDialog(sites, onSiteSelected) { showSitesDialog = false }
    }

    if (showHistoryDialog) {
        HistoryDialog(history, onHistorySelected, onHistoryDeleted) { showHistoryDialog = false }
    }
}

@Composable
fun LivePreference(
    liveAddress: String,
    onLiveAddressChange: (String) -> Unit,
    sites: List<String>,
    onSiteSelected: (String) -> Unit,
    history: List<String>,
    onHistorySelected: (String) -> Unit,
    onHistoryDeleted: (String) -> Unit,
    onFileSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showSitesDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

    EditablePreference(
        title = "Live Address",
        value = liveAddress,
        onValueChange = onLiveAddressChange,
        onEditClick = { showEditDialog = true },
        controls = {
            IconButton(onClick = { showSitesDialog = true }) {
                Icon(Icons.Default.Home, contentDescription = "Home")
            }
            IconButton(onClick = { showHistoryDialog = true }) {
                Icon(Icons.Default.History, contentDescription = "History")
            }
        },
        modifier = modifier
    )

    if (showEditDialog) {
        EditDialog(
            title = "Edit Live Address",
            value = liveAddress,
            onValueChange = onLiveAddressChange,
            onDismiss = { showEditDialog = false },
            onConfirm = { showEditDialog = false },
            extraContent = {
                IconButton(onClick = { onFileSelected(liveAddress) }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Select File")
                }
            }
        )
    }

    if (showSitesDialog) {
        SitesDialog(sites, onSiteSelected) { showSitesDialog = false }
    }

    if (showHistoryDialog) {
        HistoryDialog(history, onHistorySelected, onHistoryDeleted) { showHistoryDialog = false }
    }
}

@Composable
fun WallPaperPreference(
    wallpaperAddress: String,
    onWallpaperAddressChange: (String) -> Unit,
    onSwitchWallpaper: () -> Unit,
    onRefreshWallpaper: () -> Unit,
    onFileSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }

    EditablePreference(
        title = "Wallpaper",
        value = wallpaperAddress,
        onValueChange = onWallpaperAddressChange,
        onEditClick = { showEditDialog = true },
        controls = {
            IconButton(onClick = onSwitchWallpaper) {
                Icon(Icons.Default.Home, contentDescription = "Switch Wallpaper")
            }
            IconButton(onClick = onRefreshWallpaper) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh Wallpaper")
            }
        },
        modifier = modifier
    )

    if (showEditDialog) {
        EditDialog(
            title = "Edit Wallpaper Address",
            value = wallpaperAddress,
            onValueChange = onWallpaperAddressChange,
            onDismiss = { showEditDialog = false },
            onConfirm = { showEditDialog = false },
            extraContent = {
                IconButton(onClick = { onFileSelected(wallpaperAddress) }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Select File")
                }
            }
        )
    }
}

@Composable
fun SitesDialog(
    sites: List<String>,
    onSiteSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Site") },
        text = {
            LazyColumn {
                items(sites) { site ->
                    TextButton(
                        onClick = {
                            onSiteSelected(site)
                            onDismiss()
                        }
                    ) {
                        Text(site)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun HistoryDialog(
    history: List<String>,
    onHistorySelected: (String) -> Unit,
    onHistoryDeleted: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("History") },
        text = {
            LazyColumn {
                items(history) { historyItem ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                onHistorySelected(historyItem)
                                onDismiss()
                            }
                        ) {
                            Text(historyItem)
                        }
                        IconButton(onClick = { onHistoryDeleted(historyItem) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}


@Composable
fun PreferenceScreen() {



    var liveAddress by remember { mutableStateOf("") }
    val sites = remember { listOf("Site 1", "Site 2", "Site 3") }
    val history = remember { mutableStateListOf("History 1", "History 2", "History 3") }

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

    var wallpaperAddress by remember { mutableStateOf("") }
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