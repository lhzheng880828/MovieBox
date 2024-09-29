package com.calvin.box.movie.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator

// Define enum for navigation screens
enum class BottomNavigationScreens {
    Home, Live, History, Me
}

// Create a state variable to track the selected screen
var selectedScreen by mutableStateOf(BottomNavigationScreens.Home)

data object MainScreen:Screen {

    @Composable
    override fun Content() {
        Navigator(HomeScreen()) { navigator ->
            Scaffold(
                topBar = { /* ... */ },
                content = { CurrentScreen() },
                bottomBar = {  BottomNavigationBar(navController = navigator)}
            )
        }
    }

}

// Composable functions for each screen
class HomeScreen:Screen {

    @Composable
    override fun Content() {
        var index by remember { mutableStateOf(0) }
        HomeTabs(tabContent = {
            if(index == 0){
                Tab1Screen().Content()
            }else if(index ==1){
                Tab2Screen().Content()
            } else if(index ==2){
                Tab3Screen().Content()
            }

        }, onSelected = {
           index = it
        })

    }
}


@Composable
fun HomeTabs(
    tabContent: @Composable () -> Unit = {},
    onSelected: (index: Int) -> Unit,
){
    var state by remember { mutableStateOf(0) }
    val titles = listOf("tab1", "tab2", "tab3")
    Column {
        TabRow(selectedTabIndex = state){
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = {
                        state = index
                        onSelected(index) },
                    text = {Text(text=title, maxLines = 2, overflow = TextOverflow.Ellipsis)}
                )

            }
        }
        tabContent()
    }
}


class LiveScreen:Screen {

    @Composable
    override fun Content() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Live Screen")
        }
    }
}


class HistoryScreen:Screen {

    @Composable
    override fun Content() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("History Screen")
        }
    }
}


class MeScreen:Screen {

    @Composable
    override fun Content() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Me Screen")
        }
    }
}

// Bottom navigation bar composable
@Composable
fun BottomNavigationBar(navController: Navigator) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedScreen == BottomNavigationScreens.Home,
            onClick = { navController.replace(HomeScreen()) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Favorite, contentDescription = "Live") },
            label = { Text("Live") },
            selected = selectedScreen == BottomNavigationScreens.Live,
            onClick = { navController.replace(LiveScreen()) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Favorite, contentDescription = "History") },
            label = { Text("History") },
            selected = selectedScreen == BottomNavigationScreens.History,
            onClick = { navController.replace(HistoryScreen()) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Me") },
            label = { Text("Me") },
            selected = selectedScreen == BottomNavigationScreens.Me,
            onClick = { navController.replace(MeScreen()) }
        )
    }

}

// Define routes for each screen
private val BottomNavigationScreens.route: String
    get() = when (this) {
        BottomNavigationScreens.Home -> "home"
        BottomNavigationScreens.Live -> "live"
        BottomNavigationScreens.History -> "history"
        BottomNavigationScreens.Me -> "me"
    }
