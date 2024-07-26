package com.calvin.box.movie.data

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MuseumRepository(
    private val museumApi: MuseumApi,
    private val museumStorage: MuseumStorage,
) {

    fun initialize(scope: CoroutineScope) {
        Napier.i { "initialize invoke" }
        scope.launch {
            refresh()
        }
    }

    private suspend fun refresh() {
        val museumObjects = museumApi.getData()
        Napier.d(tag = "Repo", message = "museumObjs size: ${museumObjects.size}")
        museumStorage.saveObjects(museumObjects)
    }

    fun getObjects(): Flow<List<MuseumObject>> = museumStorage.getObjects()

    fun getObjectById(objectId: Int): Flow<MuseumObject?> = museumStorage.getObjectById(objectId)
}
