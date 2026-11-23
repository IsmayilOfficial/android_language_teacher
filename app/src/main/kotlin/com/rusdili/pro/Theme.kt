package com.rusdili.pro

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RusDiliColors = lightColorScheme(
    primary = Color(0xFF42634B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEBDD),
    secondary = Color(0xFF526278),
    tertiary = Color(0xFF8A5E00),
    background = Color(0xFFF5F6F0),
    surface = Color(0xFFFFFEFA),
    error = Color(0xFFBA1A1A)
)

@Composable
fun RusDiliTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = RusDiliColors, content = content)
}
