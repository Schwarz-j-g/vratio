package com.cuvv.vratim.writescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.random.Random

@Composable
fun SystemBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val lineColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)

    val linesGeometry = remember { generateStaticLines() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawBehind() {
                drawRect(color = backgroundColor)

                drawStoneTexture(linesGeometry, lineColor)
            }
    ) {
        content()
    }
}

class LineCoords(
    val startXRatio: Float,
    val startYRatio: Float,
    val endXRatio: Float,
    val endYRatio: Float
)

private fun generateStaticLines(): Array<LineCoords> {
    val random = Random(42)
    val lineCount = 8

    return Array(lineCount) {
        LineCoords(
            startXRatio = random.nextFloat(),
            startYRatio = random.nextFloat(),
            endXRatio = random.nextFloat(),
            endYRatio = random.nextFloat()
        )
    }
}

private inline fun DrawScope.drawStoneTexture(lines: Array<LineCoords>, color: Color) {
    val width = size.width
    val height = size.height
    val strokeWidth = 1.5f

    for (i in lines.indices) {
        val line = lines[i]
        drawLine(
            color = color,
            start = Offset(line.startXRatio * width, line.startYRatio * height),
            end = Offset(line.endXRatio * width, line.endYRatio * height),
            strokeWidth = strokeWidth
        )
    }
}