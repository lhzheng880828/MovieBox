package com.calvin.box.movie.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class PreferenceCategory(
    val name: String,
    val children: List<Preference>
)

data class Preference(
    val name: String,
    val value: Any?, // 可选值，用于存储数据的偏好设置
    val type: PreferenceType // 定义不同的偏好设置类型（例如 SWITCH、TEXT_INPUT、CHECKBOX）
)

enum class PreferenceType {
    SWITCH,
    TEXT_INPUT,
    CHECKBOX
}

@Composable
fun SwitchPreference(preference: Preference) {
    Row {
        Text(preference.name)
        Spacer(Modifier.weight(1f))
        Switch(checked = preference.value as Boolean, onCheckedChange = { newValue ->
            // 更新偏好设置值
        })
    }
}

@Composable
fun TextInputPreference(preference: Preference) {
    OutlinedTextField(
        value = preference.value as String,
        onValueChange = { newValue ->
            // 更新偏好设置值
        },
        label = { Text(preference.name) }
    )
}

// 类似地，为 SELECTOR 和 CHECKBOX 创建可组合函数

@Composable
fun PreferenceCategoryComponent(category: PreferenceCategory) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(category.name, style = MaterialTheme.typography.h6)
        Card(/*expanded = false*/) {
            Column {
                category.children.forEach { preference ->
                    when (preference.type) {
                        PreferenceType.SWITCH -> SwitchPreference(preference)
                        PreferenceType.TEXT_INPUT -> TextInputPreference(preference)
                        // 类似地，处理其他偏好设置类型
                        PreferenceType.CHECKBOX -> TODO()
                    }
                }
            }
        }
    }
}

