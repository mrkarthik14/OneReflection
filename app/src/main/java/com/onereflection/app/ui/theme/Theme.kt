package com.onereflection.app.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.onereflection.app.R
import com.onereflection.app.model.CategoryColor
import com.onereflection.app.model.ElementalTheme
import com.onereflection.app.model.TaskPriority
import java.util.Locale

val SanctuaryEase = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
val SanctuaryPageEase = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)

data class ElementalThemePalette(
    val theme: ElementalTheme,
    val isDark: Boolean,
    val darkProgress: Float = if (isDark) 1f else 0f,
    val primary: Color,
    val primaryBright: Color,
    val secondary: Color,
    val glowCore: Color,
    val glowOuter: Color,
    val onPrimary: Color,
    val pageBgTop: Color,
    val pageBgMid: Color,
    val pageBgBottom: Color,
    val surfaceCard: Color,
    val surfaceElevated: Color,
    val border: Color,
    val inkPrimary: Color,
    val inkSecondary: Color,
    val heroStart: Color,
    val heroEnd: Color,
    val surfaceTint: Color
)

fun elementalPaletteFor(theme: ElementalTheme, oledMode: Boolean): ElementalThemePalette = when (theme) {
    ElementalTheme.EMBER -> if (oledMode) {
        ElementalThemePalette(
            theme = theme,
            isDark = true,
            primary = Color(0xFFFF632F),
            primaryBright = Color(0xFFFF9E4D),
            secondary = Color(0xFFFFD166),
            glowCore = Color(0xFFFF7A29),
            glowOuter = Color(0xFFD92614),
            onPrimary = Color(0xFF1D0703),
            pageBgTop = Color(0xFF180705),
            pageBgMid = Color(0xFF110504),
            pageBgBottom = Color(0xFF0A0303),
            surfaceCard = Color(0xFF1E0D0A),
            surfaceElevated = Color(0xFF2B1410),
            border = Color(0xFF5A251B),
            inkPrimary = Color(0xFFFFF4EE),
            inkSecondary = Color(0xFFD4A79A),
            heroStart = Color(0xFF42140B),
            heroEnd = Color(0xFF1B0705),
            surfaceTint = Color(0xFF33140E)
        )
    } else {
        ElementalThemePalette(
            theme = theme,
            isDark = false,
            primary = Color(0xFFD93B1C),
            primaryBright = Color(0xFFFF783B),
            secondary = Color(0xFFE69119),
            glowCore = Color(0xFFFF6E2E),
            glowOuter = Color(0xFFFFB26B),
            onPrimary = Color.White,
            pageBgTop = Color(0xFFFFEFE7),
            pageBgMid = Color(0xFFFFF7F3),
            pageBgBottom = Color(0xFFFDEDE6),
            surfaceCard = Color(0xFFFFFFFF),
            surfaceElevated = Color(0xFFFFECE3),
            border = Color(0xFFF2C8B6),
            inkPrimary = Color(0xFF24110C),
            inkSecondary = Color(0xFF70483D),
            heroStart = Color(0xFF38120A),
            heroEnd = Color(0xFF1E0805),
            surfaceTint = Color(0xFFFCE6DC)
        )
    }

    ElementalTheme.SOLAR -> if (oledMode) {
        ElementalThemePalette(
            theme = theme,
            isDark = true,
            primary = Color(0xFFFFCE47),
            primaryBright = Color(0xFFFFE98A),
            secondary = Color(0xFFFF9F2E),
            glowCore = Color(0xFFFFD859),
            glowOuter = Color(0xFFCC8212),
            onPrimary = Color(0xFF1C1302),
            pageBgTop = Color(0xFF171104),
            pageBgMid = Color(0xFF100C03),
            pageBgBottom = Color(0xFF090702),
            surfaceCard = Color(0xFF1D1609),
            surfaceElevated = Color(0xFF2A200E),
            border = Color(0xFF57421A),
            inkPrimary = Color(0xFFFFFBF0),
            inkSecondary = Color(0xFFD1BE94),
            heroStart = Color(0xFF3D2C09),
            heroEnd = Color(0xFF1A1305),
            surfaceTint = Color(0xFF30240C)
        )
    } else {
        ElementalThemePalette(
            theme = theme,
            isDark = false,
            primary = Color(0xFFC9820C),
            primaryBright = Color(0xFFF6C236),
            secondary = Color(0xFFE36E24),
            glowCore = Color(0xFFFFCA3A),
            glowOuter = Color(0xFFFFE799),
            onPrimary = Color.White,
            pageBgTop = Color(0xFFFFF5D9),
            pageBgMid = Color(0xFFFFFBF0),
            pageBgBottom = Color(0xFFFCF2DA),
            surfaceCard = Color(0xFFFFFFFF),
            surfaceElevated = Color(0xFFFFF3D4),
            border = Color(0xFFEED7A3),
            inkPrimary = Color(0xFF231A08),
            inkSecondary = Color(0xFF6B5832),
            heroStart = Color(0xFF362708),
            heroEnd = Color(0xFF1B1405),
            surfaceTint = Color(0xFFFFF1C7)
        )
    }

    ElementalTheme.BREEZE -> if (oledMode) {
        ElementalThemePalette(
            theme = theme,
            isDark = true,
            primary = Color(0xFF52F5C8),
            primaryBright = Color(0xFFA8FFE6),
            secondary = Color(0xFF62C8FF),
            glowCore = Color(0xFF6CF7D2),
            glowOuter = Color(0xFF13876A),
            onPrimary = Color(0xFF041C16),
            pageBgTop = Color(0xFF041714),
            pageBgMid = Color(0xFF03100E),
            pageBgBottom = Color(0xFF020A09),
            surfaceCard = Color(0xFF0B201C),
            surfaceElevated = Color(0xFF122E28),
            border = Color(0xFF22544A),
            inkPrimary = Color(0xFFEEFFF9),
            inkSecondary = Color(0xFF9AC9BC),
            heroStart = Color(0xFF0C332B),
            heroEnd = Color(0xFF061A16),
            surfaceTint = Color(0xFF102E28)
        )
    } else {
        ElementalThemePalette(
            theme = theme,
            isDark = false,
            primary = Color(0xFF15856B),
            primaryBright = Color(0xFF4CE0B8),
            secondary = Color(0xFF2D7BB5),
            glowCore = Color(0xFF42DFB6),
            glowOuter = Color(0xFFA8F2DE),
            onPrimary = Color.White,
            pageBgTop = Color(0xFFE0F7F1),
            pageBgMid = Color(0xFFF2FBF8),
            pageBgBottom = Color(0xFFE4F6F1),
            surfaceCard = Color(0xFFFFFFFF),
            surfaceElevated = Color(0xFFDCF3EC),
            border = Color(0xFFB3E2D5),
            inkPrimary = Color(0xFF0C211C),
            inkSecondary = Color(0xFF3F695E),
            heroStart = Color(0xFF0B2F27),
            heroEnd = Color(0xFF071A16),
            surfaceTint = Color(0xFFD9F3EB)
        )
    }

    ElementalTheme.BLOOM -> if (oledMode) {
        ElementalThemePalette(
            theme = theme,
            isDark = true,
            primary = Color(0xFFFF75C8),
            primaryBright = Color(0xFFFFBCE8),
            secondary = Color(0xFFC58CFF),
            glowCore = Color(0xFFFF82CE),
            glowOuter = Color(0xFFAD2078),
            onPrimary = Color(0xFF210516),
            pageBgTop = Color(0xFF1A0616),
            pageBgMid = Color(0xFF11040F),
            pageBgBottom = Color(0xFF090208),
            surfaceCard = Color(0xFF200C1D),
            surfaceElevated = Color(0xFF2E1329),
            border = Color(0xFF5D2653),
            inkPrimary = Color(0xFFFFF2FA),
            inkSecondary = Color(0xFFD2A3C7),
            heroStart = Color(0xFF3E1036),
            heroEnd = Color(0xFF1A0617),
            surfaceTint = Color(0xFF32112D)
        )
    } else {
        ElementalThemePalette(
            theme = theme,
            isDark = false,
            primary = Color(0xFFC72B84),
            primaryBright = Color(0xFFFF78C4),
            secondary = Color(0xFF8C4FC4),
            glowCore = Color(0xFFFF74C3),
            glowOuter = Color(0xFFFFC4E6),
            onPrimary = Color.White,
            pageBgTop = Color(0xFFFCE6F4),
            pageBgMid = Color(0xFFFEF5FA),
            pageBgBottom = Color(0xFFFBEAF5),
            surfaceCard = Color(0xFFFFFFFF),
            surfaceElevated = Color(0xFFFBE4F2),
            border = Color(0xFFEDBEE0),
            inkPrimary = Color(0xFF240D1B),
            inkSecondary = Color(0xFF6E435D),
            heroStart = Color(0xFF380F30),
            heroEnd = Color(0xFF1B0718),
            surfaceTint = Color(0xFFFCE2F1)
        )
    }

    ElementalTheme.CHERRY_BLOSSOM -> if (oledMode) {
        ElementalThemePalette(
            theme = theme,
            isDark = true,
            primary = Color(0xFFFF8FB8),
            primaryBright = Color(0xFFFFD0E2),
            secondary = Color(0xFFFFB296),
            glowCore = Color(0xFFFF9EC3),
            glowOuter = Color(0xFFB53B6A),
            onPrimary = Color(0xFF260915),
            pageBgTop = Color(0xFF1B0B13),
            pageBgMid = Color(0xFF12070D),
            pageBgBottom = Color(0xFF0A0408),
            surfaceCard = Color(0xFF231019),
            surfaceElevated = Color(0xFF311724),
            border = Color(0xFF63304A),
            inkPrimary = Color(0xFFFFF4F8),
            inkSecondary = Color(0xFFD9AEC0),
            heroStart = Color(0xFF47182E),
            heroEnd = Color(0xFF1D0A14),
            surfaceTint = Color(0xFF361626)
        )
    } else {
        ElementalThemePalette(
            theme = theme,
            isDark = false,
            primary = Color(0xFFD64572),
            primaryBright = Color(0xFFFF94B8),
            secondary = Color(0xFFE58A65),
            glowCore = Color(0xFFFF9EC0),
            glowOuter = Color(0xFFFFD6E5),
            onPrimary = Color.White,
            pageBgTop = Color(0xFFFFEBF2),
            pageBgMid = Color(0xFFFFF7FA),
            pageBgBottom = Color(0xFFFCEEF3),
            surfaceCard = Color(0xFFFFFFFF),
            surfaceElevated = Color(0xFFFCE4ED),
            border = Color(0xFFF1C4D5),
            inkPrimary = Color(0xFF2B141E),
            inkSecondary = Color(0xFF73485B),
            heroStart = Color(0xFF44172B),
            heroEnd = Color(0xFF210A15),
            surfaceTint = Color(0xFFFDE3EC)
        )
    }

    ElementalTheme.SNOW -> if (oledMode) {
        ElementalThemePalette(
            theme = theme,
            isDark = true,
            primary = Color(0xFF8FD7FF),
            primaryBright = Color(0xFFDCF3FF),
            secondary = Color(0xFFA6B8FF),
            glowCore = Color(0xFFA2E0FF),
            glowOuter = Color(0xFF2C6FA8),
            onPrimary = Color(0xFF061626),
            pageBgTop = Color(0xFF081421),
            pageBgMid = Color(0xFF050E18),
            pageBgBottom = Color(0xFF03080F),
            surfaceCard = Color(0xFF0E1D2D),
            surfaceElevated = Color(0xFF162A40),
            border = Color(0xFF2D5175),
            inkPrimary = Color(0xFFF2FAFF),
            inkSecondary = Color(0xFFA8C6DF),
            heroStart = Color(0xFF122C47),
            heroEnd = Color(0xFF081524),
            surfaceTint = Color(0xFF142B42)
        )
    } else {
        ElementalThemePalette(
            theme = theme,
            isDark = false,
            primary = Color(0xFF2A78B8),
            primaryBright = Color(0xFF7AC7FA),
            secondary = Color(0xFF6A7FC4),
            glowCore = Color(0xFF8CD3FF),
            glowOuter = Color(0xFFD1ECFF),
            onPrimary = Color.White,
            pageBgTop = Color(0xFFE6F4FD),
            pageBgMid = Color(0xFFF4FAFF),
            pageBgBottom = Color(0xFFE9F4FC),
            surfaceCard = Color(0xFFFFFFFF),
            surfaceElevated = Color(0xFFE0F0FA),
            border = Color(0xFFBAD8EE),
            inkPrimary = Color(0xFF0E1F2E),
            inkSecondary = Color(0xFF46647D),
            heroStart = Color(0xFF122B44),
            heroEnd = Color(0xFF091726),
            surfaceTint = Color(0xFFDFF0FC)
        )
    }

    ElementalTheme.RAIN -> if (oledMode) {
        ElementalThemePalette(
            theme = theme,
            isDark = true,
            primary = Color(0xFF4FD1E8),
            primaryBright = Color(0xFFB0EEFA),
            secondary = Color(0xFF7E9BF2),
            glowCore = Color(0xFF5ADAF0),
            glowOuter = Color(0xFF176985),
            onPrimary = Color(0xFF04171F),
            pageBgTop = Color(0xFF06131A),
            pageBgMid = Color(0xFF040D13),
            pageBgBottom = Color(0xFF02080C),
            surfaceCard = Color(0xFF0B1C25),
            surfaceElevated = Color(0xFF122936),
            border = Color(0xFF244F63),
            inkPrimary = Color(0xFFEDF9FC),
            inkSecondary = Color(0xFF9BC2D1),
            heroStart = Color(0xFF0E2E3D),
            heroEnd = Color(0xFF06161F),
            surfaceTint = Color(0xFF102A36)
        )
    } else {
        ElementalThemePalette(
            theme = theme,
            isDark = false,
            primary = Color(0xFF1F6B82),
            primaryBright = Color(0xFF52B6D1),
            secondary = Color(0xFF49629E),
            glowCore = Color(0xFF5BC2DC),
            glowOuter = Color(0xFFB5E3F0),
            onPrimary = Color.White,
            pageBgTop = Color(0xFFDFEEF4),
            pageBgMid = Color(0xFFF0F7FA),
            pageBgBottom = Color(0xFFE3EFF4),
            surfaceCard = Color(0xFFFFFFFF),
            surfaceElevated = Color(0xFFDCEBF0),
            border = Color(0xFFB1D1DE),
            inkPrimary = Color(0xFF0F2029),
            inkSecondary = Color(0xFF426270),
            heroStart = Color(0xFF102D3A),
            heroEnd = Color(0xFF071720),
            surfaceTint = Color(0xFFD9ECF2)
        )
    }
}

