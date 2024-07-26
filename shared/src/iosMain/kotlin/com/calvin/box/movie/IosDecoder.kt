package com.calvin.box.movie

import io.ktor.utils.io.core.*
import platform.Foundation.*
import platform.Security.*

class IosDecoder : AesDecoder {
    override fun ecbDecrypt(data: String, key: String): String {
        val spec = padEnd(key).toByteArray()
        val encryptedData = hexToByte(data)
        val decryptedData = decryptAesEcb(encryptedData, spec)
        return NSString.create(decryptedData, NSUTF8StringEncoding).toString()
    }

    override fun cbcDecrypt(data: String, key: String, iv: String): String {
        val keySpec = padEnd(key).toByteArray()
        val ivSpec = padEnd(iv).toByteArray()
        val encryptedData = hexToByte(data)
        val decryptedData = decryptAesCbc(encryptedData, keySpec, ivSpec)
        return NSString.create(decryptedData, NSUTF8StringEncoding).toString()
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

    private fun decryptAesEcb(data: ByteArray, key: ByteArray): ByteArray {
        return data.withData { encryptedData ->
            key.withData { keyData ->
                decryptAes(encryptedData, keyData, kCCOptionECBMode, null)
            }
        }
    }

    private fun decryptAesCbc(data: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        return data.withData { encryptedData ->
            key.withData { keyData ->
                iv.withData { ivData ->
                    decryptAes(encryptedData, keyData, 0, ivData)
                }
            }
        }
    }

    private inline fun <R> ByteArray.withData(block: (NSData) -> R): R {
        return this.usePinned { pinned ->
            val nsData = NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
            block(nsData)
        }
    }

    private fun decryptAes(encryptedData: NSData, keyData: NSData, options: Int, ivData: NSData?): ByteArray {
        val decryptedData = NSMutableData.create(length = encryptedData.length.toInt())!!
        val dataOutAvailable = decryptedData.length.toULong()
        val dataOutMoved = memScoped {
            alloc<ULongVar>()
        }

        val status = CCCrypt(
            kCCDecrypt,
            kCCAlgorithmAES,
            options.toUInt() or kCCOptionPKCS7Padding,
            keyData.bytes, kCCKeySizeAES128,
            ivData?.bytes,
            encryptedData.bytes, encryptedData.length,
            decryptedData.mutableBytes, dataOutAvailable,
            dataOutMoved.ptr
        )

        if (status != kCCSuccess) {
            throw RuntimeException("Error decrypting AES: $status")
        }

        return decryptedData.subdataWithRange(NSMakeRange(0, dataOutMoved.value.toInt())).toByteArray()
    }
}
