package com.calvin.box.movie.ui.screens.tabsview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.calvin.box.movie.ui.screens.home.SwipeableTabLayout
import io.github.aakira.napier.Napier
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.ic_home_tab
import org.jetbrains.compose.resources.painterResource

internal object HomeTab : Tab {
    @Composable
    override fun Content() {
        Napier.d(tag =  TAG){"#HomeTab Content invoke"}
        val viewModel:HomeTabViewModel = getScreenModel()

        //val categoryResult by viewModel.categoryResult.collectAsState()
       // Napier.d { "categoryResult: $categoryResult" }
        SwipeableTabLayout(viewModel)
    }


    override val options: TabOptions
        @Composable
        get() {
            val image = painterResource(Res.drawable.ic_home_tab)
            return remember { TabOptions(index = 0u, title = "Home", icon = image) }
        }


    const val TAG = "movie.HomeTab"
}