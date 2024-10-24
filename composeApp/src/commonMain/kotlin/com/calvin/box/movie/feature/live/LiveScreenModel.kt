package com.calvin.box.movie.feature.live

import androidx.compose.ui.text.intl.Locale
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.api.LiveParser
import com.calvin.box.movie.api.config.LiveConfig
import com.calvin.box.movie.bean.Channel
import com.calvin.box.movie.bean.Epg
import com.calvin.box.movie.bean.EpgData
import com.calvin.box.movie.bean.Group
import com.calvin.box.movie.bean.Live
import com.calvin.box.movie.bean.PlayMediaInfo
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.impl.Callback
import com.calvin.box.movie.player.Source
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/10/21
 */
class LiveScreenModel(appDataContainer: AppDataContainer) : ScreenModel {

    private val movieRepo = appDataContainer.movieRepository
    private val liveConfig = appDataContainer.liveRepository
    private val _vodPlayState = MutableStateFlow(PlayMediaInfo(url = ""))
    val vodPlayState: StateFlow<PlayMediaInfo> = _vodPlayState

   /* val formatTime: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val formatDate: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")*/

    private val _url = MutableStateFlow<Channel?>(null)
    val url: StateFlow<Channel?> = _url

    private val _xml = MutableStateFlow<Boolean?>(null)
    val xml: StateFlow<Boolean?> = _xml

    private val _live = MutableStateFlow<Live?>(null)
    val live: StateFlow<Live?> = _live

    private val _epg = MutableStateFlow<Epg?>(null)
    val epg: StateFlow<Epg?> = _epg

    init {
        liveConfig.load(object : Callback {
            override fun success() {
                Napier.d(tag  =  TAG) { "load live config  success" }

               val home = liveConfig.getHome()
                Napier.d(tag =  TAG) { "loaded home live info: $home" }
            }

            override fun error(msg: String) {
                Napier.d(tag  =  TAG) { "load live config error: $msg" }
            }

        })
    }


    fun getLive(item: Live) {
        screenModelScope.launch {
            _live.value = movieRepo.getLive(item)
        }
    }

    fun getXml(item: Live) {
       /* screenModelScope.launch {
            _xml.value = withContext(Dispatchers.IO) {
                EpgParser.start(item)
            }
        }*/
    }

    fun getEpg(channel: Channel) {
        screenModelScope.launch {
            _epg.value =  movieRepo.getEpg(channel)
        }
    }

    fun getUrl(item: Channel) {
        screenModelScope.launch {
            _url.value = withContext(Dispatchers.IO) {
                item.msg = null
                Source.stop()
                item.url = (Source.fetch(item))
                item
            }
        }
    }

    fun getUrl(item: Channel, data: EpgData) {
        screenModelScope.launch {
            _url.value = withContext(Dispatchers.IO) {
                item.url = (item.catchup.format(item.getCurrent(), data))
                item
            }
        }
    }

    companion object {
        const val TAG = "xbox.LiveModel"
    }


}