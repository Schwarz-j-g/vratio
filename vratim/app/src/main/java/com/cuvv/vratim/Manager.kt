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

    fun importConfigFromText(rawJson: String): Boolean {
        var success = false
        scope.launch {
            success = ConfigManager.updateConfigFromText(context, rawJson)
            if (success) {
                val newConfig = ConfigManager.loadConfig(context)
                _state.update { it.copy(config = newConfig) }
                refreshServerStatuses()
            }
        }
        return success
    }
}
