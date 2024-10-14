package com.calvin.box.movie.feature.followed

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.bean.Keep
import com.calvin.box.movie.di.AppDataContainer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/10/11
 */
class KeepScreenModel(
    appData: AppDataContainer
) : ScreenModel {

    private val movieRepo = appData.movieRepository
    private val vodRepo = appData.vodRepository

    private val _keeps = MutableStateFlow<List<Keep>>(emptyList())
    val keeps: StateFlow<List<Keep>> = _keeps







    val keepIds: StateFlow<List<String>> = movieRepo.getKeepVodList().map { keepList ->
        keepList.map { keep -> keep.key }  // 将每个 Keep 对象的 name 字段转换为 String
    }.stateIn(
            scope = screenModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    private val _inSelectionMode = MutableStateFlow(false)
    val inSelectionMode: StateFlow<Boolean> = _inSelectionMode

    private val _selectedKeeps = MutableStateFlow<Set<Keep>>(emptySet())
    val selectedKeeps: StateFlow<Set<Keep>> = _selectedKeeps

    // 加载状态，用于显示Loading UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        refreshKeepList()
    }


    fun exitSelectionMode() {
        _inSelectionMode.value = false
    }

    fun toggleSelectionMode() {
        _inSelectionMode.value = !_inSelectionMode.value
        _selectedKeeps.value = emptySet()  // 进入删除模式时，清空选中状态
    }

    fun toggleSelectItem(item: Keep) {
        val currentSelection = _selectedKeeps.value.toMutableSet()
        if (currentSelection.contains(item)) {
            currentSelection.remove(item)
        } else {
            currentSelection.add(item)
        }
        _selectedKeeps.value = currentSelection
    }

    fun containInSelected(target: String):Boolean{
        val currentSelection = _selectedKeeps.value.toMutableSet()
        for (item in currentSelection) {
            if(item.key == target){
                return true
            }
        }
        return false
    }

    fun deleteSelectedItems() {
        val cid = vodRepo.getConfig().id
        val keys = _selectedKeeps.value.map { it.key }  // 删除选中的收藏项
        screenModelScope.launch(Dispatchers.IO) {
            val deleteJobs = keys.map { key ->
                async {
                    Napier.d { "xbox.Keep, delete item cid: $cid, key: $key" }
                    deleteKeep(cid, key)
                }
            }
            // 等待所有删除任务完成
            deleteJobs.awaitAll()
           // refreshHistoryList() // 删除后刷新列表
            _selectedKeeps.value = emptySet()
        }
    }


    fun setSelectedItems(keeps:List<Keep>) {
        _selectedKeeps.value = keeps.toSet()
    }

    fun deselectAllItems() {
        _selectedKeeps.value = emptySet()
    }

    // 获取所有Vod类型的Keep
    val keepVodList: Flow<List<Keep>> = movieRepo.getKeepVodList()

    // 根据CID和Key查找某个Keep
    fun findKeep(cid: Int, key: String): Flow<Keep?> = movieRepo.findKeep(cid, key)

    // 根据Key查找某个特定类型的Keep
    fun findKeepByKey(key: String): Flow<Keep?> = movieRepo.findKeepByKey(key)

    // 删除某个Vod类型的Keep
    private fun deleteKeep(cid: Int, key: String) {
        screenModelScope.launch(Dispatchers.IO) {
            movieRepo.deleteKeep(cid, key)
            refreshKeepList()
        }
    }

    // 删除某个特定类型的Keep
    fun deleteKeepByKey(key: String) {
        screenModelScope.launch(Dispatchers.IO) {
            movieRepo.deleteKeepByKey(key)
            refreshKeepList()
        }
    }

    // 删除某个CID下的所有Keep
    fun deleteKeepByCid(cid: Int) {
        screenModelScope.launch(Dispatchers.IO) {
            movieRepo.deleteKeepByCid(cid)
            refreshKeepList()
        }
    }

    // 删除所有Vod类型的Keep
    fun deleteAllKeep() {
        screenModelScope.launch(Dispatchers.IO) {
            movieRepo.deleteAllKeep()
            refreshKeepList()
        }
    }

    // 刷新列表
    private fun refreshKeepList() {
        movieRepo.getKeepVodList()
            .onEach { keepList ->
                _keeps.value = keepList // 更新 _keeps 的值
            }
            .launchIn(screenModelScope)
    }
}
