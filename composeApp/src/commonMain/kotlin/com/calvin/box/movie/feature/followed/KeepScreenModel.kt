package com.calvin.box.movie.feature.followed

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.bean.Keep
import com.calvin.box.movie.di.AppDataContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    // 加载状态，用于显示Loading UI
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 选中的项目，用于处理删除模式下的逻辑
    private val _selectedItems = MutableStateFlow<Set<Keep>>(emptySet())
    val selectedItems: StateFlow<Set<Keep>> = _selectedItems

    // 获取所有Vod类型的Keep
    val keepVodList: Flow<List<Keep>> = movieRepo.getKeepVodList()

    // 根据CID和Key查找某个Keep
    fun findKeep(cid: Int, key: String): Flow<Keep?> = movieRepo.findKeep(cid, key)

    // 根据Key查找某个特定类型的Keep
    fun findKeepByKey(key: String): Flow<Keep?> = movieRepo.findKeepByKey(key)

    // 删除某个Vod类型的Keep
    fun deleteKeep(cid: Int, key: String) {
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
        // 重新获取Keep列表的逻辑
    }
}
