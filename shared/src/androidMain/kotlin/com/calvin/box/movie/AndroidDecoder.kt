package com.calvin.box.movie

import com.github.catvod.net.HostOkHttp
import com.github.catvod.utils.Asset
import com.github.catvod.utils.Path
import io.github.aakira.napier.Napier
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AndroidDecoder : AesDecoder {
    override fun ecbDecrypt(data: String, key: String): String {
        val spec = SecretKeySpec(padEnd(key).toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, spec)
        return String(cipher.doFinal(hexToByte(data)), StandardCharsets.UTF_8)
    }

    override fun cbcDecrypt(data: String, key: String, iv: String): String {
        val keySpec = SecretKeySpec(padEnd(key).toByteArray(), "AES")
        val ivSpec = IvParameterSpec(padEnd(iv).toByteArray())
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
        val decryptedData = cipher.doFinal(hexToByte(data))
        return String(decryptedData, StandardCharsets.UTF_8)
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun getSpider(url: String): Any? {
        return try {
            val file = Path.jar(url)
            val data = extract(loadJsonData(url.substring(4)))
            if (data.isEmpty()) file else Path.write(file, Base64.decode(data))
        } catch (ignored: Exception) {
            Path.jar(url)
        }
    }
    private fun extract(data: String): String {
        val matcher = Regex("[A-Za-z0-9]{8}\\*\\*").find(data)
        return if (matcher != null) data.substring(data.indexOf(matcher.value) + 10) else ""
    }

    override fun loadJsonData(url: String): String {
        Napier.d { "#loadJsonData, url: $url" }
        return when {
            url.startsWith("file") -> Path.read(url)
            url.startsWith("assets") -> Asset.read(url)
            url.startsWith("http") -> HostOkHttp.string(url)
            else -> ""
        }
    }


    private fun padEnd(key: String): String {
        return key + "0000000000000000".substring(key.length)
    }

    private fun hexToByte(hex: String): ByteArray {
        val length = hex.length
        val byteArray = ByteArray(length / 2)
        var i = 0
        while (i < length) {
            byteArray[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
            i += 2
        }
        return byteArray
    }
}
