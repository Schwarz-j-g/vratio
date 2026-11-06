package com.cuvv.vratim.writescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MenuPanel(
    expanded: Boolean,
    onDismiss: () -> Unit,
    langMap: Map<String, String>,
    onMenuActionClick: (actionKey: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val menuBackground = Color(0xFF1E2225) // Графитовый обсидиановый
    val textPrimary = Color(0xFFE2E8F0)

    Box(
        modifier = modifier.wrapContentSize(Alignment.TopEnd)
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            modifier = Modifier.background(menuBackground)
        ) {
            DropdownMenuItem(
                text = { Text(langMap["menu_send_status"] ?: "Отправить статус", color = textPrimary) },
                onClick = {
                    onMenuActionClick("ACTION_OPEN_SEND_FREE")
                    onDismiss()
                }
            )

            DropdownMenuItem(
                text = { Text(langMap["menu_global"] ?: "Global Settings", color = textPrimary) },
                onClick = {
                    onMenuActionClick("ACTION_GLOBAL")
                    onDismiss()
                }
            )

            DropdownMenuItem(
                text = { Text(langMap["menu_paste"] ?: "Paste JSON", color = textPrimary) },
                onClick = {
                    onMenuActionClick("ACTION_PASTE")
                    onDismiss()
                }
            )
        }
    }
}