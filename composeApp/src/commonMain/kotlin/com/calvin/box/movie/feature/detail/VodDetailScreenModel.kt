package com.calvin.box.movie.feature.detail

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.data.MuseumObject
import com.calvin.box.movie.data.MuseumRepository
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.feature.videoplayerview.DetailDataCombine
import com.calvin.box.movie.feature.videoplayerview.UiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VodDetailScreenModel(appDataContainer: AppDataContainer) : ScreenModel {
    private val movieRepo = appDataContainer.movieRepository

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()


    fun getVodDetail(site : Site, vodId:String, vodName:String) {
        Napier.d { "#getVodDetail, site: ${site.key}, vodId:$vodId ,vodName: $vodName" }
        _uiState.value = UiState.Loading
        screenModelScope.launch(Dispatchers.IO) {
            val result =  movieRepo.loadVodDetailContent(site, vodId)
            if(result.list.isEmpty()){
                _uiState.value = UiState.Error("No vod list loaded")
            } else {
                val vodDetail = result.list.first()
                vodDetail.site = site
                val siteKey = site.key
                val flags = vodDetail.vodFlags
                val flag = flags[0].flag
                val episode = flags[0].episodes[0]
                Napier.d { "vodSite: $siteKey, flag: $flag, episode: $episode" }
                _uiState.value = UiState.Success(DetailDataCombine(detail = vodDetail))
            }
        }
    }

  /*  fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)*/
}
