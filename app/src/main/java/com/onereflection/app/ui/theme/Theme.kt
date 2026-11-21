package com.onereflection.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.onereflection.app.R
import com.onereflection.app.model.CategoryColor

object BrandColors {
    val SurfaceWarm = Color(0xFFFCF9F8)
    val SurfaceContainerLow = Color(0xFFF6F3F2)
    val SurfaceContainer = Color(0xFFF0EDED)
    val SurfaceContainerHigh = Color(0xFFEAE7E7)
    val SurfaceContainerHighest = Color(0xFFE5E2E1)
    val OnSurfaceDark = Color(0xFF1C1B1B)
    val OnSurfaceVariant = Color(0xFF50434A)
    val OutlineVariant = Color(0xFFD4C2C9)

    val OledBlack = Color(0xFF000000)
    val OledSurface = Color(0xFF121214)
    val OledCard = Color(0xFF17171A)
    val OledBorder = Color(0xFF26262A)

    val PrimaryPlum = Color(0xFF864B70)
    val CandyPink = Color(0xFFF5ACD7)
    val CandyPinkDeep = Color(0xFF753C61)
    val PinkFixed = Color(0xFFFFD8EC)
    val PinkFixedDim = Color(0xFFFAB0DC)
    val DeepPlumText = Color(0xFF37062A)

    val SecondaryGold = Color(0xFF6F5D00)
    val ButterYellow = Color(0xFFF9DF72)
    val ButterYellowText = Color(0xFF736200)
    val YellowFixed = Color(0xFFFCE275)

    val TertiarySlateBlue = Color(0xFF4F5D85)
    val LavenderContainer = Color(0xFFB3C1EF)
    val LavenderSoft = Color(0xFFDAE2FF)
    val LavenderText = Color(0xFF091A3E)
    val LavenderMuted = Color(0xFF38466C)

    val Matcha = Color(0xFFB8E98E)
    val MatchaDark = Color(0xFF2A4413)
    val Lilac = Color(0xFFE7A2EE)
    val LilacText = Color(0xFF531858)
    val CyanSoft = Color(0xFFA5E8EF)
    val CyanText = Color(0xFF0A4952)

    val HighPriorityBg = Color(0xFFFFDAD6)
    val HighPriorityText = Color(0xFFBA1A1A)
    val DangerText = Color(0xFF93000A)
    val EmeraldDot = Color(0xFF10B981)
}

data class CategoryThemeStyle(
    val preview: Color,
    val cardBgLight: Color,
    val badgeBg: Color,
    val badgeText: Color,
    val accentText: Color
)

fun CategoryColor.toThemeStyle(): CategoryThemeStyle = when (this) {
    CategoryColor.PINK -> CategoryThemeStyle(
        preview = BrandColors.CandyPink,
        cardBgLight = BrandColors.CandyPink,
        badgeBg = Color.White.copy(alpha = 0.92f),
        badgeText = BrandColors.OnSurfaceDark,
        accentText = BrandColors.CandyPinkDeep
    )
    CategoryColor.YELLOW -> CategoryThemeStyle(
        preview = BrandColors.ButterYellow,
        cardBgLight = BrandColors.ButterYellow,
        badgeBg = Color.White.copy(alpha = 0.92f),
        badgeText = BrandColors.OnSurfaceDark,
        accentText = BrandColors.ButterYellowText
    )
    CategoryColor.LAVENDER -> CategoryThemeStyle(
        preview = BrandColors.LavenderSoft,
        cardBgLight = BrandColors.LavenderSoft,
        badgeBg = Color.White.copy(alpha = 0.92f),
        badgeText = BrandColors.LavenderText,
        accentText = BrandColors.LavenderMuted
    )
    CategoryColor.MATCHA -> CategoryThemeStyle(
        preview = BrandColors.Matcha,
        cardBgLight = BrandColors.SurfaceContainerHigh,
        badgeBg = Color.White,
        badgeText = BrandColors.OnSurfaceDark,
        accentText = BrandColors.MatchaDark
    )
    CategoryColor.LILAC -> CategoryThemeStyle(
        preview = BrandColors.Lilac,
        cardBgLight = BrandColors.Lilac,
        badgeBg = Color.White.copy(alpha = 0.92f),
        badgeText = BrandColors.OnSurfaceDark,
        accentText = BrandColors.LilacText
    )
    CategoryColor.CYAN -> CategoryThemeStyle(
        preview = BrandColors.CyanSoft,
        cardBgLight = BrandColors.CyanSoft,
        badgeBg = Color.White.copy(alpha = 0.92f),
        badgeText = BrandColors.CyanText,
        accentText = BrandColors.CyanText
    )
    CategoryColor.SLATE -> CategoryThemeStyle(
        preview = BrandColors.SurfaceContainerHigh,
        cardBgLight = BrandColors.SurfaceContainerLow,
        badgeBg = Color.White,
        badgeText = BrandColors.OnSurfaceDark,
        accentText = BrandColors.OnSurfaceVariant
    )
}

val SpaceGroteskFontFamily = FontFamily(
    Font(R.font.space_grotesk, FontWeight.Normal),
    Font(R.font.space_grotesk, FontWeight.Medium),
    Font(R.font.space_grotesk, FontWeight.SemiBold),
    Font(R.font.space_grotesk, FontWeight.Bold)
)

val PlusJakartaSansFontFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans, FontWeight.Bold)
)

val OneReflectionTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.8).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.4).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.2).sp
    ),
    titleLarge = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    bodySmall = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 12.sp
    )
)

private val LightColors = lightColorScheme(
    primary = BrandColors.PrimaryPlum,
    onPrimary = Color.White,
    primaryContainer = BrandColors.CandyPink,
    onPrimaryContainer = BrandColors.CandyPinkDeep,
    secondary = BrandColors.SecondaryGold,
    onSecondary = Color.White,
    secondaryContainer = BrandColors.ButterYellow,
    onSecondaryContainer = BrandColors.ButterYellowText,
    tertiary = BrandColors.TertiarySlateBlue,
    onTertiary = Color.White,
    tertiaryContainer = BrandColors.LavenderContainer,
    onTertiaryContainer = BrandColors.LavenderText,
    background = BrandColors.SurfaceWarm,
    onBackground = BrandColors.OnSurfaceDark,
    surface = BrandColors.SurfaceWarm,
    onSurface = BrandColors.OnSurfaceDark,
    surfaceVariant = BrandColors.SurfaceContainerLow,
    onSurfaceVariant = BrandColors.OnSurfaceVariant,
    outlineVariant = BrandColors.OutlineVariant
)

private val OledDarkColors = darkColorScheme(
    primary = BrandColors.CandyPink,
    onPrimary = BrandColors.OnSurfaceDark,
    primaryContainer = BrandColors.PrimaryPlum,
    onPrimaryContainer = Color.White,
    secondary = BrandColors.ButterYellow,
    onSecondary = BrandColors.OnSurfaceDark,
    secondaryContainer = BrandColors.ButterYellowText,
    onSecondaryContainer = Color.White,
    tertiary = BrandColors.LavenderContainer,
    onTertiary = BrandColors.LavenderText,
    background = BrandColors.OledBlack,
    onBackground = Color.White,
    surface = BrandColors.OledBlack,
    onSurface = Color.White,
    surfaceVariant = BrandColors.OledCard,
    onSurfaceVariant = Color(0xFFA1A1AA),
    outlineVariant = BrandColors.OledBorder
)

@Composable
fun OneReflectionTheme(
    oledMode: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (oledMode) OledDarkColors else LightColors,
        typography = OneReflectionTypography,
        content = content
    )
}
