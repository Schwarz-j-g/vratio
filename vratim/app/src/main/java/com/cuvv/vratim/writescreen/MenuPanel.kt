package com.cuvv.vratim.writescreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun MenuPanel(
    expanded: Boolean,
    onDismiss: () -> Unit,
    langMap: Map<String, String>,
    onMenuActionClick: (actionKey: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!expanded) return

    val animationProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 180),
        label = "MenuAnimation"
    )

    val menuBackground = Color(0xFF1E2225)
    val textPrimary = Color(0xFFE2E8F0)
    val textSecondary = Color(0xFF94A3B8)
    val dividerColor = Color(0xFF334155)

    Popup(
        alignment = Alignment.TopEnd,
        offset = IntOffset(x = (-16).dp.value.toInt(), y = 48.dp.value.toInt()),
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = modifier
                .width(220.dp)
                .scale(0.85f + (0.15f * animationProgress))
                .alpha(animationProgress)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
                .background(menuBackground, RoundedCornerShape(12.dp))
                .padding(vertical = 6.dp)
        ) {
            CustomMenuItem(
                text = langMap["menu_send_status"] ?: "Send status",
                textColor = textPrimary,
                onClick = {
                    onMenuActionClick("ACTION_OPEN_SEND_FREE")
                    onDismiss()
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = dividerColor, thickness = 1.dp)

            CustomMenuItem(
                text = langMap["menu_global"] ?: "Global settings",
                textColor = textPrimary,
                onClick = {
                    onMenuActionClick("ACTION_GLOBAL")
                    onDismiss()
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = dividerColor, thickness = 1.dp)

            CustomMenuItem(
                text = langMap["menu_paste"] ?: "Paste JSON",
                textColor = textSecondary,
                onClick = {
                    onMenuActionClick("ACTION_PASTE")
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun CustomMenuItem(
    text: String,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}