package com.cuvv.vratim.writescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TopHeader(
    langMap: Map<String, String>,
    onMenuActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    val headerBackground = Color(0xFF0B0F12)
    val iconPlaceholderColor = Color(0xFF352F7A)
    val textPrimary = Color(0xFFE2E8F0)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(headerBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(iconPlaceholderColor, RoundedCornerShape(8.dp))
            )

            Text(
                text = langMap["title"] ?: "Control Panel",
                color = textPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Box {
            IconButton(
                onClick = { isMenuExpanded = true },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8)
                )
            }

            MenuPanel(
                expanded = isMenuExpanded,
                onDismiss = { isMenuExpanded = false },
                langMap = langMap,
                onMenuActionClick = onMenuActionClick
            )
        }
    }
}