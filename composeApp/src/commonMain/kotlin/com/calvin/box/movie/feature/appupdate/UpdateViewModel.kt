package com.calvin.box.movie.feature.appupdate


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import cafe.adriel.voyager.core.model.ScreenModel


class UpdateViewModel : ScreenModel {
    var updateAvailable by mutableStateOf(false)
        private set
    var downloadProgress by mutableStateOf(0f)
        private set
    var downloadComplete by mutableStateOf(false)
        private set

   // private val client = OkHttpClient()

    fun checkForUpdates() {
        /*viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://your-api-url.com/check-update")
                .build()

            val response = client.newCall(request).execute()
            val jsonData = response.body?.string()
            val json = JSONObject(jsonData)*/

            updateAvailable = true//json.getBoolean("updateAvailable")

    }

    fun downloadUpdate(/*context: Context*/) {
        /*val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse("https://your-api-url.com/app-update.apk")

        val request = DownloadManager.Request(downloadUri).apply {
            setTitle("App Update")
            setDescription("Downloading the latest version")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app-update.apk")
        }

        val downloadId = downloadManager.enqueue(request)

        viewModelScope.launch(Dispatchers.IO) {
            var downloading = true
            while (downloading) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)
                cursor.moveToFirst()
                val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                downloadProgress = bytesDownloaded.toFloat() / bytesTotal.toFloat()
                cursor.close()
            }
            downloadComplete = true
        }*/
    }

    fun installUpdate(/*context: Context*/) {
       /* val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "app-update.apk")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)*/
    }
}