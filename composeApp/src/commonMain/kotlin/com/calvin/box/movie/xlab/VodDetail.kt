package com.calvin.box.movie.xlab

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/30
 */

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.calvin.box.movie.bean.Site
import io.github.aakira.napier.Napier

@Composable
fun MovieDetailScreen() {
    MaterialTheme {
        Scaffold {
            BottomSheetNavigator {
                Navigator(MovieDetailContent())
            }
        }
    }
}


class MovieDetailContent:Screen {

    @Composable
    override fun Content() {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item{MovieHeader()}
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                MovieLines()

            }
            item {
                Spacer(modifier = Modifier.height(16.dp))

            }
            item {
                MovieEpisodes()

            }
            item {
                Spacer(modifier = Modifier.height(16.dp))

            }
            item {
                MovieDescription()

            }
            item {
                Spacer(modifier = Modifier.height(16.dp))

            }
            item {
                MovieSites()

            }
        }
    }


}

@Composable
fun MovieHeader() {
    Column {
        Box(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
           /* Image(
                painter = painterResource(id = R.drawable.movie_image),
                contentDescription = "Movie Image",
                contentScale = ContentScale.Crop
            )*/
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("异人之下", style = MaterialTheme.typography.titleLarge)
        Text("全27集", style = MaterialTheme.typography.titleMedium)
        Text("站源: 萌米 | App", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("年份: 2023 地区: 中国 类型: 奇幻, 剧情", style = MaterialTheme.typography.bodySmall)
        Text("导演: 许宏宇", style = MaterialTheme.typography.bodySmall)
        Text("演员: 彭昱畅, 侯明昊, 王影璇, 王学圻, 毕雯珺, 姜珮瑶, 王鹤棣", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun MovieLines() {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("线路", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                Napier.d { "Download Btn clicked." }
                bottomSheetNavigator.hide()
                bottomSheetNavigator.show(DownloadBottomSheet())
                      },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            TextButton(onClick = { /* 点击事件处理 */ }) {
                Text("下载", style = MaterialTheme.typography.labelLarge)
            }
            //Icon(painterResource(id = R.drawable.ic_arrow_right), contentDescription = null)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow {
        items(listOf("天天线路1", "天天线路2", "天天线路3", "优酷线路")) { line ->
            Button(onClick = { /* TODO */ }) {
                Text(line)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun MovieEpisodes() {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("选集", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { bottomSheetNavigator.show(EpisodesBottomSheet())  },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            TextButton(onClick = { /* 点击事件处理 */ }) {
                Text("更多", style = MaterialTheme.typography.labelLarge)
            }
            //Icon(painterResource(id = R.drawable.ic_arrow_right), contentDescription = null)
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    LazyRow {
        items((1..10).toList()) { episode ->
            Button(onClick = { /* TODO */ }) {
                Text("第${episode}集")
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun MovieDescription() {
    var expanded by remember { mutableStateOf(false) }
    val description = if (expanded) {
        "这是一个详细的剧集简介，包含了许多细节和故事情节。"
    } else {
        "这是一个简单的剧集简介。"
    }

    Column {
        Text(description, maxLines = if (expanded) Int.MAX_VALUE else 3)
        TextButton(onClick = { expanded = !expanded }) {
            Text(if (expanded) "收起" else "展开")
        }
    }
}

@Composable
fun MovieSites() {
    Column {
        Text("站点列表", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        val sites = listOf("站点1", "站点2", "站点3", "站点4")
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth()
        ) {
            SiteItem(sites)
        }

    }
}


fun LazyGridScope.SiteItem(sites:List<String>){
    items(sites) { site ->
        Button(onClick = { /* TODO */ }) {
            Text(site)
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

class DownloadBottomSheet:Screen {

    @Composable
    override fun Content() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { bottomSheetNavigator.hide() }) {
                Text("返回")
            }
            Text("下载列表", style = MaterialTheme.typography.titleLarge)
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

class EpisodesBottomSheet:Screen {
    @Composable
    override fun Content() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { bottomSheetNavigator.hide() }) {
                Text("返回")
            }
            Text("选集列表", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items((1..6).toList()) { episode ->
                    Button(onClick = { /* TODO */ }) {
                        Text("第${episode}集")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

}
