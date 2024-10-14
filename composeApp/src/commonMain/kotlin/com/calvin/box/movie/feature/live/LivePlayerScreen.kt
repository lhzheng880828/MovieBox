package com.calvin.box.movie.feature.live

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/10/17
 */
@Composable
fun LiveTvScreen( ) {

    var selectedNavItem by remember { mutableStateOf("央视频道") }
    var selectedSubMenu by remember { mutableStateOf("") }

    Row(modifier = Modifier.fillMaxSize()) {
        // 侧边栏
        SideBar(
            navItems = listOf("央视频道", "卫视频道", "数字频道"),
            selectedNavItem = selectedNavItem
        ) { item ->
            selectedNavItem = item
            selectedSubMenu = ""  // 点击一级菜单时，清空二级菜单的选择
        }

        // 二级菜单
        if (selectedNavItem.isNotEmpty()) {
            SubMenuScreen(selectedNavItem) { subMenu ->
                selectedSubMenu = subMenu
            }
        }

        // 中间内容播放区域
        if (selectedSubMenu.isNotEmpty()) {
            ChannelScreen(selectedSubMenu)
        } else {
            VideoPlayer()
        }
    }
}

@Composable
fun VideoPlayer() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Text(
            text = "视频播放区域",
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SubMenuScreen(selectedNavItem: String, onProgramClick: (String) -> Unit) {
    val programs = when (selectedNavItem) {
        "央视频道" -> listOf("CCTV1", "CCTV2")
        "卫视频道" -> listOf("河南卫视", "浙江卫视")
        else -> emptyList()
    }

    LazyColumn {
        items(programs) { program ->
            Text(
                text = program,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        onProgramClick(program)
                    }
            )
        }
    }
}

@Composable
fun SideBar(
    navItems: List<String>,
    selectedNavItem: String,
    onItemSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.Gray)
            .fillMaxHeight()
            .width(200.dp)
    ) {
        navItems.forEach { item ->
            Text(
                text = item,
                color = if (item == selectedNavItem) Color.Yellow else Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        onItemSelected(item)
                    }
            )
        }
    }
}

@Composable
fun ChannelScreen(channelName: String) {
    // 显示对应频道的直播内容
    Text(text = "正在播放: $channelName")
}

