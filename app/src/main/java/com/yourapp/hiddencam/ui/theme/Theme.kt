package com.yourapp.hiddencam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF7DD3FC),
    secondary = androidx.compose.ui.graphics.Color(0xFF22C55E),
    tertiary = androidx.compose.ui.graphics.Color(0xFFF59E0B),
    background = androidx.compose.ui.graphics.Color(0xFF0F172A),
    surface = androidx.compose.ui.graphics.Color(0xFF111827),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF082F49),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF052E16),
    onTertiary = androidx.compose.ui.graphics.Color(0xFF451A03),
    onBackground = androidx.compose.ui.graphics.Color(0xFFE2E8F0),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE2E8F0),
)

@Composable
fun HiddenCamTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography(),
        content = content,
    )
}
