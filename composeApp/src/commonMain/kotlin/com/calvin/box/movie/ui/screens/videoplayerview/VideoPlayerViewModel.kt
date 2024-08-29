package com.calvin.box.movie.ui.screens.videoplayerview

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.di.AppDataContainer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/30
 */
class VideoPlayerViewModel(appDataContainer: AppDataContainer) :ScreenModel{

    private var initAuto = false
    //private val redirect = false
    private var autoMode = false

    private val movieRepo = appDataContainer.movieRepository

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()



    fun getVodDetail(site :Site, vodId:String, vodName:String){
        Napier.d { "#getVodDetail, site: ${site.key}, vodId:$vodId ,vodName: $vodName" }
        val isFromCollection = false //TODO check the logic
        _uiState.value = UiState.Loading
        screenModelScope.launch(Dispatchers.IO) {
            if(vodId.isEmpty() || vodId.startsWith("msearch:")){
                if(isFromCollection || vodName.isEmpty()){
                    _uiState.value = UiState.Empty
                    return@launch
                }
                initAuto =true
                autoMode = true
                val matchedSites = mutableListOf<Site>()
                for(siteItem in VodConfig.get().getSites()){
                    if (isPass(siteItem)) matchedSites.add(siteItem)
                }
                if(matchedSites.isEmpty()){
                    _uiState.value = UiState.Error("no matched site")
                    return@launch
                }
                val vodList = mutableListOf<Vod>()
                try {
                    for (matchedSite in matchedSites){
                       val loopVodList = movieRepo.loadSearchContent(matchedSite, vodName, true, "1").list
                        vodList.addAll(loopVodList)
                    }
                } catch (e: Exception) {
                     e.printStackTrace()
                }
                /* val timeoutDuration = 5000L // 设置超时时间，5秒
                 val deferredResults = matchedSites.map { matchedSite ->
                     async {
                         try {
                             withTimeout(timeoutDuration) {
                                 movieRepo.loadSearchContent(matchedSite, vodName, true, "1").list
                             }
                         } catch (e: TimeoutCancellationException) {
                             emptyList() // 处理超时情况
                         } catch (e: Exception) {
                             emptyList() // 处理其他异常情况
                         }
                     }
                 }

                 deferredResults.forEach { deferred ->
                     try {
                         val result = deferred.await()
                         vodList.addAll(result)
                     } catch (e: Exception) {
                         // Handle any exceptions that might occur during await
                     }
                 }*/

                if(vodList.isNotEmpty()){
                    val vodOne = vodList[0]
                    val result =  movieRepo.loadVodDetailContent(vodOne.site!!, vodOne.vodId.toString())
                    val vodDetail = result.list.first()
                    vodDetail.site = vodOne.site
                    val flags = vodDetail.vodFlags

                    val flag = flags.get(0).flag

                    val episode = flags.get(0).episodes.get(0)

                    Napier.d { "vodSite: ${vodDetail.site}, flag: $flag, episode: $episode" }

                   val playerResult = movieRepo.loadPlayerContent(vodDetail.site!!, flag, episode.url)

                    Napier.d { "playerResult: $playerResult" }
                    val realUrl = playerResult.getRealUrl()
                    Napier.d { "realUrl: $realUrl" }
                    vodDetail.vodPlayUrl = realUrl


                    _uiState.value = UiState.Success(DetailDataCombine(detail = vodDetail, siteList = vodList))
                } else {
                    _uiState.value = UiState.Empty
                }

            } else {
                val result =  movieRepo.loadVodDetailContent(site, vodId)
                if(result.list.isEmpty()){
                    _uiState.value = UiState.Error("No vod list loaded")
                } else {
                    val vodDetail = result.list.first()

                    vodDetail.site = site
                    val flags = vodDetail.vodFlags

                    val flag = flags[0].flag

                    val episode = flags[0].episodes[0]

                    Napier.d { "vodSite: ${vodDetail.site}, flag: $flag, episode: $episode" }
                    val playerResult = movieRepo.loadPlayerContent(site, flag, episode.url)
                    Napier.d { "playerResult: $playerResult" }
                    val realUrl = playerResult.getRealUrl()
                    Napier.d { "realUrl: $realUrl" }
                    vodDetail.vodPlayUrl = realUrl
                    _uiState.value = UiState.Success(DetailDataCombine(detail = vodDetail))
                }

            }

        }
    }


    private fun isPass(item: Site): Boolean {
        if (autoMode && !item.isChangeable()) return false
        return item.isSearchable()
    }
}