val LocalElementalPalette = staticCompositionLocalOf {
    elementalPaletteFor(ElementalTheme.BLOOM, oledMode = false)
}

object OneReflectionColors {
    // Day Theme — Sunlit Sky, Cloud Ivory, Spotted Mushroom Red & Meadow Grass
    val SurfaceWarm = Color(0xFFEEF6FC)
    val SurfaceCard = Color(0xFFFFFFFF)
    val SurfaceCardLow = Color(0xFFE2F0FA)
    val SurfaceCardHigh = Color(0xFFD2E7F7)
    val HairlineLight = Color(0xFFC9DFEF)

    // Ink Hierarchy (Crisp Slate-Navy on Sunlit Sky)
    val InkPrimary = Color(0xFF17222D)
    val InkSecondary = Color(0xFF495E70)
    val InkMuted = Color(0xFF72899C)
    val OutlineSoft = Color(0xFFBCD4E6)

    // Night Theme — Starlit Midnight Navy & Bioluminescent Cyan Glow
    val OledBlack = Color(0xFF070B18)
    val OledSurface = Color(0xFF111930)
    val OledSurfaceElevated = Color(0xFF1A2645)
    val OledBorder = Color(0xFF2B3E68)

    // Day/Night Enchanted Sanctuary Accents
    val Terracotta = Color(0xFFDF4435)
    val TerracottaLight = Color(0xFF4CF5E2)
    val TerracottaDeep = Color(0xFFA6261A)
    val PeachMist = Color(0xFFFDEBE8)
    val PeachInk = Color(0xFF5C1B15)

