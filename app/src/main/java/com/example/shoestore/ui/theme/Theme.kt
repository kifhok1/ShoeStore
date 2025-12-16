package com.example.shoestore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.shoestore.R


val customColorScheme = CustomColorScheme(
    red = Red,
    accent = Accent,
    disable = Disable,
    subTextLight = SubTextLight,
    background = Background,
    block = Block,
    text = Text,
    hint = Hint,
    subTextDark = SubTextDark,
    black = Black,
    white = White
)

val Inter = FontFamily(
    Font(R.font.raleway_regular, FontWeight.Normal),
    Font(R.font.raleway_bold, FontWeight.Bold),
    Font(R.font.raleway_medium, FontWeight.Medium),
    Font(R.font.raleway_semibold, FontWeight.SemiBold)
)

val LocalCustomColors = staticCompositionLocalOf {
   customColorScheme
}
val LocalCustomTypography = staticCompositionLocalOf {
    customTypography
}

@Composable
fun ShoeStoreTheme(content: @Composable () -> Unit) {
    val customColors = customColorScheme
    val customTypography = customTypography
    CompositionLocalProvider(
        LocalCustomColors provides customColors,
        LocalCustomTypography provides customTypography,
        content = content
    )
}

object CustomTheme {
    val colors: CustomColorScheme
        @Composable
        get() = LocalCustomColors.current
    val typography: CustomTypography
        @Composable
        get() = LocalCustomTypography.current

}