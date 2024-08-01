package com.calvin.box.movie.nano.process

import com.calvin.box.movie.nano.Nano
import com.calvin.box.movie.utils.FileUtil
import com.github.catvod.utils.Path
import com.google.common.net.HttpHeaders
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Arrays
import java.util.Date
import java.util.Locale

class Local : Process {
    private val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())

    override fun isRequest(session: NanoHTTPD.IHTTPSession, path: String): Boolean {
        return path.startsWith("/file") || path.startsWith("/upload") || path.startsWith("/newFolder") || path.startsWith(
            "/delFolder"
        ) || path.startsWith("/delFile")
    }

    override fun doResponse(
        session: NanoHTTPD.IHTTPSession,
        path: String,
        files: Map<String, String>
    ): NanoHTTPD.Response {
        if (path.startsWith("/file")) return getFile(session.getHeaders(), path)
        if (path.startsWith("/upload")) return upload(session.getParms(), files)
        if (path.startsWith("/newFolder")) return newFolder(session.getParms())
        if (path.startsWith("/delFolder") || path.startsWith("/delFile")) return delFolder(session.getParms())
        return Nano.error("error url")
    }

    private fun getFile(headers: Map<String, String>, path: String): NanoHTTPD.Response {
        try {
            val file = Path.root(path.substring(5))
            if (file.isDirectory) return getFolder(file)
            if (file.isFile) return getFile(headers, file, NanoHTTPD.getMimeTypeForFile(path))
            throw FileNotFoundException()
        } catch (e: Exception) {
            return Nano.error(e.message)
        }
    }

    private fun upload(
        params: Map<String, String>,
        files: Map<String, String>
    ): NanoHTTPD.Response {
        val path = params["path"]
        for (k in files.keys) {
            val fn = params[k]
            val temp = File(files[k])
            if (fn!!.lowercase(Locale.getDefault()).endsWith(".zip")) FileUtil.extractZip(
                temp,
                Path.root(path)
            )
            else Path.copy(temp, Path.root(path, fn))
        }
        return Nano.success()
    }

    private fun newFolder(params: Map<String, String>): NanoHTTPD.Response {
        val path = params["path"]
        val name = params["name"]
        Path.root(path, name).mkdirs()
        return Nano.success()
    }

    private fun delFolder(params: Map<String, String>): NanoHTTPD.Response {
        val path = params["path"]
        Path.clear(Path.root(path))
        return Nano.success()
    }

    private fun getFolder(root: File): NanoHTTPD.Response {
        val list = root.listFiles()
        val info = JsonObject()
        info.addProperty(
            "parent",
            if (root == Path.root()) "." else root.parent.replace(Path.rootPath(), "")
        )
        if (list == null || list.size == 0) {
            info.add("files", JsonArray())
            return Nano.success(info.toString())
        }
        Arrays.sort(list) { o1: File, o2: File ->
            if (o1.isDirectory && o2.isFile) return@sort -1
            if (o1.isFile && o2.isDirectory) 1 else o1.name.compareTo(o2.name)
        }
        val files = JsonArray()
        info.add("files", files)
        for (file in list) {
            val obj = JsonObject()
            obj.addProperty("name", file.name)
            obj.addProperty("path", file.absolutePath.replace(Path.rootPath(), ""))
            obj.addProperty("time", format.format(Date(file.lastModified())))
            obj.addProperty("dir", if (file.isDirectory) 1 else 0)
            files.add(obj)
        }
        return Nano.success(info.toString())
    }

    @Throws(Exception::class)
    private fun getFile(header: Map<String, String>, file: File, mime: String): NanoHTTPD.Response {
        var startFrom: Long = 0
        var endAt: Long = -1
        var range = header["range"]
        if (range != null) {
            if (range.startsWith("bytes=")) {
                range = range.substring("bytes=".length)
                val minus = range.indexOf('-')
                try {
                    if (minus > 0) {
                        startFrom = range.substring(0, minus).toLong()
                        endAt = range.substring(minus + 1).toLong()
                    }
                } catch (ignored: NumberFormatException) {
                }
            }
        }
        val res: NanoHTTPD.Response
        val fileLen = file.length()
        val ifRange = header["if-range"]
        val etag =
            Integer.toHexString((file.absolutePath + file.lastModified() + "" + file.length()).hashCode())
        val headerIfRangeMissingOrMatching = (ifRange == null || etag == ifRange)
        val ifNoneMatch = header["if-none-match"]
        val headerIfNoneMatchPresentAndMatching =
            ifNoneMatch != null && ("*" == ifNoneMatch || ifNoneMatch == etag)
        if (headerIfRangeMissingOrMatching && range != null && startFrom >= 0 && startFrom < fileLen) {
            if (headerIfNoneMatchPresentAndMatching) {
                res = NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.NOT_MODIFIED,
                    mime,
                    ""
                )
                res.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes")
                res.addHeader(HttpHeaders.ETAG, etag)
            } else {
                if (endAt < 0) endAt = fileLen - 1
                var newLen = endAt - startFrom + 1
                if (newLen < 0) newLen = 0
                val fis = FileInputStream(file)
                fis.skip(startFrom)
                res = NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.PARTIAL_CONTENT,
                    mime,
                    fis,
                    newLen
                )
                res.addHeader(HttpHeaders.CONTENT_LENGTH, newLen.toString() + "")
                res.addHeader(HttpHeaders.CONTENT_RANGE, "bytes $startFrom-$endAt/$fileLen")
                res.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes")
                res.addHeader(HttpHeaders.ETAG, etag)
            }
        } else {
            if (headerIfRangeMissingOrMatching && range != null && startFrom >= fileLen) {
                res = NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.RANGE_NOT_SATISFIABLE,
                    NanoHTTPD.MIME_PLAINTEXT,
                    ""
                )
                res.addHeader(HttpHeaders.CONTENT_RANGE, "bytes */$fileLen")
                res.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes")
                res.addHeader(HttpHeaders.ETAG, etag)
            } else if (headerIfNoneMatchPresentAndMatching && (!headerIfRangeMissingOrMatching || range == null)) {
                res = NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.NOT_MODIFIED,
                    mime,
                    ""
                )
                res.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes")
                res.addHeader(HttpHeaders.ETAG, etag)
            } else {
                res = NanoHTTPD.newFixedLengthResponse(
                    NanoHTTPD.Response.Status.OK, mime, FileInputStream(file), file.length().toInt()
                        .toLong()
                )
                res.addHeader(HttpHeaders.CONTENT_LENGTH, fileLen.toString() + "")
                res.addHeader(HttpHeaders.ACCEPT_RANGES, "bytes")
                res.addHeader(HttpHeaders.ETAG, etag)
            }
        }
        return res
    }
}
