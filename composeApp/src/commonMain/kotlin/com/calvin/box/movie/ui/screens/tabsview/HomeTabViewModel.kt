package com.calvin.box.movie.ui.screens.tabsview


import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.bean.Class
import com.calvin.box.movie.bean.Config
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.impl.Callback
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeTabViewModel(appDataContainer: AppDataContainer) :ScreenModel{


    private val vodConfig = appDataContainer.vodRepository
    private val movieRepo = appDataContainer.movieRepository

    private val _homeClasses = MutableStateFlow(emptyList<Class>())
    val homeClasses: StateFlow<List<Class>> = _homeClasses.asStateFlow()

    private val _homeSiteFlow = MutableSharedFlow<Site>()


    init {

        screenModelScope.launch{
            movieRepo.loadAllConfig(Config.TYPE.VOD).collect{
                Napier.d(tag = TAG) { "vod config size: ${it.size}" }
                if (it.isEmpty()) {
                    Napier.d(tag = TAG){ "vod config isEmpty" }
                    return@collect
                }
                it.onEach {
                    Napier.d(tag = TAG) { "loop config: name = ${it.name}, time = ${it.time}, url = ${it.url}, home = ${it.home}" }
                }
                val lastConfig = it.first()
                 val site = vodConfig.getSite(lastConfig.home)
                if(site.key.isEmpty() || site.name.isEmpty()){
                    Napier.w(tag = TAG) { "load from database, home site is empty" }
                    return@collect
                }
                onHomeSiteChanged(site)
            }
        }

        screenModelScope.launch {
            withContext(Dispatchers.IO){
                vodConfig.load(object : Callback {
                    override fun success() {
                        Napier.d(tag = TAG) { "load config success" }
                        val site = vodConfig.getHome()
                        if(site == null || site.key.isEmpty()){
                            Napier.w(tag = TAG) { "load from network, home site is empty" }
                            return
                        }
                       onHomeSiteChanged(site)
                    }
                    override fun error(msg: String) {
                        Napier.d(tag = TAG) { "load config error: $msg" }

                    }

                })

            }
        }


        screenModelScope.launch {
            _homeSiteFlow
                .debounce(1000) // 300ms的抖动时间，可以根据实际情况调整
                .distinctUntilChanged() // 防止相同的值重复处理
                .collect { site ->
                    Napier.i(tag = TAG) { "onHomeSiteChanged invoke after debounce" }
                    withContext(Dispatchers.IO) {
                        val result = movieRepo.loadHomeContent(site)
                        _homeClasses.value = result.types
                    }
                }
        }
    }

    private fun onHomeSiteChanged(site: Site){
        Napier.i(tag = TAG){"onHomeSiteChanged invoke"}
        screenModelScope.launch {
            _homeSiteFlow.emit(site)
        }
    }



    companion object {
        const val TAG = "xbox.HomeTabViewModel"
    }
}

