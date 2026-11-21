package com.onereflection.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.model.ElementalTheme
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ScreenId
import com.onereflection.app.model.ThemeApplyScope
import com.onereflection.app.model.ThemeModeSlot
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.SanctuaryEase
import com.onereflection.app.ui.theme.elementalPaletteFor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

fun elementalIcon(theme: ElementalTheme): ImageVector = when (theme) {
    ElementalTheme.EMBER -> Icons.Filled.LocalFireDepartment
    ElementalTheme.SOLAR -> Icons.Filled.WbSunny
    ElementalTheme.BREEZE -> Icons.Filled.Air
    ElementalTheme.BLOOM -> Icons.Filled.Spa
    ElementalTheme.CHERRY_BLOSSOM -> Icons.Filled.LocalFlorist
    ElementalTheme.SNOW -> Icons.Filled.AcUnit
    ElementalTheme.RAIN -> Icons.Filled.WaterDrop
}

/**
 * Full-page living elemental atmosphere synced to BOTH the selected Day's ElementalTheme
 * (Ember / Solar / Breeze / Bloom) AND Light/Dark mode from the EnchantedDayNightToggle,
 * with smooth twilight/sunrise reveal waves and per-day elemental ripples!
 */
@Composable
fun ElementalPageAtmosphereCanvas(
    theme: ElementalTheme,
    isDark: Boolean,
    daySelectionKey: Int = 0,
    modifier: Modifier = Modifier
) {
    val palette = LocalElementalPalette.current
    val infinite = rememberInfiniteTransition(label = "page_elemental_atmosphere")
    val slowLoop by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "page_loop"
    )
    val pulse by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "page_pulse"
    )

    val darkBlend by animateFloatAsState(
        targetValue = if (isDark) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = SanctuaryEase),
        label = "atmosphere_dark_blend"
    )

    // Smooth celestial wave sweeping from the top-right Day/Night toggle when switching Light ↔ Dark
    val themeWave = remember { Animatable(1f) }
    var previousDark by remember { mutableStateOf(isDark) }
    LaunchedEffect(isDark) {
        if (isDark != previousDark) {
            previousDark = isDark
            themeWave.snapTo(0f)
            themeWave.animateTo(1f, animationSpec = tween(durationMillis = 820, easing = SanctuaryEase))
        }
    }

    // Smooth elemental bloom wave sweeping down from the Day Strip when switching days/themes
    val dayWave = remember { Animatable(1f) }
    var previousDayPair by remember { mutableStateOf(theme to daySelectionKey) }
    LaunchedEffect(theme, daySelectionKey) {
        val currentPair = theme to daySelectionKey
        if (currentPair != previousDayPair) {
            previousDayPair = currentPair
            dayWave.snapTo(0f)
            dayWave.animateTo(1f, animationSpec = tween(durationMillis = 760, easing = SanctuaryEase))
        }
    }

    val emberMix by animateFloatAsState(if (theme == ElementalTheme.EMBER) 1f else 0f, tween(700, easing = SanctuaryEase), label = "ember_mix")
    val solarMix by animateFloatAsState(if (theme == ElementalTheme.SOLAR) 1f else 0f, tween(700, easing = SanctuaryEase), label = "solar_mix")
    val breezeMix by animateFloatAsState(if (theme == ElementalTheme.BREEZE) 1f else 0f, tween(700, easing = SanctuaryEase), label = "breeze_mix")
    val bloomMix by animateFloatAsState(if (theme == ElementalTheme.BLOOM) 1f else 0f, tween(700, easing = SanctuaryEase), label = "bloom_mix")
    val sakuraMix by animateFloatAsState(if (theme == ElementalTheme.CHERRY_BLOSSOM) 1f else 0f, tween(700, easing = SanctuaryEase), label = "sakura_mix")
    val snowMix by animateFloatAsState(if (theme == ElementalTheme.SNOW) 1f else 0f, tween(700, easing = SanctuaryEase), label = "snow_mix")
    val rainMix by animateFloatAsState(if (theme == ElementalTheme.RAIN) 1f else 0f, tween(700, easing = SanctuaryEase), label = "rain_mix")

    Canvas(modifier = modifier.fillMaxSize()) {
        // 1. Base Smooth Page Gradient matching active ElementalTheme x Light/Dark
        drawRect(
            brush = Brush.verticalGradient(
                0.0f to palette.pageBgTop,
                0.45f to palette.pageBgMid,
                1.0f to palette.pageBgBottom
            )
        )

        val alphaScale = 0.72f + 0.28f * darkBlend

        // 2. EMBER: Warm volcanic top-aura + rising licking flame veil + floating glowing ember sparks
        if (emberMix > 0.01f) {
            val a = emberMix * alphaScale
            val topGlowCenter = Offset(size.width * 0.5f, size.height * 0.08f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF6A26).copy(alpha = (0.20f + 0.08f * darkBlend) * a),
                        Color(0xFFD92614).copy(alpha = (0.08f + 0.04f * darkBlend) * a),
                        Color.Transparent
                    ),
                    center = topGlowCenter,
                    radius = size.width * 0.85f
                ),
                radius = size.width * 0.85f,
                center = topGlowCenter
            )
            for (i in 0 until 14) {
                val progressY = (slowLoop + i * 0.13f) % 1f
                val y = size.height * (1.05f - progressY * 1.1f)
                val x = size.width * ((i * 0.17f + 0.08f) % 0.88f + 0.06f) + sin(pulse + i) * 14.dp.toPx()
                val sparkR = (2.2f + (i % 3) * 1.2f).dp.toPx()
                val sparkAlpha = sin(progressY * PI).toFloat().coerceIn(0f, 1f) * a * (0.45f + 0.30f * darkBlend)
                drawCircle(
                    color = if (i % 2 == 0) Color(0xFFFFA542).copy(alpha = sparkAlpha) else Color(0xFFFF5224).copy(alpha = sparkAlpha),
                    radius = sparkR,
                    center = Offset(x, y)
                )
            }
        }

        // 3. SOLAR: Radiant rotating volumetric sunbeams + golden sunlit dust motes
        if (solarMix > 0.01f) {
            val a = solarMix * alphaScale
            val sunOrigin = Offset(size.width * 0.5f, size.height * 0.04f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFDB63).copy(alpha = (0.34f - 0.08f * darkBlend) * a),
                        Color(0xFFFFB224).copy(alpha = (0.14f - 0.04f * darkBlend) * a),
                        Color.Transparent
                    ),
                    center = sunOrigin,
                    radius = size.width * 0.9f
                ),
                radius = size.width * 0.9f,
                center = sunOrigin
            )
            val rayCount = 12
            val rotDeg = slowLoop * 360f * 0.25f
            rotate(degrees = rotDeg, pivot = sunOrigin) {
                for (i in 0 until rayCount) {
                    val angle = i * (360f / rayCount)
                    rotate(degrees = angle, pivot = sunOrigin) {
                        val beamPath = Path().apply {
                            moveTo(sunOrigin.x, sunOrigin.y)
                            lineTo(sunOrigin.x - size.width * 0.06f, sunOrigin.y + size.height * 0.65f)
                            lineTo(sunOrigin.x + size.width * 0.06f, sunOrigin.y + size.height * 0.65f)
                            close()
                        }
                        drawPath(
                            path = beamPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFFE27A).copy(alpha = (0.12f - 0.04f * darkBlend) * a),
                                    Color.Transparent
                                ),
                                startY = sunOrigin.y,
                                endY = sunOrigin.y + size.height * 0.58f
                            )
                        )
                    }
                }
            }
        }

        // 4. BREEZE: Sweeping concentric orbital wind-current arcs & mint-aqua clarity wash
        if (breezeMix > 0.01f) {
            val a = breezeMix * alphaScale
            val center = Offset(size.width * 0.52f, size.height * 0.26f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF52F5C8).copy(alpha = (0.22f - 0.02f * darkBlend) * a),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width * 0.8f
                ),
                radius = size.width * 0.8f,
                center = center
            )
            val radii = listOf(size.width * 0.34f, size.width * 0.48f, size.width * 0.64f)
            radii.forEachIndexed { idx, r ->
                val startAng = (slowLoop * 360f * (if (idx % 2 == 0) 1f else -0.7f)) + idx * 75f
                drawArc(
                    color = Color(0xFF52F5C8).copy(alpha = (0.22f + 0.04f * darkBlend) * a),
                    startAngle = startAng,
                    sweepAngle = 115f - idx * 14f,
                    useCenter = false,
                    topLeft = Offset(center.x - r, center.y - r),
                    size = Size(r * 2f, r * 2f),
                    style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // 5. BLOOM: Velvet orchid glow + drifting translucent lotus petals
        if (bloomMix > 0.01f) {
            val a = bloomMix * alphaScale
            val bloomCenter = Offset(size.width * 0.5f, size.height * 0.14f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF72C6).copy(alpha = (0.20f + 0.04f * darkBlend) * a),
                        Color(0xFFAD247B).copy(alpha = (0.07f + 0.03f * darkBlend) * a),
                        Color.Transparent
                    ),
                    center = bloomCenter,
                    radius = size.width * 0.82f
                ),
                radius = size.width * 0.82f,
                center = bloomCenter
            )
            for (i in 0 until 12) {
                val p = (slowLoop + i * 0.11f) % 1f
                val px = size.width * ((i * 0.23f + 0.05f) % 0.9f) + sin(pulse + i * 1.3f) * 20.dp.toPx()
                val py = size.height * p
                val petalAlpha = sin(p * PI).toFloat().coerceIn(0f, 1f) * a * (0.44f + 0.18f * darkBlend)
                rotate(degrees = (slowLoop * 180f + i * 42f), pivot = Offset(px, py)) {
                    drawOval(
                        color = if (i % 2 == 0) Color(0xFFFF8FD6).copy(alpha = petalAlpha) else Color(0xFFFFC2EA).copy(alpha = petalAlpha),
                        topLeft = Offset(px - 4.5.dp.toPx(), py - 2.2.dp.toPx()),
                        size = Size(9.dp.toPx(), 4.4.dp.toPx())
                    )
                }
            }
        }

        // 5b. CHERRY BLOSSOM: Soft sakura canopy glow + gently swaying drifting sakura petals
        if (sakuraMix > 0.01f) {
            val a = sakuraMix * alphaScale
            val canopyOrigin = Offset(size.width * 0.78f, size.height * 0.10f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF9EC3).copy(alpha = (0.28f + 0.05f * darkBlend) * a),
                        Color(0xFFD64572).copy(alpha = (0.09f + 0.03f * darkBlend) * a),
                        Color.Transparent
                    ),
                    center = canopyOrigin,
                    radius = size.width * 0.86f
                ),
                radius = size.width * 0.86f,
                center = canopyOrigin
            )
            for (i in 0 until 18) {
                val progress = (slowLoop * (0.85f + (i % 3) * 0.18f) + i * 0.07f) % 1f
                val driftX = size.width * ((1.05f - progress * 0.55f + (i * 0.19f)) % 1.0f) +
                    sin(pulse + i * 1.15f) * 24.dp.toPx()
                val driftY = size.height * progress
                val petalAlpha = sin(progress * PI).toFloat().coerceIn(0f, 1f) * a * (0.58f + 0.18f * darkBlend)
                val pSize = (5.2f + (i % 3) * 1.8f).dp.toPx()
                rotate(degrees = slowLoop * 240f + i * 37f, pivot = Offset(driftX, driftY)) {
                    val sakuraPath = Path().apply {
                        moveTo(driftX, driftY - pSize * 0.65f)
                        cubicTo(
                            driftX + pSize * 0.85f, driftY - pSize * 0.35f,
                            driftX + pSize * 0.7f, driftY + pSize * 0.75f,
                            driftX, driftY + pSize * 0.45f
                        )
                        cubicTo(
                            driftX - pSize * 0.7f, driftY + pSize * 0.75f,
                            driftX - pSize * 0.85f, driftY - pSize * 0.35f,
                            driftX, driftY - pSize * 0.65f
                        )
                        close()
                    }
                    drawPath(
                        path = sakuraPath,
                        color = if (i % 2 == 0) {
                            Color(0xFFFFB5CF).copy(alpha = petalAlpha)
                        } else {
                            Color(0xFFFFDCE9).copy(alpha = petalAlpha)
                        }
                    )
                }
            }
        }

        // 5c. SNOW: Arctic aurora shimmer + multi-layer falling crystal snowflakes
        if (snowMix > 0.01f) {
            val a = snowMix * alphaScale
            val auroraCenter = Offset(size.width * 0.35f, size.height * 0.12f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF9ADFFF).copy(alpha = (0.24f + 0.06f * darkBlend) * a),
                        Color(0xFF5D99D6).copy(alpha = (0.09f + 0.04f * darkBlend) * a),
                        Color.Transparent
                    ),
                    center = auroraCenter,
                    radius = size.width * 0.90f
                ),
                radius = size.width * 0.90f,
                center = auroraCenter
            )
            for (i in 0 until 22) {
                val speed = 0.72f + (i % 4) * 0.16f
                val progress = (slowLoop * speed + i * 0.09f) % 1f
                val sx = size.width * ((i * 0.17f + 0.04f) % 0.94f + 0.03f) +
                    sin(pulse * 0.9f + i * 0.8f) * 14.dp.toPx()
                val sy = size.height * progress
                val flakeAlpha = sin(progress * PI).toFloat().coerceIn(0f, 1f) * a * (0.55f + 0.30f * darkBlend)
                val r = (2.1f + (i % 4) * 1.15f).dp.toPx()
                val flakeCol = if (darkBlend > 0.5f) {
                    Color(0xFFE6F7FF).copy(alpha = flakeAlpha)
                } else {
                    Color(0xFF4CA3DE).copy(alpha = flakeAlpha * 0.85f)
                }
                if (i % 3 == 0) {
                    // 6-spoke crystal snowflake
                    rotate(degrees = slowLoop * 120f + i * 25f, pivot = Offset(sx, sy)) {
                        for (spoke in 0 until 3) {
                            val ang = (spoke * 60.0) * PI / 180.0
                            val dx = (cos(ang) * r * 1.6f).toFloat()
                            val dy = (sin(ang) * r * 1.6f).toFloat()
                            drawLine(
                                color = flakeCol,
                                start = Offset(sx - dx, sy - dy),
                                end = Offset(sx + dx, sy + dy),
                                strokeWidth = 1.2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                } else {
                    drawCircle(color = flakeCol, radius = r, center = Offset(sx, sy))
                }
            }
        }

        // 5d. RAIN: Misty monsoon wash + diagonal falling rain streaks + expanding concentric ripples
        if (rainMix > 0.01f) {
            val a = rainMix * alphaScale
            val mistCenter = Offset(size.width * 0.5f, size.height * 0.08f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF52CBE3).copy(alpha = (0.20f + 0.06f * darkBlend) * a),
                        Color(0xFF1F6B82).copy(alpha = (0.09f + 0.04f * darkBlend) * a),
                        Color.Transparent
                    ),
                    center = mistCenter,
                    radius = size.width * 0.92f
                ),
                radius = size.width * 0.92f,
                center = mistCenter
            )
            // Slanted glistening rain streaks
            for (i in 0 until 24) {
                val p = (slowLoop * 2.2f + i * 0.13f) % 1f
                val startX = size.width * ((i * 0.137f + 0.05f) % 0.96f)
                val startY = size.height * p - 24.dp.toPx()
                val streakLen = (18f + (i % 4) * 8f).dp.toPx()
                val slant = streakLen * 0.20f
                val dropAlpha = sin(p * PI).toFloat().coerceIn(0f, 1f) * a * (0.42f + 0.25f * darkBlend)
                val dropCol = if (darkBlend > 0.5f) {
                    Color(0xFF9EEBF8).copy(alpha = dropAlpha)
                } else {
                    Color(0xFF2884A1).copy(alpha = dropAlpha)
                }
                drawLine(
                    color = dropCol,
                    start = Offset(startX, startY),
                    end = Offset(startX - slant, startY + streakLen),
                    strokeWidth = 1.3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            // Soft puddle ripple rings near bottom horizon
            for (rIdx in 0 until 3) {
                val rp = (slowLoop * 1.4f + rIdx * 0.33f) % 1f
                val rx = size.width * (0.22f + rIdx * 0.28f)
                val ry = size.height * (0.78f + (rIdx % 2) * 0.08f)
                val radX = (14f + rp * 54f).dp.toPx()
                val radY = radX * 0.34f
                val rAlpha = (1f - rp) * a * (0.34f + 0.14f * darkBlend)
                drawOval(
                    color = Color(0xFF6CDDF2).copy(alpha = rAlpha),
                    topLeft = Offset(rx - radX, ry - radY),
                    size = Size(radX * 2f, radY * 2f),
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }
        }

        // 6. Smoothly crossfading pixel starlight cross-sparkles (+) tied to darkBlend
        if (darkBlend > 0.01f) {
            val stars = listOf(
                Triple(0.11f, 0.09f, 0.0f),
                Triple(0.86f, 0.08f, 1.3f),
                Triple(0.75f, 0.20f, 2.4f),
                Triple(0.16f, 0.28f, 0.8f),
                Triple(0.90f, 0.34f, 1.9f)
            )
            val s = 2.2.dp.toPx()
            stars.forEach { (fx, fy, ph) ->
                val cx = size.width * fx
                val cy = size.height * fy
                val tw = (0.25f + 0.55f * sin(pulse + ph)).coerceIn(0.15f, 0.78f) * darkBlend
                val starCol = palette.primaryBright.copy(alpha = tw)
                drawRect(starCol, Offset(cx - s * 0.5f, cy - s * 1.5f), Size(s, s * 3f))
                drawRect(starCol, Offset(cx - s * 1.5f, cy - s * 0.5f), Size(s * 3f, s))
            }
        }

        // 7. Smooth Dark ↔ Light Celestial Horizon Sweep emanating from the top-right toggle orb
        val twProg = themeWave.value
        if (twProg < 0.995f) {
            val origin = Offset(size.width * 0.74f, size.height * 0.06f)
            val maxR = hypot(size.width, size.height) * 1.15f
            val curR = (maxR * twProg).coerceAtLeast(1f)
            val fade = (1f - twProg) * sin(twProg * PI).toFloat().coerceIn(0f, 1f)
            val waveTint = if (isDark) Color(0xFF4CF5E2) else Color(0xFFFFE599)
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to Color.Transparent,
                    0.72f to waveTint.copy(alpha = 0.04f * fade),
                    0.92f to waveTint.copy(alpha = 0.24f * fade),
                    1.0f to Color.Transparent,
                    center = origin,
                    radius = curR
                ),
                radius = curR,
                center = origin
            )
        }

        // 8. Smooth Per-Day Elemental Aura Bloom Ripple sweeping from the Day Strip across the page
        val dwProg = dayWave.value
        if (dwProg < 0.995f) {
            val origin = Offset(size.width * 0.5f, size.height * 0.18f)
            val maxR = hypot(size.width, size.height) * 0.95f
            val curR = (maxR * dwProg).coerceAtLeast(1f)
            val fade = (1f - dwProg) * sin(dwProg * PI).toFloat().coerceIn(0f, 1f)
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to Color.Transparent,
                    0.68f to palette.glowCore.copy(alpha = 0.05f * fade),
                    0.90f to palette.glowCore.copy(alpha = 0.26f * fade),
                    1.0f to Color.Transparent,
                    center = origin,
                    radius = curR
                ),
                radius = curR,
                center = origin
            )
        }
    }
}

