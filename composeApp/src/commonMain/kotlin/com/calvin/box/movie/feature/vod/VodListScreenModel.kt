package com.calvin.box.movie.feature.vod

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import app.cash.paging.Pager
import app.cash.paging.PagingData
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.MovieDataRepository
import com.calvin.box.movie.api.config.VodConfig
import com.calvin.box.movie.bean.Class
import com.calvin.box.movie.bean.Config
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.bean.Site
import com.calvin.box.movie.bean.Vod
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.impl.Callback

import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class VodListScreenModel(appDataContainer: AppDataContainer) :ScreenModel {

    private val vodConfig = appDataContainer.vodRepository
    private val movieRepo = appDataContainer.movieRepository

    private val _categoryResult = MutableStateFlow(Result())
    val categoryResult: StateFlow<Result> = _categoryResult.asStateFlow()

    private val _homeClasses = MutableStateFlow(emptyList<Class>())
    val homeClasses: StateFlow<List<Class>> = _homeClasses.asStateFlow()

    private val _homeSiteFlow = MutableSharedFlow<Site>()

 private val _homePagingVodList = MutableStateFlow(PagingData.empty<Vod>())
   val homePagingVodList:StateFlow<PagingData<Vod>> = _homePagingVodList.asStateFlow()

    /*val objects: StateFlow<List<Vod>> =
        movieRepo.vodList
            .stateIn(screenModelScope, SharingStarted.WhileSubscribed(5000), emptyList())*/

    init {
        Napier.d(tag = TAG) { "vodList Model init" }
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


        fun loadPagingDataFLow(categoryType: String, extCategoryMap: HashMap<String, String>):Flow<androidx.paging.PagingData<Vod>>{
        if("Home"== categoryType){
            val site = vodConfig.getHome()
            if(site == null || site.key.isEmpty()) return emptyFlow()
            val result = runBlocking(Dispatchers.IO){
                movieRepo.loadHomeContent(site)
            }
            if(result.list.isEmpty()) return emptyFlow()
             return  loadHomePageContent(result.list).flow
        } else {
             return loadCategoryPageContent(categoryType, extCategoryMap).flow
        }
    }

    fun loadCategoryContent(categoryType: String, categoryExt: HashMap<String, String>, pageNum:String): Flow<List<Vod>> {
        //screenModelScope.launch{
        return  flow {
            val site = vodConfig.getHome()?:Site()
            if(site.key.isEmpty() || site.name.isEmpty()){
                Napier.w { "home site is empty" }
                emit(emptyList())
            } else{
                val result = movieRepo.loadCategoryContent(homeSite = site, categoryType, categoryExt, pageNum=pageNum)
                /* Napier.d{"vod category name: ${category.typeName}, id: ${category.typeId}, size: ${result.list.size}"}
                 for(vod in result.list ){
                     Napier.d{"vod item loop: $vod"}
                 }*/
                emit(result.list)
            }
        }.flowOn(Dispatchers.IO)
    }

    private  fun loadCategoryPageContent( categoryType: String,
                                          categoryExt: HashMap<String, String>):Pager<Int, Vod> {
        Napier.d(tag = TAG) { "loadCategoryPageContent invoke" }
        val pagingConfig = PagingConfig(pageSize = 20, initialLoadSize = 20)
        /*check(pagingConfig.pageSize == pagingConfig.initialLoadSize) {
            "As GitHub uses offset based pagination, an elegant PagingSource implementation requires each page to be of equal size."
        }*/
        return Pager(pagingConfig) {
            RepositoryPagingSource(vodConfig, movieRepo, categoryType, categoryExt)
        }
    }

    private fun loadHomePageContent(vodList:List<Vod>):Pager<Int,Vod>{
        Napier.d(tag = TAG) { "loadHomePageContent invoke" }
        val pagingConfig = PagingConfig(pageSize = 20, initialLoadSize = 20)
        return Pager(pagingConfig) {
            HomePagingSource(vodList)
        }

    }


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
        const val TAG = "xbox.vodList"
 }
}



