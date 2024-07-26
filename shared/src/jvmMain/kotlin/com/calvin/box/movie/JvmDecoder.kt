package com.calvin.box.movie

import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class JvmDecoder : AesDecoder {
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

    override fun getSpider(url: String): Any? {
        TODO("Not yet implemented")
    }

    override fun loadJsonData(url: String): String {
        TODO("Not yet implemented")
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
