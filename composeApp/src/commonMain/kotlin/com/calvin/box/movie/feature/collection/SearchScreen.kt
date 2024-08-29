package com.calvin.box.movie.feature.collection

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/5
 */
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.calvin.box.movie.theme.hideKeyboard
import io.github.aakira.napier.Napier

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


data class MovieData(val id: String,   val title: String, val site: String, val episodes: String, val rating: String)
data class SiteData(val id: String, val site: String)

class SearchScreen(private val keyword: String = "异形：夺命舰"):Screen {

    @Composable
    override fun Content() {
        val viewModel:SearchScreenModel = getScreenModel()
        val textState by  viewModel.searchQuery.collectAsState(initial = keyword)
        val state by viewModel.state.collectAsState()
        val focusManager = LocalFocusManager.current

        //Napier.d { "SearchScreen state: $state" }
        val hotSearch =  if(state is SearchState.HotWords){
            (state as SearchState.HotWords).items
        } else if(state is SearchState.Suggestions) {
            (state as SearchState.Suggestions).items
        }else emptyList()

        val sites = if(state is SearchState.Site2VodCollection){
           val siteDatas =
               (state as SearchState.Site2VodCollection).sites.map { SiteData(it.key, it.name) }.toMutableList()
            siteDatas.add(0, SiteData("all", "全部"))
            siteDatas
        }else mutableListOf(SiteData("all", "全部"))

        val searchResults = if(state is SearchState.Site2VodCollection){
            (state as SearchState.Site2VodCollection).collections.map { MovieData(it.vodId, it.vodName, it.getSiteKey(), it.vodRemarks,it.ratio.toString()) }
        }else emptyList()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(Color.Gray.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = textState,
                    onValueChange =viewModel::onSearchQueryChanged,
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
                            hideKeyboard(focusManager)
                            viewModel.saveSearchKeyword(textState)
                            viewModel.searchVodCollection(textState)
                        }
                    )
                )
                if (textState.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear text",
                        modifier = Modifier
                            .clickable { viewModel.onSearchQueryChanged("") }
                    )
                }
            }


            if (state !is SearchState.Site2VodCollection) {
                Text("历史", style = MaterialTheme.typography.h3)
                FlowRow(data = viewModel.searchHistory,
                    onItemClick = { item ->
                        hideKeyboard(focusManager)
                        viewModel.onSearchQueryChanged(item)
                        viewModel.searchVodCollection(item)
                    },
                    onItemLongClick = { item ->
                        viewModel.removeSearchKeyword(item)
                    })
                Spacer(modifier = Modifier.height(16.dp))
                Text("热搜", style = MaterialTheme.typography.h3)
                FlowRow(data = hotSearch,
                    onItemClick = { item ->
                        hideKeyboard(focusManager)
                        viewModel.onSearchQueryChanged(item)
                        viewModel.searchVodCollection(item)
                },
                    onItemLongClick = {})
            } else {

                var selectedSite by remember { mutableStateOf(sites.first()) }
                val filteredMovies = remember(selectedSite) {
                    if (selectedSite.id == "all") searchResults
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
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FlowRow(data: List<String>,
            onItemClick: (String) -> Unit,
            onItemLongClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        var currentRowWidth = 0.dp
        var rowItems = mutableListOf<String>()

        data.forEach { item ->
            val itemWidth = (item.length * 14).dp + 32.dp // 14.dp per character + 32.dp padding and margins
            if (currentRowWidth + itemWidth > 360.dp) {
                // Create a new row with current items
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { rowItem ->
                        Chip(text = rowItem,
                            modifier = Modifier.padding(bottom = 8.dp).combinedClickable(
                                onClick = { onItemClick(rowItem) },
                                onLongClick = { onItemLongClick(rowItem) }
                            ),
                            onClick = { onItemClick(rowItem) },

                        )
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
                    Chip(text = rowItem,
                        modifier = Modifier.padding(bottom = 8.dp),
                        onClick = { onItemClick(rowItem) })
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