    val BurnishedGold = Color(0xFFD9931E)
    val GoldLuminous = Color(0xFFF8D66D)
    val HoneyMist = Color(0xFFFFF6DC)
    val HoneyInk = Color(0xFF734D08)

    val SageDeep = Color(0xFF398737)
    val SageLight = Color(0xFF6EEB9B)
    val SageMist = Color(0xFFE4F4E2)

    val IndigoDeep = Color(0xFF2D6CA3)
    val IndigoLight = Color(0xFF8FB8FF)
    val IndigoMist = Color(0xFFDFF0FD)

    val PlumAccent = Color(0xFF7A4E85)
    val LilacMist = Color(0xFFF1E7F5)

    // Semantic Legacy Token Aliases
    val PlumPrimary = Terracotta
    val CandyPink = PeachMist
    val CandyPinkDeep = TerracottaDeep
    val BlushPink = PeachMist
    val BlushPinkInk = PeachInk
    val RosePink = TerracottaLight

    val ButterYellow = HoneyMist
    val ButterYellowBright = GoldLuminous
    val ButterInk = HoneyInk

    val LavenderBlue = IndigoMist
    val LavenderBorder = Color(0xFFBDD8EE)
    val LavenderInk = IndigoDeep
    val LavenderMuted = Color(0xFF4C6B8A)
    val SlateBlue = IndigoDeep

