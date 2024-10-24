package com.calvin.box.movie.feature.live

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.bean.PlayMediaInfo
import com.calvin.box.movie.feature.videoplayerview.TAG
import com.calvin.box.movie.font.FontType
import com.calvin.box.movie.font.MediaFont
import com.calvin.box.movie.media.model.PlayerConfig
import com.calvin.box.movie.media.ui.video.VideoPlayerView
import com.calvin.box.movie.model.VideoModel
import com.calvin.box.movie.navigation.LocalNavigation
import com.calvin.box.movie.ui.components.BackButtonNavBar
import com.calvin.box.movie.utility.getSafeAreaSize
import io.github.aakira.napier.Napier
import network.chaintech.sdpcomposemultiplatform.sdp

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/10/21
 */

class WrapLiveInsertScreen( ) : Screen {

    @Composable
    override fun Content() {
        val liveModel: LiveScreenModel = getScreenModel()
        LiveInsertScreen(liveModel)
    }
}


@Composable
fun LiveInsertScreen(liveModel: LiveScreenModel) {
    val navigator = LocalNavigation.current
    val playMediaInfo by liveModel.vodPlayState.collectAsState(initial = PlayMediaInfo(url = "https://www.baidu.com"))
    val playUrl = playMediaInfo.url.takeIf { it.isNotEmpty() } ?: "https://www.baidu.com"
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
        ) {
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

                },
                currentTimeFun = {
                    Napier.d(tag = TAG) { "xbox.Player, currentTime: $it" }

                },
            )

            BackButtonNavBar {
                navigator.back()
            }
        }
        ChannelsView()
    }
    }

    @Composable
    fun ChannelsView() {

        data class Epg(val id: String, val title: String, val channelId: String)
        data class Channel(val id: String, val name: String, val categoryId: String)
        data class Category(val id: String, val name: String, val channelIds: List<String>)

        // 示例数据
        var currentChannelId: String = "CCTV1"

        val categories: List<Category> = listOf(
            Category("CCTV", "央视频道", listOf("CCTV1", "CCTV2", "CCTV3", "CCTV4", "CCTV5", "CCTV6", "CCTV7", "CCTV8")),
            Category("卫视", "卫视频道", listOf("河南卫视", "浙江卫视", "湖南卫视", "上海卫视", "江苏卫视", "海南卫视", "山东卫视")),
            Category("数字", "数字频道", listOf("数字频道1", "数字频道2", "数字频道3", "数字频道4", "数字频道5","数字频道6","数字频道7"))
        )

// 创建频道列表
        val channels: List<Channel> = listOf(
            Channel("CCTV1", "CCTV1", "CCTV"),
            Channel("CCTV2", "CCTV2", "CCTV"),
            Channel("CCTV3", "CCTV3", "CCTV"),
            Channel("CCTV4", "CCTV4", "CCTV"),
            Channel("CCTV5", "CCTV5", "CCTV"),
            Channel("CCTV6", "CCTV6", "CCTV"),
            Channel("CCTV7", "CCTV7", "CCTV"),
            Channel("CCTV8", "CCTV8", "CCTV"),
            Channel("CCTV9", "CCTV9", "CCTV"),
            Channel("河南卫视", "河南卫视", "卫视"),
            Channel("浙江卫视", "浙江卫视", "卫视"),
            Channel("湖南卫视", "湖南卫视", "卫视"),
            Channel("上海卫视", "上海卫视", "卫视"),
            Channel("江苏卫视", "江苏卫视", "卫视"),
            Channel("海南卫视", "海南卫视", "卫视"),
            Channel("山东卫视", "山东卫视", "卫视"),

            Channel("数字频道1", "数字频道1", "数字"),
            Channel("数字频道2", "数字频道2", "数字"),
            Channel("数字频道3", "数字频道3", "数字"),
            Channel("数字频道4", "数字频道4", "数字"),
            Channel("数字频道5", "数字频道5", "数字"),
            Channel("数字频道6", "数字频道6", "数字"),
            Channel("数字频道7", "数字频道7", "数字")
        )

// 创建电子节目单数据
        val epgList: List<Epg> = listOf(
            Epg("epg1", "节目1", "CCTV1"),
            Epg("epg2", "节目2", "CCTV1"),
            Epg("epg3", "节目3", "河南卫视"),
            Epg("epg4", "节目4", "河南卫视"),
            Epg("epg5", "节目5", "河南卫视"),
            Epg("epg6", "节目6", "浙江卫视"),
            Epg("epg7", "节目7", "浙江卫视"),
            Epg("epg8", "节目8", "数字频道1"),
            Epg("epg9", "节目9", "数字频道1"),
            Epg("epg10", "节目10", "数字频道1"),
        )



        // 记录当前选中的一级、二级和三级菜单
        var selectedCategoryIndex by remember { mutableStateOf(0) }
        var selectedSubCategoryIndex by remember { mutableStateOf(0) }
        var selectedEpgIndex by remember { mutableStateOf(0) }

        // 自动根据当前播放的频道设置选中的一级和二级菜单
        LaunchedEffect(currentChannelId) {
            categories.forEachIndexed { categoryIndex, category ->
                category.channelIds.forEachIndexed { subChannelIndex, channelId ->
                    if (channelId == currentChannelId) {
                        selectedCategoryIndex = categoryIndex
                        selectedSubCategoryIndex = subChannelIndex
                    }
                }
            }
        }
        // 播放区域
       // ContentPlayer(currentChannel = categories[selectedCategoryIndex].subChannels[selectedSubCategoryIndex])

        Row(modifier = Modifier.fillMaxSize()) {
            // 一级菜单
            LazyColumn(modifier = Modifier.width(100.dp)) {
                items(categories.size) { index ->
                    val category = categories[index]
                    val isSelected = index == selectedCategoryIndex
                    Text(
                        text = category.name,
                        modifier = Modifier
                            .clickable {
                                selectedCategoryIndex = index
                                selectedSubCategoryIndex = 0
                                selectedEpgIndex = 0 // 重置三级菜单选中状态
                            }
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
                            )
                            .padding(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // 二级菜单
            LazyColumn(modifier = Modifier.width(100.dp)) {
                items(categories[selectedCategoryIndex].channelIds) { channelId ->
                    val subChannel = channels.find { it.id == channelId }
                    if (subChannel != null) {
                        val isSelected = subChannel.id == channels[selectedSubCategoryIndex].id
                        Text(
                            text = subChannel.name,
                            modifier = Modifier
                                .clickable {
                                    selectedSubCategoryIndex = channels.indexOf(subChannel)
                                    // 更新当前频道
                                    currentChannelId = subChannel.id
                                }
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
                                )
                                .padding(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            // 三级电子节目单菜单
            LazyColumn(modifier = Modifier.width(100.dp)) {
                items(epgList.filter { it.channelId == currentChannelId }) { epg ->
                    val isSelected = epg.id == epgList[selectedEpgIndex].id
                    Text(
                        text = epg.title,
                        modifier = Modifier
                            .clickable {
                                selectedEpgIndex = epgList.indexOf(epg)
                                // 处理节目选择
                            }
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
                            )
                            .padding(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // 控制菜单浮出/隐藏的按钮
        /*FloatingActionButton(onClick = { showMenu = !showMenu }) {
            Icon(Icons.Default.Menu, contentDescription = null)
        }*/
    }


// 模拟的播放区域
@Composable
fun ContentPlayer(currentChannel: String) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .padding(16.dp)) {
        Text(
            text = "Playing $currentChannel",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

// 模拟的一级菜单和二级菜单数据结构
data class Category(val name: String, val subChannels: List<String>)

