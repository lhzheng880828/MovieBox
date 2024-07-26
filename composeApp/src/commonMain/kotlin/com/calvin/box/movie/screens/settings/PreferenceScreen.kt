package com.calvin.box.movie.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun PreferenceScreen(categories: List<PreferenceCategory>) {
    Column {
        categories.forEach { category ->
            PreferenceCategoryComponent(category)
        }
    }
}
