package com.calvin.box.movie


import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.calvin.box.movie.player.Players
import com.calvin.box.movie.utils.LanguageUtil
import com.github.catvod.utils.Prefers
import kotlin.math.max
import kotlin.math.min

object Setting {
    val doh: String
        get() = Prefers.getString("doh")

    fun putDoh(doh: String?) {
        Prefers.put("doh", doh)
    }

    val proxy: String
        get() = Prefers.getString("proxy")

    fun putProxy(proxy: String?) {
        Prefers.put("proxy", proxy)
    }

    val keep: String
        get() = Prefers.getString("keep")

    fun putKeep(keep: String?) {
        Prefers.put("keep", keep)
    }

    val keyword: String
        get() = Prefers.getString("keyword")

    fun putKeyword(keyword: String?) {
        Prefers.put("keyword", keyword)
    }

    val hot: String
        get() = Prefers.getString("hot")

    fun putHot(hot: String?) {
        Prefers.put("hot", hot)
    }

    val ua: String
        get() = Prefers.getString("ua")

    fun putUa(ua: String?) {
        Prefers.put("ua", ua)
    }

    val wall: Int
        get() = Prefers.getInt("wall", 1)

    fun putWall(wall: Int) {
        Prefers.put("wall", wall)
    }

    val reset: Int
        get() = Prefers.getInt("reset", 0)

    fun putReset(reset: Int) {
        Prefers.put("reset", reset)
    }

    val player: Int
        get() = Prefers.getInt("player", Players.EXO)

    fun putPlayer(player: Int) {
        Prefers.put("player", player)
    }

    val livePlayer: Int
        get() = Prefers.getInt("player_live", player)

    fun putLivePlayer(player: Int) {
        Prefers.put("player_live", player)
    }

    fun getDecode(player: Int): Int {
        return Prefers.getInt("decode_$player", Players.HARD)
    }

    fun putDecode(player: Int, decode: Int) {
        Prefers.put("decode_$player", decode)
    }

    val render: Int
        get() = Prefers.getInt("render", 0)

    fun putRender(render: Int) {
        Prefers.put("render", render)
    }

    val quality: Int
        get() = Prefers.getInt("quality", 2)

    fun putQuality(quality: Int) {
        Prefers.put("quality", quality)
    }

    val size: Int
        get() = Prefers.getInt("size", 2)

    fun putSize(size: Int) {
        Prefers.put("size", size)
    }

    fun getViewType(viewType: Int): Int {
        return Prefers.getInt("viewType", viewType)
    }

    fun putViewType(viewType: Int) {
        Prefers.put("viewType", viewType)
    }

    val scale: Int
        get() = Prefers.getInt("scale")

    fun putScale(scale: Int) {
        Prefers.put("scale", scale)
    }

    val liveScale: Int
        get() = Prefers.getInt("scale_live", scale)

    fun putLiveScale(scale: Int) {
        Prefers.put("scale_live", scale)
    }

    val subtitle: Int
        get() = min(
            max(
                Prefers.getInt("subtitle", 16).toDouble(), 14.0
            ), 48.0
        ).toInt()

    fun putSubtitle(subtitle: Int) {
        Prefers.put("subtitle", subtitle)
    }

    val http: Int
        get() = Prefers.getInt("exo_http", 1)

    fun putHttp(http: Int) {
        Prefers.put("exo_http", http)
    }

    val buffer: Int
        get() = min(
            max(
                Prefers.getInt("exo_buffer").toDouble(),
                1.0
            ), 15.0
        ).toInt()

    fun putBuffer(buffer: Int) {
        Prefers.put("exo_buffer", buffer)
    }

    val flag: Int
        get() = Prefers.getInt("flag")

    fun putFlag(flag: Int) {
        Prefers.put("flag", flag)
    }

    val episode: Int
        get() = Prefers.getInt("episode")

    fun putEpisode(episode: Int) {
        Prefers.put("episode", episode)
    }

    val background: Int
        get() = Prefers.getInt("background", 2)

    fun putBackground(background: Int) {
        Prefers.put("background", background)
    }

    val rtsp: Int
        get() = Prefers.getInt("rtsp")

    fun putRtsp(rtsp: Int) {
        Prefers.put("rtsp", rtsp)
    }

    val siteMode: Int
        get() = Prefers.getInt("site_mode", 1)

    fun putSiteMode(mode: Int) {
        Prefers.put("site_mode", mode)
    }

