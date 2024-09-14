package com.calvin.box.movie.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.calvin.box.movie.ContextProvider
import com.github.catvod.Init
import io.github.aakira.napier.Napier

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

fun batteryLevel(): Int {
    val context = ContextProvider.context as Context
    val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
}

fun restartApp( ) {
    val context = ContextProvider.context as Context
    val pkgName = context.packageName
    Napier.d { "#restartApp, pkgName: $pkgName" }
    val intent = context.packageManager.getLaunchIntentForPackage(pkgName)
    val componentName = intent!!.component
    Napier.d { "#restartApp, componentName: $componentName" }
    val mainIntent = Intent.makeRestartActivityTask(componentName)
    context.startActivity(mainIntent)
    Runtime.getRuntime().exit(0)
}