/**
 * Miniature Elemental Day Pill for the 7-day strip (M 13 .. S 19).
 * Features smooth spring lift, animated text/surface colors, glowing elemental halo crossfade,
 * and morphing active dot indicator when switching between days!
 */
@Composable
fun ElementalDayPill(
    dayLabel: String,
    dayNumber: String,
    theme: ElementalTheme,
    selected: Boolean,
    oledMode: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayPalette = elementalPaletteFor(theme, oledMode)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val infinite = rememberInfiniteTransition(label = "day_pill_glow")
    val glowPulse by infinite.animateFloat(
        initialValue = 0.78f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pill_pulse"
    )

    val pillScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.92f
            selected -> 1.05f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 420f),
        label = "day_pill_scale"
    )
    val liftY by animateDpAsState(
        targetValue = if (selected) (-2.5).dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 380f),
        label = "day_pill_lift"
    )
    val selectedGlowAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 420, easing = SanctuaryEase),
        label = "day_pill_glow_alpha"
    )
    val dotWidth by animateDpAsState(
        targetValue = if (selected) 14.dp else 5.dp,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 420f),
        label = "day_pill_dot_w"
    )
    val dotHeight by animateDpAsState(
        targetValue = if (selected) 5.5.dp else 5.dp,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 420f),
        label = "day_pill_dot_h"
    )

    val containerBg by animateColorAsState(
        targetValue = when {
            selected -> dayPalette.heroStart
            oledMode -> dayPalette.surfaceCard.copy(alpha = 0.86f)
            else -> Color.White.copy(alpha = 0.90f)
        },
        animationSpec = tween(durationMillis = 480, easing = SanctuaryEase),
        label = "day_pill_bg"
    )
    val borderCol by animateColorAsState(
        targetValue = if (selected) dayPalette.primaryBright else dayPalette.border.copy(alpha = 0.75f),
        animationSpec = tween(durationMillis = 480, easing = SanctuaryEase),
        label = "day_pill_border"
    )
    val labelCol by animateColorAsState(
        targetValue = if (selected) Color.White.copy(alpha = 0.84f) else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 400, easing = SanctuaryEase),
        label = "day_pill_label_col"
    )
    val numberCol by animateColorAsState(
        targetValue = if (selected) Color.White else MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(durationMillis = 400, easing = SanctuaryEase),
        label = "day_pill_num_col"
    )
    val dotCol by animateColorAsState(
        targetValue = if (selected) dayPalette.primaryBright else dayPalette.primary.copy(alpha = 0.68f),
        animationSpec = tween(durationMillis = 420, easing = SanctuaryEase),
        label = "day_pill_dot_col"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pillScale
                scaleY = pillScale
                translationY = liftY.toPx()
            }
            .testTag(testTag)
            .clip(RoundedCornerShape(20.dp))
            .background(containerBg)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        if (selectedGlowAlpha > 0.01f) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            dayPalette.glowCore.copy(alpha = 0.58f * glowPulse * selectedGlowAlpha),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2f, size.height * 0.25f),
                        radius = size.height * 0.76f
                    ),
                    radius = size.height * 0.76f,
                    center = Offset(size.width / 2f, size.height * 0.25f)
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.Transparent,
            border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderCol)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = dayLabel,
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp,
                    color = labelCol
                )
                Text(
                    text = dayNumber,
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.5.sp,
                    color = numberCol
                )
                Box(
                    modifier = Modifier
                        .size(width = dotWidth, height = dotHeight)
                        .clip(CircleShape)
                        .background(dotCol)
                )
            }
        }
    }
}

