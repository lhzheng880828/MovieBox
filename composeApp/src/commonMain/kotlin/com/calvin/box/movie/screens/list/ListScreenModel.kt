package com.calvin.box.movie.screens.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.data.MuseumObject
import com.calvin.box.movie.data.MuseumRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.*

class ListScreenModel(museumRepository: MuseumRepository) :ScreenModel {

    private val _uiState = MutableStateFlow(MuseumUiState.Success(emptyList()))
    val uiState: StateFlow<MuseumUiState> = _uiState


    val objects: StateFlow<List<MuseumObject>> =
        museumRepository.getObjects()
            .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        Napier.d { "init invoke" }
        museumRepository.initialize(screenModelScope)
    }
}
sealed class MuseumUiState {
    data class Success(val museums: List<MuseumObject>): MuseumUiState()
    data class Error(val exception: Throwable): MuseumUiState()
}