    val Matcha = SageMist
    val MatchaInk = SageDeep
    val WarmLilac = LilacMist
    val SoftCyan = Color(0xFFDCF6F4)
    val SoftCyanInk = Color(0xFF12635E)

    val HighPrioBg = Color(0xFFFCE4E1)
    val HighPrioInk = Color(0xFFB3281E)
    val DangerInk = Color(0xFF8C1D18)
}

fun formatLiters(value: Double): String {
    val rounded = kotlin.math.round(value * 100.0) / 100.0
    return if (rounded % 0.1 == 0.0 || kotlin.math.abs((rounded * 10) - kotlin.math.round(rounded * 10)) < 0.001) {
        String.format(Locale.US, "%.1f", rounded)
    } else {
        String.format(Locale.US, "%.2f", rounded)
    }
}

fun categoryPreviewColor(color: CategoryColor): Color = when (color) {
    CategoryColor.PINK -> OneReflectionColors.Terracotta
    CategoryColor.YELLOW -> OneReflectionColors.BurnishedGold
    CategoryColor.LAVENDER -> OneReflectionColors.IndigoDeep
    CategoryColor.MATCHA -> OneReflectionColors.SageDeep
    CategoryColor.LILAC -> OneReflectionColors.PlumAccent
    CategoryColor.CYAN -> OneReflectionColors.SoftCyanInk
    CategoryColor.SLATE -> OneReflectionColors.InkSecondary
}

