package com.example.formulariokotlin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Orange = Color(0xFFFF6D00)
val OrangeDark = Color(0xFFC75A00)
val DarkGray = Color(0xFF121212)

private val DarkColorPalette = darkColorScheme(
    primary = Orange,
    secondary = OrangeDark,
    background = DarkGray,
    surface = DarkGray,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun DarkOrangeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        typography = Typography(),
        content = content
    )
}
