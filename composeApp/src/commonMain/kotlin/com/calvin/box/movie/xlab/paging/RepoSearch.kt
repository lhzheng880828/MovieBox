package com.calvin.box.movie.xlab.paging

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/29
 */

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import io.github.aakira.napier.Napier

@Composable
fun RepoSearchContent(
    viewModel: ViewModel,
    onEvent: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Napier.d { "RepoSearchContent invoke" }
    when (viewModel) {
        ViewModel.Empty -> {
            Scaffold(
                topBar = {
                    SearchField(
                        searchTerm = "",
                        onEvent = onEvent,
                        onRefreshList = {},
                    )
                },
                content = {},
                modifier = modifier,
            )
        }

        is ViewModel.SearchResults -> {
            val repositories = viewModel.repositories.collectAsLazyPagingItems()
            Scaffold(
                topBar = {
                    SearchField(
                        searchTerm = viewModel.searchTerm,
                        onEvent = onEvent,
                        onRefreshList = { repositories.refresh() },
                    )
                },
                content = {
                    SearchResults(repositories)
                },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun SearchResults(repositories: LazyPagingItems<Repository>) {
    LazyColumn(
        Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (val loadState = repositories.loadState.refresh) {
            LoadStateLoading -> {
                item {
                    CircularProgressIndicator()
                }
            }

            is LoadStateNotLoading -> {
                items(repositories.itemCount) { index ->
                    val repository = repositories[index]
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            repository!!.fullName,
                            Modifier.weight(1f),
                        )
                        Text(repository.stargazersCount.toString())
                    }
                }
            }

            is LoadStateError -> {
                item {
                    Text(loadState.error.message!!)
                }
            }

            else -> error("when should be exhaustive")
        }
    }
}