package com.calvin.box.movie.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calvin.box.movie.model.MockData
import com.calvin.box.movie.ui.components.AddBanner
import com.calvin.box.movie.ui.components.HomeVideoSection
import com.calvin.box.movie.utility.getSafeAreaSize
import network.chaintech.sdpcomposemultiplatform.sdp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun HomeView() {
    val dataStore = MockData()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            /*.padding(top = 48.sdp)*/
            /* .background(
                brush = Brush.verticalGradient(
                    colors = MyApplicationTheme.colors.gradientPrimary,
                )
            )*/,
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.sdp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        /*.background(
                            brush = Brush.linearGradient(
                                colors = MyApplicationTheme.colors.homeTopGradient
                            )
                        )*/
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = getSafeAreaSize().top.dp)
                            .fillMaxSize()
                    ) {
                        TopView()
                    }
                }

                AddBanner(
                    title = "Subscribe Now",
                    image = dataStore.topAddBanner
                )

                HomeVideoSection(
                    data = dataStore.hotRightNow(),
                    title = "Hot Right Now \uD83D\uDD25"
                )

                HomeVideoSection(
                    data = dataStore.machoMix(),
                    title = "Macho Mix: Must Watch Movies"
                )

                HomeVideoSection(
                    data = dataStore.popularInKids(),
                    title = "Popular In Kids & Family"
                )

                HomeVideoSection(
                    data = dataStore.actionAdventure(),
                    title = "Action & Adventure"
                )

                AddBanner(
                    title = "Motu Patlu: Streaming Now",
                    image = dataStore.bottomAddBanner
                )

                HomeVideoSection(
                    data = dataStore.hollywoodFinest(),
                    title = "Hollywood's Finest"
                )

                HomeVideoSection(
                    data = dataStore.southDubbed(),
                    title = "South Dubbed Hits"
                )

                HomeVideoSection(
                    data = dataStore.globalHits(),
                    title = "Global Hits In Hindi"
                )

                Spacer(modifier = Modifier.height(56.sdp))
            }
        }
    }
}


