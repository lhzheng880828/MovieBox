package com.calvin.box.movie.feature.vod

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.di.AppDataContainer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.*

class ListScreenModel(appDataContainer: AppDataContainer) :ScreenModel {

    private val _uiState = MutableStateFlow(VodUiState.Success(emptyList()))
    val uiState: StateFlow<VodUiState> = _uiState

    val movieRepo = appDataContainer.movieRepository

    val objects: StateFlow<List<Vod>> =
        movieRepo.vodList
            .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        Napier.d { "init invoke" }
       // museumRepository.initialize(screenModelScope)
    }
}
sealed class VodUiState {
    data class Success(val vods: List<Vod>): VodUiState()
    data class Error(val exception: Throwable): VodUiState()
}