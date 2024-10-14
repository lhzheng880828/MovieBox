package com.calvin.box.movie.feature.videoplayerview

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.bean.Config
import com.calvin.box.movie.bean.History
import com.calvin.box.movie.bean.Keep
import com.calvin.box.movie.bean.PlayMediaInfo
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.di.AppDataContainer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

    private val config = appDataContainer.vodRepository

    private val pref = appDataContainer.prefApi

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _vodPlayState = MutableStateFlow(PlayMediaInfo(url = ""))
    val vodPlayState: StateFlow<PlayMediaInfo> = _vodPlayState

    private val _keepState = MutableStateFlow(false)
    val keepState: StateFlow<Boolean> = _keepState

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
                    val flag = flags[0].flag
                    val episode = flags[0].episodes[0]
                    Napier.d { "vodSite: ${vodDetail.site}, flag: $flag, episode: $episode" }
                   val playerResult = movieRepo.loadPlayerContent(vodDetail.site!!, flag, episode.url)
                    Napier.d { "playerResult: $playerResult" }
                    val realUrl = playerResult.getRealUrl()
                    Napier.d { "realUrl: $realUrl" }
                    vodDetail.vodPlayUrl = realUrl
                    vodDetail.playMediaInfo = PlayMediaInfo(playerResult.getHeaders(),realUrl, playerResult.format, playerResult.drm, playerResult.subs )
                    _uiState.value =
                        UiState.Success(DetailDataCombine(detail = vodDetail, siteList = vodList))
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
                    val headers = playerResult.header
                    Napier.d { "realUrl: $realUrl, headers: $headers" }
                    vodDetail.vodPlayUrl = realUrl
                    vodDetail.playMediaInfo = PlayMediaInfo(playerResult.getHeaders(),realUrl, playerResult.format, playerResult.drm, playerResult.subs )
                    _uiState.value = UiState.Success(DetailDataCombine(detail = vodDetail))
                }

            }

        }
    }

    fun getVodPlayerContent(site :Site, line:String, url:String) {
        screenModelScope.launch(Dispatchers.IO) {
          val playerResult =  movieRepo.loadPlayerContent(site, line, url)
            val realUrl = playerResult.getRealUrl()
            val headers = playerResult.header
            Napier.d { "#getVodPlayerContent, realUrl: $realUrl, headers: $headers" }
            val playMediaInfo = PlayMediaInfo(playerResult.getHeaders(),realUrl, playerResult.format, playerResult.drm, playerResult.subs)
            _vodPlayState.value = playMediaInfo
        }
    }
    private var mHistory: History? = null

    fun getOrCreateHistory(siteKey: String, vodId: String, vod: Vod) {
        val configId = config.getConfig().id
        val historyKey = siteKey+ MoiveDatabase.SYMBOL+vodId+MoiveDatabase.SYMBOL+configId
        mHistory = History.find(historyKey) ?: createHistory(historyKey,configId, vod)
    }
    private fun createHistory(historyKey: String, configId: Int, vod: Vod): History {
        return History().apply {
            key =  historyKey
            cid = configId
            vodName = vod.vodName
            vodPic = vod.vodPic
            findEpisode(vod.vodFlags)
            speed = runBlocking { pref.playSpeed.get() }
        }
    }

    fun updateHistory( currentTime: Int, totalTime: Int){
        mHistory?.apply {
            this.position = currentTime.toLong()
            this.duration = totalTime.toLong()
            val isInCognito = runBlocking { pref.incognito.get() }
            if (position >= 0 && duration > 0 && !isInCognito) {
                update()
            }
            if (this.ending > 0 && duration > 0 && this.ending + position >= duration) {
                // 在视频播放结束时停止回调并处理下一步逻辑
                // mClock.setCallback(null)
                //checkNext()
            }
        }
    }


    private fun isPass(item: Site): Boolean {
        if (autoMode && !item.isChangeable()) return false
        return item.isSearchable()
    }

    fun initKeepState(siteKey :String, vodId:String): Boolean {
        val configId = config.getConfig().id
        val keepId = siteKey+ MoiveDatabase.SYMBOL+vodId+MoiveDatabase.SYMBOL+configId
       val find = Keep.find(keepId)!=null
        Napier.d { "#initKeepState, find: $find" }
        return find
    }

    fun toggleKeep(siteKey: String, siteName: String, vodId: String, vodName: String, vodPic: String) {
        val configId = config.getConfig().id
        val keepKey = siteKey+ MoiveDatabase.SYMBOL+vodId+MoiveDatabase.SYMBOL+configId
        screenModelScope.launch {
            val currentKeep = Keep.find(keepKey)
            if (currentKeep != null) {
                currentKeep.delete()
                _keepState.value = false
                //Notify.show(R.string.keep_del)
            } else {
                createKeep(keepKey, siteName,vodName,  vodPic)
                _keepState.value = true
               // Notify.show(R.string.keep_add)
            }
        }
    }

    private suspend fun createKeep(keepKey: String, siteName: String, voidName: String, vodPic:String) {
        val keep = Keep(keepKey).apply {
            val config = movieRepo.configDao.findOne(Config.TYPE.VOD.ordinal)
            this.cid = config?.id ?: 0
            this.siteName = siteName
            this.vodPic = vodPic
            this.vodName = voidName
        }
        keep.save()
    }

    fun checkKeep(key: String) {
        screenModelScope.launch {
            _keepState.value = Keep.find(key) != null
        }
    }

}



