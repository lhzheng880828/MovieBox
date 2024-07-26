package com.calvin.box.movie.ui.screens.tabsview

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.bean.Class
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.impl.Callback
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeTabViewModel(appDataContainer: AppDataContainer) :ScreenModel{


    private val vodConfig = appDataContainer.vodRepository
    private val movieRepo = appDataContainer.movieRepository

    private val _homeResult = MutableStateFlow(Result())
    val homeResult: StateFlow<Result> = _homeResult.asStateFlow()

    private val _categoryResult = MutableStateFlow(Result())
    val categoryResult: StateFlow<Result> = _categoryResult.asStateFlow()


    val homeVodList:StateFlow<List<Vod>> = homeResult.map { it.list }.stateIn(screenModelScope,
        SharingStarted.WhileSubscribed(5000L),
        emptyList() )

    val categoryVodList:StateFlow<List<Vod>> = categoryResult.map { it.list }.stateIn(screenModelScope,
        SharingStarted.WhileSubscribed(5000L),
        emptyList() )


    init {
        screenModelScope.launch {
            withContext(Dispatchers.IO){
                vodConfig.load(object : Callback {
                    override fun success() {
                        Napier.d(tag = TAG) { "load config success" }
                        val site = vodConfig.getHome()?:Site()
                        if(site.key.isEmpty() || site.name.isEmpty()){
                            Napier.w { "home site is empty" }
                            return
                        }
                        screenModelScope.launch{
                            withContext(Dispatchers.IO){
                                _homeResult.value = movieRepo.loadHomeContent(site)
                            }
                        }
                    }
                    override fun error(msg: String) {
                        Napier.d(tag = TAG) { "load config error: $msg" }

                    }

                })

            }
        }
       /* screenModelScope.launch{
            homeResult.collect{
                val result = it
                val categories = result.types
                val site = vodConfig.getHome()?:Site()
                if(site.key.isEmpty() || site.name.isEmpty()){
                    Napier.w { "home site is empty" }
                } else {
                    for (category in categories){
                        loadCategoryContent(site, category)
                    }
                }

            }
        }*/

    }



     fun loadCategoryContent(category: Class): Flow<List<Vod>> {
         //screenModelScope.launch{
          return  flow {
              val site = vodConfig.getHome()?:Site()
                if(site.key.isEmpty() || site.name.isEmpty()){
                    Napier.w { "home site is empty" }
                     emit(emptyList())
                } else{
                    val result = movieRepo.loadCategoryContent(homeSite = site, category = category)
                    /* Napier.d{"vod category name: ${category.typeName}, id: ${category.typeId}, size: ${result.list.size}"}
                     for(vod in result.list ){
                         Napier.d{"vod item loop: $vod"}
                     }*/
                    emit(result.list)
                }
            }.flowOn(Dispatchers.IO)
     }

        // }



   /* private fun map(result: Result): List<Class> {
        val types: MutableList<Class>  = mutableListOf()
        for (type in result.types){
            if (result.filters.containsKey(type.typeId)){
                val filters = result.filters[type.typeId]
                type.filters = filters?: emptyList()
            }
        }

        for (cate in VodConfig.get().getHome().categories){
            for (type in result.types){
                if (cate == type.typeName) {
                    types.add(type)
                }
            }
        }
        return types
    }*/

    companion object {
        const val TAG = "movie.HomeTabViewModel"
    }
}