package com.fmspcoding.notesapp.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import com.fmspcoding.notesapp.presentation.ui.LocalSpacing
import com.fmspcoding.notesapp.presentation.ui.Spacing

private val DarkColorPalette = darkColors(
    primary = Purple,
    primaryVariant = Blue,
    secondary = Teal,
    background = Black,
    surface = Black,
 //   onBackground = TextWhite
)

private val LightColorPalette = lightColors(
    primary = Purple,
    primaryVariant = Blue,
    secondary = Teal,
    background = White,
    surface = White

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun NotesTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}