/**
 * Exact Video-Style Elemental Theme Showcase Carousel:
 * Vertical frosted-glass capsules (`01 Ember`, `02 Solar`, `03 Breeze`, `04 Bloom`)
 * with full Canvas flame tongues, volumetric rotating sunbeams, sweeping breeze vortex arcs,
 * unfurling glowing lotus petals, and glossy dark-floor mirror reflections!
 */
@Composable
fun ElementalThemeCarousel(
    selectedTheme: ElementalTheme,
    onSelectTheme: (ElementalTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeDarkPalette = elementalPaletteFor(selectedTheme, oledMode = true)
    val infinite = rememberInfiniteTransition(label = "carousel_elemental_fx")
    val animPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "carousel_phase"
    )
    val wavePhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "carousel_wave"
    )

    val stageBgTop by animateColorAsState(activeDarkPalette.pageBgTop, tween(520), label = "stage_top")
    val stageBgBottom by animateColorAsState(activeDarkPalette.pageBgBottom, tween(520), label = "stage_bottom")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("elemental_theme_carousel_stage"),
        shape = RoundedCornerShape(30.dp),
        color = stageBgBottom,
        border = BorderStroke(1.dp, activeDarkPalette.border)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0.0f to stageBgTop,
                        0.72f to stageBgBottom,
                        1.0f to Color(0xFF040305)
                    )
                )
                .padding(vertical = 22.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ElementalTheme.entries.forEach { theme ->
                    ElementalShowcaseCapsule(
                        theme = theme,
                        selected = theme == selectedTheme,
                        animPhase = animPhase,
                        wavePhase = wavePhase,
                        onClick = { onSelectTheme(theme) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ElementalShowcaseCapsule(
    theme: ElementalTheme,
    selected: Boolean,
    animPhase: Float,
    wavePhase: Float,
    onClick: () -> Unit
) {
    val palette = elementalPaletteFor(theme, oledMode = true)
    val cardScale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 0.90f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMediumLow),
        label = "capsule_scale"
    )
    val fxAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0.0f,
        animationSpec = tween(420),
        label = "capsule_fx_alpha"
    )
    val capsuleWidth by animateDpAsState(if (selected) 130.dp else 112.dp, spring(), label = "capsule_w")
    val capsuleHeight by animateDpAsState(if (selected) 196.dp else 174.dp, spring(), label = "capsule_h")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(cardScale)
            .testTag("elemental_capsule_${theme.name.lowercase()}")
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .width(capsuleWidth + 46.dp)
                .height(capsuleHeight + 48.dp),
            contentAlignment = Alignment.Center
        ) {
            // Live Signature Elemental Aura Canvas BEHIND the glass capsule!
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val capW = capsuleWidth.toPx()
                val capH = capsuleHeight.toPx()

                if (fxAlpha > 0.01f) {
                    when (theme) {
                        ElementalTheme.EMBER -> drawEmberCapsuleFlames(cx, cy, capW, capH, wavePhase, animPhase, fxAlpha)
                        ElementalTheme.SOLAR -> drawSolarCapsuleRays(cx, cy, capW, capH, animPhase, wavePhase, fxAlpha)
                        ElementalTheme.BREEZE -> drawBreezeCapsuleRings(cx, cy, capW, capH, animPhase, fxAlpha)
                        ElementalTheme.BLOOM -> drawBloomCapsulePetals(cx, cy, capW, capH, wavePhase, fxAlpha)
                        ElementalTheme.CHERRY_BLOSSOM -> drawCherryBlossomCapsule(cx, cy, capW, capH, animPhase, wavePhase, fxAlpha)
                        ElementalTheme.SNOW -> drawSnowCapsule(cx, cy, capW, capH, animPhase, wavePhase, fxAlpha)
                        ElementalTheme.RAIN -> drawRainCapsule(cx, cy, capW, capH, animPhase, fxAlpha)
                    }
                }
            }

            // Translucent Glass Pill Capsule
            Box(
                modifier = Modifier
                    .width(capsuleWidth)
                    .height(capsuleHeight)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(
                        Brush.verticalGradient(
                            colors = if (selected) {
                                listOf(
                                    palette.primaryBright.copy(alpha = 0.88f),
                                    palette.primary.copy(alpha = 0.72f),
                                    palette.heroStart.copy(alpha = 0.92f)
                                )
                            } else {
                                listOf(
                                    Color(0xFF141114).copy(alpha = 0.92f),
                                    palette.heroStart.copy(alpha = 0.88f)
                                )
                            }
                        )
                    )
                    .then(Modifier)
            ) {
                // Crisp Glass Rim Border
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (selected) 0.85f else 0.25f),
                                palette.primaryBright.copy(alpha = if (selected) 0.55f else 0.16f)
                            )
                        ),
                        cornerRadius = CornerRadius(size.width / 2f, size.width / 2f),
                        style = Stroke(width = if (selected) 1.8.dp.toPx() else 1.dp.toPx())
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 18.dp, horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = theme.code,
                        fontFamily = MonoFontFamily,
                        fontSize = 9.5.sp,
                        letterSpacing = 1.sp,
                        color = Color.White.copy(alpha = if (selected) 0.92f else 0.55f)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = elementalIcon(theme),
                            contentDescription = theme.displayName,
                            tint = Color.White,
                            modifier = Modifier.size(if (selected) 30.dp else 24.dp)
                        )
                        Text(
                            text = theme.displayName,
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = if (selected) 21.sp else 17.sp,
                            color = Color.White
                        )
                        Text(
                            text = theme.tagline,
                            fontFamily = MonoFontFamily,
                            fontSize = 7.5.sp,
                            letterSpacing = 1.sp,
                            color = Color.White.copy(alpha = if (selected) 0.9f else 0.6f)
                        )
                    }

                    Spacer(Modifier.height(4.dp))
                }
            }
        }

        // Glossy Dark-Floor Mirror Reflection underneath the capsule (matching video!)
        Box(
            modifier = Modifier
                .width(capsuleWidth * 0.82f)
                .height(24.dp)
                .alpha(if (selected) 0.45f else 0.18f)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            palette.glowCore.copy(alpha = 0.65f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

private fun DrawScope.drawEmberCapsuleFlames(
    cx: Float,
    cy: Float,
    capW: Float,
    capH: Float,
    wavePhase: Float,
    animPhase: Float,
    alpha: Float
) {
    // Hot crimson-orange radial core
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF7E29).copy(alpha = 0.68f * alpha),
                Color(0xFFE02B12).copy(alpha = 0.30f * alpha),
                Color.Transparent
            ),
            center = Offset(cx, cy - capH * 0.12f),
            radius = capH * 0.72f
        ),
        radius = capH * 0.72f,
        center = Offset(cx, cy - capH * 0.12f)
    )

    // Rising licking flame tongues behind top of capsule!
    val tongues = listOf(-0.34f, -0.18f, 0.0f, 0.19f, 0.35f)
    tongues.forEachIndexed { idx, xFrac ->
        val sway = sin(wavePhase * 1.4f + idx * 1.3f) * capW * 0.08f
        val flameH = capH * (0.28f + (idx % 2) * 0.12f + 0.05f * cos(wavePhase + idx))
        val baseX = cx + capW * xFrac
        val baseY = cy - capH * 0.22f
        val flamePath = Path().apply {
            moveTo(baseX - capW * 0.16f, baseY + capH * 0.22f)
            quadraticTo(baseX - capW * 0.12f + sway * 0.4f, baseY - flameH * 0.45f, baseX + sway, baseY - flameH)
            quadraticTo(baseX + capW * 0.14f + sway * 0.4f, baseY - flameH * 0.4f, baseX + capW * 0.16f, baseY + capH * 0.22f)
            close()
        }
        drawPath(
            path = flamePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFE066).copy(alpha = 0.90f * alpha),
                    Color(0xFFFF681F).copy(alpha = 0.80f * alpha),
                    Color(0xFFB81B0B).copy(alpha = 0.20f * alpha)
                ),
                startY = baseY - flameH,
                endY = baseY + capH * 0.22f
            )
        )
    }

    // Drifting sparks above flames
    for (i in 0 until 6) {
        val p = (animPhase + i * 0.17f) % 1f
        val sx = cx + sin(wavePhase + i * 2f) * capW * 0.42f
        val sy = cy - capH * 0.25f - p * capH * 0.42f
        drawCircle(
            color = Color(0xFFFFD87A).copy(alpha = (1f - p) * alpha),
            radius = 2.2.dp.toPx(),
            center = Offset(sx, sy)
        )
    }
}

