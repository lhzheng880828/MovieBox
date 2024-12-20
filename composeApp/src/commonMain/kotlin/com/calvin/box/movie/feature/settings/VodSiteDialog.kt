package com.calvin.box.movie.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.calvin.box.movie.bean.Site
import kotlinx.coroutines.runBlocking

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/9/14
 */
@Composable
fun VodSitesDialog(
    siteCallback: SiteCallback,
    sites: List<Site>,
    onSiteSelected: (String) -> Unit,
    showSearchBar: Boolean = true,
    showChange: Boolean = true,
    onDismiss: () -> Unit,
) {
    var keyword by remember { mutableStateOf("") }
    var filteredSites by remember { mutableStateOf(sites) }
    val initSelectedIndex = filteredSites.indexOfFirst { it.activated }.takeIf { it != -1 } ?: 0
    val itemToScrollTo by remember { mutableStateOf(initSelectedIndex) }

    // 更新站点列表
    fun searchSite() {
        filteredSites = if (keyword.isBlank()) {
            sites
        } else {
            sites.filter { it.name.contains(keyword, ignoreCase = true) }
        }
    }
    BoxWithConstraints {
        val maxHeight = maxHeight * 2 / 3
        val maxWidth = maxWidth * 0.8f

    // Dialog 构建
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "选择站点") },
        text = {
            Column {
                if (showSearchBar) {
                    SearchBar(
                        keyword = keyword,
                        onKeywordChange = {
                            keyword = it
                            searchSite()
                        },
                        onSearchClick = { searchSite() }
                    )
                }
                // 创建 LazyListState
                val listState = rememberLazyListState()

                // 在 Composable 的 LaunchedEffect 中实现滚动
                LaunchedEffect(itemToScrollTo) {
                    itemToScrollTo.let { index ->
                        listState.animateScrollToItem(index)
                    }
                }

                LazyColumn(state = listState) {
                    items(filteredSites) { site ->
                        SiteListItem(
                            site = site,
                            activated = site.activated,
                            onTextClick = {
                                siteCallback.setSite(site)
                                onSiteSelected(site.name)
                                // 关闭对话框
                                // 更新列表中选中的站点
                                filteredSites = filteredSites.map {
                                    if (it.key == site.key) {
                                        it.copy(activated = true) // 选中的 site
                                    } else {
                                        it.copy(activated = false) // 未选中的 site
                                    }
                                }
                                onDismiss()
                            },
                            onSearchClick = { _, it ->
                                site.setSearchable(!it.isSearchable())
                                runBlocking { it.save() }
                                searchSite()
                                siteCallback.onChanged()
                            },
                            onChangeClick = { _, it ->
                                site.setChangeable(!it.isChangeable())
                                runBlocking { it.save() }
                                searchSite()
                            },
                            onSearchLongClick = { it ->
                                val result = !it.isSearchable()
                                sites.forEach {
                                    it.setSearchable(result)
                                    runBlocking { it.save() }
                                }
                                searchSite()
                                siteCallback.onChanged()
                                true
                            },
                            onChangeLongClick = { it ->
                                val result = !it.isChangeable()
                                sites.forEach {
                                    it.setChangeable(result)
                                    runBlocking { it.save() }
                                }
                                searchSite()
                                true
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false), // 禁用默认宽度限制
        modifier = Modifier
            .heightIn(max = maxHeight)
            .widthIn(max = maxWidth)
            .padding(8.dp)
    )
    }
}

@Composable
fun SearchBar(
    keyword: String,
    onKeywordChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = keyword,
            onValueChange = onKeywordChange,
            label = { Text("搜索") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchClick() })
        )
        IconButton(onClick = { onSearchClick() }) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
    }
}

@Composable
fun SiteListItem(
    site: Site,
    activated: Boolean,
    onTextClick: () -> Unit,
    onSearchClick: (Int, Site) -> Unit,
    onChangeClick: (Int, Site) -> Unit,
    onSearchLongClick: (Site) -> Boolean,
    onChangeLongClick: (Site) -> Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onTextClick()
            }
            .background(color = if (activated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = site.name,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(
            onClick = {
                onSearchClick(
                    0,
                    site
                )
            },
            /*onLongClick = { onSearchLongClick(site) }*/
        ) {
            Icon(
                Icons.Filled.Search,
                //painter = painterResource(id = if (site.isSearchable) R.drawable.ic_site_search else R.drawable.ic_site_block),
                contentDescription = null
            )
        }
        IconButton(
            onClick = {
                onChangeClick(
                    0,
                    site
                )
            },
            /*onLongClick = { onChangeLongClick(site) }*/
        ) {
            Icon(
                Icons.Filled.ChangeCircle,
                //painter = painterResource(id = if (site.isChangeable) R.drawable.ic_site_change else R.drawable.ic_site_block),
                contentDescription = null
            )
        }
    }
}

interface SiteCallback {
    fun setSite(item: Site)

    fun onChanged()
}