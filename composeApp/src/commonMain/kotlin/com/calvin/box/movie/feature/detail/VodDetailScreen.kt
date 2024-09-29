package com.calvin.box.movie.feature.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.bean.Episode
import com.calvin.box.movie.bean.Flag
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.model.VideoModel
import com.calvin.box.movie.screens.EmptyScreenContent
import com.calvin.box.movie.feature.videoplayerview.UiState
import com.calvin.box.movie.utils.UrlProcessor
import io.github.aakira.napier.Napier
import io.kamel.core.utils.cacheControl
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.request.header
import io.ktor.client.utils.CacheControl
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.back
import moviebox.composeapp.generated.resources.label_artist
import moviebox.composeapp.generated.resources.label_credits
import moviebox.composeapp.generated.resources.label_date
import moviebox.composeapp.generated.resources.label_department
import moviebox.composeapp.generated.resources.label_dimensions
import moviebox.composeapp.generated.resources.label_medium
import moviebox.composeapp.generated.resources.label_repository
import moviebox.composeapp.generated.resources.label_title
import org.jetbrains.compose.resources.stringResource

data class VodDetailScreen(val viewModel: VideoModel) : Screen {
    @Composable
    override fun Content() {

        val siteKey:String = viewModel.siteKey
        val vodId: String = viewModel.id
        val vodName: String = viewModel.title
        val vodPic: String = viewModel.thumb
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: VodDetailScreenModel = getScreenModel()
        val site = VodConfig.get().getSite(siteKey)
        screenModel.getVodDetail(site, vodId, vodName)
        val detailUiState by screenModel.uiState.collectAsState()
         Napier.d { "vodDetailScreen uiState: $detailUiState" }
        if(detailUiState is UiState.Success){
            val detail = (detailUiState as UiState.Success).data.detail
            val list = (detailUiState as UiState.Success).data.siteList
            Napier.d { "detail: $detail, siteList: ${list.size}" }
        }
        AnimatedContent(detailUiState is UiState.Success) { objectAvailable ->
            if (objectAvailable) {
                if(detailUiState is UiState.Success){
                    val detail = (detailUiState as UiState.Success).data.detail
                    ObjectDetails(detail, onBackClick = { navigator.pop() })
                } else {
                    EmptyScreenContent(Modifier.fillMaxSize())
                }
            } else {
                if(detailUiState is UiState.Loading || detailUiState is UiState.Initial){
                    ProgressLayout()
                } else if(detailUiState is UiState.Empty || detailUiState is UiState.Error){
                    EmptyScreenContent(Modifier.fillMaxSize())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ObjectDetails(
    obj: Vod,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("标题") }, // 可以设置标题
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
                    }
                }

            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            val orgFlags = obj.vodFlags.mapIndexed { index, flag ->
                if(index==0){
                    flag.copy(activated = true)
                } else {
                    flag.copy(activated = false)
                }
            }

            var flags  by  remember { mutableStateOf(orgFlags)   }

            var episodes by remember { mutableStateOf(flags[0].episodes)  }
            val url = obj.vodPic
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
                contentDescription = obj.vodName,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )

            SelectionContainer {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        obj.vodName,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(6.dp))
                    LabeledInfo(stringResource(Res.string.label_title), obj.vodName)
                    LabeledInfo(stringResource(Res.string.label_artist), obj.vodArea)
                    LabeledInfo(stringResource(Res.string.label_date), obj.vodYear)
                    LabeledInfo(stringResource(Res.string.label_dimensions), obj.vodArea)
                    LabeledInfo(stringResource(Res.string.label_medium), obj.vodRemarks)
                    LabeledInfo(stringResource(Res.string.label_department), obj.vodTag)
                    LabeledInfo(stringResource(Res.string.label_repository), obj.vodDirector)
                    LabeledInfo(stringResource(Res.string.label_credits), obj.vodRemarks)
                }
            }
            Text(
                text = obj.getFormatVodContent(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Line 列表展示
            androidx.compose.material3.Text("线路", style = MaterialTheme.typography.headlineSmall)
            LazyRow {
                items(flags){
                        flag ->
                    LineItem(
                        flag = flag,
                        onClick = { clickedFlag ->
                            // 更新 flags 列表，设置点击的选项为 active 其他为 false
                            flags = flags.map { currentFlag ->
                                if (currentFlag.show == clickedFlag.show) {
                                    currentFlag.copy(activated = true) // 选中的 flag
                                } else {
                                    currentFlag.copy(activated = false) // 未选中的 flag
                                }
                            }.toMutableList()

                            episodes =  clickedFlag.episodes.mapIndexed { index, episode ->
                                if(index==0){
                                    episode.copy(activated = true)
                                } else {
                                    episode.copy(activated = false)
                                }
                            } .toMutableList()

                        }
                    )
                }

            }

            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.Text("选集", style = MaterialTheme.typography.headlineSmall)
            LazyRow  {
                items(episodes) { clickedEpisode ->
                    EpisodeItem(
                        episode = clickedEpisode,
                        onClick = {
                            episodes = episodes.map { currentEpisodes ->
                                if (currentEpisodes.name == clickedEpisode.name) {
                                    currentEpisodes.copy(activated = true)
                                } else {
                                    currentEpisodes.copy(activated = false)
                                }
                            }.toMutableList()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LabeledInfo(
    label: String,
    data: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier.padding(vertical = 4.dp)) {
        Spacer(Modifier.height(6.dp))
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("$label: ")
                }
                append(data)
            }
        )
    }
}

@Composable
fun ProgressLayout( ) {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

// FlagItem 实现
@Composable
fun LineItem(flag: Flag, onClick: (Flag) -> Unit) {
    Text(
        text = flag.show,
        modifier = Modifier
            .padding(8.dp)
            .background(
                color = if (flag.activated) Color.Blue else Color.Gray,
                shape = MaterialTheme.shapes.small
            )
            .clickable { onClick(flag) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White,
        fontSize = 14.sp
    )
}

@Composable
fun EpisodeItem(
    episode: Episode,
    onClick: () -> Unit
) {
    Text(
        text = episode.name,
        modifier = Modifier
            .fillMaxWidth()  // 对应match_parent
            .wrapContentHeight()
            .background(
                color = if (episode.activated) Color.Blue else Color.Gray,
                shape = MaterialTheme.shapes.small // 模拟drawable/shape_item的圆角背景
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        //color = colorResource(id = R.color.text), // 对应textColor
        fontSize = 14.sp,
        maxLines = 1, // 限制为单行，类似于ellipsize的效果
        overflow = TextOverflow.Ellipsis, // 对应ellipsize="marquee"
        textAlign = TextAlign.Start
    )
}

