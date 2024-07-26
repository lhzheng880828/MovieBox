package com.calvin.box.movie

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.calvin.box.movie.model.Fruittie
import com.calvin.box.movie.screens.fruitties.FruittieItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //MovieApp()
            //ThemeApp()
            DarkmovieMainView()
        }
    }
}
@Preview(name = "Light Mode", heightDp = 100, device = Devices.PIXEL_4)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode",device = Devices.PIXEL_4)
@Composable
fun AppAndroidPreview() {
    //MovieApp()
    FruittieItem(item = Fruittie(name="apple", fullName = "Red apple", calories = "apple calories"), onAddToCart = {})
}