sealed class VodUiState {
    data class Success(val vods: List<Vod>): VodUiState()
    data class Error(val exception: Throwable): VodUiState()
}

private class HomePagingSource(val vodList:List<Vod>): PagingSource<Int, Vod>() {
    override fun getRefreshKey(state: androidx.paging.PagingState<Int, Vod>): Int? {
        return /*state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }*/null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Vod> {
        val page = params.key ?: 0
        val pageSize = params.loadSize.coerceAtMost(20)

        val startIndex = page * pageSize
        val endIndex = minOf((page + 1) * pageSize, vodList.size)

        return if (startIndex < vodList.size) {
            LoadResult.Page(
                data = vodList.subList(startIndex, endIndex),
                prevKey = if (page > 0) page - 1 else null,
                nextKey = if (endIndex < vodList.size) page + 1 else null
            )
        } else {
            LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }
    }
}



private class RepositoryPagingSource(
    val vodConfig: VodConfig,
    val movieRepo: MovieDataRepository,
    val categoryType: String,
    val categoryExt: HashMap<String, String>
) : PagingSource<Int, Vod>() {

    override suspend fun load(params: PagingSourceLoadParams<Int>): PagingSourceLoadResult<Int, Vod> {
        val page = params.key ?: FIRST_PAGE_INDEX
        /*val httpResponse = httpClient.get("https://api.github.com/search/repositories") {
            url {
                parameters.append("page", page.toString())
                parameters.append("per_page", params.loadSize.toString())
                parameters.append("sort", "stars")
                parameters.append("q", searchTerm)
            }
            headers {
                append(HttpHeaders.Accept, "application/vnd.github.v3+json")
            }
        }*/
        Napier.d { "#load, page: $page" }
        val site = vodConfig.getHome()?: Site()
        if(site.key.isEmpty() || site.name.isEmpty()){
            Napier.w { "home site is empty" }
            return PagingSourceLoadResultError<Int, Vod>(
                Exception("Received site is empty."),
            ) as PagingSourceLoadResult<Int, Vod>
        } else{
            val result = withContext(Dispatchers.IO){
                movieRepo.loadCategoryContent(homeSite = site, categoryType = categoryType, categoryExt = categoryExt, pageNum = page.toString())
            }
            Napier.d{"vod category  id: $categoryType, size: ${result.list.size}"}
            /* for(vod in result.list ){
                 Napier.d{"vod item loop: $vod"}
             }*/
            //emit(result.list)
            return PagingSourceLoadResultPage(
                data = result.list,
                prevKey = (page - 1).takeIf { it >= FIRST_PAGE_INDEX },
                nextKey = if (result.list.isNotEmpty()) page + 1 else null,
            ) as PagingSourceLoadResult<Int, Vod>

        }

        /*return when {
            httpResponse.status.isSuccess() -> {
                val repositories = httpResponse.body<Repositories>()
                PagingSourceLoadResultPage(
                    data = repositories.items,
                    prevKey = (page - 1).takeIf { it >= FIRST_PAGE_INDEX },
                    nextKey = if (repositories.items.isNotEmpty()) page + 1 else null,
                ) as PagingSourceLoadResult<Int, Repository>
            }

            httpResponse.status == HttpStatusCode.Forbidden -> {
                PagingSourceLoadResultError<Int, Repository>(
                    Exception("Whoops! You just exceeded the GitHub API rate limit."),
                ) as PagingSourceLoadResult<Int, Repository>
            }

            else -> {
                PagingSourceLoadResultError<Int, Repository>(
                    Exception("Received a ${httpResponse.status}."),
                ) as PagingSourceLoadResult<Int, Repository>
            }
        }*/
    }

    override fun getRefreshKey(state: PagingState<Int, Vod>): Int? = null

    companion object {

        /**
         * The GitHub REST API uses [1-based page numbering](https://docs.github.com/en/rest/overview/resources-in-the-rest-api#pagination).
         */
        const val FIRST_PAGE_INDEX = 1
    }
}