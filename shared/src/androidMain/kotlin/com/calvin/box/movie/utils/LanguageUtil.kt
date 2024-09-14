package com.calvin.box.movie.utils

import android.app.Application
import android.content.Context
import com.calvin.box.movie.ContextProvider
import com.calvin.box.movie.Setting
import com.yariksoffice.lingver.Lingver
import java.util.Locale

object LanguageUtil {
    fun init(application: Application) {
        Lingver.init(application, getLocale(Setting.language))
    }

    fun setLocale(locale: Locale) {
        Lingver.getInstance().setLocale(ContextProvider.context as Context, locale)
    }

    fun locale(): Int {
        if (Locale.getDefault().language != "zh") return 0
        if (Locale.getDefault().country == "CN") return 1
        return 2
    }

    fun getLocale(lang: Int): Locale {
        return if (lang == 1) Locale.SIMPLIFIED_CHINESE
        else if (lang == 2) Locale.TRADITIONAL_CHINESE
        else Locale.ENGLISH
    }
}
