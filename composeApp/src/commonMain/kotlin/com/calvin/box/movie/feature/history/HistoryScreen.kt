package com.calvin.box.movie.feature.history

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Sync
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.calvin.box.movie.bean.History
import com.calvin.box.movie.navigation.LocalNavigation
import com.calvin.box.movie.theme.BackHandler
import com.calvin.box.movie.utils.UrlProcessor
import io.github.aakira.napier.Napier
import io.kamel.core.utils.cacheControl
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.request.header
import io.ktor.client.utils.CacheControl
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.InternalResourceApi

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/6
 */
//data class MovieItem(val title: String, val imageUrl: String = "https://picsum.photos/seed/${(0..100000).random()}/256/256")

class HistoryScreen:Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val enableSync = false
        val nv = LocalNavigation.current
        val viewModel: HistoryScreenModel = getScreenModel()
        val movies by viewModel.movies.collectAsState()
        val inSelectionMode by viewModel.inSelectionMode.collectAsState()
        val selectedMovies by viewModel.selectedMovies.collectAsState()
        var showDeleteDialog by remember { mutableStateOf(false) }
        BackHandler{
            if (inSelectionMode) {
                viewModel.exitSelectionMode()
            } else {
                nv.back()
            }
        }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("最近观看", fontSize = 24.sp) },
                    actions = {
                        if (inSelectionMode) {
                            IconButton(onClick = { viewModel.selectAllItems() }) {
                                Icon(Icons.Default.SelectAll, contentDescription = "Select All")
                            }
                            IconButton(onClick = { viewModel.deselectAllItems() }) {
                                Icon(Icons.Default.Deselect, contentDescription = "Deselect All")
                            }
                            IconButton(onClick = {
                                if (selectedMovies.isNotEmpty()) {
                                    showDeleteDialog = true
                                }else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("没有选中的观看历史")
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        } else {
                            if(enableSync){
                                IconButton(onClick = {
                                    Napier.d { "xbox.history, sync clicked"}
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = "Sync Button"
                                    )
                                }
                            }

                            IconButton(onClick = {
                                Napier.d { "xbox.history, delete Btn clicked" }
                                viewModel.toggleDeleteMode()

                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Button"
                                )

                            }
                        }


                    }
                )
            }
        ) { innerPadding ->
            MovieGrid(
                movies = movies,
                inSelectionMode = inSelectionMode,
                selectedItems = selectedMovies,
                onClickItem = { viewModel.toggleSelectItem(it) },
                onRadioBtnClick = { _, item ->
                    viewModel.toggleSelectItem(item)
                },
                modifier = Modifier.padding(innerPadding)
            )

            if (showDeleteDialog) {
                DeleteConfirmationDialog(
                    onConfirm = {
                        viewModel.deleteSelectedItems()
                        showDeleteDialog = false
                        viewModel.toggleDeleteMode()
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }

    }
}


@OptIn(InternalResourceApi::class)
@Composable
fun MovieGrid(movies: List<History>,
              inSelectionMode: Boolean,
              selectedItems: Set<History>,
              onClickItem: (History) -> Unit,
              onRadioBtnClick: (Boolean, History) -> Unit,
              modifier: Modifier) {
    Napier.d { "xbox.history, refresh history grid, inSelectionMode:$inSelectionMode" }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(4.dp),
    ) {
        items(movies) { item ->
            val isSelected = selectedItems.contains(item)
            MovieItemView(
                item = item,
                selected = isSelected,
                inSelectionMode = inSelectionMode,
                onClick = {
                    if (inSelectionMode) {
                        //onDeleteItem(item)
                    } else {
                        onClickItem(item)
                    }
                },
                onRadioBtnClick =  {
                    onRadioBtnClick(it, item)
                }
            )
        }

    }
}

@Composable
fun MovieItemView(
    item: History,
    selected: Boolean,
    inSelectionMode: Boolean,
    onClick: () -> Unit,
    onRadioBtnClick:(Boolean)->Unit,
) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
    ) {
        val transition = updateTransition(targetState = selected, label = "selected")

        val roundedCornerShape by transition.animateDp(label = "corner") { selected ->
            if (selected) 16.dp else 0.dp
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onClick() }
        ) {
            val url = item.vodPic
            val (processedUrl, headers) = remember(url) { UrlProcessor.processUrl(url) }
            KamelImage(
                resource = asyncPainterResource(data = processedUrl){
                    requestBuilder { // this: HttpRequestBuilder
                        for(h in headers){
                            header(h.key, h.value)
                        }
                        // parameter("Key", "Value")
                        cacheControl(CacheControl.MAX_AGE)
                    }
                },
                contentDescription = item.vodName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3/4f)
                    //.background(Color.LightGray)
                    .clip(RoundedCornerShape(roundedCornerShape)),
                onLoading = { /* 加载状态的UI */ },
                onFailure = { /* 加载失败的UI */ }
            )
            BasicText(item.vodName, modifier = Modifier.padding(top = 4.dp))
            BasicText(item.siteName, modifier = Modifier.padding(top = 4.dp))

        }

        if (inSelectionMode) {
            if (selected) {
                val bgColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                Icon(
                    Icons.Filled.CheckCircle,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .border(2.dp, bgColor, CircleShape)
                        .clip(CircleShape)
                        .background(bgColor)
                        .clickable {
                            onRadioBtnClick(selected)
                        }
                )
            } else {
                Icon(
                    Icons.Filled.RadioButtonUnchecked,
                    tint = Color.White.copy(alpha = 0.7f),
                    contentDescription = null,
                    modifier = Modifier.padding(6.dp)
                        .clickable {
                        onRadioBtnClick(selected)
                    }
                )
            }
        }
    }

}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "确认删除") },
        text = { Text(text = "确定要删除选中的收藏项吗？") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
