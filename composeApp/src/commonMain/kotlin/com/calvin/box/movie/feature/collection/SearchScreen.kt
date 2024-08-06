package com.calvin.box.movie.feature.collection

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/5
 */
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 模拟数据
data class SearchRecord(val id: Int, val text: String)
data class HotSearch(val id: Int, val text: String)
data class CollectItem(val id: Int, val name: String)
data class VodItem(val id: Int, val title: String, val imageUrl: String)

val mockSearchRecords = List(10) { SearchRecord(it, "搜索记录 $it") }
val mockHotSearches = List(20) { HotSearch(it, "热门搜索 $it") }
val mockCollectItems = List(15) { CollectItem(it, "收藏 $it") }
val mockVodItems = List(30) { VodItem(it, "视频 $it", "https://example.com/image$it.jpg") }

@Composable
fun DeprecatedSearchScreen() {
    var showAgent by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        SearchBar(
            onViewToggle = { showAgent = !showAgent }
        )

        if (showAgent) {
            AgentContent()
        } else {
            ResultContent()
        }
    }
}

@Composable
fun SearchBar(onViewToggle: () -> Unit) {
    var keyword by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = keyword,
            onValueChange = { keyword = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("搜索关键词") },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.Blue,
                placeholderColor = Color.Blue,
                cursorColor = Color.Blue,
                backgroundColor = Color.Transparent
            )
        )

        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Site"
            )
        }

        IconButton(onClick = onViewToggle) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "View"
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AgentContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "搜索记录",
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                fontSize = 18.sp
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mockSearchRecords) { record ->
                    Chip(
                        onClick = { /* TODO */ },
                       // colors = ChipDefaults.chipColors(backgroundColor = Color.DarkGray)
                    ) {
                        Text(record.text )
                    }
                }
            }
        }

        item {
            Text(
                text = "热门搜索",
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
                fontSize = 18.sp
            )
        }

        items(mockHotSearches.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { hotSearch ->
                    Button(
                        onClick = { /* TODO */ },
                        //colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(hotSearch.text )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchRecordSection(records: List<SearchRecord>) {
    Text(
        text = "搜索记录",
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        fontSize = 18.sp
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(records) { record ->
            Chip(
                onClick = { /* TODO */ },
                //colors = ChipDefaults.chipColors(backgroundColor = Color.DarkGray)
            ) {
                Text(record.text )
            }
        }
    }
}

@Composable
fun SearchHotSection(hotSearches: List<HotSearch>) {
    Text(
        text = "热门搜索",
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
        //color = Color.White,
        fontSize = 18.sp
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(hotSearches) { hotSearch ->
            Button(
                onClick = { /* TODO */ },
              //  colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
            ) {
                Text(hotSearch.text/*, color = Color.White*/)
            }
        }
    }
}

@Composable
fun ResultContent() {
    Row(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.width(120.dp),
            contentPadding = PaddingValues(start = 8.dp, top = 8.dp, bottom = 8.dp)
        ) {
            items(mockCollectItems) { item ->
                Text(
                    text = item.name,
                    modifier = Modifier.padding(vertical = 8.dp),
                   // color = Color.White
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(end = 8.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mockVodItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f/9f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // 这里应该是一个图片，但为了简单起见，我们使用Text
                        Text(item.title, /*color = Color.White*/)
                    }
                }
            }
        }
    }
}


data class MovieData(val id: Int,   val title: String, val site: String, val episodes: String, val rating: String)
data class SiteData(val id: Int, val site: String)

@Composable
fun SearchScreen() {
    var textState by remember { mutableStateOf("") }

    val searchHistory = listOf(
        "异人之下", "战狼·战狼", "康斯坦丁2", "坚落的审判", "死侍与金刚狼", "我爱你", "康斯坦丁", "潜行"
    )
    val hotSearch = listOf(
        "开心锣铛", "战狼2", "战狼·战狼", "哈小浪轻松一刻 第三季", "孤战迷城",
        "你比星光美丽", "小猪佩奇全集", "新猫和老鼠 第四季", "汪汪队立大功全集",
        "小马宝莉 第8季", "你好，星期六 2024", "唐朝诡事录之西行", "戴拿奥特曼",
        "周处除三害", "熊出没全集", "错位", "熊出没之探险日记", "汪汪队立大功 第十季",
        "奔跑吧第8季", "长相思 第二季"
    )

    val sites = listOf(
        SiteData(0,"全部"),
        SiteData(1,"睿片") ,
        SiteData(2,"酷看"),
        SiteData(3,"萌米"),
        SiteData(4,"热播"),
        SiteData(5,"南瓜")
    )

    val searchResults = listOf(
        MovieData(0, "异人之下", "睿片", "第27集", "7.0"),
        MovieData(1, "异人之下", "酷看", "共27集", "8.0"),
        MovieData(2,"异人之下", "萌米", "全27集", "9.0"),
        MovieData(3,"异人之下V1", "萌米", "全27集", "9.0"),
        MovieData(4,"异人之下V2", "萌米", "全27集", "9.0"),
        MovieData(5,"异人之下HD", "睿片", "全27集", "9.0"),
        MovieData(6,"异人之下国语", "酷看", "全27集", "9.0")

    )

    //var searchKey = ""

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            BasicTextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                maxLines = 1,
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // 启动搜索逻辑
                       // textState = searchKey
                    }
                )
            )
            if (textState.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear text",
                    modifier = Modifier
                        .clickable { textState = "" }
                )
            }
        }


        if (textState.isEmpty()) {
            Text("历史", style = MaterialTheme.typography.h3)
            FlowRow(data = searchHistory,onItemClick = { item ->
                textState = item
                // 启动搜索逻辑
            })
            Spacer(modifier = Modifier.height(16.dp))
            Text("热搜", style = MaterialTheme.typography.h3)
            FlowRow(data = hotSearch,onItemClick = { item ->
                textState = item
                // 启动搜索逻辑
            })
        } else {

            var selectedSite by remember { mutableStateOf(sites.first()) }
            val filteredMovies = remember(selectedSite) {
                if (selectedSite.id == 0) searchResults
                else searchResults.filter { it.site == selectedSite.site }
            }

            Row(modifier = Modifier.fillMaxSize()) {
                // 左侧类目列表
                SiteList(
                    sites = sites,
                    selectedSite = selectedSite,
                    onSiteSelected = { selectedSite = it }
                )

                // 右侧电影列表
                MovieList(movies = filteredMovies)
            }


          /*  LazyColumn {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        sites.forEach { site ->
                            Chip(text = site, modifier = Modifier.padding(bottom = 8.dp))
                        }
                    }
                }
                items(searchResults) { movie ->
                    MovieItem(movie)
                }
            }*/
        }
    }
}

