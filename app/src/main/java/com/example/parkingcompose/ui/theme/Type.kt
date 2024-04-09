package com.example.parkingcompose.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.parkingcompose.R

// Set of Material typography styles to start with

private val ParkingFont = FontFamily(

    Font(R.font.sairasemicondensed_regular, FontWeight.Normal),
    Font(R.font.sairasemicondensed_medium, FontWeight.Medium),
    Font(R.font.sairasemicondensed_semibold, FontWeight.SemiBold),
    Font(R.font.sairasemicondensed_extralight, FontWeight.ExtraLight),
    Font(R.font.sairasemicondensed_bold, FontWeight.Bold),
    Font(R.font.sairasemicondensed_light, FontWeight.Light),
    Font(R.font.sairasemicondensed_extrabold, FontWeight.ExtraBold),
    Font(R.font.sairasemicondensed_black, FontWeight.Black),
    Font(R.font.sairasemicondensed_thin, FontWeight.Thin)


)

val ButtonTextStyle = TextStyle(
    fontFamily = ParkingFont,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    letterSpacing = 1.25.sp
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = ParkingFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),



    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)