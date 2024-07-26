package com.calvin.box.movie

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.calvin.box.movie.feature.settings.SettingsScreen
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
fun DarkmovieMainView() {
    MyApplicationTheme {
        val navigation = remember { NavigationProvider() }
        val screenContainer = remember { ScreenContainerProvider() }
        CompositionLocalProvider(
            LocalScreenContainer provides screenContainer,
            LocalNavigation provides navigation,
        ) {
            Napier.i {  "enter compositionProvider" }
            TabNavigator(
                HomeTab,
                tabDisposable = {
                    TabDisposable(
                        navigator = it,
                        tabs = listOf(HomeTab, ReelsTab, MusicTab)
                    )
                }
            ) {
                Napier.i {  "enter Navigator" }
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
        Napier.i {  "enter homeScreen" }
        val nv = LocalNavigator.currentOrThrow
        Scaffold(
            modifier = Modifier
                ///.background(color = MyApplicationTheme.colors.bottomTabBarColor)
                .padding(bottom = getSafeAreaSize().bottom.dp),
            scaffoldState = rememberScaffoldState(),
            //backgroundColor = MyApplicationTheme.colors.bottomTabBarColor,
            bottomBar = {
                BottomNavigation(
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
            topBar = { RootScreenAppBar("Home", false,
                onRefreshActionClick = {},
                onSettingsActionClick = {
                   nv.push(SettingsScreen())
                } ) },

        ) {

            CurrentTab()
        }
    }
}



// Copyright 2021, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0



@Composable
fun RootScreenAppBar(
    title: String,
   // loggedIn: Boolean,
    //user: TraktUser?,
    refreshing: Boolean,
    onRefreshActionClick: () -> Unit ,
    onSettingsActionClick: () -> Unit ,
    modifier: Modifier = Modifier,
    //scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        modifier = modifier,
        //colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        //scrollBehavior = scrollBehavior,
        title = { Text(text = title) },
        actions = {
            // This button refresh allows screen-readers, etc to trigger a refresh.
            // We only show the button to trigger a refresh, not to indicate that
            // we're currently refreshing, otherwise we have 4 indicators showing the
            // same thing.
            Crossfade(
                targetState = refreshing,
                modifier = Modifier.align(Alignment.CenterVertically),
            ) { isRefreshing ->
                if (!isRefreshing) {
                    RefreshButton(onClick = onRefreshActionClick)
                }
            }

           /* UserProfileButton(
                loggedIn = loggedIn,
                "",//user = user,
                onClick = onUserActionClick,
                modifier = Modifier.align(Alignment.CenterVertically),
            )*/
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
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val title = tab.options.title
    BottomNavigationItem(
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