    val syncMode: Int
        get() = Prefers.getInt("sync_mode")

    fun putSyncMode(mode: Int) {
        Prefers.put("sync_mode", mode)
    }

    val isBootLive: Boolean
        get() = Prefers.getBoolean("boot_live")

    fun putBootLive(boot: Boolean) {
        Prefers.put("boot_live", boot)
    }

    val isInvert: Boolean
        get() = Prefers.getBoolean("invert")

    fun putInvert(invert: Boolean) {
        Prefers.put("invert", invert)
    }

    val isAcross: Boolean
        get() = Prefers.getBoolean("across", true)

    fun putAcross(across: Boolean) {
        Prefers.put("across", across)
    }

    val isChange: Boolean
        get() = Prefers.getBoolean("change", true)

    fun putChange(change: Boolean) {
        Prefers.put("change", change)
    }

    val update: Boolean
        get() = Prefers.getBoolean("update", true)

    fun putUpdate(update: Boolean) {
        Prefers.put("update", update)
    }

    val isPlayWithOthers: Boolean
        get() = Prefers.getBoolean("play_with_others", false)

    fun putPlayWithOthers(play: Boolean) {
        Prefers.put("play_with_others", play)
    }

    val isDanmu: Boolean
        get() = Prefers.getBoolean("danmu")

    fun putDanmu(danmu: Boolean) {
        Prefers.put("danmu", danmu)
    }

    val isDanmuLoad: Boolean
        get() = Prefers.getBoolean("danmu_load", true)

    fun putDanmuLoad(load: Boolean) {
        Prefers.put("danmu_load", load)
    }

    val danmuSpeed: Int
        get() = min(
            max(
                Prefers.getInt("danmu_speed", 2).toDouble(), 0.0
            ), 3.0
        ).toInt()

    fun putDanmuSpeed(speed: Int) {
        Prefers.put("danmu_speed", speed)
    }

    val danmuSize: Float
        get() = min(
            max(
                Prefers.getFloat("danmu_size", 1.0f).toDouble(), 0.6
            ), 2.0
        ).toFloat()

    fun putDanmuSize(size: Float) {
        Prefers.put("danmu_size", size)
    }

    fun getDanmuLine(line: Int): Int {
        return min(
            max(
                Prefers.getInt("danmu_line", line).toDouble(), 1.0
            ), 15.0
        ).toInt()
    }

    fun putDanmuLine(line: Int) {
        Prefers.put("danmu_line", line)
    }

    val danmuAlpha: Int
        get() = min(
            max(
                Prefers.getInt("danmu_alpha", 90).toDouble(), 10.0
            ), 100.0
        ).toInt()

    fun putDanmuAlpha(alpha: Int) {
        Prefers.put("danmu_alpha", alpha)
    }

    val isCaption: Boolean
        get() = Prefers.getBoolean("caption")

    fun putCaption(caption: Boolean) {
        Prefers.put("caption", caption)
    }

    val isTunnel: Boolean
        get() = Prefers.getBoolean("exo_tunnel")

    fun putTunnel(tunnel: Boolean) {
        Prefers.put("exo_tunnel", tunnel)
    }

    val backupMode: Int
        get() = Prefers.getInt("backup_mode", 1)

    fun putBackupMode(auto: Int) {
        Prefers.put("backup_mode", auto)
    }

    val isZhuyin: Boolean
        get() = Prefers.getBoolean("zhuyin")

    fun putZhuyin(zhuyin: Boolean) {
        Prefers.put("zhuyin", zhuyin)
    }

    val thumbnail: Float
        get() = 0.3f * quality + 0.4f

    val isBackgroundOff: Boolean
        get() = background == 0

    val isBackgroundOn: Boolean
        get() = background == 1 || background == 2

    val isBackgroundPiP: Boolean
        get() = background == 2

    fun hasCaption(): Boolean {
        val context = ContextProvider.context as Context
        return Intent(Settings.ACTION_CAPTIONING_SETTINGS).resolveActivity(context.packageManager) != null
    }

    val isDisplayTime: Boolean
        get() = Prefers.getBoolean("display_time", false)

    fun putDisplayTime(display: Boolean) {
        Prefers.put("display_time", display)
    }

    val isDisplaySpeed: Boolean
        get() = Prefers.getBoolean("display_speed", false)

