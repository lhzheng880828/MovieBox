package com.calvin.box.movie.feature.history

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.calvin.box.movie.bean.History
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.navigation.LocalNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/10/11
 */
class HistoryScreenModel(appData: AppDataContainer) : ScreenModel {
    private val movieRepo = appData.movieRepository
    private val vodRepo = appData.vodRepository
    //根据CID获取所有历史记录


    private val _movies = MutableStateFlow<List<History>>(emptyList())

    val movies: StateFlow<List<History>> = _movies



    init {
         refreshHistoryList()
    }

    private val _inSelectionMode = MutableStateFlow(false)
    val inSelectionMode: StateFlow<Boolean> = _inSelectionMode

    private val _selectedMovies = MutableStateFlow<Set<History>>(emptySet())
    val selectedMovies: StateFlow<Set<History>> = _selectedMovies

    fun exitSelectionMode() {
        _inSelectionMode.value = false
    }

    fun toggleDeleteMode() {
        _inSelectionMode.value = !_inSelectionMode.value
        _selectedMovies.value = emptySet()  // 进入删除模式时，清空选中状态
    }

    fun toggleSelectItem(item: History) {
        val currentSelection = _selectedMovies.value.toMutableSet()
        if (currentSelection.contains(item)) {
            currentSelection.remove(item)
        } else {
            currentSelection.add(item)
        }
        _selectedMovies.value = currentSelection
    }

    fun deleteSelectedItems() {
        val cid = vodRepo.getConfig().id
        val keys = _selectedMovies.value.map { it.key }  // 删除选中的收藏项
        screenModelScope.launch(Dispatchers.IO) {
            val deleteJobs = keys.map { key ->
                async {
                    deleteHistory(cid, key)
                }
            }
            // 等待所有删除任务完成
            deleteJobs.awaitAll()
            _selectedMovies.value = emptySet()
            refreshHistoryList() // 删除后刷新列表
        }
    }

    fun selectAllItems() {
        _selectedMovies.value = movies.value.toSet()
    }

    fun deselectAllItems() {
        _selectedMovies.value = emptySet()
    }

    // 根据CID和Key查找某个历史记录
    fun findHistoryByKey(cid: Int, key: String): Flow<History?> = movieRepo.findHistoryByKey(cid, key)

    // 根据CID和VodName查找某个历史记录
    fun findHistoryByName(cid: Int, vodName: String): Flow<List<History>> = movieRepo.findHistoryByName(cid, vodName)

    // 删除指定CID和Key的历史记录
    fun deleteHistory(cid: Int, key: String) {
        screenModelScope.launch(Dispatchers.IO) {
            movieRepo.deleteHistory(cid, key)
            refreshHistoryList()
        }
    }

    // 删除指定CID下的所有历史记录
    fun deleteHistoryByCid(cid: Int) {
        screenModelScope.launch(Dispatchers.IO) {
            movieRepo.deleteHistoryByCid(cid)
            refreshHistoryList()
        }
    }

    // 删除所有历史记录
    fun deleteAllHistory() {
        screenModelScope.launch(Dispatchers.IO) {
            movieRepo.deleteAllHistory()
            refreshHistoryList()
        }
    }

    // 刷新历史记录列表
    private fun refreshHistoryList() {
        movieRepo.getHistoryByCid(vodRepo.getConfig().id) // 传入 cid
            .onEach { historyList ->
                _movies.value = historyList // 更新 _movies 的值
            }
            .launchIn(screenModelScope)
    }
}
