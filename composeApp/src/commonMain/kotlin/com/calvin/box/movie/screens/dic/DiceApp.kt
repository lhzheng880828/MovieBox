/*
 * Copyright 2022 The Android Open Source Project
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
package com.calvin.box.movie.screens.dic

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.calvin.box.movie.DiceSettings
import moviebox.composeapp.generated.resources.*
import moviebox.composeapp.generated.resources.Res
import moviebox.composeapp.generated.resources.app_name
import moviebox.composeapp.generated.resources.loading_settings
import moviebox.composeapp.generated.resources.roll_button_text
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

class DiceScreen():Screen {

    @Composable
    override fun Content() {
        val viewModel: DiceViewModel = getScreenModel()
        val settingsState by viewModel.settings.collectAsState()
        val resultState by viewModel.result.collectAsState()

        val settings = settingsState

        Scaffold(
            topBar = { TopAppBar(title = { Text(text = stringResource(Res.string.app_name)) }) },
        ) { paddingValues ->
            if (settings == null) {
                Text(stringResource(Res.string.loading_settings))
            } else {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                ) {
                    GameArea(viewModel, settings, resultState, modifier = Modifier.weight(1f))
                    Divider(Modifier.padding(16.dp))
                    Settings(viewModel, settings)
                }
            }
        }
    }

    companion object{}
}

@Composable
private fun GameArea(
    viewModel: DiceViewModel,
    settings: DiceSettings,
    resultState: DiceRollResult,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { viewModel.rollDice() }) {
            val text = buildString {
                append(
                    pluralStringResource(
                        Res.plurals.roll_button_text,
                        quantity = settings.diceCount,
                        settings.diceCount,
                        settings.sideCount,
                    )
                )
                if (settings.uniqueRollsOnly) {
                    appendLine()
                    append(stringResource(Res.string.roll_button_text_unique_suffix))
                }
            }

            AnimatedContent(text, label = "Roll Indicator") {
                Text(text = it, textAlign = TextAlign.Center)
            }
        }
        Text(
            text = when (resultState) {
                DiceRollResult.Error -> stringResource(Res.string.roll_result_error)
                DiceRollResult.Initial -> stringResource(Res.string.roll_result_waiting)
                is DiceRollResult.Success -> stringResource(Res.string.roll_result, resultState.values.sum(), resultState.values.joinToString())
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(4.dp),
        )
    }
}

@Composable
private fun Settings(
    viewModel: DiceViewModel,
    settings: DiceSettings,
    modifier: Modifier = Modifier,
) {
    var diceCount by remember { mutableIntStateOf(settings.diceCount) }
    var sideCount by remember { mutableIntStateOf(settings.sideCount) }
    var uniqueRollsOnly by remember { mutableStateOf(settings.uniqueRollsOnly) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(Res.string.configure_roll_settings), fontWeight = FontWeight.Bold)

        val unsavedNumber = diceCount != settings.diceCount
        ValueChooser(
            text = stringResource(Res.string.number_of_dice, diceCount),
            value = diceCount,
            onValueChange = { diceCount = it },
            unsaved = unsavedNumber,
            validRange = 1..10,
        )

        val unsavedSides = sideCount != settings.sideCount
        ValueChooser(
            text = stringResource(Res.string.sides_of_dice, sideCount),
            value = sideCount,
            onValueChange = { sideCount = it },
            unsaved = unsavedSides,
            validRange = 3..100,
        )

        val unsavedUnique = uniqueRollsOnly != settings.uniqueRollsOnly
        CheckboxChooser(
            text = stringResource(Res.string.unique_rolls),
            value = uniqueRollsOnly,
            onValueChange = { uniqueRollsOnly = it },
            unsaved = unsavedUnique,
        )

        Button(
            onClick = { viewModel.saveSettings(diceCount, sideCount, uniqueRollsOnly) },
            enabled = unsavedNumber || unsavedSides || unsavedUnique,
        ) {
            Text(stringResource(Res.string.save_settings))
        }
    }
}

@Composable
private fun ValueChooser(
    text: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    unsaved: Boolean,
    validRange: IntRange,
    modifier: Modifier = Modifier,
) {
    val buttonModifier = Modifier
        .padding(8.dp)
        .size(48.dp)

    Row(
        modifier = modifier.width(300.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Button(
            onClick = { onValueChange((value - 1).coerceIn(validRange)) },
            modifier = buttonModifier,
        ) {
            Text(text = "-")
        }

        Text(text = if (unsaved) "$text*" else text)

        Button(
            onClick = { onValueChange((value + 1).coerceIn(validRange)) },
            modifier = buttonModifier,
        ) {
            Text(text = "+")
        }
    }
}

@Composable
private fun CheckboxChooser(
    text: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    unsaved: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.width(300.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Checkbox(checked = value, onCheckedChange = onValueChange)
        Text(text, modifier = Modifier.clickable { onValueChange(!value) })
        Text("*", modifier = Modifier.alpha(if (unsaved) 1f else 0f))
    }
}
