package com.calvin.box.movie.ui.screens.tabsview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.calvin.box.movie.ui.screens.reels.ReelsContentView
import com.calvin.box.movie.utility.BottomNavigationBarHeight
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.ic_foryou_tab
import org.jetbrains.compose.resources.painterResource

internal object ReelsTab: Tab {
    @Composable
    override fun Content() {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = BottomNavigationBarHeight)
        ){
            ReelsContentView()
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val image = painterResource(Res.drawable.ic_foryou_tab)
            return remember { TabOptions(index = 0u, title = "Reels", icon = image) }
        }
}