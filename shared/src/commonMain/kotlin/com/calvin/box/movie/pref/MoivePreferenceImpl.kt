package com.calvin.box.movie.pref

import com.calvin.box.movie.MoiveSettings
import com.calvin.box.movie.Theme
import com.calvin.box.movie.player.Players
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class MoivePreferenceImpl(
    private val moiveSettings: MoiveSettings,
    override val scope: CoroutineScope,
) : BasePreference {


    override val theme: Preference<Theme> by lazy {
        MappingPreference(KEY_THEME, Theme.SYSTEM, ::getThemeForStorageValue, ::themeToStorageValue)
    }

    override val useDynamicColors: Preference<Boolean> by lazy {
        BooleanPreference(KEY_USE_DYNAMIC_COLORS, true)
    }

    override val vodUrl: Preference<String> by lazy {
        StringPreference(KEY_VOD_URL, "")
    }

    override val liveUrl: Preference<String> by lazy{
        StringPreference(KEY_LIVE_URL, "")

    }
    override val wallpaperUrl: Preference<String> by lazy {
        StringPreference(KEY_WALLPAPER_URL, "")
    }
    override val volume: Preference<Int> by lazy {
        IntPreference(KEY_VOLUME, 0)
    }

    override val doh: Preference<String> by lazy {
        StringPreference(KEY_DOH, "")
    }
    override val proxy: Preference<String> by lazy {
        StringPreference(KEY_PROXY, "")
    }
    override val keep: Preference<String> by lazy {
        StringPreference(KEY_KEEP, "")
    }
    override val keyword: Preference<String> by lazy {
        StringPreference(KEY_KEYWORD, "")
    }
    override val hot: Preference<String> by lazy {
        StringPreference(KEY_HOT, "")
    }
    override val ua: Preference<String> by lazy {
        StringPreference(KEY_UA, "")
    }
    override val wall: Preference<Int> by lazy {
        IntPreference(KEY_WALL, 1)
    }
    override val reset: Preference<Int> by lazy {
        IntPreference(KEY_RESET, 0)
    }
    override val player: Preference<Int> by lazy {
        IntPreference(KEY_PLAYER, Players.EXO)
    }

    override fun decode(player: Int):Preference<Int> {
        return IntPreference(KEY_DECODE_PREFIX+player, Players.EXO)
    }
    override val playerLive: Preference<Int> by lazy {
        IntPreference(KEY_PLAYER_LIVE, 0/*getPlayer()*/)
    }
    override val render: Preference<Int> by lazy {
        IntPreference(KEY_RENDER, 0)
    }
    override val quality: Preference<Int> by lazy {
        IntPreference(KEY_QUALITY, 2)
    }
    override val size: Preference<Int> by lazy {
        IntPreference(KEY_SIZE, 2)
    }
    override val viewType: Preference<Int> by lazy {
        IntPreference(KEY_VIEWTYPE, 0)
    }
    override val scale: Preference<Int> by lazy {
        IntPreference(KEY_SCALE, 1)
    }
    override val scaleLive: Preference<Int> by lazy {
        IntPreference(KEY_SCALE_LIVE, 0/*getScale()*/)
    }
    override val subtitle: Preference<Int> by lazy {
        IntPreference(KEY_SUBTITLE, 16)
    }
    override val exoHttp: Preference<Int> by lazy {
        IntPreference(KEY_EXO_HTTP, 1)
    }
    override val exoBuffer: Preference<Int> by lazy {
        IntPreference(KEY_EXO_BUFFER, 1)
    }
    override val flag: Preference<Int> by lazy {
        IntPreference(KEY_FLAG, 0)
    }
    override val episode: Preference<Int> by lazy {
        IntPreference(KEY_EPISODE, 0)
    }
    override val background: Preference<Int> by lazy {
        IntPreference(KEY_BACKGROUND, 2)
    }
    override val rtsp: Preference<Int> by lazy {
        IntPreference(KEY_RTSP, 0)
    }
    override val siteMode: Preference<Int> by lazy {
        IntPreference(KEY_SITE_MODE, 1)
    }
    override val bootLive: Preference<Boolean> by lazy {
        BooleanPreference(KEY_BOOT_LIVE, false)
    }
    override val invert: Preference<Boolean> by lazy {
        BooleanPreference(KEY_INVERT, false)
    }
    override val across: Preference<Boolean> by lazy {
        BooleanPreference(KEY_ACROSS, true)
    }
    override val change: Preference<Boolean> by lazy {
        BooleanPreference(KEY_CHANGE, true)
    }
    override val update: Preference<Boolean> by lazy {
        BooleanPreference(KEY_UPDATE, true)
    }
    override val danmu: Preference<Boolean> by lazy {
        BooleanPreference(KEY_DANMU, false)
    }
    override val danmuLoad: Preference<Boolean> by lazy {
        BooleanPreference(KEY_DANMU_LOAD, true)
    }
    override val danmuSpeed: Preference<Int> by lazy {
        IntPreference(KEY_DANMU_SPEED, 2)
    }
    override val danmuSize: Preference<Float> by lazy {
        FloatPreference(KEY_DANMU_SIZE, 1.0f)
    }
    override val danmuLine: Preference<Int> by lazy {
        IntPreference(KEY_DANMU_LINE, 1)
    }
    override val danmuAlpha: Preference<Int> by lazy {
        IntPreference(KEY_DANMU_ALPHA, 90)
    }
    override val caption: Preference<Boolean> by lazy {
        BooleanPreference(KEY_CAPTION, false)
    }
    override val exoTunnel: Preference<Boolean> by lazy {
        BooleanPreference(KEY_EXO_TUNNEL, false)
    }
    override val backupMode: Preference<Int> by lazy {
        IntPreference(KEY_BACKUP_MODE, 1)
    }
    override val displayTime: Preference<Boolean> by lazy {
        BooleanPreference(KEY_DISPLAY_TIME, false)
    }
    override val displaySpeed: Preference<Boolean> by lazy {
        BooleanPreference(KEY_DISPLAY_SPEED, false)
    }
    override val displayDuration: Preference<Boolean> by lazy {
        BooleanPreference(KEY_DISPLAY_DURATION, false)
    }
    override val displayMiniProgress: Preference<Boolean> by lazy {
        BooleanPreference(KEY_DISPLAY_MINI_PROGRESS, false)
    }
    override val displayVideoTitle: Preference<Boolean> by lazy {
        BooleanPreference(KEY_DISPLAY_VIDEO_TITLE, false)
    }
    override val playSpeed: Preference<Float> by lazy {
        FloatPreference(KEY_PLAY_SPEED, 1.0f)
    }
    override val fullscreenMenuKey: Preference<Int> by lazy {
        IntPreference(KEY_FULLSCREEN_MENU_KEY, 0)
    }
    override val homeMenuKey: Preference<Int> by lazy {
        IntPreference(KEY_HOME_MENU_KEY, 0)
    }
    override val homeSiteLock: Preference<Boolean> by lazy {
        BooleanPreference(KEY_HOME_SITE_LOCK, false)
    }
    override val incognito: Preference<Boolean> by lazy {
        BooleanPreference(KEY_INCOGNITO, false)
    }
    override val smallWindowBackKey: Preference<Int> by lazy {
        IntPreference(KEY_SMALL_WINDOW_BACK_KEY, 0)
    }
    override val homeDisplayName: Preference<Boolean> by lazy {
        BooleanPreference(KEY_HOME_DISPLAY_NAME, false)
    }
    override val aggregatedSearch: Preference<Boolean> by lazy {
        BooleanPreference(KEY_AGGREGATED_SEARCH, false)
    }
    override val homeUi: Preference<Int> by lazy {
        IntPreference(KEY_HOME_UI, 1)
    }
    override val homeButtons: Preference<String> by lazy {
        StringPreference(KEY_HOME_BUTTONS, "")
    }
    override val homeButtonsSorted: Preference<String> by lazy {
        StringPreference(KEY_HOME_BUTTONS_SORTED, "")
    }
    override val homeHistory: Preference<Boolean> by lazy {
        BooleanPreference(KEY_HOME_HISTORY, true)
    }
    override val configCache: Preference<Int> by lazy {
        IntPreference(KEY_CONFIG_CACHE, 0)
    }
    override val language: Preference<Int> by lazy {
        IntPreference(KEY_LANGUAGE, 0/*LanguageUtil.locale()*/)
    }
    override val parseWebView: Preference<Int> by lazy {
        IntPreference(KEY_PARSE_WEBVIEW, 0)
    }
    override val removeAd: Preference<Boolean> by lazy {
        BooleanPreference(KEY_REMOVE_AD, false)
    }
    override val thunderCacheDir: Preference<String> by lazy {
        StringPreference(KEY_THUNDER_CACHE_DIR, "")
    }


    private inner class BooleanPreference(
        private val key: String,
        override val defaultValue: Boolean = false,
    ) : Preference<Boolean> {
        override suspend fun set(value: Boolean) = withContext(scope.coroutineContext) {
            moiveSettings.putBoolean(key, value)
        }

        override suspend fun get(): Boolean = withContext(scope.coroutineContext/*dispatchers.io*/) {
            moiveSettings.getBoolean(key, defaultValue)
        }

        override val flow: Flow<Boolean> by lazy { moiveSettings.getBooleanFlow(key, defaultValue) }
    }

    private inner class IntPreference(
        private val key: String,
        override val defaultValue: Int ,
    ) : Preference<Int> {
        override suspend fun set(value: Int) = withContext(scope.coroutineContext) {
            moiveSettings.putInt(key, value)
        }

        override suspend fun get(): Int = withContext(scope.coroutineContext/*dispatchers.io*/) {
            moiveSettings.getInt(key, defaultValue)
        }

        override val flow: Flow<Int> by lazy { moiveSettings.getIntFlow(key, defaultValue) }
    }

    private inner class FloatPreference(
        private val key: String,
        override val defaultValue: Float ,
    ) : Preference<Float> {
        override suspend fun set(value: Float) = withContext(scope.coroutineContext) {
            moiveSettings.putFloat(key, value)
        }

        override suspend fun get(): Float = withContext(scope.coroutineContext/*dispatchers.io*/) {
            moiveSettings.getFloat(key, defaultValue)
        }

        override val flow: Flow<Float> by lazy { moiveSettings.getFloatFlow(key, defaultValue) }
    }


    private inner class StringPreference(
        private val key: String,
        override val defaultValue: String
    ) : Preference<String> {

        override suspend fun set(value: String) = withContext(scope.coroutineContext) {
            moiveSettings.putString(key, value)
        }

        override suspend fun get(): String = withContext(scope.coroutineContext) {
            moiveSettings.getStringOrNull(key) ?: defaultValue
        }

        override val flow: Flow<String> by lazy { moiveSettings.getStringOrNullFlow(key).mapNotNull { it?:"" } }
    }


    private inner class MappingPreference<V>(
        private val key: String,
        override val defaultValue: V,
        private val toValue: (String) -> V,
        private val fromValue: (V) -> String,
    ) : Preference<V> {
        override suspend fun set(value: V) = withContext(scope.coroutineContext/*dispatchers.io*/) {
            Napier.d { "set(), strValue: $value" }

            moiveSettings.putString(key,  fromValue(value))
        }

        override suspend fun get(): V = withContext(scope.coroutineContext/*dispatchers.io*/) {
           val strValue = moiveSettings.getStringOrNull(key)
            Napier.d { "get(), strValue: $strValue" }
            strValue?.let(toValue) ?: defaultValue
        }

        override val flow: Flow<V> by lazy {
            moiveSettings.getStringOrNullFlow(key).map { string ->
                if (string != null) {
                    toValue(string)
                } else {
                    defaultValue
                }
            }
        }
    }
    private fun themeToStorageValue(theme: Theme): String = when (theme) {
        Theme.LIGHT -> THEME_LIGHT_VALUE
        Theme.DARK -> THEME_DARK_VALUE
        Theme.SYSTEM -> THEME_SYSTEM_VALUE
    }

    private fun getThemeForStorageValue(value: String) = when (value) {
        THEME_LIGHT_VALUE -> Theme.LIGHT
        THEME_DARK_VALUE -> Theme.DARK
        else -> Theme.SYSTEM
    }

}
internal const val KEY_THEME = "pref_theme"
internal const val KEY_USE_DYNAMIC_COLORS = "pref_dynamic_colors"
internal const val KEY_SITE_URL = "pref_url"
internal const val KEY_VOD_URL = "pref_vod_url"
internal const val KEY_LIVE_URL = "pref_live_url"
internal const val KEY_WALLPAPER_URL = "pref_wallpaper_url"
internal const val KEY_VOLUME = "pref_volume"
internal const val KEY_DOH = "doh"
internal const val KEY_PROXY = "proxy"
internal const val KEY_KEEP = "keep"
internal const val KEY_KEYWORD = "keyword"
internal const val KEY_HOT = "hot"
internal const val KEY_UA = "ua"
internal const val KEY_WALL = "wall"
internal const val KEY_RESET = "reset"
internal const val KEY_PLAYER = "player"
internal const val KEY_PLAYER_LIVE = "player_live"
internal const val KEY_DECODE_PREFIX = "decode_"
internal const val KEY_RENDER = "render"
internal const val KEY_QUALITY = "quality"
internal const val KEY_SIZE = "size"
internal const val KEY_VIEWTYPE = "viewType"
internal const val KEY_SCALE = "scale"
internal const val KEY_SCALE_LIVE = "scale_live"
internal const val KEY_SUBTITLE = "subtitle"
internal const val KEY_EXO_HTTP = "exo_http"
internal const val KEY_EXO_BUFFER = "exo_buffer"
internal const val KEY_FLAG = "flag"
internal const val KEY_EPISODE = "episode"
internal const val KEY_BACKGROUND = "background"
internal const val KEY_RTSP = "rtsp"
internal const val KEY_SITE_MODE = "site_mode"
internal const val KEY_BOOT_LIVE = "boot_live"
internal const val KEY_INVERT = "invert"
internal const val KEY_ACROSS = "across"
internal const val KEY_CHANGE = "change"
internal const val KEY_UPDATE = "update"
internal const val KEY_DANMU = "danmu"
internal const val KEY_DANMU_LOAD = "danmu_load"
internal const val KEY_DANMU_SPEED = "danmu_speed"
internal const val KEY_DANMU_SIZE = "danmu_size"
internal const val KEY_DANMU_LINE = "danmu_line"
internal const val KEY_DANMU_ALPHA = "danmu_alpha"
internal const val KEY_CAPTION = "caption"
internal const val KEY_EXO_TUNNEL = "exo_tunnel"
internal const val KEY_BACKUP_MODE = "backup_mode"
internal const val KEY_DISPLAY_TIME = "display_time"
internal const val KEY_DISPLAY_SPEED = "display_speed"
internal const val KEY_DISPLAY_DURATION = "display_duration"
internal const val KEY_DISPLAY_MINI_PROGRESS = "display_mini_progress"
internal const val KEY_DISPLAY_VIDEO_TITLE = "display_video_title"
internal const val KEY_PLAY_SPEED = "play_speed"
internal const val KEY_FULLSCREEN_MENU_KEY = "fullscreen_menu_key"
internal const val KEY_HOME_MENU_KEY = "home_menu_key"
internal const val KEY_HOME_SITE_LOCK = "home_site_lock"
internal const val KEY_INCOGNITO = "incognito"
internal const val KEY_SMALL_WINDOW_BACK_KEY = "small_window_back_key"
internal const val KEY_HOME_DISPLAY_NAME = "home_display_name"
internal const val KEY_AGGREGATED_SEARCH = "aggregated_search"
internal const val KEY_HOME_UI = "home_ui"
internal const val KEY_HOME_BUTTONS = "home_buttons"
internal const val KEY_HOME_BUTTONS_SORTED = "home_buttons_sorted"
internal const val KEY_HOME_HISTORY = "home_history"
internal const val KEY_CONFIG_CACHE = "config_cache"
internal const val KEY_LANGUAGE = "language"
internal const val KEY_PARSE_WEBVIEW = "parse_webview"
internal const val KEY_REMOVE_AD = "remove_ad"
internal const val KEY_THUNDER_CACHE_DIR = "thunder_cache_dir"


