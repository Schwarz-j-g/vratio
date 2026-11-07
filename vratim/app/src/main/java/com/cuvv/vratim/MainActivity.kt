package com.cuvv.vratim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.cuvv.vratim.writescreen.AppContainer

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = Manager(applicationContext)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    val appState by manager.state.collectAsState()

                    val currentConfig = appState.config
                    if (currentConfig != null) {

                        AppContainer(
                            config = currentConfig,
                            serverStatuses = appState.serverStatuses,
                            actionLog = appState.actionLog,
                            onRefreshTriggered = { manager.refreshServerStatuses() },
                            onJsonPasteTriggered = { textFromUiField ->
                                manager.importConfigWithLogging(textFromUiField)
                            },
                            onResetActionLog = { manager.resetActionLog() },
                            onSendPacketTriggered = { entityId, status ->
                                manager.sendPacket(entityId, status)
                            },
                            onGlobalConfigSaved = { url, get, post, lang ->
                                manager.saveGlobalConfig(url, get, post, lang)
                            },
                            onEntityMappingsSaved = { entityId, updatedMappings ->
                                manager.saveEntityMappings(entityId, updatedMappings)
                            }
                        )
                    }
                }
            }
        }
    }
}