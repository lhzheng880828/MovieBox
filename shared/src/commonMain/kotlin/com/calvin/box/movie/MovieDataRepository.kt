package com.calvin.box.movie

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.calvin.box.movie.api.LiveParser
import com.calvin.box.movie.api.config.LiveConfig
import com.calvin.box.movie.bean.ApkVersion
import com.calvin.box.movie.bean.Channel
import com.calvin.box.movie.bean.Config
import com.calvin.box.movie.bean.DownloadStatus
import com.calvin.box.movie.bean.Epg
import com.calvin.box.movie.bean.Group
import com.calvin.box.movie.bean.History
import com.calvin.box.movie.bean.Hot
import com.calvin.box.movie.bean.Keep
import com.calvin.box.movie.bean.Live
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.network.MoiveApi
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

class MovieDataRepository (
    private val api: MoiveApi,
    var database: MoiveDatabase,
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
){

    private val spiderLoader:SpiderLoader by lazy {
        getSpiderLoader()
    }

    val configDao = database.getConfigDao()
    val keepDao = database.getKeepDao()
    val historyDao = database.getHistoryDao()

     fun loadFirstConfig(type: Config.TYPE): Flow<Config?> {
        return database.getConfigDao().findOneFlow(type.ordinal)
    }

    suspend fun saveConfig(type: Config.TYPE, name:String, url:String){
        val config = Config.create(type.ordinal, url, name);

        database.getConfigDao().insertOrUpdate(config)
    }

    suspend fun delConfig(url:String){
        database.getConfigDao().delete(url)
    }

     fun loadAllConfig(type: Config.TYPE): Flow<List<Config>>{
        return database.getConfigDao().findByTypeFlow(type.ordinal)
    }

    // 获取所有Vod类型的Keep
    fun getKeepVodList(): Flow<List<Keep>> = flow {
        emit(keepDao.getVod())
    }.flowOn(Dispatchers.IO)

    // 根据CID和Key查找某个Keep
    fun findKeep(cid: Int, key: String): Flow<Keep?> = flow {
        emit(keepDao.find(cid, key))
    }.flowOn(Dispatchers.IO)

    // 根据Key查找某个特定类型的Keep
    fun findKeepByKey(key: String): Flow<Keep?> = flow {
        emit(keepDao.find(key))
    }.flowOn(Dispatchers.IO)

    // 删除某个Vod类型的Keep
    suspend fun deleteKeep(cid: Int, key: String) {
        keepDao.delete(cid, key)
    }

    // 删除某个特定类型的Keep
    suspend fun deleteKeepByKey(key: String) {
        keepDao.delete(key)
    }

    // 删除某个CID下的所有Keep
    suspend fun deleteKeepByCid(cid: Int) {
        keepDao.delete(cid)
    }

    // 删除所有Vod类型的Keep
    suspend fun deleteAllKeep() {
        keepDao.delete()
    }

    // 根据CID获取观看历史记录
    fun getHistoryByCid(cid: Int): Flow<List<History>> = flow {
        emit(historyDao.find(cid))
    }.flowOn(Dispatchers.IO)

    // 根据CID和Key查找具体的历史记录
    fun findHistoryByKey(cid: Int, key: String): Flow<History?> = flow {
        emit(historyDao.find(cid, key))
    }.flowOn(Dispatchers.IO)

    // 根据CID和VodName查找历史记录
    fun findHistoryByName(cid: Int, vodName: String): Flow<List<History>> = flow {
        emit(historyDao.findByName(cid, vodName))
    }.flowOn(Dispatchers.IO)

    // 删除指定CID和Key的历史记录
    suspend fun deleteHistory(cid: Int, key: String) {
        historyDao.delete(cid, key)
    }

    // 删除指定CID下的所有历史记录
    suspend fun deleteHistoryByCid(cid: Int) {
        historyDao.delete(cid)
    }

    // 删除所有历史记录
    suspend fun deleteAllHistory() {
        historyDao.delete()
    }


    suspend fun getHotwords():Hot{
       return api.getHotword()
    }

    suspend fun getSuggest(keyword: String):Flow<List<String>>{
        return api.getSuggest(keyword)
    }

    suspend fun getApkVersion(dev:Boolean, name:String): ApkVersion{
        return api.getApkVersion(dev, name)
    }
    fun download(dev: Boolean, name: String): Flow<DownloadStatus>{
        return api.download(dev, name)
    }

    suspend fun getLive(live: Live):Live {
       return  withContext(Dispatchers.IO) {
            LiveParser.start(live)
            parseTimeZone(live.epg)
            verify(live)
           live
        }

    }

    private var liveTimeZone = TimeZone.currentSystemDefault()
    private fun parseTimeZone(url: String) {
        try {
            if (!url.contains("serverTimeZone=")) return
            val zoneId = getPlatform().getQueryParameter(url, "serverTimeZone")
            if(zoneId.isNullOrEmpty()) return
            val timeZone = TimeZone.of(zoneId)
            liveTimeZone = timeZone
        } catch (e: Exception) {
            // Handle exception
        }
    }

    private fun verify(item: Live) {
        val iterator = item.groups.iterator()
        while (iterator.hasNext()) {
            val group = iterator.next()
            if (group.channels.isEmpty()) iterator.remove()
        }
        if (item.groups.isEmpty() || item.groups[0].isKeep()) return
        item.groups.add(0, Group.create("收藏", false))
        LiveConfig.get().setKeep(item.groups)
    }

    suspend fun getEpg(channel: Channel): Epg? {
        val now: Instant = Clock.System.now()
        val today = now.toLocalDateTime(liveTimeZone)
        val todayDate: LocalDate = today.date
        val todayTime: LocalTime = today.time
        val formatDatePattern = "yyyy-MM-dd"

        @OptIn(FormatStringsInDatetimeFormats::class)
        val dateFormat = LocalDate.Format {
            byUnicodePattern(formatDatePattern)
        }
        val date = dateFormat.format(todayDate)
        val url = channel.epg.replace("{date}", date)
       return withContext(Dispatchers.IO) {
            if (channel.data?.date != (date)) {
                val epg = api.getEpg(url)
                epg.key = channel.tvgName
                val formatDateTimePattern = "yyyy-MM-ddHH:mm"
                @OptIn(FormatStringsInDatetimeFormats::class)
                val dateTimeFormat = LocalDateTime.Format {
                    byUnicodePattern(formatDateTimePattern)
                }
                channel.data = (Epg.setDateTime(epg, liveTimeZone, dateTimeFormat))
            }
           channel.data?.selected()
        }
    }

    private var _homeResult:Result? = null

    //这里做一个主页缓存
    suspend fun loadHomeContent(site: Site):Result {
        val homeRes = spiderLoader.loadHomeContent(site)
        _homeResult = homeRes
        return homeRes
    }

    fun loadHomeContentFromCache(): Result? {
        Napier.i { "#loadHomeContentFromCache invoke" }
        return _homeResult
    }

    suspend fun loadCategoryContent(homeSite: Site, categoryType: String, categoryExt: HashMap<String, String>, pageNum:String):Result{
        Napier.d { "#loadCategoryContent invoke, site: $homeSite, categoryType: $categoryType, pageNum: $pageNum" }
        return spiderLoader.loadCategoryContent(homeSite, categoryType,categoryExt , page = pageNum)
    }

    suspend fun loadVodDetailContent(site:Site, vodId:String):Result{
        Napier.d { "#loadVodDetailContent invoke, siteKey: ${site.key}, vodId: $vodId" }
        return spiderLoader.loadDetailContent(site, vodId)
    }

    suspend fun loadSearchContent(site: Site, keyword:String, quick: Boolean,  page:String):Result{
        Napier.d { "#loadSearchContent invoke, siteKey: ${site.key}, keyword: $keyword, quick: $quick, page: $page" }
        return spiderLoader.loadSearchContent(site, keyword, quick, page)
    }

    suspend fun loadPlayerContent(site: Site, flag:String, url: String):Result{
        Napier.d { "#loadPlayerContent invoke, siteKey: ${site.key}, flag: $flag, url: $url" }
        return spiderLoader.loadPlayerContent(site, url, flag)
    }

    val vodList:Flow<List<Vod>> = emptyFlow()

}