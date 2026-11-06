package com.cuvv.vratim

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

object ConfigManager {
    private const val FILE_NAME = "app-config.json"

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        coerceInputValues = true
    }

    suspend fun loadConfig(context: Context): Config = withContext(Dispatchers.IO) {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            val defaultCfg = createDefaultConfig()
            saveConfigInternal(file, defaultCfg)
            return@withContext defaultCfg
        }

        return@withContext try {
            val jsonString = file.readText()
            jsonParser.decodeFromString<Config>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            createDefaultConfig()
        }
    }

    suspend fun updateConfigFromText(context: Context, rawJson: String): Boolean = withContext(Dispatchers.IO) {
        if (rawJson.isBlank()) return@withContext false
        return@withContext try {
            val parsedConfig = jsonParser.decodeFromString<Config>(rawJson)

            if (parsedConfig.isValid()) {
                val file = File(context.filesDir, FILE_NAME)
                saveConfigInternal(file, parsedConfig)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun updateUrl(context: Context, newUrl: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val currentConfig = loadConfig(context)
            val updatedConfig = currentConfig.copy(url = newUrl)

            val file = File(context.filesDir, FILE_NAME)
            saveConfigInternal(file, updatedConfig)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    private fun saveConfigInternal(file: File, config: Config) {
        val jsonString = jsonParser.encodeToString(Config.serializer(), config)
        file.bufferedWriter().use { writer ->
            writer.write(jsonString)
        }
    }

    private fun createDefaultConfig(): Config {
        val defaultEntities = listOf(
            EntityItem(
                name = "Сущность 1",
                entityId = 1,
                texts = listOf(
                    Text(0, "Тест Выкл"),
                    Text(1, "Тест Вкл")
                )
            )
        )

        val defaultLangs = listOf(
            mapOf(
                "lang" to "ru",
                "title" to "Панель управления",
                "btn_send" to "Отправить",
                "btn_back" to "Назад",
                "btn_save" to "Сохранить изменения",
                "menu_edit" to "Редактировать настройки",
                "menu_load" to "Загрузить настройки",
                "menu_paste" to "Вставить настройки",
                "menu_lang" to "Язык",
                "menu_refresh" to "Обновить статус",
                "global_settings" to "Глобальный"
            ),
            mapOf(
                "lang" to "en",
                "title" to "Control Panel",
                "btn_send" to "Send",
                "btn_back" to "Back",
                "btn_save" to "Save Changes",
                "menu_edit" to "Edit Settings",
                "menu_load" to "Load Settings",
                "menu_paste" to "Paste Settings",
                "menu_lang" to "Language",
                "menu_refresh" to "Refresh Status",
                "global_settings" to "Global"
            )
        )

        return Config(
            url = "https://localhost:1010",
            getPrefix = "/api/v1/pull/client",
            postPrefix = "/api/v1/push",
            currentLang = "ru",
            entities = defaultEntities,
            languages = defaultLangs
        )
    }
}