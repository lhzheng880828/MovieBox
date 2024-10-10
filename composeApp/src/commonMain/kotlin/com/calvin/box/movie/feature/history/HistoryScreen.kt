package com.calvin.box.movie.feature.history

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Sync
import androidx.compose.ui.draw.clip
import cafe.adriel.voyager.core.screen.Screen
import io.github.aakira.napier.Napier
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import network.chaintech.sdpcomposemultiplatform.sdp
import org.jetbrains.compose.resources.InternalResourceApi

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/6
 */
data class MovieItem(val title: String, val imageUrl: String = "https://picsum.photos/seed/${(0..100000).random()}/256/256")

class HistoryScreen:Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val movies = remember {
            mutableStateListOf(
                MovieItem("解密"),
                MovieItem("唐朝诡事录"),
                MovieItem("从21世纪安...")
            )
        }

        var showDeleteButton by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("最近观看", fontSize = 24.sp) },
                    actions = {
                        IconButton(onClick = {
                            Napier.d { "xbox.history, sync clicked"}
                            }) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Button"
                            )
                        }
                        IconButton(onClick = {
                            Napier.d { "xbox.history, delete Btn clicked" }
                            showDeleteButton = !showDeleteButton

                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Button"
                            )

                        }
                    }
                )
            }
        ) { innerPadding ->
            MovieGrid(
                movies = movies,
                showDeleteButton = showDeleteButton,
                onDeleteItem = { movie ->
                    movies.remove(movie)
                },
                modifier = Modifier.padding(innerPadding)
            )
        }

    }
}


@OptIn(InternalResourceApi::class)
@Composable
fun MovieGrid(movies: List<MovieItem>, showDeleteButton: Boolean, onDeleteItem: (MovieItem) -> Unit, modifier: Modifier) {
    Napier.d { "xbox.history, refresh history grid, showDeleteButton:$showDeleteButton" }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(movies) { item ->
            Box(modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(), // 确保 Box 占满整个宽度
                contentAlignment = Alignment.Center // 将内容居中
                 ) {
                val transition = updateTransition(true, label ="selected")

                val roundedCornerShape by transition.animateDp(label ="corner"){ selected ->
                    if(selected)16.dp else 0.dp
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        Napier.d { "xbox.history, history item clicked" }
                    }
                ) {
                    KamelImage(
                        resource = asyncPainterResource(item.imageUrl),
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.sdp)
                            //.padding(8.sdp)
                            .clip(RoundedCornerShape(roundedCornerShape)),
                        onLoading = { /* 可以在这里添加加载状态的UI */ },
                        onFailure = { /* 可以在这里添加加载失败的UI */ }
                    )
                    BasicText(item.title,  modifier = Modifier.padding(top = 8.dp))
                    if (showDeleteButton) {
                        Napier.d { "xbox.history, grid item, showDeleteButton:$showDeleteButton" }
                        IconButton(onClick = { onDeleteItem(item) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Item Delete Button"
                            )
                        }
                    }
                }
            }
        }
    }
}