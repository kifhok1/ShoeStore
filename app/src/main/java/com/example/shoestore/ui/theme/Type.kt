package com.example.shoestore.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class CustomTypography(
    val HeadingRegular34: TextStyle,
    val HeadingRegular32: TextStyle,
    val HeadingBold30: TextStyle,
    val HeadingRegular26: TextStyle,
    val HeadingSemiBold16: TextStyle,
    val SubtitleRegular16: TextStyle,
    val BodyRegular24: TextStyle,
    val BodyRegular20: TextStyle,
    val BodySemiBold18: TextStyle,
    val BodyMedium16: TextStyle,
    val BodyRegular16: TextStyle,
    val BodyMedium14: TextStyle,
    val BodyRegular14: TextStyle,
    val BodyRegular12: TextStyle
)


val customTypography = CustomTypography(
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