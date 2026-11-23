package com.rusdili.pro

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF42634B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEBDD),
    onPrimaryContainer = Color(0xFF102A19),
    secondary = Color(0xFF526278),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6E3F7),
    onSecondaryContainer = Color(0xFF0F1D2E),
    tertiary = Color(0xFF8A5E00),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDF9E),
    onTertiaryContainer = Color(0xFF2C1B00),
    background = Color(0xFFF5F6F0),
    onBackground = Color(0xFF1A1C19),
    surface = Color(0xFFFFFEFA),
    onSurface = Color(0xFF1A1C19),
    surfaceVariant = Color(0xFFE1E4DC),
    onSurfaceVariant = Color(0xFF444841),
    outline = Color(0xFF747870),
    outlineVariant = Color(0xFFC4C8BE),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA2CFA9),
    onPrimary = Color(0xFF0C381D),
    primaryContainer = Color(0xFF284D33),
    onPrimaryContainer = Color(0xFFBCEBCE),
    secondary = Color(0xFFBAC8DF),
    onSecondary = Color(0xFF243245),
    secondaryContainer = Color(0xFF3A485C),
    onSecondaryContainer = Color(0xFFD6E3F7),
    tertiary = Color(0xFFF6BE48),
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF634000),
    onTertiaryContainer = Color(0xFFFFDF9E),
    background = Color(0xFF111411),
    onBackground = Color(0xFFE2E3DD),
    surface = Color(0xFF171B17),
    onSurface = Color(0xFFE2E3DD),
    surfaceVariant = Color(0xFF3D423C),
    onSurfaceVariant = Color(0xFFC1C6BC),
    surfaceContainer = Color(0xFF1E231E),
    surfaceContainerHigh = Color(0xFF272D27),
    outline = Color(0xFF8B9187),
    outlineVariant = Color(0xFF444841),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun RusDiliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
