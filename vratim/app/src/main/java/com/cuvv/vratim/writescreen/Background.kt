package com.cuvv.vratim.writescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope


val ObsidianLineColor = Color(0xFF331465).copy(alpha = 0.35f)

@Composable
fun SystemBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = Color(0xFF000F12))
                drawObsidianTexture()
            }
    ) {
        content()
    }
}

private fun DrawScope.drawObsidianTexture() {
    val w = size.width
    val h = size.height
    val strokeWidth = 1.2f

    drawLine(
        color = ObsidianLineColor,
        start = Offset(0f, h * 0.25f),
        end = Offset(w, h * 0.75f),
        strokeWidth = strokeWidth
    )

    drawLine(
        color = ObsidianLineColor,
        start = Offset(w * 0.15f, h),
        end = Offset(w * 0.85f, 0f),
        strokeWidth = strokeWidth
    )

    drawLine(color = ObsidianLineColor, start = Offset(w * 0.7f, 0f), end = Offset(w, h * 0.15f), strokeWidth = strokeWidth)
    drawLine(color = ObsidianLineColor, start = Offset(w * 0.55f, 0f), end = Offset(w, h * 0.28f), strokeWidth = strokeWidth)

    drawLine(color = ObsidianLineColor, start = Offset(0f, h * 0.65f), end = Offset(w * 0.4f, h), strokeWidth = strokeWidth)
    drawLine(color = ObsidianLineColor, start = Offset(0f, h * 0.8f), end = Offset(w * 0.25f, h), strokeWidth = strokeWidth)

    drawLine(color = ObsidianLineColor, start = Offset(w * 0.38f, h * 0.44f), end = Offset(w * 0.55f, h * 0.28f), strokeWidth = strokeWidth)
    drawLine(color = ObsidianLineColor, start = Offset(w * 0.51f, h * 0.52f), end = Offset(w * 0.35f, h * 0.76f), strokeWidth = strokeWidth)
    drawLine(color = ObsidianLineColor, start = Offset(w * 0.73f, h * 0.61f), end = Offset(w * 0.85f, h * 0.35f), strokeWidth = strokeWidth)
}