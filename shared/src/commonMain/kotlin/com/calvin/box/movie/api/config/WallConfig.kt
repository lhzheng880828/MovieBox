package com.calvin.box.movie.api.config

import com.calvin.box.movie.App
import com.calvin.box.movie.bean.Config
import com.calvin.box.movie.di.AppDataContainer
import com.calvin.box.movie.impl.Callback
import com.calvin.box.movie.getSpiderLoader
import kotlin.jvm.JvmStatic

class WallConfig private constructor() {

    private lateinit var config: Config
    private var sync: Boolean = false

    private object Loader {
        val INSTANCE = WallConfig()
    }

    companion object {
        @JvmStatic
        fun get(): WallConfig = Loader.INSTANCE

        @JvmStatic
        fun getUrl(): String = get().getConfig().url

        @JvmStatic
        fun getDesc(): String = get().getConfig().desc

        @JvmStatic
        fun load(config: Config, callback: Callback) {
            get().clear().config(config).load(callback)
        }

        @JvmStatic
        fun refresh(index: Int) {
           /* Setting.putWall(index)
            RefreshEvent.wall()*/
        }
    }

    fun init(appDataContainer: AppDataContainer): WallConfig {
        Config.setDatabase(appDataContainer)
        return config(Config.wall())
    }

    fun config(config: Config): WallConfig {
        this.config = config
        this.sync = config.url == VodConfig.get().getWall()
        return this
    }

    fun clear(): WallConfig {
        return this
    }

    fun getConfig(): Config {
        return config ?: Config.wall()
    }

    fun load(callback: Callback) {
        App.execute {
            loadConfig(callback)
        }
    }

    private fun loadConfig(callback: Callback) {
        try {
            val fileValid = getSpiderLoader().writeWallPaper("wallpaper_" + 0, getUrl())  // write(Path.files("wallpaper_" + 0))
            if (fileValid) {
                refresh(0)
            } else {
                config = Config.find(VodConfig.get().getWall(), 2)
            }
            App.post { callback.success() }
            config.update()
        } catch (e: Throwable) {
            App.post { callback.error(/*Notify.getError(R.string.error_config_parse, e)*/"config parse error") }
            config = Config.find(VodConfig.get().getWall(), 2)
            e.printStackTrace()
        }
    }



    fun needSync(url: String): Boolean {
        return sync || config.url.isEmpty() || url == config.url
    }
}
