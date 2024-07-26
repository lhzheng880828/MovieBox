package com.calvin.box.movie

object Constant {
    //快進時間單位
    const val INTERVAL_SEEK: Int = 10 * 1000

    //控件隱藏時間
    const val INTERVAL_HIDE: Int = 5 * 1000

    //網路偵測間隔
    const val INTERVAL_TRAFFIC: Int = 500

    //點播爬蟲時間
    const val TIMEOUT_VOD: Int = 30 * 1000

    //直播爬蟲時間
    const val TIMEOUT_LIVE: Int = 30 * 1000

    //節目爬蟲時間
    const val TIMEOUT_EPG: Int = 5 * 1000

    //節目爬蟲時間
    const val TIMEOUT_XML: Int = 15 * 1000

    //播放超時時間
    const val TIMEOUT_PLAY: Int = 15 * 1000

    //解析預設時間
    const val TIMEOUT_PARSE_DEF: Int = 15 * 1000

    //嗅探超時時間
    const val TIMEOUT_PARSE_WEB: Int = 15 * 1000

    //直播解析時間
    const val TIMEOUT_PARSE_LIVE: Int = 10 * 1000

    //同步超時時間
    const val TIMEOUT_SYNC: Int = 2 * 1000

    //传送超時時間
    const val TIMEOUT_TRANSMIT: Int = 60 * 1000

    //搜尋線程數量
    const val THREAD_POOL: Int = 5
}
