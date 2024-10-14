package com.calvin.box.movie

import Greeting
import SERVER_PORT
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    routing {
        staticFiles("/files", File("/var/www/html")) {

        }
        staticResources("/resources", "assets"){

        }
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        get("/data.json") {
            val inputStream: InputStream? = javaClass.getResourceAsStream("/data.json")

            // 检查资源是否存在
            if (inputStream != null) {
                // 读取文件内容
                val jsonData = inputStream.bufferedReader().use { it.readText() }

                // 反序列化 JSON 数据
                val response = Json.decodeFromString(ApiResponse.serializer(), jsonData)

                // 返回响应
                call.respond(response)
            } else {
                // 资源未找到，返回 404 错误
                call.respondText("Resource not found", status = io.ktor.http.HttpStatusCode.NotFound)
            }
        }
    }

}

@Serializable
data class ApiResponse(val message: String, val status: Int)