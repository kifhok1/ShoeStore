package com.example.shoestore.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColorScheme(
    val red: Color,
    val accent: Color,
    val disable: Color,
    val subTextLight: Color,
    val background: Color,
    val block: Color,
    val text: Color,
    val hint: Color,
    val subTextDark: Color,
    val black: Color,
    val white: Color
)

val Red = Color(0xFFF87265)
val Accent = Color(0xFF48B2E7)
val Disable = Color(0xFF2B6B8B)
val SubTextLight = Color(0xFFD8D8D8)
val Background = Color(0xFFF7F7F9)
val Block = Color(0xFFFFFFFF)
val Text = Color(0xFF2B2B2B)
val Hint = Color(0xFFF6A6A6A)
val SubTextDark = Color(0xFF707B81)
val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)

