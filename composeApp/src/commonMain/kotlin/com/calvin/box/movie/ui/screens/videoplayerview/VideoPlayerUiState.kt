package com.calvin.box.movie.ui.screens.videoplayerview

import com.calvin.box.movie.bean.Vod

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/1
 */
sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    object Empty : UiState()
    data class Success(val data: DetailDataCombine) : UiState()
    data class Error(val message: String) : UiState()
}

data class DetailDataCombine(val detail: Vod, val siteList:List<Vod> = emptyList())