package com.calvin.box.movie.bean

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class ClearKey(
    val keys: MutableList<Keys>,
    val type: String
) {

    companion object {
        @Throws(Exception::class)
        fun objectFrom(str: String): ClearKey {
            val item = Json.decodeFromString<ClearKey>(str)
            if (item.keys.isEmpty()) throw Exception()
            return item
        }

        fun get(line: String): ClearKey {
            val item = ClearKey(keys = mutableListOf(), type = "temporary")
            item.addKeys(line)
            return item
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun addKeys(line: String) {
        line.split(",").forEach { s ->
            val a = s.split(":")
            val kid = encodeBase64UrlSafe(hex2byte(a[0].trim()))
            val k = encodeBase64UrlSafe(hex2byte(a[1].trim()))
            keys.add(Keys(k = k, kid=kid))
        }
    }

    @Serializable
    data class Keys(
        val kty: String = "oct",
        val k: String,
        val kid: String
    )

    override fun toString(): String {
        return Json.encodeToString(this)
    }

    private fun hex2byte(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Decoder.Character.digit(s[i], 16) shl 4) + Decoder.Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun encodeBase64UrlSafe(input: ByteArray): String {
        // Base64 encode
        var encoded = Base64.encode(input)

        // Replace + with - and / with _
        encoded = encoded.replace('+', '-').replace('/', '_')

        // Remove padding =
        return encoded.trimEnd('=')
    }
}
