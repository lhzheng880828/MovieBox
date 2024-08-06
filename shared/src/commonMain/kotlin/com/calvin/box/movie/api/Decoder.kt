import com.calvin.box.movie.PlatformDecoder
import com.calvin.box.movie.utils.UrlUtil
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
//import io.ktor.client.engine.okhttp.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import kotlin.text.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.text.Regex

object Decoder {

  //  private val client = HttpClient(OkHttp)
  private val decoder = PlatformDecoder()


    fun getJson(url: String): String  {
        Napier.d{"#getJson invoke, url: $url"}
        val key = if (url.contains(";")) url.split(";")[2] else ""
        val parsedUrl = if (url.contains(";")) url.split(";")[0] else url
        var data = decoder.loadJsonData(parsedUrl)
        if (data.isEmpty()) throw Exception()
        if (isValidJson(data)) return fix(parsedUrl, data)
        if (data.contains("**")) data = base64(data)
        if (data.startsWith("2423")) data = cbc(data)
        if (key.isNotEmpty()) data = ecb(data, key)
        data = extractJsonString(data)
        return fix(parsedUrl, data)
    }

    private fun extractJsonString(input: String): String {
        /*val stringBuilder = StringBuilder()
        var openBraces = 0
        var insideJson = false

        for (char in input) {
            if (char == '{') {
                openBraces++
                insideJson = true
            }

            if (insideJson) {
                stringBuilder.append(char)
            }

            if (char == '}') {
                openBraces--
                if (openBraces == 0 && insideJson) {
                    break
                }
            }
        }

        val jsonString = stringBuilder.toString()
        return if (jsonString.isNotEmpty() && openBraces == 0) {
            jsonString
        } else {
            "No valid JSON object found"
        }*/
        // 移除单行注释
        val noSingleLineComments = input.replace(Regex("(?<!:)//.*"), "")
        // 移除多行注释
        val noComments = noSingleLineComments.replace(Regex("/\\*[\\s\\S]*?\\*/"), "")
        return noComments
    }

    private fun fix(url: String, data: String): String {
        var fixedUrl = url
        var fixedData = data
        if (url.startsWith("file") || url.startsWith("assets")) fixedUrl = UrlUtil.convert(url)
        if (data.contains("../")) fixedData = fixedData.replace("../", UrlUtil.resolve(fixedUrl, "../"))
        if (data.contains("./")) fixedData = fixedData.replace("./", UrlUtil.resolve(fixedUrl, "./"))
        return fixedData
    }

    fun getExt(ext: String): String = runBlocking {
        try {
            base64(decoder.loadJsonData(ext.substring(4)))
        } catch (ignored: Exception) {
            ""
        }
    }

    private fun ecb(data: String, key: String): String {
        return decoder.ecbDecrypt(data, key)
    }

    private fun cbc(data: String): String {
        val decode = String(hexToByte(data)).toLowerCase()
        val key = padEnd(decode.substring(decode.indexOf("$#") + 2, decode.indexOf("#$")))
        val iv = padEnd(decode.substring(decode.length - 13))
        return decoder.cbcDecrypt(data, key, iv)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun base64(data: String): String {
        val extract = extract(data)
        return if (extract.isEmpty()) data else String(Base64.decode(extract))
    }

    private fun extract(data: String): String {
        val matcher = Regex("[A-Za-z0-9]{8}\\*\\*").find(data)
        return if (matcher != null) data.substring(data.indexOf(matcher.value) + 10) else ""
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

    private fun isValidJson(data: String): Boolean {
        return try {
            Json.decodeFromString<Map<String, Any>>(data)
            true
        } catch (e: Exception) {
            false
        }
    }

    object Character {
         fun digit(ch: Char, radix: Int): Int {
            return when (ch) {
                in '0'..'9' -> ch - '0'
                in 'A'..'Z' -> ch - 'A' + 10
                in 'a'..'z' -> ch - 'a' + 10
                else -> -1
            }.takeIf { it < radix } ?: -1
        }
    }

}