internal const val THEME_LIGHT_VALUE = "light"
internal const val THEME_DARK_VALUE = "dark"
internal const val THEME_SYSTEM_VALUE = "system"





interface BasePreference {

    val scope:CoroutineScope

    val theme: Preference<Theme>
    val useDynamicColors: Preference<Boolean>
    val vodUrl: Preference<String>
    val liveUrl: Preference<String>
    val wallpaperUrl: Preference<String>
    val volume: Preference<Int>

    val doh: Preference<String>
    val proxy: Preference<String>
    val keep: Preference<String>
    val keyword: Preference<String>
    val hot: Preference<String>
    val ua: Preference<String>
    val wall: Preference<Int>
    val reset: Preference<Int>
    val player: Preference<Int>
     fun decode(player: Int): Preference<Int>
    val playerLive: Preference<Int>
    val render: Preference<Int>
    val quality: Preference<Int>
    val size: Preference<Int>
    val viewType: Preference<Int>
    val scale: Preference<Int>
    val scaleLive: Preference<Int>
    val subtitle: Preference<Int>
    val exoHttp: Preference<Int>
    val exoBuffer: Preference<Int>
    val flag: Preference<Int>
    val episode: Preference<Int>
    val background: Preference<Int>
    val rtsp: Preference<Int>
    val siteMode: Preference<Int>
    val bootLive: Preference<Boolean>
    val invert: Preference<Boolean>
    val across: Preference<Boolean>
    val change: Preference<Boolean>
    val update: Preference<Boolean>
    val danmu: Preference<Boolean>
    val danmuLoad: Preference<Boolean>
    val danmuSpeed: Preference<Int>
    val danmuSize: Preference<Float>
    val danmuLine: Preference<Int>
    val danmuAlpha: Preference<Int>
    val caption: Preference<Boolean>
    val exoTunnel: Preference<Boolean>
    val backupMode: Preference<Int>
    val displayTime: Preference<Boolean>
    val displaySpeed: Preference<Boolean>
    val displayDuration: Preference<Boolean>
    val displayMiniProgress: Preference<Boolean>
    val displayVideoTitle: Preference<Boolean>
    val playSpeed: Preference<Float>
    val fullscreenMenuKey: Preference<Int>
    val homeMenuKey: Preference<Int>
    val homeSiteLock: Preference<Boolean>
    val incognito: Preference<Boolean>
    val smallWindowBackKey: Preference<Int>
    val homeDisplayName: Preference<Boolean>
    val aggregatedSearch: Preference<Boolean>
    val homeUi: Preference<Int>
    val homeButtons: Preference<String>
    val homeButtonsSorted: Preference<String>
    val homeHistory: Preference<Boolean>
    val configCache: Preference<Int>
    val language: Preference<Int>
    val parseWebView: Preference<Int>
    val removeAd: Preference<Boolean>
    val thunderCacheDir: Preference<String>

}

interface PlayerPreference {

}

interface PersonalPreference{

}

