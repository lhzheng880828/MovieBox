package com.calvin.box.movie

//import androidx.compose.material3.rememberScaffoldState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.feature.collection.SearchScreen
import com.calvin.box.movie.feature.followed.FollowedScreen
import com.calvin.box.movie.feature.history.HistoryScreen
import com.calvin.box.movie.feature.settings.SettingsScreen
import com.calvin.box.movie.feature.settings.SiteCallback
import com.calvin.box.movie.feature.settings.VodSitesDialog
import com.calvin.box.movie.font.FontType
import com.calvin.box.movie.font.MediaFont
import com.calvin.box.movie.navigation.LocalNavigation
import com.calvin.box.movie.navigation.LocalScreenContainer
import com.calvin.box.movie.navigation.NavigationProvider
import com.calvin.box.movie.navigation.ScreenContainerProvider
import com.calvin.box.movie.theme.MyApplicationTheme
import com.calvin.box.movie.ui.screens.tabsview.HomeTab
import com.calvin.box.movie.ui.screens.tabsview.MusicTab
import com.calvin.box.movie.ui.screens.tabsview.ReelsTab
import com.calvin.box.movie.utility.BottomNavigationBarHeight
import com.calvin.box.movie.utility.getSafeAreaSize
import io.github.aakira.napier.Napier
import network.chaintech.sdpcomposemultiplatform.sdp

@Composable
fun MyMovieApp() {
    MyApplicationTheme {
        val navigation = remember { NavigationProvider() }
        val screenContainer = remember { ScreenContainerProvider() }
        CompositionLocalProvider(
            LocalScreenContainer provides screenContainer,
            LocalNavigation provides navigation,
        ) {
            Napier.i { "enter compositionProvider" }
            TabNavigator(
                HomeTab,
                tabDisposable = {
                    TabDisposable(
                        navigator = it,
                        tabs = listOf(HomeTab, ReelsTab, MusicTab)
                    )
                }
            ) {
                Napier.i { "enter Navigator" }
                Navigator(HomeScreen()) {
                    navigation.initialize()
                    SlideTransition(it)
                }
            }
        }
    }
}

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        Napier.i { "enter homeScreen" }
        val nv = LocalNavigator.currentOrThrow
        val viewModel: HomeScreenModel = getScreenModel()
        Scaffold(
            modifier = Modifier
                ///.background(color = MyApplicationTheme.colors.bottomTabBarColor)
                .padding(bottom = getSafeAreaSize().bottom.dp),
            //scaffoldState = rememberScaffoldState(),
            //backgroundColor = MyApplicationTheme.colors.bottomTabBarColor,
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .height(BottomNavigationBarHeight),
                    //contentColor = MyApplicationTheme.colors.bottomTabBarColor,
                    //backgroundColor = MyApplicationTheme.colors.bottomTabBarColor,
                ) {
                    TabNavigationItem(tab = HomeTab)
                    TabNavigationItem(tab = ReelsTab)
                    TabNavigationItem(tab = MusicTab)
                }
            },
            topBar = {
                RootScreenAppBar(
                    refreshing = false,
                    viewModel = viewModel,
                    onRefreshActionClick = {},
                    onSearchActionClick = {
                        Napier.d { "search field clicked." }
                        nv.push(SearchScreen())
                    },
                    onFavoriteActionClick = { nv.push(FollowedScreen()) },
                    onHistoryActionClick = { nv.push(HistoryScreen()) },
                    onSettingsActionClick = { nv.push(SettingsScreen()) }
                )
            },

            ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding) // 添加内边距
                    .fillMaxSize()
            ) {
                CurrentTab()
            }

        }
    }
}


