package com.calvin.box.movie

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.calvin.box.movie.screens.fruitties.FruitListScreen

@Composable
fun MovieApp() {
    MaterialTheme {
        //Navigator(ListScreen)
        //Navigator(MainScreen)
        Navigator(FruitListScreen(0))
        //Navigator(DiceScreen())
    }
}
