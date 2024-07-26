package com.calvin.box.movie.feature.appupdate

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class UpdateScreen :Screen{
    //val context = LocalContext.current


    @Composable
    override fun Content() {
        val viewModel:UpdateViewModel = getScreenModel()
            LaunchedEffect(Unit) {
                viewModel.checkForUpdates()
            }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.updateAvailable) {
                Text("新版本可用!")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.downloadUpdate() }) {
                    Text("下载更新")
                }
            } else {
                Text("已是最新版本")
            }

            if (viewModel.downloadProgress > 0 && !viewModel.downloadComplete) {
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(progress = viewModel.downloadProgress)
                Text("下载进度: ${(viewModel.downloadProgress * 100).toInt()}%")
            }

            if (viewModel.downloadComplete) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.installUpdate() }) {
                    Text("安装更新")
                }
            }
        }
    }



}