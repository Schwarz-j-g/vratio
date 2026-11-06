package com.cuvv.vratim

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object NetworkManager {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = false
            })
        }
    }

    suspend fun sendPacket(fullUrl: String, packet: TransportPacket): Boolean {
        if (fullUrl.isBlank()) return false

        return try {
            val response: HttpResponse = client.post(fullUrl) {
                contentType(ContentType.Application.Json)
                setBody(packet)
            }

            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun fetchPackets(fullUrl: String): ByteArray? {
        if (fullUrl.isBlank()) return null
        return try {
            val response = client.get(fullUrl)
            if (response.status != HttpStatusCode.OK) return null

            val rawJsonText = response.bodyAsText()

            val jsonArray = Json.parseToJsonElement(rawJsonText).jsonArray

            val statusBuffer = ByteArray(256)

            for (i in jsonArray.indices) {
                val packetObj = jsonArray[i].jsonObject

                val payloadArray = packetObj["payload"]?.jsonArray ?: continue
                if (payloadArray.size >= 2) {
                    val id = payloadArray[0].jsonPrimitive.int
                    val status = payloadArray[1].jsonPrimitive.int

                    if (id in 0..255) {
                        statusBuffer[id] = status.toByte()
                    }
                }
            }
            statusBuffer
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}