private fun DrawScope.drawSolarCapsuleRays(
    cx: Float,
    cy: Float,
    capW: Float,
    capH: Float,
    animPhase: Float,
    wavePhase: Float,
    alpha: Float
) {
    val center = Offset(cx, cy)
    val pulse = 0.94f + 0.06f * sin(wavePhase)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFE57F).copy(alpha = 0.65f * alpha),
                Color(0xFFFFAE19).copy(alpha = 0.28f * alpha),
                Color.Transparent
            ),
            center = center,
            radius = capH * 0.72f * pulse
        ),
        radius = capH * 0.72f * pulse,
        center = center
    )

    // Rotating golden starburst rays radiating from capsule
    val rayCount = 16
    rotate(degrees = animPhase * 90f, pivot = center) {
        for (i in 0 until rayCount) {
            rotate(degrees = i * (360f / rayCount), pivot = center) {
                val len = capH * (if (i % 2 == 0) 0.70f else 0.56f) * pulse
                val rayPath = Path().apply {
                    moveTo(cx - 3.dp.toPx(), cy)
                    lineTo(cx, cy - len)
                    lineTo(cx + 3.dp.toPx(), cy)
                    close()
                }
                drawPath(
                    path = rayPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFFFFE07A).copy(alpha = 0.65f * alpha)
                        ),
                        startY = cy - len,
                        endY = cy
                    )
                )
            }
        }
    }
}

