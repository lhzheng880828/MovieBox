package com.calvin.box.movie.bean


/*import android.text.TextUtils
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import com.fongmi.android.tv.server.Server
import com.github.catvod.utils.Util*/
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
//import java.util.*

@Serializable
data class Drm(
    @SerialName("key") private val key: String = "",
    @SerialName("type") private val type: String = ""
) {



   /* private fun getUUID(): UUID {
        return when {
            type.contains("playready") -> C.PLAYREADY_UUID
            type.contains("widevine") -> C.WIDEVINE_UUID
            type.contains("clearkey") -> C.CLEARKEY_UUID
            else -> C.UUID_NIL
        }
    }*/

    fun getUri(): String {
        return if (key.startsWith("http")) {
            key
        } else {
            //Server.get().getAddress("license/") + Util.base64(getKey(), Util.URL_SAFE)
            ""
        }
    }

    /*fun get(): MediaItem.DrmConfiguration {
        return MediaItem.DrmConfiguration.Builder(getUUID())
            .setLicenseUri(getUri())
            .build()
    }*/

    companion object {
        fun create(key: String, type: String): Drm {
            return Drm(key, type)
        }
    }
}
