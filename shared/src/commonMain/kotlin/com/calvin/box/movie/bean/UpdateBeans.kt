package com.calvin.box.movie.bean

/*
 *Author:cl
 *Email:lhzheng@grandstream.cn
 *Date:2024/9/5
 */
sealed class DownloadStatus {
    object Started : DownloadStatus()
    data class Progress(val percentage: Int) : DownloadStatus()
    data class Success(val filePath: String) : DownloadStatus()
    data class Error(val message: String?) : DownloadStatus()
}

data class UpdateInfo(val name: String, val desc: String, val code: Int)

sealed class VersionCheckStatus {
    object Checking : VersionCheckStatus()
    data class NeedUpdate(val name: String, val desc: String) : VersionCheckStatus()
    object NoUpdate : VersionCheckStatus()
}

data class UpdateStatus(
    val updateAvailable: Boolean = false,
    val downloadProgress: Float = 0f,
    val downloadComplete: Boolean = false
)