private fun DrawScope.drawBreezeCapsuleRings(
    cx: Float,
    cy: Float,
    capW: Float,
    capH: Float,
    animPhase: Float,
    alpha: Float
) {
    val center = Offset(cx, cy)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF52F5C8).copy(alpha = 0.45f * alpha),
                Color(0xFF189E7E).copy(alpha = 0.16f * alpha),
                Color.Transparent
            ),
            center = center,
            radius = capH * 0.65f
        ),
        radius = capH * 0.65f,
        center = center
    )

    // Concentric rotating orbital wind-vortex rings (matching frames 0:03, 0:04, 0:11!)
    val rings = listOf(
        Triple(capW * 0.68f, capH * 0.58f, 1f),
        Triple(capW * 0.80f, capH * 0.66f, -0.75f),
        Triple(capW * 0.92f, capH * 0.74f, 0.55f)
    )
    rings.forEachIndexed { idx, (rx, ry, speed) ->
        val startAngle = (animPhase * 360f * speed) + idx * 65f
        drawArc(
            brush = Brush.sweepGradient(
                0.0f to Color.Transparent,
                0.5f to Color(0xFF8CFFDE).copy(alpha = 0.85f * alpha),
                1.0f to Color.Transparent,
                center = center
            ),
            startAngle = startAngle,
            sweepAngle = 210f,
            useCenter = false,
            topLeft = Offset(cx - rx, cy - ry),
            size = Size(rx * 2f, ry * 2f),
            style = Stroke(width = (2.2f - idx * 0.4f).dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawBloomCapsulePetals(
    cx: Float,
    cy: Float,
    capW: Float,
    capH: Float,
    wavePhase: Float,
    alpha: Float
) {
    val center = Offset(cx, cy)
    val breathe = 0.96f + 0.05f * sin(wavePhase)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF72C8).copy(alpha = 0.55f * alpha),
                Color(0xFFAD1E78).copy(alpha = 0.22f * alpha),
                Color.Transparent
            ),
            center = center,
            radius = capH * 0.68f * breathe
        ),
        radius = capH * 0.68f * breathe,
        center = center
    )

    // Symmetrical glowing lotus petals unfurling behind the Bloom capsule (matching frames 0:02, 0:05, 0:06!)
    val petalAngles = listOf(-68f, -42f, -18f, 0f, 18f, 42f, 68f)
    petalAngles.forEachIndexed { idx, angle ->
        val scaleOut = (if (angle == 0f) 1.05f else 0.96f) * breathe
        rotate(degrees = angle, pivot = Offset(cx, cy + capH * 0.30f)) {
            val petalH = capH * 0.78f * scaleOut
            val petalW = capW * 0.42f
            val baseY = cy + capH * 0.30f
            val petalPath = Path().apply {
                moveTo(cx, baseY)
                cubicTo(cx - petalW, baseY - petalH * 0.35f, cx - petalW * 0.65f, baseY - petalH * 0.85f, cx, baseY - petalH)
                cubicTo(cx + petalW * 0.65f, baseY - petalH * 0.85f, cx + petalW, baseY - petalH * 0.35f, cx, baseY)
                close()
            }
            drawPath(
                path = petalPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFE5F6).copy(alpha = 0.94f * alpha),
                        Color(0xFFFF78CB).copy(alpha = 0.85f * alpha),
                        Color(0xFFB51E7B).copy(alpha = 0.55f * alpha)
                    ),
                    startY = baseY - petalH,
                    endY = baseY
                )
            )
            drawPath(
                path = petalPath,
                color = Color.White.copy(alpha = (0.35f + (idx % 2) * 0.15f) * alpha),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

private fun DrawScope.drawCherryBlossomCapsule(
    cx: Float,
    cy: Float,
    capW: Float,
    capH: Float,
    animPhase: Float,
    wavePhase: Float,
    alpha: Float
) {
    val center = Offset(cx, cy)
    val breathe = 0.95f + 0.05f * sin(wavePhase)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF9EC3).copy(alpha = 0.62f * alpha),
                Color(0xFFD64572).copy(alpha = 0.24f * alpha),
                Color.Transparent
            ),
            center = center,
            radius = capH * 0.72f * breathe
        ),
        radius = capH * 0.72f * breathe,
        center = center
    )
    // 5-petal sakura blossoms orbiting softly behind the glass pill
    for (i in 0 until 8) {
        val ang = animPhase * 2f * PI.toFloat() + i * (2f * PI.toFloat() / 8f)
        val bx = cx + cos(ang) * capW * 0.68f
        val by = cy + sin(ang) * capH * 0.56f
        val pr = (5.5f + (i % 2) * 2f).dp.toPx()
        for (p in 0 until 5) {
            val pa = (p * 72f + animPhase * 90f) * PI.toFloat() / 180f
            val px = bx + cos(pa) * pr * 0.7f
            val py = by + sin(pa) * pr * 0.7f
            drawCircle(
                color = if (i % 2 == 0) Color(0xFFFFC5DA).copy(alpha = 0.9f * alpha) else Color(0xFFFFE5EF).copy(alpha = 0.85f * alpha),
                radius = pr * 0.55f,
                center = Offset(px, py)
            )
        }
        drawCircle(
            color = Color(0xFFFFE08A).copy(alpha = 0.9f * alpha),
            radius = pr * 0.28f,
            center = Offset(bx, by)
        )
    }
}

