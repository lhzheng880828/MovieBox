package com.calvin.box.movie.screens.detail

import cafe.adriel.voyager.core.model.ScreenModel
import com.calvin.box.movie.data.MuseumObject
import com.calvin.box.movie.data.MuseumRepository
import kotlinx.coroutines.flow.Flow

class DetailScreenModel(private val museumRepository: MuseumRepository) : ScreenModel {
    fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)
}
