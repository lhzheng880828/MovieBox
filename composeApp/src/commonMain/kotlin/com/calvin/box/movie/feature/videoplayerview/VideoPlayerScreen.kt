package com.calvin.box.movie.feature.videoplayerview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.bean.Episode
import com.calvin.box.movie.bean.Flag
import com.calvin.box.movie.bean.PlayMediaInfo
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.font.FontType
import com.calvin.box.movie.font.MediaFont
import com.calvin.box.movie.media.model.PlayerConfig
import com.calvin.box.movie.media.ui.video.VideoPlayerView
import com.calvin.box.movie.model.VideoModel
import com.calvin.box.movie.navigation.LocalNavigation
import com.calvin.box.movie.ui.components.BackButtonNavBar
import com.calvin.box.movie.utility.getSafeAreaSize
import io.github.aakira.napier.Napier
import kotlinx.coroutines.runBlocking
import network.chaintech.sdpcomposemultiplatform.sdp

class WrapVideoPlayerView(private val currentVideo: VideoModel) : Screen {
    private val siteKey = currentVideo.siteKey
    private val vodId = currentVideo.id
    private val vodName = currentVideo.title


    @Composable
    override fun Content() {
        val vodDetailModel: VideoPlayerViewModel = getScreenModel()
        if(siteKey.isNotEmpty()){
            val site =VodConfig.get().getSite(siteKey)
            vodDetailModel.getVodDetail(site, vodId, vodName)
            BottomSheetNavigator {
                VideoPlayerContentView(currentVideo, vodDetailModel)
            }
        }

    }
}

