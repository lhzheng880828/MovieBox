package com.calvin.box.movie.feature.collection

import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.bean.Vod

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/29
 */
sealed class SearchState {
    object Initial : SearchState()
    object Loading : SearchState()
    data class HotWords(val items: List<String>) : SearchState()
    data class Suggestions(val items: List<String>) : SearchState()
    data class Site2VodCollection(val sites:List<Site>, val collections: List<Vod>): SearchState()
}
