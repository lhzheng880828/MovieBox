package com.calvin.box.movie.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.label_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun Tabs(){

}

class Tab1Screen:  Tab {

    override val options: TabOptions
    @Composable
    get() {
        val title = stringResource(Res.string.label_title)
        val icon = rememberVectorPainter(Icons.Default.Home)

        return remember {
            TabOptions(
                index = 0u,
                title = title,
                icon = icon
            )
        }
    }

    @Composable
    override fun Content() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tab1 Screen")
        }
    }
}

class Tab2Screen: Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.label_title)
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tab2 Screen")
        }
    }
}

class Tab3Screen: Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(Res.string.label_title)
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tab3 Screen")
        }
    }
}