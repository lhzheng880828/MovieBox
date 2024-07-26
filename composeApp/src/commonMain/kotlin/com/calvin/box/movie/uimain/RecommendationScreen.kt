package com.calvin.box.movie.uimain

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource


@Composable
fun RecommendationScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
        
    ) {
        items(listOf("电影", "电视剧", "综艺", "纪录片")) { category ->
            CategoryHeader(category)

            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(getMovieRecommendations(category).take(6)) { movie ->
                    MovieRecommendationItem(movie)
                }
            }
        }
    }
}

@Composable
fun CategoryHeader(category: String) {
    Text(
        text = category,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Text(
        text = "查看更多",
        color = Color.Blue,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun MovieRecommendationItem(movie: Movie) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(200.dp)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            contentDescription = movie.title,
            // Load image from network or local storage
          painter = painterResource( Res.drawable.compose_multiplatform)
        )

        Text(
            text = movie.title,
            modifier = Modifier
                //.align(Alignment.CenterHorizontally)
                .padding(4.dp)
        )
    }
}

// Sample data
data class Movie(val title: String, val imageUrl: String)

fun getMovieRecommendations(category: String): List<Movie> {
    // Fetch movie recommendations from API or database
    return listOf(
        Movie("电影1", "https://image.com/movie1.jpg"),
        Movie("电影2", "https://image.com/movie2.jpg"),
        Movie("电影3", "https://image.com/movie3.jpg"),
        // ...
    )
}
