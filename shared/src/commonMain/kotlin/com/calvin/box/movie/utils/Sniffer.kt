package com.calvin.box.movie.utils


object Sniffer {
    val CLICKER = Regex("\\[a=cr:(\\{.*?\\})\\/](.*?)\\[\\/a]")

    val AI_PUSH = Regex("(http|https|rtmp|rtsp|smb|ftp|thunder|magnet|ed2k|mitv|tvbox-xg|jianpian|video):[^\\s]+", RegexOption.MULTILINE)

    val SNIFFER = Regex("http((?!http).){12,}?\\.(m3u8|mp4|mkv|flv|mp3|m4a|aac|mpd)\\?.*|http((?!http).){12,}\\.(m3u8|mp4|mkv|flv|mp3|m4a|aac|mpd)|http((?!http).)*?video/tos*|http((?!http).)*?obj/tos*")

    val THUNDER = Regex("(magnet|thunder|ed2k):.*")

    fun isThunder(url: String): Boolean {
        return THUNDER.containsMatchIn(url) || isTorrent(url)
    }

    fun isTorrent(url: String): Boolean {
        return !url.startsWith("magnet") && url.split(";")[0].endsWith(".torrent")
    }

    fun isVideoFormat(url:String?):Boolean{
        return false
    }
}