// Copyright 2021, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootScreenAppBar(
    title: String = "主站",
    viewModel: HomeScreenModel,
    // loggedIn: Boolean,
    //user: TraktUser?,
    refreshing: Boolean,
    onRefreshActionClick: () -> Unit,
    onSearchActionClick: () -> Unit,
    onFavoriteActionClick: () -> Unit,
    onHistoryActionClick: () -> Unit,
    onSettingsActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    //scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    Napier.d { "draw RootScreenAppBar" }
    val showSite by remember { mutableStateOf(viewModel.showSite()) }

    var showSitesDialog by remember { mutableStateOf(false) }

    val siteList by remember { mutableStateOf(viewModel.getSiteList()) }

    val siteName = viewModel.getHomeSite()?.name
    val titleName = if(siteName.isNullOrEmpty()) title else siteName

    TopAppBar(
        modifier = modifier,
        //colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        //scrollBehavior = scrollBehavior,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                //horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Site Home",
                    modifier = Modifier
                        .padding(start = 2.dp, end = 4.dp)
                        .clickable {
                            showSitesDialog = true
                        }
                )
                if (showSite) {
                    Text(
                        text = titleName,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(start = 2.dp, end = 4.dp)
                            .clickable {
                                showSitesDialog = true
                            }

                    )
                }
                if (!showSite) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                onSearchActionClick()
                            }
                    ) {
                        SearchBar(viewModel = viewModel)
                    }
                }
            }
        },
        actions = {
            // This button refresh allows screen-readers, etc to trigger a refresh.
            // We only show the button to trigger a refresh, not to indicate that
            // we're currently refreshing, otherwise we have 4 indicators showing the
            // same thing.
            /* Crossfade(
                 targetState = refreshing,
                 modifier = Modifier.align(Alignment.CenterVertically),
             ) { isRefreshing ->
                 if (!isRefreshing) {
                     RefreshButton(onClick = onRefreshActionClick)
                 }
             }*/

            /* UserProfileButton(
                 loggedIn = loggedIn,
                 "",//user = user,
                 onClick = onUserActionClick,
                 modifier = Modifier.align(Alignment.CenterVertically),
             )*/
            if (showSite) {
                IconButton(
                    onClick = onSearchActionClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Button"
                    )
                }
            }

            IconButton(
                onClick = onFavoriteActionClick
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite Button"
                )
            }
            IconButton(
                onClick = onHistoryActionClick
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History Button"
                )
            }

            IconButton(
                onClick = onSettingsActionClick
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings Button"
                )
            }
        },
    )

    if (showSitesDialog) {
        VodSitesDialog(
            siteCallback = object : SiteCallback {
                override fun setSite(item: Site) {
                    viewModel.setHomeSite(item)
                }

                override fun onChanged() {

                }

            },
            showSearchBar = false,
            sites = siteList,
            onSiteSelected = {},

            ) { showSitesDialog = false }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchBar(viewModel: HomeScreenModel) {
    val hotState by viewModel.hotState.collectAsState()
    val randomIndex by viewModel.randomIndex.collectAsState()
    Napier.d { "draw SearchBar ${hotState.size}, randomIndex: $randomIndex" }
    var textState by remember { mutableStateOf("") }
    var randomHotWord = ""
    if (hotState.isNotEmpty()) {
        if (randomIndex < hotState.size) {
            randomHotWord = hotState[randomIndex]
        } else {
            randomHotWord = hotState[0]
        }
    }
    TextField(
        value = textState,
        readOnly = true,
        enabled = false,
        onValueChange = { textState = it },
        placeholder = { Text(randomHotWord, maxLines = 1) },
        modifier = Modifier
            .width(160.dp)
            .height(56.dp),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
        },
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors().copy(
            //focusedContainerColor = Color.Transparent,
            //unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        )
    )

}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val title = tab.options.title
    NavigationBarItem(
        /*modifier = Modifier
            .background(MyApplicationTheme.colors.bottomTabBarColor),*/
        alwaysShowLabel = true,
        label = {
            Text(
                modifier = Modifier
                    .padding(bottom = 4.sdp),
                text = title,
                style = MediaFont.lexendDeca(
                    size = FontType.Small,
                    type = MediaFont.LexendDeca.Regular
                ),
            )
        },
        selected = tabNavigator.current.key == tab.key,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let {
                Icon(
                    painter = it,
                    contentDescription = tab.options.title,
                    modifier = Modifier
                        .padding(top = 10.sdp, bottom = 5.sdp)
                        .size(20.sdp)
                )
            }
        },
        // selectedContentColor = Color.White,
        // unselectedContentColor = MyApplicationTheme.colors.secondaryText
    )
}