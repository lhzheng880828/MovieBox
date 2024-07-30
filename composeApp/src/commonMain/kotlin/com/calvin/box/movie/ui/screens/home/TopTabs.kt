package com.calvin.box.movie.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.tab.*
import com.calvin.box.movie.bean.Class
import com.calvin.box.movie.feature.vod.VodListScreen
import com.calvin.box.movie.ui.screens.tabsview.HomeTabViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.ic_home_tab
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp
import org.jetbrains.compose.resources.painterResource


internal object RecommendTab : Tab {
    @Composable
    override fun Content() {
        HomeView()
    }


    override val options: TabOptions
        @Composable
        get() {
            val image = painterResource(Res.drawable.ic_home_tab)
            return remember { TabOptions(index = 0u, title = "推荐", icon = image) }
        }
}

internal object movieTab : Tab {
    @Composable
    override fun Content() {

       Column(/*modifier = Modifier.padding(top = 48.sdp)*/) {
            Text(text = "电影 Screen", color = Color.Red, fontSize = 20.ssp)
           Text(text = "电影 Screen", color = Color.Red, fontSize = 30.ssp)
           Text(text = "电影 Screen", color = Color.Red,fontSize = 50.ssp)

       }
    }


    override val options: TabOptions
        @Composable
        get() {
            val image = painterResource(Res.drawable.ic_home_tab)
            return remember { TabOptions(index = 1u, title = "电影", icon = image) }
        }
}



internal object TVShowTab : Tab {
    @Composable
    override fun Content() {
        Column {
            Text(text = "电视剧 Screen", color = Color.Red)
            Text(text = "电影 Screen", color = Color.Red, fontSize = 20.ssp)
            Text(text = "电影 Screen", color = Color.Red, fontSize = 30.ssp)
        }

    }


    override val options: TabOptions
        @Composable
        get() {
            val image = painterResource(Res.drawable.ic_home_tab)
            return remember { TabOptions(index = 2u, title = "电视剧", icon = image) }
        }
}

internal object ShortDramaTab : Tab {
    @Composable
    override fun Content() {
        Text(text = "短剧 Screen" , color = Color.Red)
    }


    override val options: TabOptions
        @Composable
        get() {
            val image = painterResource(Res.drawable.ic_home_tab)
            return remember { TabOptions(index = 3u, title = "短剧", icon = image) }
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableTabLayout(viewModel: HomeTabViewModel) {
    val homeResult by viewModel.homeResult.collectAsState()
    val categories: List<Class> = homeResult.types

    val tabs = mutableListOf<Tab>()
    for (category in categories){
        Napier.d { "loop category: $category" }
        if(category.typeId.isEmpty()) category.typeId = category.typeName
        viewModel.loadCategoryContent(category, pageNum = "1")
        tabs.add(VodListScreen(category,viewModel))
    }
    tabs.add(0, VodListScreen(Class(typeId =  "Home", typeName = "推荐"), viewModel))



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
                    tabs.forEachIndexed { index, tabItem ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                    tabNavigator.current = tabItem
                                }
                                      },
                            text = { Text(tabItem.options.title) },
                            icon = {
                                tabItem.options.icon?.let { it1 ->
                                    Icon(
                                        painter = it1,
                                        contentDescription = null
                                    )
                                }
                            }
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
                    tabs[page].Content()
                }
            }
        }
        LaunchedEffect(pagerState.currentPage) {
            tabNavigator.current = tabs[pagerState.currentPage]
        }
    }
}




@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    Tab(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { tab.options.icon?.let { Icon(painter = it, contentDescription = tab.options.title) } }
    )
}
