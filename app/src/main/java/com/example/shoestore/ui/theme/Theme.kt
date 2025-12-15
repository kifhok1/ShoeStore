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


private val сolorScheme = CustomColorScheme(
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
    Font(R.font.raleway, FontWeight.Normal),
    Font(R.font.raleway_bold, FontWeight.Bold),
    Font(R.font.raleway_medium, FontWeight.Medium),
    Font(R.font.raleway_semibold, FontWeight.SemiBold)
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColorScheme(
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
}
val LocalCustomTypography = staticCompositionLocalOf {
    CustomTypography(
        HeadingRegular34 = TextStyle(
            fontSize = 34.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal
        ),
        HeadingRegular32 = TextStyle(
            fontSize = 32.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal
        ),
        HeadingBold30 = TextStyle(
            fontSize = 30.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Bold
        ),
        HeadingRegular26 = TextStyle(
            fontSize = 26.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal
        ),
        HeadingSemiBold16 = TextStyle(
            fontSize = 16.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold
        ),
        SubtitleRegular16 = TextStyle(
            fontSize = 16.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal
        ),
        BodyRegular24 = TextStyle(
            fontSize = 24.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal
        ),
        BodyRegular20 = TextStyle(
            fontSize = 20.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal
        ),
        BodySemiBold18 = TextStyle(
            fontSize = 18.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold
        ),
        BodyMedium16 = TextStyle(
            fontSize = 16.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Medium
        ),
        BodyRegular16 = TextStyle(
            fontSize = 16.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal
        ),
        BodyMedium14 = TextStyle(
            fontSize = 14.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Medium
        ),
        BodyRegular14 = TextStyle(
            fontSize = 14.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal
        ),
        BodyRegular12 = TextStyle(
            fontSize = 12.sp,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal
        )
    )
}

@Composable
fun ShoeStoreTheme(content: @Composable () -> Unit) {
    val customColors = сolorScheme
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