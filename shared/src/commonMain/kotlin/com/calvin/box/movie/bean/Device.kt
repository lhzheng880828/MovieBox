package com.calvin.box.movie.bean

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.calvin.box.movie.utils.UrlUtil
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.calvin.box.movie.db.MoiveDatabase
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

@Serializable
@Entity(indices = [Index(value = ["uuid", "name"], unique = true)])
data class Device(
    @PrimaryKey(autoGenerate = true)
    @SerialName("id")
    var id: Int = 0,

    @SerialName("uuid")
    var uuid: String = "",

    @SerialName("name")
    var name: String = "",

    @SerialName("ip")
    var ip: String = "",

    @SerialName("type")
    var type: Int = 0
) {

    val isLeanback: Boolean
        get() = type == 0

    val isMobile: Boolean
        get() = type == 1

    val isDLNA: Boolean
        get() = type == 2

    val isApp: Boolean
        get() = isLeanback || isMobile

    val host: String
        get() = if (isDLNA) uuid else UrlUtil.host(ip)

    suspend fun save(): Device {
       MoiveDatabase.get().getDeviceDao().insertOrUpdate(this)
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Device) return false
        return uuid == other.uuid && name == other.name
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

   // override fun toString(): String = App.gson().toJson(this)

    companion object {
        fun get(): Device {
            return Device(
                uuid = "123456abc",//Util.getAndroidId(),
                name = "Huawei",//Util.getDeviceName(),
                ip = "192.168.125.124",//Server.get().getAddress(),
                type = 0,//Product.getDeviceType()
            )
        }

       /* fun get(item: org.fourthline.cling.model.meta.Device<*, *, *>): Device {
            return Device(
                uuid = item.identity.udn.identifierString,
                name = item.details.friendlyName,
                type = 2
            )
        }*/

        fun objectFrom(str: String): Device {
            return Json.decodeFromString(str)
        }

        fun getAll(): List<Device> {
          //  return AppDatabase.get().getDeviceDao().findAll()
          return  mutableListOf<Device>()
        }

        fun delete() {
           runBlocking { MoiveDatabase.get().getDeviceDao().delete()}
        }
    }
}