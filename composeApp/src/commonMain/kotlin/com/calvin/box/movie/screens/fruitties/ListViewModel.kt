/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.calvin.box.movie.screens.fruitties

/*import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory*/
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.calvin.box.movie.FakeDataRepository
import com.calvin.box.movie.db.CartItemDetails
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.model.Fruittie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FruitViewModel(private val appDataContainer: AppDataContainer) : ScreenModel{

    private var repository:FakeDataRepository = appDataContainer.fakeRepository

    val uiState: StateFlow<HomeUiState> =
        repository.getData().map { HomeUiState(it) }
            .stateIn(
                scope = screenModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )

    val cartUiState: StateFlow<CartUiState> =
        repository.cartDetails.map { CartUiState(it.items) }
            .stateIn(
                scope = screenModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CartUiState()
            )

    fun addItemToCart(fruittie: Fruittie) {
        screenModelScope.launch {
            withContext(Dispatchers.IO){
                repository.addToCart(fruittie)
            }
        }
    }

    companion object {
        /*val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as App)
                val repository = application.container.dataRepository
                MainViewModel(repository = repository)
            }
        }*/
    }
}

/**
 * Ui State for ListScreen
 */
data class HomeUiState(val itemList: List<Fruittie> = listOf())

/**
 * Ui State for Cart
 */
data class CartUiState(val itemList: List<CartItemDetails> = listOf())

private const val TIMEOUT_MILLIS = 5_000L
