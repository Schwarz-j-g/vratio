package com.cuvv.vratim


import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

data class AppState(
    val config: Config? = null,
    val serverStatuses: ByteArray = ByteArray(256)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AppState
        if (config != other.config) return false
        return serverStatuses.contentEquals(other.serverStatuses)
    }

    override fun hashCode(): Int {
        var result = config?.hashCode() ?: 0
        result = 31 * result + serverStatuses.contentHashCode()
        return result
    }
}

class Manager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        scope.launch {
            val initialConfig = ConfigManager.loadConfig(context)
            _state.update { it.copy(config = initialConfig) }

            refreshServerStatuses()
        }
    }

    fun refreshServerStatuses() {
        val config = _state.value.config ?: return
        scope.launch {
            val fullUrl = "${config.url}${config.getPrefix}"
            val result = withContext(Dispatchers.IO) {
                NetworkManager.fetchPackets(fullUrl)
            }
            if (result != null) {
                _state.update { it.copy(serverStatuses = result) }
            }
        }
    }

    fun sendPacket(entityId: Int, status: Int) {
        val config = _state.value.config ?: return
        scope.launch {
            // Выполняем Zero-allocation сборку 2-байтового кадра
            val packet = TransportPacket.fromEntity(entityId, status)
            val fullUrl = "${config.url}${config.postPrefix}"

            val success = withContext(Dispatchers.IO) {
                NetworkManager.sendPacket(fullUrl, packet)
            }

            if (success) {
                refreshServerStatuses()
            }
        }
    }

    fun saveGlobalConfig(url: String, get: String, post: String, lang: String) {
        val currentConfig = _state.value.config ?: return
        scope.launch {
            val updatedConfig = currentConfig.copy(
                url = url,
                getPrefix = get,
                postPrefix = post,
                currentLang = lang
            )
            val rawJson = kotlinx.serialization.json.Json.encodeToString(Config.serializer(), updatedConfig)
            val success = ConfigManager.updateConfigFromText(context, rawJson)
            if (success) {
                _state.update { it.copy(config = updatedConfig) }
            }
        }
    }

    fun saveEntityMappings(entityId: Int, updatedMappings: List<Text>) {
        val currentConfig = _state.value.config ?: return
        scope.launch {
            val updatedEntities = currentConfig.entities.map { entity ->
                if (entity.getActiveId(currentConfig.entities.indexOf(entity)) == entityId) {
                    entity.copy(texts = updatedMappings)
                } else {
                    entity
                }
            }
            val updatedConfig = currentConfig.copy(entities = updatedEntities)
            val rawJson = kotlinx.serialization.json.Json.encodeToString(Config.serializer(), updatedConfig)
            val success = ConfigManager.updateConfigFromText(context, rawJson)
            if (success) {
                _state.update { it.copy(config = updatedConfig) }
            }
        }
    }

    fun importConfigFromText(rawJson: String, onError: (String) -> Unit) {
        if (rawJson.isBlank()) {
            onError("The clipboard is empty or contains non-text data")
            return
        }

        scope.launch {
            try {
                val jsonParser = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }

                val parsedConfig = jsonParser.decodeFromString<Config>(rawJson)

                if (!parsedConfig.isValid()) {
                    throw IllegalArgumentException("Validation failed: Number of entities > 256, or ID/Status is out of byte")
                }

                val success = ConfigManager.updateConfigFromText(context, rawJson)
                if (success) {
                    val newConfig = ConfigManager.loadConfig(context)
                    _state.update { it.copy(config = newConfig) }
                    refreshServerStatuses()
                } else {
                    throw IllegalStateException("The file system refused to rewrite app-config.json")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                val errorMessage = e.localizedMessage ?: e.message ?: "Unknown JSON parsing error"
                onError("Import failed: $errorMessage")
            }
        }
    }
}