private fun DrawScope.drawSnowCapsule(
    cx: Float,
    cy: Float,
    capW: Float,
    capH: Float,
    animPhase: Float,
    wavePhase: Float,
    alpha: Float
) {
    val center = Offset(cx, cy)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFA2E0FF).copy(alpha = 0.56f * alpha),
                Color(0xFF3E83C4).copy(alpha = 0.22f * alpha),
                Color.Transparent
            ),
            center = center,
            radius = capH * 0.72f
        ),
        radius = capH * 0.72f,
        center = center
    )
    // Rotating crystal snowflake mandala halo behind capsule
    rotate(degrees = animPhase * 60f, pivot = center) {
        val r = capH * 0.62f
        for (i in 0 until 6) {
            val a = (i * 60.0) * PI / 180.0
            val ex = cx + (cos(a) * r).toFloat()
            val ey = cy + (sin(a) * r).toFloat()
            drawLine(
                color = Color(0xFFDDF4FF).copy(alpha = 0.65f * alpha),
                start = center,
                end = Offset(ex, ey),
                strokeWidth = 1.6.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
    // Falling snow dots
    for (i in 0 until 10) {
        val p = (animPhase + i * 0.11f) % 1f
        val sx = cx + sin(wavePhase + i * 1.4f) * capW * 0.72f
        val sy = cy - capH * 0.62f + p * capH * 1.24f
        drawCircle(
            color = Color.White.copy(alpha = sin(p * PI).toFloat().coerceIn(0f, 1f) * 0.9f * alpha),
            radius = (2f + (i % 3)).dp.toPx(),
            center = Offset(sx, sy)
        )
    }
}

private fun DrawScope.drawRainCapsule(
    cx: Float,
    cy: Float,
    capW: Float,
    capH: Float,
    animPhase: Float,
    alpha: Float
) {
    val center = Offset(cx, cy)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF5ADAF0).copy(alpha = 0.52f * alpha),
                Color(0xFF1A6C88).copy(alpha = 0.22f * alpha),
                Color.Transparent
            ),
            center = center,
            radius = capH * 0.68f
        ),
        radius = capH * 0.68f,
        center = center
    )
    // Slanted glowing rain streaks around capsule
    for (i in 0 until 12) {
        val p = (animPhase * 1.8f + i * 0.14f) % 1f
        val rx = cx - capW * 0.78f + (i / 11f) * capW * 1.56f
        val ry = cy - capH * 0.65f + p * capH * 1.25f
        val len = 14.dp.toPx()
        drawLine(
            color = Color(0xFFB5F0FC).copy(alpha = sin(p * PI).toFloat().coerceIn(0f, 1f) * 0.85f * alpha),
            start = Offset(rx, ry),
            end = Offset(rx - len * 0.22f, ry + len),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
    // Expanding puddle ripple underneath capsule
    val rip = (animPhase * 1.3f) % 1f
    val rx = capW * (0.45f + rip * 0.42f)
    val ry = rx * 0.28f
    drawOval(
        color = Color(0xFF8CE8FA).copy(alpha = (1f - rip) * 0.75f * alpha),
        topLeft = Offset(cx - rx, cy + capH * 0.52f - ry),
        size = Size(rx * 2f, ry * 2f),
        style = Stroke(width = 1.5.dp.toPx())
    )
}

/**
 * Full Elemental Theme Studio Sheet:
 * - Scope Switcher: Apply to **Entire App** OR **This Page Only**
 * - Dual Light & Dark Theme Picker: Pick separate themes for Light Mode AND Dark Mode!
 * - All 7 Elemental Themes: Ember, Solar, Breeze, Bloom, Cherry Blossom, Snow, Rain
 * - Plus 7-Day rhythm strip customization & live Light/Dark preview toggle!
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElementalThemeSettingsSheet(
    uiState: OneReflectionUiState,
    onToggleOled: () -> Unit,
    onSelectDay: (Int) -> Unit,
    onAssignThemeToDay: (Int, ElementalTheme) -> Unit,
    onApplyThemeWithScope: (ThemeApplyScope, ThemeModeSlot, ScreenId, ElementalTheme) -> Unit,
    onClearPageTheme: (ScreenId, ThemeModeSlot) -> Unit,
    onResetDefaults: () -> Unit,
    onToggleAmbientSound: () -> Unit,
    onSetAmbientSoundVolume: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedScope by remember(uiState.currentScreen) {
        mutableStateOf(
            if (uiState.hasCustomPageTheme(uiState.currentScreen, uiState.oledMode)) {
                ThemeApplyScope.SINGLE_PAGE
            } else {
                ThemeApplyScope.ENTIRE_APP
            }
        )
    }
    var editingSlot by remember(uiState.oledMode) {
        mutableStateOf(if (uiState.oledMode) ThemeModeSlot.DARK else ThemeModeSlot.LIGHT)
    }
    var editingDay by remember(uiState.selectedDayIndex) { mutableIntStateOf(uiState.selectedDayIndex) }

    val palette = LocalElementalPalette.current
    val currentScreen = uiState.currentScreen
    val lightThemeForTarget = if (selectedScope == ThemeApplyScope.SINGLE_PAGE) {
        uiState.pageLightThemes[currentScreen] ?: uiState.resolvedThemeFor(currentScreen, isDark = false)
    } else {
        if (uiState.useGlobalLightTheme) uiState.lightGlobalTheme
        else uiState.dayThemes.getOrElse(editingDay) { uiState.lightGlobalTheme }
    }
    val darkThemeForTarget = if (selectedScope == ThemeApplyScope.SINGLE_PAGE) {
        uiState.pageDarkThemes[currentScreen] ?: uiState.darkGlobalTheme
    } else {
        uiState.darkGlobalTheme
    }
    val activeCarouselTheme = if (editingSlot == ThemeModeSlot.DARK) darkThemeForTarget else lightThemeForTarget
    val hasPageOverride = uiState.hasCustomPageTheme(currentScreen, editingSlot == ThemeModeSlot.DARK)

    val days = remember {
        listOf(
            "M" to "13",
            "T" to "14",
            "W" to "15",
            "T" to "16",
            "F" to "17",
            "S" to "18",
            "S" to "19"
        )
    }
    val dayFullNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = palette.surfaceCard,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ELEMENTAL THEME STUDIO",
                        fontFamily = MonoFontFamily,
                        fontSize = 9.5.sp,
                        letterSpacing = 1.sp,
                        color = palette.primary
                    )
                    Text(
                        text = "Page & Dual-Mode Themes",
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EnchantedDayNightToggle(
                        isDark = uiState.oledMode,
                        onToggle = {
                            onToggleOled()
                            editingSlot = if (!uiState.oledMode) ThemeModeSlot.DARK else ThemeModeSlot.LIGHT
                        },
                        width = 74.dp,
                        height = 36.dp,
                        testTag = "settings_sheet_day_night_toggle"
                    )
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("theme_settings_close")
                            .clip(CircleShape)
                            .background(palette.surfaceElevated)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close theme settings", modifier = Modifier.size(18.dp))
                    }
                }
            }

            // 1. Scope Toggle: Single Page vs Entire App
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(
                    text = "1. APPLY SCOPE",
                    fontFamily = MonoFontFamily,
                    fontSize = 9.5.sp,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val entireSelected = selectedScope == ThemeApplyScope.ENTIRE_APP
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("theme_scope_entire_app")
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedScope = ThemeApplyScope.ENTIRE_APP },
                        shape = RoundedCornerShape(16.dp),
                        color = if (entireSelected) palette.primary else palette.surfaceElevated,
                        border = BorderStroke(1.dp, if (entireSelected) Color.Transparent else palette.border)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 11.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Public,
                                contentDescription = "Entire App",
                                tint = if (entireSelected) palette.onPrimary else palette.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Entire App",
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = if (entireSelected) palette.onPrimary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    val pageSelected = selectedScope == ThemeApplyScope.SINGLE_PAGE
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("theme_scope_single_page")
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedScope = ThemeApplyScope.SINGLE_PAGE },
                        shape = RoundedCornerShape(16.dp),
                        color = if (pageSelected) palette.primary else palette.surfaceElevated,
                        border = BorderStroke(1.dp, if (pageSelected) Color.Transparent else palette.border)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 11.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Layers,
                                contentDescription = "This Page Only",
                                tint = if (pageSelected) palette.onPrimary else palette.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "This Page Only",
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = if (pageSelected) palette.onPrimary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                if (selectedScope == ThemeApplyScope.SINGLE_PAGE) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Customizing only: ${currentScreen.subtitle}",
                            fontFamily = BodyFontFamily,
                            fontSize = 11.5.sp,
                            color = palette.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (hasPageOverride) {
                            Text(
                                text = "Use Global Theme",
                                fontFamily = BodyFontFamily,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .testTag("theme_clear_page_override")
                                    .clip(CircleShape)
                                    .clickable { onClearPageTheme(currentScreen, editingSlot) }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // 2. Separate Light Theme vs Dark Theme Slots
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(
                    text = "2. CHOOSE SEPARATE LIGHT & DARK THEMES",
                    fontFamily = MonoFontFamily,
                    fontSize = 9.5.sp,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val lightSelected = editingSlot == ThemeModeSlot.LIGHT
                    val lightPal = elementalPaletteFor(lightThemeForTarget, oledMode = false)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("theme_slot_light")
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                editingSlot = ThemeModeSlot.LIGHT
                                if (uiState.oledMode) onToggleOled()
                            },
                        shape = RoundedCornerShape(18.dp),
                        color = if (lightSelected) palette.surfaceTint else palette.surfaceElevated,
                        border = BorderStroke(if (lightSelected) 1.8.dp else 1.dp, if (lightSelected) palette.primary else palette.border)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(Icons.Filled.LightMode, contentDescription = null, tint = lightPal.primary, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "LIGHT MODE",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = lightThemeForTarget.displayName,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    val darkSelected = editingSlot == ThemeModeSlot.DARK
                    val darkPal = elementalPaletteFor(darkThemeForTarget, oledMode = true)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("theme_slot_dark")
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                editingSlot = ThemeModeSlot.DARK
                                if (!uiState.oledMode) onToggleOled()
                            },
                        shape = RoundedCornerShape(18.dp),
                        color = if (darkSelected) palette.surfaceTint else palette.surfaceElevated,
                        border = BorderStroke(if (darkSelected) 1.8.dp else 1.dp, if (darkSelected) palette.primary else palette.border)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(Icons.Filled.DarkMode, contentDescription = null, tint = darkPal.primary, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "DARK MODE",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = darkThemeForTarget.displayName,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            // 3. All 7 Elemental Worlds Carousel (Ember, Solar, Breeze, Bloom, Cherry Blossom, Snow, Rain)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val targetTitle = buildString {
                    append(if (editingSlot == ThemeModeSlot.DARK) "DARK" else "LIGHT")
                    append(" · ")
                    append(if (selectedScope == ThemeApplyScope.SINGLE_PAGE) currentScreen.subtitle.uppercase() else "ENTIRE APP")
                }
                Text(
                    text = "3. PICK WORLD FOR $targetTitle",
                    fontFamily = MonoFontFamily,
                    fontSize = 9.5.sp,
                    letterSpacing = 0.8.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                ElementalThemeCarousel(
                    selectedTheme = activeCarouselTheme,
                    onSelectTheme = { chosen ->
                        onApplyThemeWithScope(selectedScope, editingSlot, currentScreen, chosen)
                    }
                )

                // Paired Elemental Ambient Soundscape Strip
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("theme_sheet_ambient_sound_card"),
                    shape = RoundedCornerShape(18.dp),
                    color = palette.surfaceTint,
                    border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.30f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (uiState.ambientSoundEnabled) {
                                    ElementalEqualizerBars(
                                        isPlaying = true,
                                        color = palette.primary,
                                        barCount = 3,
                                        height = 14.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        tint = palette.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                    Text(
                                        text = "PAIRED SOUNDSCAPE · ${activeCarouselTheme.displayName.uppercase()}",
                                        fontFamily = MonoFontFamily,
                                        fontSize = 8.5.sp,
                                        letterSpacing = 0.7.sp,
                                        color = palette.primary
                                    )
                                    Text(
                                        text = activeCarouselTheme.soundscapeTitle,
                                        fontFamily = BodyFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            OneReflectionSwitch(
                                checked = uiState.ambientSoundEnabled,
                                onCheckedChange = onToggleAmbientSound,
                                activeTrackColor = palette.primary,
                                testTag = "theme_sheet_ambient_switch"
                            )
                        }

                        if (uiState.ambientSoundEnabled) {
                            Slider(
                                value = uiState.ambientSoundVolume,
                                onValueChange = onSetAmbientSoundVolume,
                                valueRange = 0.05f..1.0f,
                                modifier = Modifier.testTag("theme_sheet_ambient_slider"),
                                colors = SliderDefaults.colors(
                                    thumbColor = palette.primary,
                                    activeTrackColor = palette.primary,
                                    inactiveTrackColor = palette.border.copy(alpha = 0.65f)
                                )
                            )
                        }
                    }
                }
            }

            // 4. Optional 7-Day Rhythm Strip (when customizing Light Mode Entire App)
            if (selectedScope == ThemeApplyScope.ENTIRE_APP && editingSlot == ThemeModeSlot.LIGHT) {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text(
                        text = "DAILY RHYTHM (${dayFullNames[editingDay].uppercase()} · TAP DAY TO ASSIGN)",
                        fontFamily = MonoFontFamily,
                        fontSize = 9.sp,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        days.forEachIndexed { idx, (lbl, num) ->
                            ElementalDayPill(
                                dayLabel = lbl,
                                dayNumber = num,
                                theme = uiState.dayThemes.getOrElse(idx) { ElementalTheme.BLOOM },
                                selected = idx == editingDay,
                                oledMode = uiState.oledMode,
                                testTag = "settings_day_pill_$idx",
                                onClick = {
                                    editingDay = idx
                                    onSelectDay(idx)
                                    onAssignThemeToDay(idx, activeCarouselTheme)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Quick Actions: Done + Reset Defaults
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("theme_apply_confirm")
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onDismiss() },
                    shape = RoundedCornerShape(18.dp),
                    color = palette.primary
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 13.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = palette.onPrimary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Done · ${activeCarouselTheme.displayName}",
                            fontFamily = BodyFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = palette.onPrimary
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .testTag("theme_reset_defaults")
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onResetDefaults() },
                    shape = RoundedCornerShape(18.dp),
                    color = palette.surfaceElevated,
                    border = BorderStroke(1.dp, palette.border)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Reset defaults", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Reset",
                            fontFamily = BodyFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
        }
    }
}
