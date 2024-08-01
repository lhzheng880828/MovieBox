package com.calvin.box.movie.utils

import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.github.catvod.Init

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/8/1
 */
fun getAndroidId(): String {
    try {
        val id =
            Settings.Secure.getString(Init.context().contentResolver, Settings.Secure.ANDROID_ID)
        if (TextUtils.isEmpty(id)) throw NullPointerException()
        return id
    } catch (e: Exception) {
        return "0000000000000000"
    }
}

fun getDeviceName(): String {
    val model = Build.MODEL
    val manufacturer = Build.MANUFACTURER
    return if (model.startsWith(manufacturer)) model else "$manufacturer $model"
}