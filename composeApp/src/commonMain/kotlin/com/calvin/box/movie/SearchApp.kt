package com.calvin.box.movie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.calvin.box.movie.xlab.paging.Event
import com.calvin.box.movie.xlab.paging.RepoSearchContent
import com.calvin.box.movie.xlab.paging.RepoSearchPresenter
import com.calvin.box.movie.xlab.paging.RepoSearchTheme
import com.calvin.box.movie.xlab.paging.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import cafe.adriel.voyager.navigator.Navigator

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/7/29
 */

import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object SearchScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { SearchScreenModel() }
        val viewModel by screenModel.viewModels.collectAsState()

        RepoSearchTheme {
            RepoSearchContent(
                viewModel = viewModel,
                onEvent = { event ->
                    screenModel.onEvent(event)
                },
            )
        }
    }
}



class SearchScreenModel : ScreenModel {
    private val presenter = RepoSearchPresenter()
    private val events = MutableSharedFlow<Event>(extraBufferCapacity = Int.MAX_VALUE)
    private val _viewModels = MutableStateFlow<ViewModel>(ViewModel.Empty)
    val viewModels = _viewModels.asStateFlow()

    init {
        screenModelScope.launch(Dispatchers.IO) {
            _viewModels.emitAll(presenter.produceViewModels(events))
        }
    }

    fun onEvent(event: Event) {
        events.tryEmit(event)
    }
}


@Composable
fun SearchApp() {
    Navigator(SearchScreen)
}