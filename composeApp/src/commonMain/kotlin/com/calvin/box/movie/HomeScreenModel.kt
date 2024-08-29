package com.calvin.box.movie

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/29
 */
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.bean.Hot
import com.calvin.box.movie.di.AppDataContainer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random

// ViewModel to manage the hot data
class HomeScreenModel(val appData: AppDataContainer) : ScreenModel {
    private val _hotState = MutableStateFlow<List<String>>(emptyList())
    val hotState: StateFlow<List<String>> = _hotState.asStateFlow()

    private val _randomIndex = MutableStateFlow(0)
    val randomIndex: StateFlow<Int> = _randomIndex.asStateFlow()
    init {
        initHot()
        getHot()
    }

    private fun initHot() {
        screenModelScope.launch(Dispatchers.IO) {
            val json = Json{ignoreUnknownKeys=true}
            val hot:Hot = json.decodeFromString(appData.prefApi.hot.get())
            _hotState.value = hot.getData().map { it.title }
        }
        screenModelScope.launch(Dispatchers.Default){
            updateHotPeriodically()

        }
    }

    private fun getHot() {
        screenModelScope.launch(Dispatchers.IO) {
            val hotData = appData.movieRepository.getHotwords()
            appData.prefApi.hot.set(Json.encodeToString(hotData))
          val hotList =  hotData.getData().map { it.title }
           Napier.d{"hotList size from network: ${hotList.size}"}
            _hotState.value = hotList
        }
    }

    private suspend fun updateHotPeriodically() {
        while (true) {
            delay(10 * 1000) // 10 seconds delay
            if (_hotState.value.isNotEmpty() ) {
                val randomIndex = Random.nextInt(_hotState.value.size.coerceAtMost(20))
                _randomIndex.value = randomIndex

            }
        }
    }
}

