package com.cuvv.vratim.writescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

@Composable
fun TopHeader(
    langMap: Map<String, String>,
    onMenuActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    val headerBackground = Color(0xFF121B1F)
    val textPrimary = Color(0xFFE2E8F0)
    val componentColor = Color(0xFF94A3B8)

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
                    .background(Color(0xFF161A1D), RoundedCornerShape(8.dp))
                    .drawBehind { drawIconCrystalStructure(componentColor) }
            )

            Text(
                text = langMap["title"] ?: "Custom title",
                color = textPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Box {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Transparent, CircleShape)
                    .clickable { isMenuExpanded = true }
                    .drawBehind { drawCustomSettingsGear(componentColor) }
            )

            MenuPanel(
                expanded = isMenuExpanded,
                onDismiss = { isMenuExpanded = false },
                langMap = langMap,
                onMenuActionClick = onMenuActionClick
            )
        }
    }
}

private fun DrawScope.drawIconCrystalStructure(lineColor: Color) {
    val w = size.width
    val h = size.height
    val strokeWidth = 1.0f
    val alphaColor = lineColor.copy(alpha = 0.4f)

    drawLine(color = alphaColor, start = Offset(w * 0.2f, h * 0.2f), end = Offset(w * 0.8f, h * 0.3f), strokeWidth = strokeWidth)
    drawLine(color = alphaColor, start = Offset(w * 0.8f, h * 0.3f), end = Offset(w * 0.5f, h * 0.8f), strokeWidth = strokeWidth)
    drawLine(color = alphaColor, start = Offset(w * 0.5f, h * 0.8f), end = Offset(w * 0.2f, h * 0.2f), strokeWidth = strokeWidth)
}

private fun DrawScope.drawCustomSettingsGear(gearColor: Color) {
    val cx = size.width / 2f
    val cy = size.height / 2f
    val outerRadius = size.width * 0.35f
    val innerRadius = size.width * 0.22f
    val gearThickness = 2.5f

    drawCircle(
        color = gearColor,
        radius = (outerRadius + innerRadius) / 2f,
        center = Offset(cx, cy),
        style = Stroke(width = gearThickness)
    )

    drawCircle(
        color = gearColor,
        radius = innerRadius * 0.5f,
        center = Offset(cx, cy),
        style = Stroke(width = 1.5f)
    )

    val toothWidth = 4.5f
    val toothHeight = outerRadius - innerRadius

    for (i in 0 until 8) {
        rotate(degrees = i * 45f, pivot = Offset(cx, cy)) {
            drawRect(
                color = gearColor,
                topLeft = Offset(cx - toothWidth / 2f, cy - outerRadius),
                size = Size(toothWidth, toothHeight)
            )
        }
    }
}