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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.calvin.box.movie.bean.Class
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.model.VideoModel
import com.calvin.box.movie.navigation.LocalNavigation
import com.calvin.box.movie.screens.EmptyScreenContent
import com.calvin.box.movie.ui.screens.tabsview.HomeTabViewModel
import io.github.aakira.napier.Napier
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.ic_home_tab
import org.jetbrains.compose.resources.painterResource

data class VodListScreen(val category: Class, val viewModel: HomeTabViewModel) : Tab {
    @Composable
    override fun Content() {
        val navigator = LocalNavigation.current
        val homeResult by viewModel.homeResult.collectAsState()
        val categoryVodList by viewModel.loadCategoryContent(category).collectAsState(emptyList())
        val objects =  if(category.typeId == "Home") {homeResult.list} else {categoryVodList}

        AnimatedContent(objects.isNotEmpty()) { objectsAvailable ->
            if (objectsAvailable) {
                ObjectGrid(
                    objects = objects,
                    onObjectClick = { vodObj ->
                        Napier.d { "vod item clicked" }
                        val videoModel = VideoModel(id = vodObj.vodId, description = vodObj.vodTag, sources = vodObj.vodPlayUrl,
                            subtitle = "", thumb = vodObj.vodPic, title = vodObj.vodName)
                        navigator.goToVideoPlayerScreen(videoModel)
                    }
                )
            } else {
                EmptyScreenContent(Modifier.fillMaxSize())
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val image = painterResource(Res.drawable.ic_home_tab)
            return remember { TabOptions(index = 2u, title = category.typeName, icon = image) }
        }
}

@Composable
private fun ObjectGrid(
    objects: List<Vod>,
    onObjectClick: (Vod) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow() {  }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(objects, key = { it.vodId }) { obj ->
            ObjectFrame(
                obj = obj,
                onClick = { onObjectClick(obj) },
            )
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
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        KamelImage(
            resource = asyncPainterResource(data = obj.vodPic),
            contentDescription = obj.vodName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.LightGray),
        )

        Spacer(Modifier.height(2.dp))

        Text(obj.vodName, style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold))
        Text(obj.vodTag, style = MaterialTheme.typography.body2)
        Text(obj.vodYear, style = MaterialTheme.typography.caption)
    }
}

private const val TAG = "VodListScreen"