@Composable
private fun VideoPlayerContentView(currentVideo: VideoModel,
                                   vodDetailModel: VideoPlayerViewModel,
                                    ) {
    val detailUiState by vodDetailModel.uiState.collectAsState()
    when (detailUiState) {
        is UiState.Loading, is UiState.Initial -> {
            Text("Loading detail...")
        }
        is UiState.Empty -> {
            Text("Loaded detail empty")
        }
        is UiState.Error -> {
            Text("Loaded detail error")
        }

        is UiState.Success, is UiState.UPDATE -> {
            val detail = if(detailUiState is UiState.Success) (detailUiState as UiState.Success).data.detail else {
                (detailUiState as UiState.UPDATE).data.detail
            }
            val siteVodList = if(detailUiState is UiState.Success) (detailUiState as UiState.Success).data.siteList else {
                (detailUiState as UiState.UPDATE).data.siteList
            }
            val navigator = LocalNavigation.current
            val video by remember { mutableStateOf(currentVideo) }
            val playInfo = detail.playMediaInfo ?: PlayMediaInfo(url = detail.vodPlayUrl ?: "")
            val vodSite = detail.site!!
            val firstLine = detail.vodFlags.firstOrNull()
            var line by remember { mutableStateOf(firstLine) }

            val firstEpisode = firstLine?.episodes?.firstOrNull()
            var episode by remember { mutableStateOf(firstEpisode) }

            val playMediaInfo by vodDetailModel.vodPlayState.collectAsState(initial = playInfo)
            val playUrl = playMediaInfo.url.takeIf { it.isNotEmpty() } ?: detail.vodPlayUrl

            //TODO 切换线路及剧集列表时需要更新收藏状态
            val initKeepValue =  vodDetailModel.initKeepState(siteKey = currentVideo.siteKey, vodId = currentVideo.id)
            val keepState by vodDetailModel.keepState.collectAsState(initial = initKeepValue)

            //TODO 切换线路及剧集列表时需要更新播放历史
            var totalTime = 0
            var currentTime = 0
            vodDetailModel.getOrCreateHistory(vodSite.key, detail.vodId, detail)
            Napier.d(tag = TAG) { "VideoPlayer play url: $playUrl, keepState: $keepState" }

            Column(
                modifier = Modifier
                    /* 修改整个界面的背景主题
                    .background(
                         brush = Brush.verticalGradient(
                         colors = GradientPrimary,
                         )
                     )*/
                    .fillMaxSize()
                    .padding(top = getSafeAreaSize().top.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.TopStart
                ){
                    VideoPlayerView(
                        modifier = Modifier.fillMaxWidth()
                            .height(164.sdp),
                        url1 = playUrl,
                        playMediaInfo = playMediaInfo,
                        playerConfig = PlayerConfig(
                            seekBarActiveTrackColor = Color.Red,
                            seekBarInactiveTrackColor = Color.White,
                            seekBarBottomPadding = 8.sdp,
                            pauseResumeIconSize = 30.sdp,
                            controlHideIntervalSeconds = 5,
                            topControlSize = 20.sdp,
                            durationTextStyle = MediaFont.lexendDeca(
                                size = FontType.Regular,
                                type = MediaFont.LexendDeca.Medium
                            ),
                            fastForwardBackwardIconSize = 28.sdp,
                            controlTopPadding = 10.sdp
                        ),
                        totalTimeFun = {
                            Napier.d(tag = TAG) { "xbox.Player, totalTime: $it" }
                            totalTime = it
                        },
                        currentTimeFun = {
                            Napier.d(tag = TAG) { "xbox.Player, currentTime: $it" }
                            currentTime = it
                            //save history
                           vodDetailModel.updateHistory(currentTime, totalTime)
                        },
                    )

                    BackButtonNavBar {
                        navigator.back()
                    }
                }
                downLoadView(keepState, onFavoriteClick = { favorited ->
                    if(favorited){

                    } else {

                    }
                    vodDetailModel.toggleKeep(vodSite.key, vodSite.name, currentVideo.id,
                        currentVideo.title, currentVideo.thumb)
                })

                Spacer(modifier = Modifier.height(4.sdp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(horizontal = 8.sdp)
                ) {
                    item (span = {
                        GridItemSpan(2)
                    })
                    {
                        videoDetails( detail, siteVodList,
                            onLineClicked = { clickedLine ->
                                if (clickedLine.flag != line?.flag) {
                                    line = clickedLine
                                    if (vodSite != null && line != null && episode != null) {
                                        vodDetailModel.getVodPlayerContent(site = vodSite, line!!.flag, episode!!.url)
                                    }
                                } else {
                                    Napier.i(tag = TAG) { "click same line" }
                                }

                            },
                            onEpisodeClicked = {clickedEpisode ->
                                if (clickedEpisode.url != episode?.url) {
                                    episode = clickedEpisode
                                    if (vodSite != null && line != null && episode != null) {
                                        vodDetailModel.getVodPlayerContent(site = vodSite, line!!.flag, episode!!.url)
                                    }
                                } else {
                                    Napier.i(tag = TAG) { "click same episode" }
                                }
                            })
                    }

                    /* items(MockData().getFilteredData(video)) {
                         Column(
                             modifier = Modifier.padding(4.sdp)
                         ) {
                             Box(
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .height(200.sdp)
                                     .clip(RoundedCornerShape(8.sdp))
                                     .background(Border)
                                     .border(
                                         width = 1.sdp,
                                         color = Border,
                                         shape =  RoundedCornerShape(8.sdp)
                                     )
                                     .pointerInput(Unit) {
                                         detectTapGestures { _ ->
                                             video = it
                                         }
                                     }

                             ){
                                 FromRemote(
                                     painterResource = it.thumb,
                                     contentScale = ContentScale.Crop,
                                     modifier = Modifier
                                         .fillMaxSize()
                                 )
                             }
                         }
                     }

                     item (span = {
                         GridItemSpan(2)
                     })
                     {
                         Spacer(modifier = Modifier.height(16.sdp))
                     }*/
                }
            }
        }
    }



}

@Composable
private fun videoDetails(  vod: Vod,
                         siteVodList: List<Vod>,
                         onLineClicked: (Flag) -> Unit,
                         onEpisodeClicked: (Episode) -> Unit ) {
    Row(
        modifier = Modifier.padding(vertical = 13.sdp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(7.sdp)
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            val flags = vod.vodFlags.mapIndexed { index, flag ->
                if(index==0){
                    flag.copy(activated = true)
                } else {
                    flag.copy(activated = false)
                }
            }


            val episodes = flags[0].episodes.mapIndexed { index, episode ->
                if(index==0){
                    episode.copy(activated = true)
                } else {
                    episode.copy(activated = false)
                }
            }


            MovieHeader(vod)
            Spacer(modifier = Modifier.height(4.sdp))
            MovieLines(flags, onClick = { clickedFlag->
                onLineClicked(clickedFlag)

            })
            Spacer(modifier = Modifier.height(4.sdp))
            MovieEpisodes(episodes, onClick = { clickedEpisode->
                onEpisodeClicked(clickedEpisode)
            })
            Spacer(modifier = Modifier.height(16.dp))
            MovieDescription(vod.getFormatVodContent())
            Spacer(modifier = Modifier.height(16.dp))
            if(siteVodList.isNotEmpty()){
                MovieSites(siteVodList, onClick = {

                })
            }


          /*  Text(
                text = video.subtitle,
                style = MediaFont.lexendDeca(
                    size = FontType.Small,
                    type = MediaFont.LexendDeca.Regular
                ),
                //color = MyApplicationTheme.colors.secondaryText,
                modifier = Modifier.padding(horizontal = 4.sdp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            downLoadView()

            Spacer(modifier = Modifier.height(8.sdp))
            AddBanner(
                title = "Anokhi Kahani Atoot Bandhan ki",
                image = MockData().detailBanner,
                padding = 5.sdp
            )

            Spacer(modifier = Modifier.height(20.sdp))

            Text(
                text = "More Like This",
                style = MediaFont.lexendDeca(
                    size = FontType.SubHeading,
                    type = MediaFont.LexendDeca.Medium
                ),
                fontSize = 14.ssp,
                fontWeight = FontWeight.Bold,
                //color = MyApplicationTheme.colors.white,
                modifier = Modifier.padding(horizontal = 4.sdp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )*/
        }
    }
}

@Composable
fun MovieHeader(vod: Vod) {
    Column {
        /*Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                 painter = painterResource(id = R.drawable.movie_image),
                 contentDescription = "Movie Image",
                 contentScale = ContentScale.Crop
             )
        }*/
        Spacer(modifier = Modifier.height(8.dp))
        Text(vod.vodName,
            style = MediaFont.lexendDeca(
            size = FontType.SubHeading,
            type = MediaFont.LexendDeca.Medium),
            modifier = Modifier.padding(horizontal = 4.sdp)
        )
        Text(vod.vodRemarks, style = MaterialTheme.typography.titleMedium)
        Text(
            /*"站源: 萌米 | App"*/ vod.getSiteName(),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            /*"年份: 2023 地区: 中国 类型: 奇幻, 剧情"*/vod.vodYear+" "+vod.vodArea+" "+vod.typeName,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(/*"导演: 许宏宇"*/vod.vodDirector, style = MaterialTheme.typography.bodyMedium)
        Text(
            /*"演员: 彭昱畅, 侯明昊, 王影璇, 王学圻, 毕雯珺, 姜珮瑶, 王鹤棣"*/vod.vodActor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun downLoadView(favorited:Boolean, onFavoriteClick: (checked: Boolean) -> Unit) {
    var isFavorited by remember { mutableStateOf(favorited) }
    Napier.d(tag = TAG) { "#downLoadView, isFavorited:$isFavorited" }
    Row(
        modifier = Modifier.padding(vertical =  12.sdp, horizontal = 4.sdp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(20.sdp)
    ) {

        downloadItem(Icons.Default.Add, "播放列表")

        downloadItem(Icons.Default.Share, "分享", size = 16.sdp)

        downloadItem(Icons.Default.Download, "下载")

        downloadItem(
            image = if (isFavorited) Icons.Default.Favorite else Icons.Outlined.Favorite,
            title = if (isFavorited) "已收藏" else "未收藏",
            size = 19.sdp,
            onClick = {
                isFavorited = !isFavorited
                onFavoriteClick(isFavorited)
            }
        )

    }
}

@Composable
private fun downloadItem(
    image: ImageVector,
    title: String,
    size: Dp = 19.sdp,
    onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier. height(32.sdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
       /* FromLocalDrawable(
            painterResource = image,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size)
        )*/
        Icon(image,
            contentDescription = title,
            modifier = Modifier
                .size(size)
                .clickable(onClick = onClick))

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = title,
            style = MediaFont.lexendDeca(
                size = FontType.Small,
                type = MediaFont.LexendDeca.Regular
            ),
            modifier = Modifier,
            maxLines = 1
        )
    }
}

@Composable
fun MovieLines(lines:List<Flag>,
               onClick: (Flag) -> Unit) {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    //var lines by remember { mutableStateOf( initialLines.toMutableList()) }
    var selectedFlag by remember { mutableStateOf(lines[0].flag) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
         Text("线路", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                Napier.i(tag = TAG) { "Download Btn clicked." }
                bottomSheetNavigator.hide()
                bottomSheetNavigator.show(DownloadBottomSheet())
            },
            modifier = Modifier.padding(start = 8.dp)
        ) {
             Text("下载", style = MaterialTheme.typography.labelMedium)
            //Icon(painterResource(id = R.drawable.ic_arrow_right), contentDescription = null)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow {
        items(lines) { line ->
            Text(
                text = line.show,
                modifier = Modifier
                    .background(
                        color = if (line.flag == selectedFlag) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) .clickable {
                        onClick(line)
                        selectedFlag = line.flag
                         Napier.d(tag = TAG) { "#MovieLines, clicked line: $selectedFlag" }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun MovieEpisodes(
    episodes: List<Episode>,
    onClick: (Episode) -> Unit) {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    var selectedName by remember { mutableStateOf(episodes[0].name) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
         Text("选集", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                Napier.d(tag = TAG) { "more btn clicked." }
                bottomSheetNavigator.show(EpisodesBottomSheet(episodes, onClick = {
                Napier.d(tag = TAG) { "clicked episode item:  ${it.name}" }
            })) },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("更多", style = MaterialTheme.typography.labelMedium)
            //Icon(painterResource(id = R.drawable.ic_arrow_right), contentDescription = null)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow {
        items(episodes) { episode ->
            Text(
                text = episode.name,
                modifier = Modifier
                    .background(
                        color = if (episode.name == selectedName) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    )
                    .clickable {
                        onClick(episode)
                        selectedName =  episode.name
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun MovieDescription(desc:String) {
    var expanded by remember { mutableStateOf(false) }
    val description = if (expanded) {
        desc
    } else {
        if(desc.length>20){
            desc.subSequence(0,20).toString()
        } else {
            desc
        }
    }

    Column {
        Text(description, maxLines = if (expanded) Int.MAX_VALUE else 3)
        TextButton(onClick = { expanded = !expanded }) {
            Text(if (expanded) "收起" else "展开")
        }
    }
}

@Composable
fun MovieSites(siteVodList: List<Vod>,
               onClick: (Vod) -> Unit,
               ) {
    Column {
        Text("站点列表", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        siteVodList.chunked(2).forEach { rowSites ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowSites.forEach { siteVod ->
                    Button(
                        onClick = { onClick(siteVod)},
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = if (siteVod == rowSites.last()) 0.dp else 4.dp)
                    ) {
                        Text(siteVod.getSiteName())
                    }
                }
                // 如果这一行只有一个按钮，添加一个空的权重来保持对齐
                if (rowSites.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


class DownloadBottomSheet:Screen {

    @Composable
    override fun Content() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight(1 / 2f),
            ) {
            Button(onClick = { bottomSheetNavigator.hide() }) {
                 Text("返回")
            }
            Text("下载列表", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items((1..6).toList()) { episode ->
                    Button(onClick = { /* TODO */ }) {
                        Text("下载第${episode}集")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }


}

//[Android Jetpack Compose 实现一个电视剧选集界面] https://blog.csdn.net/zxj2589/article/details/139605000
class EpisodesBottomSheet(private val episodes: List<Episode>, val onClick: (Episode) -> Unit):Screen {
    @Composable
    override fun Content() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val selectedStates = remember { mutableStateListOf<Boolean>().apply {
            repeat(episodes.size) { add(false) }
        } }
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight(1 / 2f),
        ) {
            Button(onClick = { bottomSheetNavigator.hide() }) {
                Text("返回")
            }
            Text("选集列表", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 这里设置列数为2，可以根据需要调整
                modifier = Modifier.padding(8.dp) // 可选，设置网格的内边距
            ) {
                items(episodes) { episode ->
                    val index = episodes.indexOf(episode)
                    Button(onClick = {
                        selectedStates[index] = !selectedStates[index]
                        onClick(episode)
                                     },
                        modifier = Modifier.padding(2.dp),
                        // 根据选中状态更改按钮的颜色
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedStates[index]) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(episode.name)
                    }
                }
            }
        }
    }

}

const val TAG = "xbox.Player"