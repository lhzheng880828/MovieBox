package com.calvin.box.movie.feature.vod

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.ic_home_tab
import org.jetbrains.compose.resources.painterResource


data class VodListScreen(
    val categoryType: String, val categoryName: String,
    val categoryExt: HashMap<String, String> = HashMap(),

) : Tab {
    @Composable
    override fun Content() {
        Napier.d(tag = TAG) { " categoryType: $categoryType, categoryName: $categoryName"  }
        val viewModel: VodListScreenModel = getScreenModel()
        val pagingFlow = viewModel.loadPagingDataFLow(categoryType,categoryExt)
        PagingView(pagingFlow)

    }

    override val options: TabOptions
        @Composable
        get() {
            val image = painterResource(Res.drawable.ic_home_tab)
            return remember { TabOptions(index = 2u, title = categoryName, icon = image) }
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

private const val TAG = "xbox.VodListScreen"