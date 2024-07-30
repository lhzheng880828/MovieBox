package com.calvin.box.movie.ui.screens.videoplayerview

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.di.AppDataContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/30
 */
class VideoPlayerViewModel(appDataContainer: AppDataContainer) :ScreenModel{

    private val movieRepo = appDataContainer.movieRepository

    private val _vodDetail = MutableStateFlow (Vod())
    val vodDetail = _vodDetail.asStateFlow()

    fun getVodDetail(site :Site, vodId:String){
        screenModelScope.launch {
          val result =  movieRepo.loadVodDetailContent(site, vodId)
           val vod = result.list.first()
            _vodDetail.value =vod
        }
    }
}