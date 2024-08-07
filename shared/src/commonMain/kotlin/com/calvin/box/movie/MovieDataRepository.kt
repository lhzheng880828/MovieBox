package com.calvin.box.movie

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.calvin.box.movie.bean.Class
import com.calvin.box.movie.bean.Config
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.db.MoiveDatabase
import com.calvin.box.movie.network.MoiveApi
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

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

    suspend fun getJsonData(){
        api.getData(0)
    }

    suspend fun loadHomeContent(site: Site):Result{
       // Napier.d { "#loadHomeContent invoke, site: $site" }
        return spiderLoader.loadHomeContent(site)
    }

    suspend fun loadCategoryContent(homeSite: Site, category: Class, pageNum:String):Result{
        Napier.d { "#loadCategoryContent invoke, site: $homeSite, category: $category, pageNum: $pageNum" }
        return spiderLoader.loadCategoryContent(homeSite, category, page = pageNum)
    }

    suspend fun loadVodDetailContent(site:Site, vodId:String):Result{
        Napier.d { "#loadVodDetailContent invoke, siteKey: ${site.key}, vodId: $vodId" }
        return spiderLoader.loadDetailContent(site, vodId)
    }

    suspend fun loadSearchContent(site: Site, keyword:String, quick: Boolean,  page:String):Result{
        return spiderLoader.loadSearchContent(site, keyword, quick, page)
    }

    suspend fun loadPlayerContent(site: Site, flag:String, url: String):Result{
        return spiderLoader.loadPlayerContent(site, url, flag)
    }

    val vodList:Flow<List<Vod>> = emptyFlow()

}