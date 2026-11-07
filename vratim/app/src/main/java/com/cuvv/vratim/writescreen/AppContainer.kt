package com.cuvv.vratim.writescreen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.cuvv.vratim.Config
import com.cuvv.vratim.EntityItem
import com.cuvv.vratim.Text

const val SCREEN_READ_LIST = 0
const val SCREEN_SEND_STATUS = 1
const val SCREEN_EDIT_GLOBAL = 2
const val SCREEN_EDIT_ENTITY = 3
const val SCREEN_PASTE_CONFIG = 4;

@Composable
fun AppContainer(
    config: Config,
    serverStatuses: ByteArray,
    actionLog: String,
    onRefreshTriggered: () -> Unit,
    onJsonPasteTriggered: (rawJson: String) -> Unit,
    onResetActionLog: () -> Unit,
    onSendPacketTriggered: (entityId: Int, status: Int) -> Unit,
    onGlobalConfigSaved: (url: String, get: String, post: String, lang: String) -> Unit,
    onEntityMappingsSaved: (entityId: Int, mappings: List<Text>) -> Unit,
) {
    var currentScreen by rememberSaveable { mutableStateOf(SCREEN_READ_LIST) }

    var activeEntityIdForWrite by rememberSaveable { mutableStateOf(-1) }
    var activeEntityIdForEdit by rememberSaveable { mutableStateOf(-1) }

    val langMap = remember(config.currentLang, config.languages) {
        config.languages.firstOrNull { it["lang"] == config.currentLang } ?: emptyMap()
    }

    SystemBackground {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            TopHeader(
                langMap = langMap,
                onMenuActionClick = { actionKey ->
                    when (actionKey) {
                        "ACTION_GLOBAL" -> {
                            currentScreen = SCREEN_EDIT_GLOBAL
                        }
                        "ACTION_PASTE" -> {
                            currentScreen = SCREEN_PASTE_CONFIG
                        }
                        "ACTION_REFRESH" -> {
                            onRefreshTriggered()
                        }
                        "ACTION_OPEN_SEND_FREE" -> {
                            activeEntityIdForWrite = -1
                            currentScreen = SCREEN_SEND_STATUS
                        }
                    }
                }
            )

            Box(modifier = Modifier.weight(1f)) {
                when (currentScreen) {
                    SCREEN_READ_LIST -> {
                        EntityReadList(
                            config = config,
                            serverStatuses = serverStatuses,
                            onEntityClick = { entityId ->
                                activeEntityIdForEdit = entityId
                                currentScreen = SCREEN_EDIT_ENTITY
                            },
                            onRefreshClick = onRefreshTriggered
                        )
                    }

                    SCREEN_SEND_STATUS -> {
                        SendStatus(
                            config = config,
                            initialEntityId = activeEntityIdForWrite,
                            langMap = langMap,
                            onBackClick = { currentScreen = SCREEN_READ_LIST },
                            onSendClick = { entityId, status ->
                                onSendPacketTriggered(entityId, status)
                                currentScreen = SCREEN_READ_LIST
                            }
                        )
                    }

                    SCREEN_EDIT_GLOBAL -> {
                        EditGlobalEntity(
                            config = config,
                            langMap = langMap,
                            onBackClick = { currentScreen = SCREEN_READ_LIST },
                            onSaveClick = { url, get, post, lang ->
                                onGlobalConfigSaved(url, get, post, lang)
                                currentScreen = SCREEN_READ_LIST
                            }
                        )
                    }

                    SCREEN_EDIT_ENTITY -> {
                        EditEntity(
                            config = config,
                            editingEntityId = activeEntityIdForEdit,
                            langMap = langMap,
                            onBackClick = { currentScreen = SCREEN_READ_LIST },
                            onSaveClick = { entityId, updatedMappings ->
                                onEntityMappingsSaved(entityId, updatedMappings)
                                currentScreen = SCREEN_READ_LIST
                            }
                        )
                    }

                    SCREEN_PASTE_CONFIG -> {
                        PasteConfig(
                            langMap = langMap,
                            actionLog = actionLog,
                            onBackClick = {
                                onResetActionLog()
                                currentScreen = SCREEN_READ_LIST
                            },
                            onSaveClick = { rawJson ->
                                onJsonPasteTriggered(rawJson)
                            }
                        )
                    }
                }
            }
        }
    }
}