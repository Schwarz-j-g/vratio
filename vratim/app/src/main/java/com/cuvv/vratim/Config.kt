package com.cuvv.vratim

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Text (
    @SerialName("status")
    val status: Int,

    @SerialName("text")
    val text: String
)

@Serializable
data class EntityItem(
    @SerialName("name")
    val name: String,

    @SerialName("id")
    val entityId: Int? = null,

    @SerialName("texts")
    val texts: List<Text> = emptyList()
) {
    inline fun getActiveId(fallback: Int): Int {
        return entityId ?: fallback
    }
}

@Serializable
data class Config (
    @SerialName("url")
    val url: String,

    @SerialName("get")
    val getPrefix: String,

    @SerialName("post")
    val postPrefix: String,

    @SerialName("lang")
    val currentLang: String = "en",

    @SerialName("entities")
    val entities: List<EntityItem>,

    @SerialName("langs")
    val languages: List<Map<String, String>> = emptyList()
) {

    fun isValid(): Boolean {
        if (entities.isEmpty() || entities.size > 256) return false

        for (i in entities.indices) {
            val entity = entities[i]
            val id = entity.getActiveId(i)

            if (id < 0 || id > 255) return false

            val texts = entity.texts
            for (j in texts.indices) {
                if (texts[j].status < 0 || texts[j].status > 255) return false
            }
        }

        return true
    }
}