fun categoryCardBackground(color: CategoryColor, oledMode: Boolean): Color {
    if (oledMode) return OneReflectionColors.OledSurface
    return when (color) {
        CategoryColor.PINK -> OneReflectionColors.PeachMist
        CategoryColor.YELLOW -> OneReflectionColors.HoneyMist
        CategoryColor.LAVENDER -> OneReflectionColors.IndigoMist
        CategoryColor.MATCHA -> OneReflectionColors.SageMist
        CategoryColor.LILAC -> OneReflectionColors.LilacMist
        CategoryColor.CYAN -> OneReflectionColors.SoftCyan
        CategoryColor.SLATE -> OneReflectionColors.SurfaceCard
    }
}

fun priorityBadgeColors(priority: TaskPriority): Pair<Color, Color> = when (priority) {
    TaskPriority.HIGH -> OneReflectionColors.HighPrioBg to OneReflectionColors.HighPrioInk
    TaskPriority.MEDIUM -> OneReflectionColors.HoneyMist to OneReflectionColors.HoneyInk
    TaskPriority.LOW -> OneReflectionColors.SageMist to OneReflectionColors.SageDeep
}

val DisplayFontFamily: FontFamily = FontFamily(
    Font(R.font.fraunces, FontWeight.Normal),
    Font(R.font.fraunces, FontWeight.Medium),
    Font(R.font.fraunces, FontWeight.SemiBold),
    Font(R.font.fraunces, FontWeight.Bold)
)

val BodyFontFamily: FontFamily = FontFamily(
    Font(R.font.dm_sans, FontWeight.Normal),
    Font(R.font.dm_sans, FontWeight.Medium),
    Font(R.font.dm_sans, FontWeight.SemiBold),
    Font(R.font.dm_sans, FontWeight.Bold)
)

val MonoFontFamily: FontFamily = FontFamily(
    Font(R.font.jetbrains_mono, FontWeight.Normal),
    Font(R.font.jetbrains_mono, FontWeight.Medium),
    Font(R.font.jetbrains_mono, FontWeight.Bold)
)

val SpaceGroteskFontFamily: FontFamily = DisplayFontFamily
val PlusJakartaFontFamily: FontFamily = BodyFontFamily

private val OneReflectionTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = DisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 44.sp,
        lineHeight = 48.sp,
        letterSpacing = (-1.2).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = DisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 35.sp,
        letterSpacing = (-0.6).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = DisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 23.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.3).sp
    ),
    titleLarge = TextStyle(
        fontFamily = DisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 21.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = MonoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.8.sp
    )
)

