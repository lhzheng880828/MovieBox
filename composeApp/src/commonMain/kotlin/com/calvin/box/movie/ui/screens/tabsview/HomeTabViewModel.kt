package com.calvin.box.movie.ui.screens.tabsview

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeTabViewModel(appDataContainer: AppDataContainer) :ScreenModel{


    private val vodConfig = appDataContainer.vodRepository
    private val movieRepo = appDataContainer.movieRepository

    private val _homeResult = MutableStateFlow(Result())
    val homeResult: StateFlow<Result> = _homeResult.asStateFlow()

    private val _categoryResult = MutableStateFlow(Result())
    val categoryResult: StateFlow<Result> = _categoryResult.asStateFlow()





    var homePagingVodList = flow<androidx.paging.PagingData<Vod>> { PagingData.empty<androidx.paging.PagingData<Vod>>() }
    //val homePagingVodList: StateFlow<PagingData<Vod>> =  _homePagingVodList.asStateFlow()

     var categoryPagingVodList = flow<androidx.paging.PagingData<Vod>> { PagingData.empty<androidx.paging.PagingData<Vod>>() }
    //val categoryPagingVodList: StateFlow<PagingData<Vod>> =  _categoryPagingVodList.asStateFlow()



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
                                val result = movieRepo.loadHomeContent(site)
                                _homeResult.value = result
                                homePagingVodList =  loadHomePageContent(result.list).flow

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

    fun loadPagingDataFLow(category: Class):Flow<androidx.paging.PagingData<Vod>>{
        if("Home"== category.typeId){
            return homePagingVodList
        } else {
            val pageFlow = loadCategoryPageContent(category)
             return pageFlow.flow
        }
    }



     fun loadCategoryContent(category: Class, pageNum:String): Flow<List<Vod>> {
         //screenModelScope.launch{
          return  flow {
              val site = vodConfig.getHome()?:Site()
                if(site.key.isEmpty() || site.name.isEmpty()){
                    Napier.w { "home site is empty" }
                     emit(emptyList())
                } else{
                    val result = movieRepo.loadCategoryContent(homeSite = site, category = category, pageNum=pageNum)
                    /* Napier.d{"vod category name: ${category.typeName}, id: ${category.typeId}, size: ${result.list.size}"}
                     for(vod in result.list ){
                         Napier.d{"vod item loop: $vod"}
                     }*/
                    emit(result.list)
                }
            }.flowOn(Dispatchers.IO)
     }

   private  fun loadCategoryPageContent(category: Class):Pager<Int, Vod> {
        val pagingConfig = PagingConfig(pageSize = 20, initialLoadSize = 20)
        /*check(pagingConfig.pageSize == pagingConfig.initialLoadSize) {
            "As GitHub uses offset based pagination, an elegant PagingSource implementation requires each page to be of equal size."
        }*/
       return Pager(pagingConfig) {
            RepositoryPagingSource(vodConfig, movieRepo, category)
        }
    }

     private fun loadHomePageContent(vodList:List<Vod>):Pager<Int,Vod>{
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
        const val TAG = "movie.HomeTabViewModel"
    }
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
    val category: Class
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
        val site = vodConfig.getHome()?:Site()
        if(site.key.isEmpty() || site.name.isEmpty()){
            Napier.w { "home site is empty" }
            return PagingSourceLoadResultError<Int, Vod>(
                Exception("Received site is empty."),
            ) as PagingSourceLoadResult<Int, Vod>
        } else{
            val result = withContext(Dispatchers.IO){
                movieRepo.loadCategoryContent(homeSite = site, category = category, pageNum = page.toString())
            }
             Napier.d{"vod category name: ${category.typeName}, id: ${category.typeId}, size: ${result.list.size}"}
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