    fun putDisplaySpeed(display: Boolean) {
        Prefers.put("display_speed", display)
    }

    val isDisplayDuration: Boolean
        get() = Prefers.getBoolean("display_duration", false)

    fun putDisplayDuration(display: Boolean) {
        Prefers.put("display_duration", display)
    }

    val isDisplayMiniProgress: Boolean
        get() = Prefers.getBoolean("display_mini_progress", false)

    fun putDisplayMiniProgress(display: Boolean) {
        Prefers.put("display_mini_progress", display)
    }

    val isDisplayVideoTitle: Boolean
        get() = Prefers.getBoolean("display_video_title", false)

    fun putDisplayVideoTitle(display: Boolean) {
        Prefers.put("display_video_title", display)
    }

    val playSpeed: Float
        get() = Prefers.getFloat("play_speed", 1.0f)

    fun putPlaySpeed(speed: Float) {
        Prefers.put("play_speed", speed)
    }

    fun putFullscreenMenuKey(key: Int) {
        Prefers.put("fullscreen_menu_key", key)
    }

    val fullscreenMenuKey: Int
        get() = Prefers.getInt("fullscreen_menu_key", 0)

    fun putHomeMenuKey(key: Int) {
        Prefers.put("home_menu_key", key)
    }

    val homeMenuKey: Int
        get() = Prefers.getInt("home_menu_key", 0)

    val isHomeSiteLock: Boolean
        get() = Prefers.getBoolean("home_site_lock", false)

    fun putHomeSiteLock(lock: Boolean) {
        Prefers.put("home_site_lock", lock)
    }

    val isIncognito: Boolean
        get() = Prefers.getBoolean("incognito")

    fun putIncognito(incognito: Boolean) {
        Prefers.put("incognito", incognito)
    }

    fun putSmallWindowBackKey(key: Int) {
        Prefers.put("small_window_back_key", key)
    }

    val smallWindowBackKey: Int
        get() = Prefers.getInt("small_window_back_key", 0)

    fun putHomeDisplayName(change: Boolean) {
        Prefers.put("home_display_name", change)
    }

    val isHomeDisplayName: Boolean
        get() = Prefers.getBoolean("home_display_name", false)

    val isAggregatedSearch: Boolean
        get() = Prefers.getBoolean("aggregated_search", false)

    fun putAggregatedSearch(search: Boolean) {
        Prefers.put("aggregated_search", search)
    }

    fun putHomeUI(key: Int) {
        Prefers.put("home_ui", key)
    }

    val homeUI: Int
        get() = Prefers.getInt("home_ui", 1)

    fun putHomeButtons(buttons: String?) {
        Prefers.put("home_buttons", buttons)
    }

    fun getHomeButtons(defaultValue: String?): String {
        return Prefers.getString("home_buttons", defaultValue)
    }

    fun putHomeButtonsSorted(buttons: String?) {
        Prefers.put("home_buttons_sorted", buttons)
    }

    fun getHomeButtonsSorted(defaultValue: String?): String {
        return Prefers.getString("home_buttons_sorted", defaultValue)
    }

    val isHomeHistory: Boolean
        get() = Prefers.getBoolean("home_history", true)

    fun putHomeHistory(show: Boolean) {
        Prefers.put("home_history", show)
    }

    fun putConfigCache(key: Int) {
        Prefers.put("config_cache", key)
    }

    val configCache: Int
        get() = min(Prefers.getInt("config_cache", 0).toDouble(), 2.0)
            .toInt()

    fun putLanguage(key: Int) {
        Prefers.put("language", key)
    }

    val language: Int
        get() = Prefers.getInt("language", LanguageUtil.locale())

    fun putParseWebView(key: Int) {
        Prefers.put("parse_webview", key)
    }

    val parseWebView: Int
        get() = Prefers.getInt("parse_webview", 0)

    val isSiteSearch: Boolean
        get() = Prefers.getBoolean("site_search", false)

    fun putSiteSearch(search: Boolean) {
        Prefers.put("site_search", search)
    }

    val isRemoveAd: Boolean
        get() = Prefers.getBoolean("remove_ad", false)

    fun putRemoveAd(remove: Boolean) {
        Prefers.put("remove_ad", remove)
    }

    val thunderCacheDir: String
        get() = Prefers.getString("thunder_cache_dir", "")

    fun putThunderCacheDir(dir: String?) {
        Prefers.put("thunder_cache_dir", dir)
    }

}
