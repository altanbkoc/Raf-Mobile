package com.altankoc.raf.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RafColorScheme = lightColorScheme(
    primary = RafMain,
    secondary = RafDark2,
    tertiary = RafLight,
    background = RafDark1,
    surface = RafDark1,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun RafTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RafColorScheme,
        typography = Typography,
        content = content
    )
}