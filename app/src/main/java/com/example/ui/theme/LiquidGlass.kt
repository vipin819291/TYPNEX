package com.example.ui.theme

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(20.dp),
    isDark: Boolean = false,
    alpha: Float = if (isDark) 0.65f else 0.78f,
    borderWidth: Dp = 1.2.dp,
    elevation: Dp = 8.dp,
    accentGlow: Color? = null
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "glassShimmer")
    val shimmerPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween( durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glassShimmer"
    )

    val surfaceColor = if (isDark) {
        // Deep OLED obsidian glass
        Color(0xFF030712).copy(alpha = alpha)
    } else {
        // Crisp frosted liquid glass
        Color.White.copy(alpha = alpha)
    }

    val topBorderColor = accentGlow ?: if (isDark) {
        Color(0x9038BDF8)
    } else {
        Color(0x990088FF)
    }

    val bottomBorderColor = if (isDark) {
        Color(0x301E293B)
    } else {
        Color(0x40E2E8F0)
    }

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            topBorderColor,
            Color.White.copy(alpha = if (isDark) 0.25f else 0.6f),
            bottomBorderColor,
            topBorderColor.copy(alpha = 0.3f)
        ),
        start = Offset(0f, 0f),
        end = Offset(400f, 600f)
    )

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            ambientColor = if (isDark) Color(0x60000000) else Color(0x300088FF),
            spotColor = if (isDark) Color(0x4038BDF8) else Color(0x2506B6D4)
        )
        .clip(shape)
        .background(surfaceColor)
        .border(
            width = borderWidth,
            brush = borderBrush,
            shape = shape
        )
}

/**
 * Dynamic interesting background color that shifts smoothly and dynamically,
 * with floating fluid liquid glass orbs and WhatsApp-style subtle doodle patterns.
 */
@Composable
fun DynamicFluidBackground(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val transition = rememberInfiniteTransition(label = "fluidTheme")

    // Dynamic color morphing between vibrant jewel tones
    val color1 by transition.animateColor(
        initialValue = if (isDark) Color(0xFF02040A) else Color(0xFFE0F2FE),
        targetValue = if (isDark) Color(0xFF070B19) else Color(0xFFEDE9FE),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color1"
    )

    val color2 by transition.animateColor(
        initialValue = if (isDark) Color(0xFF0A0F26) else Color(0xFFCCFBF1),
        targetValue = if (isDark) Color(0xFF140B26) else Color(0xFFFCE7F3),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color2"
    )

    val color3 by transition.animateColor(
        initialValue = if (isDark) Color(0xFF000000) else Color(0xFFF0FDF4),
        targetValue = if (isDark) Color(0xFF040914) else Color(0xFFE0F7FA),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color3"
    )

    val orbAnimX by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbX"
    )

    val orbAnimY by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbY"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(color1, color2, color3)
                )
            )
    ) {
        // Fluid glowing dynamic orbs and WhatsApp-style textured doodles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Floating luminous liquid glass orbs
            val orb1Center = Offset(width * orbAnimX, height * 0.25f)
            val orb2Center = Offset(width * (1f - orbAnimX), height * orbAnimY)
            val orb3Center = Offset(width * 0.5f, height * (0.85f - (orbAnimY * 0.15f)))

            // Orb 1: Cyan / Electric Blue glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isDark) Color(0x3500D2FF) else Color(0x4538BDF8),
                        Color.Transparent
                    ),
                    center = orb1Center,
                    radius = width * 0.55f
                ),
                center = orb1Center,
                radius = width * 0.55f
            )

            // Orb 2: Violet / Magenta glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isDark) Color(0x308B5CF6) else Color(0x35C084FC),
                        Color.Transparent
                    ),
                    center = orb2Center,
                    radius = width * 0.6f
                ),
                center = orb2Center,
                radius = width * 0.6f
            )

            // Orb 3: Emerald Teal glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isDark) Color(0x2500F5A0) else Color(0x3034D399),
                        Color.Transparent
                    ),
                    center = orb3Center,
                    radius = width * 0.5f
                ),
                center = orb3Center,
                radius = width * 0.5f
            )

            // Subtle WhatsApp-style chat doodle wallpaper pattern
            val doodleColor = if (isDark) Color(0x0CFFFFFF) else Color(0x100088FF)
            val stroke = Stroke(width = 1.2f)

            // Grid of repetitive whimsical chat doodles: speech bubble, paperplane, key, doc
            val stepX = 140f
            val stepY = 140f
            var currY = 40f
            var rowIdx = 0

            while (currY < height) {
                var currX = if (rowIdx % 2 == 0) 40f else 110f
                while (currX < width) {
                    val patternType = ((currX + currY).toInt() / 140) % 4
                    when (patternType) {
                        0 -> {
                            // Chat bubble doodle
                            val bubblePath = Path().apply {
                                reset()
                                moveTo(currX, currY)
                                lineTo(currX + 24f, currY)
                                quadraticTo(currX + 28f, currY, currX + 28f, currY + 4f)
                                lineTo(currX + 28f, currY + 16f)
                                quadraticTo(currX + 28f, currY + 20f, currX + 24f, currY + 20f)
                                lineTo(currX + 8f, currY + 20f)
                                lineTo(currX + 2f, currY + 26f)
                                lineTo(currX + 4f, currY + 20f)
                                lineTo(currX, currY + 20f)
                                close()
                            }
                            drawPath(bubblePath, color = doodleColor, style = stroke)
                        }
                        1 -> {
                            // Document doodle
                            val docPath = Path().apply {
                                reset()
                                moveTo(currX, currY)
                                lineTo(currX + 16f, currY)
                                lineTo(currX + 22f, currY + 6f)
                                lineTo(currX + 22f, currY + 24f)
                                lineTo(currX, currY + 24f)
                                close()
                            }
                            drawPath(docPath, color = doodleColor, style = stroke)
                            drawLine(doodleColor, Offset(currX + 4f, currY + 10f), Offset(currX + 16f, currY + 10f), strokeWidth = 1f)
                            drawLine(doodleColor, Offset(currX + 4f, currY + 14f), Offset(currX + 14f, currY + 14f), strokeWidth = 1f)
                        }
                        2 -> {
                            // Lock / Security doodle
                            val lockPath = Path().apply {
                                reset()
                                addRect(androidx.compose.ui.geometry.Rect(currX, currY + 8f, currX + 18f, currY + 22f))
                            }
                            drawPath(lockPath, color = doodleColor, style = stroke)
                            drawArc(
                                color = doodleColor,
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(currX + 3f, currY + 1f),
                                size = androidx.compose.ui.geometry.Size(12f, 14f),
                                style = stroke
                            )
                        }
                        else -> {
                            // Paperplane / send doodle
                            val planePath = Path().apply {
                                reset()
                                moveTo(currX, currY + 12f)
                                lineTo(currX + 20f, currY)
                                lineTo(currX + 10f, currY + 22f)
                                lineTo(currX + 8f, currY + 15f)
                                lineTo(currX, currY + 12f)
                                close()
                            }
                            drawPath(planePath, color = doodleColor, style = stroke)
                        }
                    }
                    currX += stepX
                }
                currY += stepY
                rowIdx++
            }
        }

        // Child content over the dynamic liquid canvas
        content()
    }
}
