package com.calvin.box.movie.feature.vod

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.model.VideoModel
import com.calvin.box.movie.navigation.LocalNavigation
import com.calvin.box.movie.screens.EmptyScreenContent
import com.calvin.box.movie.utils.UrlProcessor
import io.github.aakira.napier.Napier
import io.kamel.core.utils.cacheControl
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.request.header
import io.ktor.client.utils.CacheControl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.ic_home_tab
import org.jetbrains.compose.resources.painterResource


data class VodListScreen(
    val categoryType: String, val categoryName: String,
    val categoryExt: HashMap<String, String> = HashMap(),

) : Tab {
    @Composable
    override fun Content() {
        Napier.d(tag = TAG) { " categoryType: $categoryType, categoryName: $categoryName" }
        val viewModel: VodListScreenModel = getScreenModel()
        val pagingFlow = viewModel.loadPagingDataFLow(categoryType, categoryExt)
        val movieList = (1..100).map { "Movie $it" }
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        var showFilterMenu by remember { mutableStateOf(false) }
        val isScrollingUp = listState.firstVisibleItemIndex > 0
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (isScrollingUp) {
                            // 滚动到第一行
                            scope.launch { listState.animateScrollToItem(0) }
                        } else {
                            // 打开过滤菜单
                            showFilterMenu = true
                        }
                    }
                ) {
                    if (isScrollingUp) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = "Scroll to Top")
                    } else {
                        Icon(Icons.Default.FilterList, contentDescription = "Open Filter")
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                /* LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    items(movieList.size) { index ->
                        Text(
                            text = movieList[index],
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }*/
                PagingView(pagingFlow)
                if (showFilterMenu) {
                    FilterMenu(onDismiss = { showFilterMenu = false })
                }
            }
        }
    }


    override val options: TabOptions
        @Composable
        get() {
            val image = painterResource(Res.drawable.ic_home_tab)
            return remember { TabOptions(index = 2u, title = categoryName, icon = image) }
        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterMenu(onDismiss: () -> Unit) {
    var selectedGenre by remember { mutableStateOf<String?>(null) }
    var selectedYear by remember { mutableStateOf<String?>(null) }
    var selectedRegion by remember { mutableStateOf<String?>(null) }

    // 底部弹出的过滤菜单
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 类型筛选栏
            LazyRow {
                items(listOf("恐怖片", "科幻片", "犯罪片")) { genre ->
                    Button(onClick = { selectedGenre = genre }) {
                        Text(genre)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // 年份筛选栏
            LazyRow {
                items(listOf("2024", "2023", "2022")) { year ->
                    Button(onClick = { selectedYear = year }) {
                        Text(year )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // 地区筛选栏
            LazyRow {
                items(listOf("大陆", "欧美", "日韩")) { region ->
                    Button(onClick = { selectedRegion = region }) {
                        Text(region)
                    }
                }
            }

        }
    }
}

@Composable
private fun PagingView(pagingFlow: Flow<PagingData<Vod>>){
    val navigator = LocalNavigation.current
    val objects = pagingFlow.collectAsLazyPagingItems()
    Napier.d(tag = TAG) { " refresh vod list: objSize: ${objects.itemCount}"  }
    // test code
    // val objects = flow { emit(PagingData.empty<Vod>())  }.collectAsLazyPagingItems()
    AnimatedContent(objects.itemCount>0) { objectsAvailable ->
        if (objectsAvailable) {
            ObjectGrid(
                objects = objects,
                onObjectClick = { vodObj ->

                    val homeSiteKey = VodConfig.get().getHome()?.key
                    val isIndexs =  VodConfig.get().getHome()?.isIndexs()
                    val isFolder = vodObj.isFolder()
                    val isManga = vodObj.isManga()
                    val action = vodObj.action
                    Napier.d(tag = TAG) { "vod item clicked, homeSiteKey:$homeSiteKey, isIndexs: $isIndexs," +
                            " isFolder: $isFolder, isManga:$isManga, action: $action" }

                    val videoModel = VideoModel(id = vodObj.vodId, description = vodObj.vodTag, sources = vodObj.vodPlayUrl,
                        subtitle = "", thumb = vodObj.vodPic, title = vodObj.vodName, siteKey = homeSiteKey!!)
                    navigator.goToVideoPlayerScreen(videoModel)

                    if(action.isNotEmpty()){

                    } else if(isFolder){

                    } else {
                        if(isIndexs == true){

                        } else if(isManga){
                            navigator.goToDetailScreen(videoModel)
                        } else {
                            navigator.goToVideoPlayerScreen(videoModel)
                        }

                    }

                }
            )
        } else {
            EmptyScreenContent(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun ObjectGrid(
    objects: LazyPagingItems<Vod>,
    onObjectClick: (Vod) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow() {  }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(objects.itemCount) { index ->
            objects[index]?.let {
                ObjectFrame(
                    obj = it,
                    onClick = { onObjectClick(it) },
                )
            }
        }
    }
}

@Composable
private fun ObjectFrame(
    obj: Vod,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .padding(2.dp)
            .clickable { onClick() }
    ) {
        val url = obj.vodPic
        val (processedUrl, headers) = remember(url) { UrlProcessor.processUrl(url) }
        //Napier.d { "processedUrl: $processedUrl, headers: $headers" }
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
            contentDescription = obj.vodName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3/4f)
                .background(Color.LightGray),
        )
        Spacer(Modifier.height(2.dp))
        Text(obj.vodName, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), maxLines = 2)
        //Text(obj.vodTag, style = MaterialTheme.typography.labelMedium)
        //Text(obj.vodYear, style = MaterialTheme.typography.labelMedium)
    }
}

private val TAG = "xbox.VodListScreen"