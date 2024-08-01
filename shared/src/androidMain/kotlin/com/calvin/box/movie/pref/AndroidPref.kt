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
        val context = ContextProvider.context as Context
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getString(key: String): String {
        return getString(key, "")
    }

    @SuppressLint("SuspiciousIndentation")
    fun getString(key: String, defaultValue: String): String {
        return try {
          val prefStr = getPrefers().getString(key, defaultValue)
            return prefStr?:defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun put(key: String, obj: Any?) {
        if (obj == null) return
        if (obj is String) {
            getPrefers().edit().putString(key, obj as String?).apply()
        } else if (obj is Boolean) {
            getPrefers().edit().putBoolean(key, (obj as Boolean?)!!).apply()
        } else if (obj is Float) {
            getPrefers().edit().putFloat(key, (obj as Float?)!!).apply()
        } else if (obj is Int) {
            getPrefers().edit().putInt(key, (obj as Int?)!!).apply()
        } else if (obj is Long) {
            getPrefers().edit().putLong(key, (obj as Long?)!!).apply()
        } else if (obj is LazilyParsedNumber) {
            getPrefers().edit().putInt(key, obj.toInt()).apply()
        }
    }
}