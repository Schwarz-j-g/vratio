package com.cuvv.vratim

import android.content.Context
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
                            onRefreshTriggered = { manager.refreshServerStatuses() },
                            onJsonPasteTriggered = {
                                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clipData = clipboard.primaryClip
                                if (clipData != null && clipData.itemCount > 0) {

                                    val rawText = clipData.getItemAt(0).coerceToText(applicationContext)?.toString() ?: ""
                                    val cleanText = rawText.replace("\r", "").trim()

                                    manager.importConfigFromText(cleanText) { errorLog ->
                                        throw RuntimeException("DIAGNOSTIC CRASH: $errorLog")
                                    }
                                }
                            },
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
