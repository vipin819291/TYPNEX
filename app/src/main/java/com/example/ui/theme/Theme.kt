package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TypnexDarkColorScheme = darkColorScheme(
    primary = TypnexCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF0F2644),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = TypnexViolet,
    onSecondary = Color.White,
    background = Color(0xFF000000), // OLED Black
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF030712),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF38BDF8)
)

private val TypnexLightColorScheme = lightColorScheme(
    primary = TypnexElectricBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = TypnexViolet,
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFF0088FF)
)

@Composable
fun TypnexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) TypnexDarkColorScheme else TypnexLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
