package com.calvin.box.movie.xlab.paging

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/29
 */

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class RepoSearchPresenter {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            val json = Json {
                ignoreUnknownKeys = true
            }
            json(json)
        }
    }

    private var latestSearchTerm = ""

    private val pager: Pager<Int, Repository> = run {
        val pagingConfig = PagingConfig(pageSize = 20, initialLoadSize = 20)
        check(pagingConfig.pageSize == pagingConfig.initialLoadSize) {
            "As GitHub uses offset based pagination, an elegant PagingSource implementation requires each page to be of equal size."
        }
        Pager(pagingConfig) {
            RepositoryPagingSource(httpClient, latestSearchTerm)
        }
    }

    fun produceViewModels(events: Flow<Event>): Flow<ViewModel> {
        return events.map { event ->
            when (event) {
                is Event.SearchTerm -> {
                    latestSearchTerm = event.searchTerm
                    if (event.searchTerm.isEmpty()) {
                        ViewModel.Empty
                    } else {
                        ViewModel.SearchResults(latestSearchTerm, pager.flow)
                    }
                }
            }
        }
    }
}

private class RepositoryPagingSource(
    private val httpClient: HttpClient,
    private val searchTerm: String,
) : PagingSource<Int, Repository>() {

    override suspend fun load(params: PagingSourceLoadParams<Int>): PagingSourceLoadResult<Int, Repository> {
        val page = params.key ?: FIRST_PAGE_INDEX
        val httpResponse = httpClient.get("https://api.github.com/search/repositories") {
            url {
                parameters.append("page", page.toString())
                parameters.append("per_page", params.loadSize.toString())
                parameters.append("sort", "stars")
                parameters.append("q", searchTerm)
            }
            headers {
                append(HttpHeaders.Accept, "application/vnd.github.v3+json")
            }
        }
        return when {
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
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Repository>): Int? = null

    companion object {

        /**
         * The GitHub REST API uses [1-based page numbering](https://docs.github.com/en/rest/overview/resources-in-the-rest-api#pagination).
         */
        const val FIRST_PAGE_INDEX = 1
    }
}