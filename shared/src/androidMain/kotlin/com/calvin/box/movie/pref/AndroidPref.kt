package com.calvin.box.movie.pref

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.github.catvod.Init
import com.github.catvod.utils.Prefers
import com.google.gson.internal.LazilyParsedNumber
import com.calvin.box.movie.ContextProvider
import android.content.Context


/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/2
 */
object AndroidPref{
    private fun getPrefers(): SharedPreferences {
        val context = ContextProvider.context as  Context
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun get(key: String, defaultValue: Any): Any {
        return with(getPrefers()) {
            when (defaultValue) {
                is String -> getString(key, defaultValue) ?: defaultValue
                is Boolean -> getBoolean(key, defaultValue)
                is Float -> getFloat(key, defaultValue)
                is Int -> getInt(key, defaultValue)
                is Long -> getLong(key, defaultValue)
                is LazilyParsedNumber -> getInt(key, defaultValue.toInt())
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    fun put(key: String, value: Any?) {
        value?.let {
            with(getPrefers().edit()) {
                when (value) {
                    is String -> putString(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Float -> putFloat(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is LazilyParsedNumber -> putInt(key, value.toInt())
                    else -> throw IllegalArgumentException("Unsupported type")
                }.apply()  // 提交编辑
            }
        }
    }
}