package com.rusdili.pro

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.rusdili.pro.core.common.LearningLanguage

private val RussianLightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6EBFF),
    onPrimaryContainer = Color(0xFF00274A),
    secondary = Color(0xFF47607A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5E3F5),
    onSecondaryContainer = Color(0xFF0D1E30),
    tertiary = Color(0xFFD98200),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0A6),
    onTertiaryContainer = Color(0xFF2E1B00),
    background = Color(0xFFF4F7FB),
    onBackground = Color(0xFF171C22),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF171C22),
    surfaceVariant = Color(0xFFE1E8F2),
    onSurfaceVariant = Color(0xFF424A54),
    outline = Color(0xFF727B87),
    outlineVariant = Color(0xFFC2CCD9),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val RussianDarkColorScheme = darkColorScheme(
    primary = Color(0xFF2196F3),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF13487C),
    onPrimaryContainer = Color(0xFFD6EBFF),
    secondary = Color(0xFFADC6E2),
    onSecondary = Color(0xFF183047),
    secondaryContainer = Color(0xFF2F4760),
    onSecondaryContainer = Color(0xFFD5E3F5),
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF442800),
    tertiaryContainer = Color(0xFF623D00),
    onTertiaryContainer = Color(0xFFFFE0A6),
    background = Color(0xFF0E141B),
    onBackground = Color(0xFFE2E8F0),
    surface = Color(0xFF151D26),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF24303D),
    onSurfaceVariant = Color(0xFFBAC5D3),
    surfaceContainer = Color(0xFF19222C),
    surfaceContainerHigh = Color(0xFF212C38),
    outline = Color(0xFF6B7888),
    outlineVariant = Color(0xFF334152),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val SpanishLightColorScheme = lightColorScheme(
    primary = Color(0xFFE53935),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = Color(0xFF8C5319),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDCC0),
    onSecondaryContainer = Color(0xFF2D1600),
    tertiary = Color(0xFFE09F00),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE599),
    onTertiaryContainer = Color(0xFF2B1D00),
    background = Color(0xFFFDF6F5),
    onBackground = Color(0xFF221919),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF221919),
    surfaceVariant = Color(0xFFF4E2E0),
    onSurfaceVariant = Color(0xFF534342),
    outline = Color(0xFF857371),
    outlineVariant = Color(0xFFD8C2BF),
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val SpanishDarkColorScheme = darkColorScheme(
    primary = Color(0xFFE53935),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF7D1918),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = Color(0xFFF2BE8C),
    onSecondary = Color(0xFF472500),
    secondaryContainer = Color(0xFF673B07),
    onSecondaryContainer = Color(0xFFFFDCC0),
    tertiary = Color(0xFFFFCA4D),
    onTertiary = Color(0xFF3F2D00),
    tertiaryContainer = Color(0xFF5C4200),
    onTertiaryContainer = Color(0xFFFFE599),
    background = Color(0xFF1A1111),
    onBackground = Color(0xFFF2E3E2),
    surface = Color(0xFF221818),
    onSurface = Color(0xFFF2E3E2),
    surfaceVariant = Color(0xFF3A2828),
    onSurfaceVariant = Color(0xFFD5BEBC),
    surfaceContainer = Color(0xFF271C1C),
    surfaceContainerHigh = Color(0xFF322424),
    outline = Color(0xFF8A7270),
    outlineVariant = Color(0xFF4C3736),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val FrenchLightColorScheme = lightColorScheme(
    primary = Color(0xFF7E57C2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE2FF),
    onPrimaryContainer = Color(0xFF270E5C),
    secondary = Color(0xFF5E5B7D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4DFFF),
    onSecondaryContainer = Color(0xFF1B1836),
    tertiary = Color(0xFF00838F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB8F0F6),
    onTertiaryContainer = Color(0xFF002023),
    background = Color(0xFFF8F5FC),
    onBackground = Color(0xFF1D1A22),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1D1A22),
    surfaceVariant = Color(0xFFEBE4F4),
    onSurfaceVariant = Color(0xFF494554),
    outline = Color(0xFF7A7585),
    outlineVariant = Color(0xFFCBC4D6),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val FrenchDarkColorScheme = darkColorScheme(
    primary = Color(0xFF7E57C2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF492A82),
    onPrimaryContainer = Color(0xFFEDE2FF),
    secondary = Color(0xFFC8C2EB),
    onSecondary = Color(0xFF2F2D4D),
    secondaryContainer = Color(0xFF464365),
    onSecondaryContainer = Color(0xFFE4DFFF),
    tertiary = Color(0xFF5DD5E2),
    onTertiary = Color(0xFF00363C),
    tertiaryContainer = Color(0xFF004F57),
    onTertiaryContainer = Color(0xFFB8F0F6),
    background = Color(0xFF14111B),
    onBackground = Color(0xFFE8E2F0),
    surface = Color(0xFF1C1824),
    onSurface = Color(0xFFE8E2F0),
    surfaceVariant = Color(0xFF2E283B),
    onSurfaceVariant = Color(0xFFC8C0D6),
    surfaceContainer = Color(0xFF211C2B),
    surfaceContainerHigh = Color(0xFF2A2437),
    outline = Color(0xFF7D748E),
    outlineVariant = Color(0xFF403952),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private fun colorSchemeFor(language: LearningLanguage, darkTheme: Boolean): ColorScheme =
    when (language) {
        LearningLanguage.RUSSIAN -> if (darkTheme) RussianDarkColorScheme else RussianLightColorScheme
        LearningLanguage.SPANISH -> if (darkTheme) SpanishDarkColorScheme else SpanishLightColorScheme
        LearningLanguage.FRENCH -> if (darkTheme) FrenchDarkColorScheme else FrenchLightColorScheme
    }

@Composable
fun RusDiliTheme(
    language: LearningLanguage = LearningLanguage.RUSSIAN,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorSchemeFor(language, darkTheme),
        content = content
    )
}
