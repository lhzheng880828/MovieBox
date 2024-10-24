package com.calvin.box.movie

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.calvin.box.movie.feature.live.LiveFullScreen

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/10/17
 */
class LiveTvActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveFullScreen()
        }
    }

}