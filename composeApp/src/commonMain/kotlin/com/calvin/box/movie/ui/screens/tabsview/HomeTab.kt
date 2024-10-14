package com.calvin.box.movie.ui.screens.tabsview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.calvin.box.movie.bean.Class
import com.calvin.box.movie.feature.vod.VodListScreen
import com.calvin.box.movie.feature.vod.VodListScreenModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.ic_home_tab
import network.chaintech.sdpcomposemultiplatform.sdp
import org.jetbrains.compose.resources.painterResource

internal object HomeTab : Tab {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        Napier.d(tag =  TAG){"#HomeTab Content invoke"}
        val viewModel:HomeTabViewModel = getScreenModel()
        val categories by viewModel.homeClasses.collectAsState()
        if(categories.isEmpty()) return
        val tabs =
            mutableListOf<Tab>().apply {
                for (category in categories) {
                    Napier.d(tag = TAG) { "loop category: $category" }
                    if (category.typeId.isEmpty()) category.typeId = category.typeName
                    val categoryExt = category.getExtend(false)
                    add(VodListScreen(category.typeId, category.typeName, categoryExt))
                }
                Napier.d(tag = TAG) { "add homeTab to index 0 " }
                // 添加第一个推荐的 tab
                add(0, VodListScreen("Home", "推荐"))
            }


        val tab = tabs[0]
        TabNavigator(tab = tab,
            tabDisposable = {
                TabDisposable(
                    navigator = it,
                    tabs = tabs
                )
            })
        {
            val tabNavigator = LocalTabNavigator.current
            val pagerState = rememberPagerState(pageCount = { tabs.size })
            Scaffold(
                topBar = {
                    val coroutineScope = rememberCoroutineScope()
                    ScrollableTabRow(selectedTabIndex = pagerState.currentPage,
                        edgePadding = 0.sdp) {
                        Napier.d(tag = TAG) { " tabs for eachIndexed" }
                        tabs.forEachIndexed { index, tabItem ->
                            Napier.d(tag = TAG) { " loop tab index: $index" }
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                        tabNavigator.current = tabItem
                                    }
                                },
                                text = { Text(tabItem.options.title) },
                                /*icon = {
                                    tabItem.options.icon?.let { it1 ->
                                        Icon(
                                            painter = it1,
                                            contentDescription = null
                                        )
                                    }
                                }*/
                            )
                        }
                    }
                }
            ){
                    innerPadding ->
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        //.fillMaxSize()
                        .padding(innerPadding)
                ) { page ->
                    run {
                        Napier.d(tag = TAG) { "run page" }
                        tabs[page].Content()
                    }
                }
            }
            LaunchedEffect(pagerState.currentPage) {
                Napier.d(tag = TAG) { "launchedEffect" }
                tabNavigator.current = tabs[pagerState.currentPage]
            }
        }
    }


    override val options: TabOptions
        @Composable
        get() {
            val image = painterResource(Res.drawable.ic_home_tab)
            return remember { TabOptions(index = 0u, title = "Home", icon = image) }
        }


    const val TAG = "xbox.HomeTab"
}