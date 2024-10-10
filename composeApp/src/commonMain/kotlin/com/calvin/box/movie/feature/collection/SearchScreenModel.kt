package com.calvin.box.movie.feature.collection

import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.bean.Hot
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.di.AppDataContainer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SearchScreenModel(appDataContainer: AppDataContainer) : StateScreenModel<SearchState>(SearchState.Initial) {

    private val prefApi = appDataContainer.prefApi
    private val movieRepo = appDataContainer.movieRepository
    private val gson = Json { ignoreUnknownKeys = true }


    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()



    init {
        searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                mutableState.value = SearchState.Loading
                if (query.isEmpty()) {
                    getHot()
                } else {
                    getSuggest(query)
                }
            }.onEach {
                mutableState.value = it
            }
            .launchIn(screenModelScope)


    }
    private suspend fun getHot(): Flow<SearchState> = flow {
        val hotItems = withContext(Dispatchers.IO){
            Hot.get(prefApi.hot.get())
        }
        emit(SearchState.HotWords(hotItems.take(20)))
    }




    private suspend fun getSuggest(text: String): Flow<SearchState> =  movieRepo.getSuggest(text).flowOn(Dispatchers.IO).map {
        SearchState.Suggestions(it)
    }




    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    var searchHistory = mutableStateListOf<String>()
        private set

    init {
        loadSearchHistory()
    }

    fun saveSearchKeyword(keyword: String) {
        searchHistory.remove(keyword)
        searchHistory.add(0, keyword)

        if (searchHistory.size > 8) {
            searchHistory.removeAt(searchHistory.size - 1)
        }
        saveToPreferences()
    }

    fun removeSearchKeyword(keyword: String) {
        searchHistory.remove(keyword)
        saveToPreferences()
    }

    private fun loadSearchHistory() {
        val json = runBlocking { prefApi.keyword.get() }
        searchHistory.clear()
        searchHistory.addAll(gson.decodeFromString(json))
    }

    private fun saveToPreferences() {
        val json = gson.encodeToString(searchHistory.toList())
        runBlocking { prefApi.keyword.set(json) }
    }

    fun searchVodCollection(keyword: String)  {
        val vodList = mutableMapOf<Site, List<Vod>>()
        screenModelScope.launch(Dispatchers.IO) {
            val matchedSites = mutableListOf<Site>()
            for (siteItem in VodConfig.get().getSites()) {
                if (isPass(siteItem)) matchedSites.add(siteItem)
            }
            Napier.d { "matchedSite size: ${matchedSites.size}" }
            for (matchedSite in matchedSites/*.filterIndexed { index, site -> index < 10 }*/) {
                try {
                    val loopVodList = movieRepo.loadSearchContent(matchedSite, keyword, true, "1").list
                    vodList[matchedSite] = loopVodList
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mutableState.value = SearchState.Site2VodCollection(vodList.keys.toList(), vodList.values.flatten())
            }

        }
    }

    private fun isPass(item: Site): Boolean {
        // if (autoMode && !item.isChangeable()) return false
        return item.isSearchable()
    }
}
