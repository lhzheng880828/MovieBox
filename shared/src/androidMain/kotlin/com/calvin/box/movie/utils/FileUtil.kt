package com.calvin.box.movie.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.core.content.FileProvider
import com.calvin.box.movie.App
import com.calvin.box.movie.ContextProvider
import com.calvin.box.movie.impl.Callback
import com.github.catvod.utils.Path
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URLConnection
import java.text.DecimalFormat
import java.util.Enumeration
import java.util.zip.GZIPInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.math.log10
import kotlin.math.pow
object FileUtil {
    fun getWall(index: Int): File {
        return Path.files("wallpaper_$index")
    }

    fun openFile(file: File) {

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val context:Context = ContextProvider.context as Context
        intent.setDataAndType(getShareUri(context, file), getMimeType(file.name))
        (ContextProvider.context as Context).startActivity(intent)
    }

    fun zipFolder(folder: File, zip: File?) {
        try {
            val zipOut = ZipOutputStream(FileOutputStream(zip))
            folderToZip("", folder, zipOut)
            zipOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    private fun folderToZip(parentPath: String, folder: File, zipOut: ZipOutputStream) {
        val listFile = folder.listFiles()
        if(listFile == null || listFile.isEmpty()) return
        for (file in listFile) {
            if (file.isDirectory) {
                folderToZip(parentPath + file.name + "/", file, zipOut)
                continue
            }
            val zipEntry = ZipEntry(parentPath + file.name)
            zipOut.putNextEntry(zipEntry)

            val `in` = FileInputStream(file)
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while ((`in`.read(buffer).also { bytesRead = it }) != -1) {
                zipOut.write(buffer, 0, bytesRead)
            }
            `in`.close()
        }
    }

    fun extractGzip(target: File?, path: File?) {
        val buffer = ByteArray(1024)
        try {
            GZIPInputStream(BufferedInputStream(FileInputStream(target))).use { `is` ->
                BufferedOutputStream(
                    FileOutputStream(path)
                ).use { os ->
                    var read: Int
                    while ((`is`.read(buffer).also { read = it }) != -1) os.write(buffer, 0, read)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun extractZip(target: File?, path: File?) {
        try {
            ZipFile(target).use { zip ->
                val entries: Enumeration<*> = zip.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement() as ZipEntry
                    val out = File(path, entry.name)
                    if (entry.isDirectory) out.mkdirs()
                    else Path.copy(zip.getInputStream(entry), out)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearCache(callback: Callback?) {
        App.execute {
            Path.clear(Path.cache())
            if (callback != null) App.post(callback::success)
        }
    }

    fun getCacheSize():String {
        return byteCountToDisplaySize(getFolderSize(Path.cache()))
    }

    fun getShareUri(path: String): Uri {
        val context:Context = ContextProvider.context as Context
        return getShareUri(context, File(path.replace("file://", "")))
    }

    fun getShareUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )
    }

    private fun getMimeType(fileName: String): String {
        val mimeType = URLConnection.guessContentTypeFromName(fileName)
        return if (TextUtils.isEmpty(mimeType)) "*/*" else mimeType
    }

    private fun getFolderSize(file: File?): Long {
        var size: Long = 0
        if (file == null) return 0
        if (file.isDirectory) for (f in Path.list(file)) size += getFolderSize(f)
        else size = file.length()
        return size
    }

    private fun byteCountToDisplaySize(size: Long): String {
        if (size <= 0) return "0 KB"
        val units = arrayOf("bytes", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }
}
