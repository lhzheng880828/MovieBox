package com.calvin.box.movie.ui.screens.tabsview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.calvin.box.movie.feature.live.LiveTvScreen
import com.calvin.box.movie.getPlatform
import com.calvin.box.movie.utility.BottomNavigationBarHeight
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.ic_music_tab
import org.jetbrains.compose.resources.painterResource

internal object LiveTab: Tab {
    private const val singleActivity = true
    @Composable
    override fun Content() {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = BottomNavigationBarHeight)
        ){
           if(singleActivity){
               getPlatform().navigateToLiveScreen("com.calvin.box.movie.LiveTvActivity")
           } else {
               LiveTvScreen()
           }

        }
    }




    override val options: TabOptions
        @Composable
        get() {
            val image = painterResource(Res.drawable.ic_music_tab)
            return remember { TabOptions(index = 0u, title = "Live", icon = image) }
        }
}