@Composable
fun FlowRow(data: List<String>, onItemClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        var currentRowWidth = 0.dp
        var rowItems = mutableListOf<String>()

        data.forEach { item ->
            val itemWidth = (item.length * 14).dp + 32.dp // 14.dp per character + 32.dp padding and margins
            if (currentRowWidth + itemWidth > 360.dp) {
                // Create a new row with current items
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { rowItem ->
                        Chip(text = rowItem, modifier = Modifier.padding(bottom = 8.dp), onClick = { onItemClick(rowItem) })
                    }
                }
                // Reset for the next row
                currentRowWidth = itemWidth
                rowItems = mutableListOf(item)
            } else {
                currentRowWidth += itemWidth + 8.dp // Adding space between items
                rowItems.add(item)
            }
        }

        // Add the last row
        if (rowItems.isNotEmpty()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { rowItem ->
                    Chip(text = rowItem, modifier = Modifier.padding(bottom = 8.dp),onClick = { onItemClick(rowItem) })
                }
            }
        }
    }
}

@Composable
fun Chip(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .wrapContentHeight()
            .clickable { onClick()},
        shape = RoundedCornerShape(16.dp),
        //colors = CardDefaults.cardColors(containerColor = Color.Gray)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = text )
        }
    }
}

@Composable
fun SiteList(
    sites: List<SiteData>,
    selectedSite: SiteData,
    onSiteSelected: (SiteData) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .width(120.dp)
            .fillMaxHeight()
    ) {
        items(sites) { site ->
            SiteItem(
                site = site,
                isSelected = site == selectedSite,
                onSiteSelected = onSiteSelected
            )
        }
    }
}

@Composable
fun SiteItem(
    site: SiteData,
    isSelected: Boolean,
    onSiteSelected: (SiteData) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSiteSelected(site) }
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Text(
            text = site.site,
            color = if (isSelected) MaterialTheme.colors.primary else Color.Black,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun MovieList(movies: List<MovieData>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(movies) { movie ->
            MovieItem(movie = movie)
        }
    }
}

/*@Composable
fun MovieItem(movie: Movie) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(2f / 3f),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 这里应该是电影海报图片，为了简化，我们用一个占位符 Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = movie.title,
                style = MaterialTheme.typography.body2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}*/

@Composable
fun MovieItem(movie: MovieData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { },
        //elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(text = movie.title, fontSize = 20.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "站点: ${movie.site}", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "集数: ${movie.episodes}", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "评分: ${movie.rating}", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DefaultPreview() {
    SearchScreen()
}