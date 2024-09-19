package com.calvin.box.movie.feature.folder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import cafe.adriel.voyager.core.screen.Screen
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.bean.Style
import network.chaintech.sdpcomposemultiplatform.sdp
import network.chaintech.sdpcomposemultiplatform.ssp

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/9/19
 */
data class FolderScreen(val vodKey: String, val result: Result):Screen {

    @Composable
    override fun Content() {
        var selectedType by remember { mutableStateOf(result.types[0]) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.sdp)
        ) {
            Text(
                text = selectedType.typeName,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(12.sdp),
                color = Color.White,
                fontSize = 14.ssp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // FragmentContainerView equivalent in Compose, TypeFragment handling
            selectedType.getStyle()?.let {
                TypeFragmentScreen(
                    key = key,
                    typeId = selectedType.typeId,
                    style = it,
                    isFlagged = selectedType.typeFlag == "1"
                )
            }
        }
    }


    // Handle back press using BackHandler in Compose
    /*BackHandle {
        if (canNavigateBack()) {
            activity.onBackPressed()
        }
    }*/
}

// This function replaces your TypeFragment handling
@Composable
fun TypeFragmentScreen(key: String, typeId: String, style: Style, isFlagged: Boolean) {
    // Logic to display content of TypeFragment based on arguments
    // You can manage states and display different UI here
    Text("Type Fragment content for typeId: $typeId")
}

fun canNavigateBack(): Boolean {
    // Implement logic that checks if the fragment can navigate back
    // You need to define your logic here based on fragment requirements
    return true
}


