package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.math.BcsDiagramType

@Composable
fun BcsDiagramView(
    diagramType: BcsDiagramType,
    modifier: Modifier = Modifier
) {
    if (diagramType == BcsDiagramType.NONE) return

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            when (diagramType) {
                BcsDiagramType.GEAR_ROTATION -> {
                    // Draw 3 interlocking gears
                    val r = h * 0.28f
                    val y = h * 0.5f
                    val x1 = w * 0.25f
                    val x2 = w * 0.50f
                    val x3 = w * 0.75f

                    // Gear 1 (Clockwise)
                    drawCircle(color = primaryColor, radius = r, center = Offset(x1, y), style = Stroke(width = 6f))
                    drawCircle(color = primaryColor.copy(alpha = 0.3f), radius = r * 0.4f, center = Offset(x1, y))

                    // Gear 2 (Counter-Clockwise)
                    drawCircle(color = secondaryColor, radius = r, center = Offset(x2, y), style = Stroke(width = 6f))
                    drawCircle(color = secondaryColor.copy(alpha = 0.3f), radius = r * 0.4f, center = Offset(x2, y))

                    // Gear 3 (Clockwise)
                    drawCircle(color = tertiaryColor, radius = r, center = Offset(x3, y), style = Stroke(width = 6f))
                    drawCircle(color = tertiaryColor.copy(alpha = 0.3f), radius = r * 0.4f, center = Offset(x3, y))
                }

                BcsDiagramType.PULLEY_SYSTEM -> {
                    // Fixed ceiling and single pulley with weight
                    val ceilingY = h * 0.2f
                    drawLine(color = onSurface, start = Offset(w * 0.2f, ceilingY), end = Offset(w * 0.8f, ceilingY), strokeWidth = 5f)

                    val pulleyCenter = Offset(w * 0.5f, ceilingY + 30f)
                    drawLine(color = onSurface, start = Offset(w * 0.5f, ceilingY), end = pulleyCenter, strokeWidth = 3f)
                    drawCircle(color = primaryColor, radius = 24f, center = pulleyCenter, style = Stroke(width = 5f))

                    // Rope left and right
                    drawLine(color = secondaryColor, start = Offset(pulleyCenter.x - 24f, pulleyCenter.y), end = Offset(pulleyCenter.x - 24f, h * 0.85f), strokeWidth = 3f)
                    drawLine(color = secondaryColor, start = Offset(pulleyCenter.x + 24f, pulleyCenter.y), end = Offset(pulleyCenter.x + 24f, h * 0.7f), strokeWidth = 3f)

                    // Weight box on left
                    drawRect(color = primaryColor, topLeft = Offset(pulleyCenter.x - 44f, h * 0.75f), size = androidx.compose.ui.geometry.Size(40f, 30f))
                }

                BcsDiagramType.COMPASS_DIRECTION -> {
                    // North path (4km) then East path (3km) forming right triangle
                    val start = Offset(w * 0.35f, h * 0.85f)
                    val northPoint = Offset(w * 0.35f, h * 0.25f)
                    val eastPoint = Offset(w * 0.65f, h * 0.25f)

                    // Path North
                    drawLine(color = primaryColor, start = start, end = northPoint, strokeWidth = 5f)
                    // Path East
                    drawLine(color = primaryColor, start = northPoint, end = eastPoint, strokeWidth = 5f)
                    // Displacement Line (Hypotenuse)
                    drawLine(
                        color = tertiaryColor,
                        start = start,
                        end = eastPoint,
                        strokeWidth = 4f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                }

                BcsDiagramType.TRIANGLE_COUNT -> {
                    // Triangle with subdivided base
                    val top = Offset(w * 0.5f, h * 0.15f)
                    val bLeft = Offset(w * 0.15f, h * 0.85f)
                    val bRight = Offset(w * 0.85f, h * 0.85f)

                    val path = Path().apply {
                        moveTo(top.x, top.y)
                        lineTo(bLeft.x, bLeft.y)
                        lineTo(bRight.x, bRight.y)
                        close()
                    }
                    drawPath(path = path, color = primaryColor, style = Stroke(width = 4f))

                    // 3 dividing lines from top to base
                    val step = (bRight.x - bLeft.x) / 4f
                    for (i in 1..3) {
                        val basePt = Offset(bLeft.x + step * i, bLeft.y)
                        drawLine(color = secondaryColor, start = top, end = basePt, strokeWidth = 3f)
                    }
                }

                else -> {}
            }
        }
    }
}