@Composable
private fun animateThemeColor(target: Color): Color {
    val color by animateColorAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 660, easing = SanctuaryEase),
        label = "elemental_color_anim"
    )
    return color
}

@Composable
private fun animatedPalette(target: ElementalThemePalette): ElementalThemePalette {
    val darkProgress by animateFloatAsState(
        targetValue = if (target.isDark) 1f else 0f,
        animationSpec = tween(durationMillis = 660, easing = SanctuaryEase),
        label = "theme_dark_progress"
    )
    return target.copy(
        darkProgress = darkProgress,
        primary = animateThemeColor(target.primary),
        primaryBright = animateThemeColor(target.primaryBright),
        secondary = animateThemeColor(target.secondary),
        glowCore = animateThemeColor(target.glowCore),
        glowOuter = animateThemeColor(target.glowOuter),
        onPrimary = animateThemeColor(target.onPrimary),
        pageBgTop = animateThemeColor(target.pageBgTop),
        pageBgMid = animateThemeColor(target.pageBgMid),
        pageBgBottom = animateThemeColor(target.pageBgBottom),
        surfaceCard = animateThemeColor(target.surfaceCard),
        surfaceElevated = animateThemeColor(target.surfaceElevated),
        border = animateThemeColor(target.border),
        inkPrimary = animateThemeColor(target.inkPrimary),
        inkSecondary = animateThemeColor(target.inkSecondary),
        heroStart = animateThemeColor(target.heroStart),
        heroEnd = animateThemeColor(target.heroEnd),
        surfaceTint = animateThemeColor(target.surfaceTint)
    )
}

@Composable
fun OneReflectionTheme(
    oledMode: Boolean,
    elementalTheme: ElementalTheme = ElementalTheme.BLOOM,
    content: @Composable () -> Unit
) {
    val palette = animatedPalette(elementalPaletteFor(elementalTheme, oledMode))

    val onPrimaryContainer = animateThemeColor(if (oledMode) palette.primaryBright else palette.inkPrimary)
    val onSecondary = animateThemeColor(if (oledMode) palette.onPrimary else Color.White)
    val onSecondaryContainer = animateThemeColor(if (oledMode) palette.secondary else palette.inkPrimary)
    val tertiary = animateThemeColor(if (oledMode) OneReflectionColors.SageLight else OneReflectionColors.SageDeep)
    val tertiaryContainer = animateThemeColor(if (oledMode) Color(0xFF112C22) else OneReflectionColors.SageMist)
    val onTertiaryContainer = animateThemeColor(if (oledMode) Color(0xFFCFFCE0) else OneReflectionColors.SageDeep)

    val scheme: ColorScheme = if (oledMode) {
        darkColorScheme(
            primary = palette.primary,
            onPrimary = palette.onPrimary,
            primaryContainer = palette.surfaceTint,
            onPrimaryContainer = onPrimaryContainer,
            secondary = palette.secondary,
            onSecondary = onSecondary,
            secondaryContainer = palette.surfaceElevated,
            onSecondaryContainer = onSecondaryContainer,
            tertiary = tertiary,
            tertiaryContainer = tertiaryContainer,
            onTertiaryContainer = onTertiaryContainer,
            background = palette.pageBgMid,
            onBackground = palette.inkPrimary,
            surface = palette.surfaceCard,
            onSurface = palette.inkPrimary,
            surfaceVariant = palette.surfaceElevated,
            onSurfaceVariant = palette.inkSecondary,
            outline = palette.border
        )
    } else {
        lightColorScheme(
            primary = palette.primary,
            onPrimary = palette.onPrimary,
            primaryContainer = palette.surfaceTint,
            onPrimaryContainer = onPrimaryContainer,
            secondary = palette.secondary,
            onSecondary = onSecondary,
            secondaryContainer = palette.surfaceElevated,
            onSecondaryContainer = onSecondaryContainer,
            tertiary = tertiary,
            tertiaryContainer = tertiaryContainer,
            onTertiaryContainer = onTertiaryContainer,
            background = palette.pageBgMid,
            onBackground = palette.inkPrimary,
            surface = palette.surfaceCard,
            onSurface = palette.inkPrimary,
            surfaceVariant = palette.surfaceElevated,
            onSurfaceVariant = palette.inkSecondary,
            outline = palette.border
        )
    }

    CompositionLocalProvider(LocalElementalPalette provides palette) {
        MaterialTheme(
            colorScheme = scheme,
            typography = OneReflectionTypography,
            content = content
        )
    }
}
