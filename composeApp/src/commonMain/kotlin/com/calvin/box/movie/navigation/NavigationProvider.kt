package com.calvin.box.movie.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import com.calvin.box.movie.bean.Result
import com.calvin.box.movie.model.VideoModel
import kotlinx.coroutines.flow.MutableStateFlow

class NavigationProvider : Navigation {

    private val navigatorStack = MutableStateFlow<Navigator?>(null)
    private val screenContainer = MutableStateFlow<ScreenContainer?>(null)

    @Composable
    fun initialize() {
        navigatorStack.value = LocalNavigator.current
        screenContainer.value = LocalScreenContainer.current
    }

    override fun back(): Boolean {
        return tryAction { it.pop() }
    }

    override fun backToRoot(): Boolean {
        return tryAction { nav ->
//            nav.popUntil { it.key }
            nav.popUntilRoot()
        }
    }
    override fun goToVideoPlayerScreen(currentVideo: VideoModel): Boolean {
        return tryAction { nav ->
            screenContainer.value?.goToVideoPlayerScreen(currentVideo)?.let {
                nav.push(it)

            }
        }
    }

    override fun goToDetailScreen(currentVideo: VideoModel): Boolean {
        return tryAction { nav ->
            screenContainer.value?.goToDetailScreen(currentVideo)?.let {
                nav.push(it)

            }
        }
    }

    override fun goToFolderScreen(key: String, result: Result): Boolean {
        return tryAction { nav ->
            screenContainer.value?.goToFolderScreen(key, result)?.let {
                nav.push(it)

            }
        }
    }

    private fun tryAction(action: (Navigator) -> Unit): Boolean {
        return try {
            navigatorStack.value?.let(action)
            true
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun BottomSheetNavigatorContent(
        bottomSheetNavigator: BottomSheetNavigator,
        content: @Composable () -> Unit
    ) {
        BottomSheetNavigator {
            content()
        }
    }
}