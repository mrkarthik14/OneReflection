package com.onereflection.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.onereflection.app.ui.theme.SanctuaryEase
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pixel-Art Enchanted Day ↔ Night Crystal Orb Theme Toggle.
 *
 * - Light (Day) state: Sunlit cerulean sky, floating cloud, warm glowing sun & spotted red mushroom
 *   magnified inside the left crystal glass orb, Japanese stone lantern & lush meadow grass.
 * - Dark (Night) state: Deep starlit midnight sky, twinkling 4-point pixel stars, drifting fireflies,
 *   shadowed meadow & warm lit lantern window, crescent moon & glowing bioluminescent cyan mushrooms
 *   magnified inside the right crystal glass orb.
 * - Transition: Smooth left↔right crystal orb glide paired with a pixel-matrix mosaic dissolve wipe
 *   peaking at the midpoint (0.5).
 */
@Composable
fun EnchantedDayNightToggle(
    isDark: Boolean,
    onToggle: () -> Unit,
    width: Dp = 84.dp,
    height: Dp = 42.dp,
    testTag: String = "oled_toggle_button",
    modifier: Modifier = Modifier
) {
    // Smooth 780ms glide synced with full-app celestial Light ↔ Dark wave
    val progress by animateFloatAsState(
        targetValue = if (isDark) 1f else 0f,
        animationSpec = tween(durationMillis = 780, easing = SanctuaryEase),
        label = "day_night_progress"
    )

    val ambient = rememberInfiniteTransition(label = "day_night_ambient")
    val ambientPhase by ambient.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_phase"
    )
    val twinklePhase by ambient.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle_phase"
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMedium),
        label = "day_night_press_scale"
    )
    val accessibilityLabel = if (isDark) "Switch to Light Day Theme" else "Switch to Dark Night Theme"
    val ambientShadow = lerp(Color(0xFF62AEE8), Color(0xFF4CF5E2), progress)
    val spotShadow = lerp(Color(0xFF8AA8C0), Color(0xFF0A142E), progress)

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .testTag(testTag)
            .shadow(
                elevation = (6f + 2f * progress).dp,
                shape = CircleShape,
                ambientColor = ambientShadow,
                spotColor = spotShadow
            )
            .clip(CircleShape)
            .semantics {
                contentDescription = accessibilityLabel
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch,
                onClick = onToggle
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val pillPath = Path().apply {
                addRoundRect(
                    androidx.compose.ui.geometry.RoundRect(
                        left = 0f,
                        top = 0f,
                        right = w,
                        bottom = h,
                        cornerRadius = CornerRadius(h / 2f, h / 2f)
                    )
                )
            }

            // Mosaic dissolve wave peaks cleanly at progress = 0.5
            val mosaicPeak = sin(progress * PI).toFloat().coerceIn(0f, 1f)
            val nightAlpha = ((progress - 0.35f) / 0.3f).coerceIn(0f, 1f)
            val dayAlpha = 1f - nightAlpha

            // 1. Draw Main Background Pill Scene
            clipPath(pillPath) {
                if (dayAlpha > 0.01f) {
                    drawDayPixelScene(
                        alpha = dayAlpha,
                        ambientPhase = ambientPhase,
                        twinklePhase = twinklePhase
                    )
                }
                if (nightAlpha > 0.01f) {
                    drawNightPixelScene(
                        alpha = nightAlpha,
                        ambientPhase = ambientPhase,
                        twinklePhase = twinklePhase
                    )
                }

                // Mid-transition Pixel-Matrix Mosaic Dissolve Overlay (matches frame 0:02!)
                if (mosaicPeak > 0.05f) {
                    drawPixelMosaicDissolve(
                        intensity = mosaicPeak,
                        progress = progress,
                        isInsideOrb = false
                    )
                }

                // Subtle inner top/bottom capsule depth shadow
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.24f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.28f)
                        )
                    ),
                    cornerRadius = CornerRadius(h / 2f, h / 2f)
                )
            }

            // 2. Draw Translucent Magnifying Glass Crystal Orb Thumb gliding Left ↔ Right
            val orbPadding = h * 0.055f
            val orbRadius = (h - orbPadding * 2f) / 2f
            val orbMinX = orbPadding + orbRadius
            val orbMaxX = w - orbPadding - orbRadius
            val orbCenter = Offset(
                x = orbMinX + (orbMaxX - orbMinX) * progress,
                y = h / 2f
            )

            // Soft drop shadow beneath the crystal orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.42f),
                        Color.Transparent
                    ),
                    center = Offset(orbCenter.x, orbCenter.y + h * 0.04f),
                    radius = orbRadius * 1.18f
                ),
                radius = orbRadius * 1.18f,
                center = Offset(orbCenter.x, orbCenter.y + h * 0.04f)
            )

            val orbPath = Path().apply {
                addOval(
                    androidx.compose.ui.geometry.Rect(
                        left = orbCenter.x - orbRadius,
                        top = orbCenter.y - orbRadius,
                        right = orbCenter.x + orbRadius,
                        bottom = orbCenter.y + orbRadius
                    )
                )
            }

            clipPath(orbPath) {
                // Optical 1.24x magnification of the pixel world inside the glass orb!
                scale(scaleX = 1.24f, scaleY = 1.24f, pivot = orbCenter) {
                    if (dayAlpha > 0.01f) {
                        drawDayPixelScene(
                            alpha = dayAlpha,
                            ambientPhase = ambientPhase,
                            twinklePhase = twinklePhase
                        )
                    }
                    if (nightAlpha > 0.01f) {
                        drawNightPixelScene(
                            alpha = nightAlpha,
                            ambientPhase = ambientPhase,
                            twinklePhase = twinklePhase
                        )
                    }
                }

                // Mid-transition Pixel-Matrix Mosaic inside the orb (curved disco/pixel reflection!)
                if (mosaicPeak > 0.05f) {
                    drawPixelMosaicDissolve(
                        intensity = mosaicPeak,
                        progress = progress,
                        isInsideOrb = true,
                        orbCenter = orbCenter,
                        orbRadius = orbRadius
                    )
                }

                // Spherical glass lens edge shading (refraction depth)
                drawCircle(
                    brush = Brush.radialGradient(
                        0.0f to Color.Transparent,
                        0.68f to Color.Transparent,
                        0.90f to lerp(Color(0xFF4F82A8), Color(0xFF0A1D2B), progress).copy(alpha = 0.34f),
                        1.0f to Color.Black.copy(alpha = 0.48f),
                        center = orbCenter,
                        radius = orbRadius
                    ),
                    radius = orbRadius,
                    center = orbCenter
                )

                // Top-left glossy curved crystal highlight
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.92f),
                            Color.White.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        start = Offset(orbCenter.x - orbRadius * 0.7f, orbCenter.y - orbRadius * 0.7f),
                        end = Offset(orbCenter.x + orbRadius * 0.3f, orbCenter.y + orbRadius * 0.1f)
                    ),
                    startAngle = 200f,
                    sweepAngle = 82f,
                    useCenter = false,
                    topLeft = Offset(orbCenter.x - orbRadius * 0.84f, orbCenter.y - orbRadius * 0.84f),
                    size = Size(orbRadius * 1.68f, orbRadius * 1.68f),
                    style = Stroke(width = h * 0.055f, cap = StrokeCap.Round)
                )

                // Bottom-right subtle bioluminescent/sunlit internal caustic bounce
                drawArc(
                    color = lerp(Color(0xFFFFF5CC), Color(0xFF66FFF0), progress).copy(alpha = 0.55f),
                    startAngle = 22f,
                    sweepAngle = 68f,
                    useCenter = false,
                    topLeft = Offset(orbCenter.x - orbRadius * 0.84f, orbCenter.y - orbRadius * 0.84f),
                    size = Size(orbRadius * 1.68f, orbRadius * 1.68f),
                    style = Stroke(width = h * 0.032f, cap = StrokeCap.Round)
                )
            }

            // Crisp Glass Orb Outer Rim Stroke
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        lerp(Color(0xFFA7C8E0), Color(0xFF426685), progress).copy(alpha = 0.6f),
                        Color.White.copy(alpha = 0.4f)
                    ),
                    start = Offset(orbCenter.x - orbRadius, orbCenter.y - orbRadius),
                    end = Offset(orbCenter.x + orbRadius, orbCenter.y + orbRadius)
                ),
                radius = orbRadius,
                center = orbCenter,
                style = Stroke(width = 1.25.dp.toPx())
            )

            // Crisp Capsule Outer Rim Bevel
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lerp(Color.White, Color(0xFF4D6A94), progress).copy(alpha = 0.75f),
                        lerp(Color(0xFF9EB9CE), Color(0xFF1A2540), progress).copy(alpha = 0.6f)
                    )
                ),
                cornerRadius = CornerRadius(h / 2f, h / 2f),
                style = Stroke(width = 1.2.dp.toPx())
            )
        }
    }
}

private fun DrawScope.px(
    col: Float,
    row: Float,
    wCells: Float = 1f,
    hCells: Float = 1f,
    color: Color,
    alpha: Float = 1f
) {
    val cols = 38f
    val rows = 19f
    val cw = size.width / cols
    val ch = size.height / rows
    drawRect(
        color = color.copy(alpha = color.alpha * alpha),
        topLeft = Offset(col * cw, row * ch),
        size = Size(wCells * cw + 0.35f, hCells * ch + 0.35f)
    )
}

/**
 * Day Pixel-Art Scene: Cerulean sky, radiant sun, red-spotted mushroom (left),
 * white pixel cloud, Japanese stone lantern with warm flame, mossy stones, and lush meadow.
 */
private fun DrawScope.drawDayPixelScene(
    alpha: Float,
    ambientPhase: Float,
    twinklePhase: Float
) {
    // 1. Bright Cerulean-to-Cream Sky Gradient
    drawRect(
        brush = Brush.verticalGradient(
            0.0f to Color(0xFF5FB7F5).copy(alpha = alpha),
            0.52f to Color(0xFFACE2FF).copy(alpha = alpha),
            0.84f to Color(0xFFECFAFF).copy(alpha = alpha),
            1.0f to Color(0xFFE6F4E2).copy(alpha = alpha)
        )
    )

    // 2. Radiant Warm Sun Glow (upper-left behind the red-spotted mushroom)
    val sunPulse = 0.94f + 0.06f * sin(twinklePhase)
    val sunCenter = Offset(size.width * 0.21f, size.height * 0.38f)
    drawCircle(
        brush = Brush.radialGradient(
            0.0f to Color(0xFFFFFFFF).copy(alpha = 0.98f * alpha),
            0.42f to Color(0xFFFFF5C2).copy(alpha = 0.90f * alpha),
            0.78f to Color(0xFFFFE58A).copy(alpha = 0.42f * alpha),
            1.0f to Color.Transparent,
            center = sunCenter,
            radius = size.height * 0.46f * sunPulse
        ),
        radius = size.height * 0.46f * sunPulse,
        center = sunCenter
    )

    // 3. Drifting White Pixel Cloud (upper right)
    val cloudShift = sin(ambientPhase * 2f * PI).toFloat() * 1.1f
    px(23f + cloudShift, 2.5f, 5f, 1.2f, Color.White, alpha * 0.95f)
    px(22f + cloudShift, 3.6f, 7f, 1.1f, Color(0xFFF1F9FF), alpha * 0.92f)
    px(15f - cloudShift * 0.6f, 4.5f, 3f, 0.9f, Color.White, alpha * 0.72f)

    // 4. Floating Warm Sunlit Pollen Motes
    val moteShift = sin(twinklePhase) * 0.5f
    px(18f, 6.5f + moteShift, 0.7f, 0.7f, Color(0xFFFFE094), alpha * 0.85f)
    px(26.5f, 7.2f - moteShift, 0.7f, 0.7f, Color(0xFFFFC98A), alpha * 0.78f)
    px(8.5f, 11.2f + moteShift, 0.6f, 0.6f, Color(0xFFFFF5B8), alpha * 0.8f)

    // 5. Lush Meadow Ground, Soil & Stone Brick Base
    px(0f, 15.2f, 38f, 1.3f, Color(0xFF72C944), alpha)
    px(0f, 16.3f, 38f, 1.1f, Color(0xFF4EA12D), alpha)
    px(0f, 17.3f, 38f, 0.9f, Color(0xFF6B4F3B), alpha)
    px(0f, 18.1f, 38f, 1.2f, Color(0xFF7C7671), alpha)
    // Stone brick mortar accents
    for (col in listOf(4f, 10f, 16f, 22f, 28f, 33f)) {
        px(col, 18.2f, 1f, 0.8f, Color(0xFF56514D), alpha)
    }

    // 6. Mossy Boulders & Grass Tufts
    px(2f, 13.8f, 4f, 2f, Color(0xFF5A9C35), alpha)
    px(3f, 12.8f, 2f, 1.2f, Color(0xFF7AD14E), alpha)
    px(29f, 13.5f, 5f, 2.2f, Color(0xFF7F868C), alpha)
    px(30f, 12.5f, 3f, 1.2f, Color(0xFF5DA83A), alpha)
    px(33f, 14.0f, 4f, 1.6f, Color(0xFF4A912D), alpha)

    // 7. Hero Red-and-White Spotted Pixel Mushroom (Left — magnified by Day orb!)
    // Stem
    px(11.3f, 10.6f, 1.5f, 5.0f, Color(0xFFFFF5E1), alpha)
    px(12.2f, 10.6f, 0.7f, 5.0f, Color(0xFFE0CCA8), alpha)
    // Red Cap
    px(9.0f, 8.2f, 6.2f, 2.5f, Color(0xFFE5382B), alpha)
    px(10.0f, 7.0f, 4.2f, 1.4f, Color(0xFFF04B3E), alpha)
    px(11.0f, 6.2f, 2.2f, 1.0f, Color(0xFFF55F52), alpha)
    // White Spots
    px(10.0f, 8.8f, 1.1f, 1.0f, Color.White, alpha)
    px(12.6f, 7.4f, 1.0f, 0.9f, Color.White, alpha)
    px(13.6f, 9.0f, 0.9f, 0.9f, Color.White, alpha)
    // Cap underside shadow
    px(9.4f, 10.5f, 5.4f, 0.6f, Color(0xFF8C1E16), alpha)

    // 8. Japanese Stone Lantern (Tōrō) in Center-Right
    px(20.8f, 5.8f, 1.0f, 1.0f, Color(0xFF5E656B), alpha) // Top finial
    px(19.0f, 6.8f, 4.6f, 1.1f, Color(0xFF747C82), alpha) // Roof
    px(19.8f, 7.9f, 3.0f, 1.9f, Color(0xFF596066), alpha) // Lantern housing
    px(20.6f, 8.3f, 1.4f, 1.1f, Color(0xFFFFCB47), alpha) // Warm flame window
    px(19.6f, 9.8f, 3.4f, 0.9f, Color(0xFF878F96), alpha) // Mid shelf
    px(20.7f, 10.7f, 1.2f, 3.6f, Color(0xFF717980), alpha) // Pillar
    px(19.6f, 14.2f, 3.4f, 1.3f, Color(0xFF60676E), alpha) // Base

    // 9. Small Cozy Brown/Cream Mushrooms on Right
    px(25.2f, 12.6f, 0.9f, 2.8f, Color(0xFFF2E2C4), alpha)
    px(24.0f, 11.4f, 3.3f, 1.3f, Color(0xFF8C6B4F), alpha)
    px(24.8f, 11.7f, 0.8f, 0.6f, Color(0xFFFFF4DC), alpha)
    px(27.8f, 13.4f, 0.8f, 2.0f, Color(0xFFF2E2C4), alpha)
    px(26.9f, 12.5f, 2.6f, 1.0f, Color(0xFF7A5B40), alpha)
}

/**
 * Night Pixel-Art Scene: Deep midnight indigo sky, twinkling 4-point pixel stars,
 * drifting green fireflies, shadowed red mushroom & warm lit stone lantern,
 * bright crescent moon & bioluminescent glowing cyan mushrooms (right — magnified by Night orb!).
 */
private fun DrawScope.drawNightPixelScene(
    alpha: Float,
    ambientPhase: Float,
    twinklePhase: Float
) {
    // 1. Deep Starlit Midnight Sky Gradient
    drawRect(
        brush = Brush.verticalGradient(
            0.0f to Color(0xFF060914).copy(alpha = alpha),
            0.55f to Color(0xFF0E1730).copy(alpha = alpha),
            0.88f to Color(0xFF15253E).copy(alpha = alpha),
            1.0f to Color(0xFF0E1A24).copy(alpha = alpha)
        )
    )

    // 2. Twinkling 4-Point Pixel Cross Stars (+)
    val stars = listOf(
        Triple(5.5f, 3.2f, 0.0f),
        Triple(11.5f, 4.6f, 1.2f),
        Triple(16.0f, 2.6f, 2.4f),
        Triple(21.5f, 3.8f, 0.8f),
        Triple(31.5f, 3.0f, 1.8f),
        Triple(33.5f, 6.2f, 3.1f),
        Triple(7.8f, 7.6f, 2.0f)
    )
    stars.forEach { (sx, sy, offset) ->
        val tw = (0.45f + 0.55f * sin(twinklePhase + offset)).coerceIn(0.25f, 1f)
        val starCol = if (offset > 1.5f) Color(0xFFFFF2B8) else Color.White
        px(sx, sy, 0.8f, 0.8f, starCol, alpha * tw)
        px(sx - 0.8f, sy, 0.8f, 0.8f, starCol, alpha * tw * 0.7f)
        px(sx + 0.8f, sy, 0.8f, 0.8f, starCol, alpha * tw * 0.7f)
        px(sx, sy - 0.8f, 0.8f, 0.8f, starCol, alpha * tw * 0.7f)
        px(sx, sy + 0.8f, 0.8f, 0.8f, starCol, alpha * tw * 0.7f)
    }

    // 3. Glowing Pixel Crescent Moon (Upper-Right inside Night Orb!)
    val moonCenter = Offset(size.width * 0.73f, size.height * 0.34f)
    drawCircle(
        brush = Brush.radialGradient(
            0.0f to Color(0xFFFFFFFF).copy(alpha = 0.85f * alpha),
            0.45f to Color(0xFFD8F6FF).copy(alpha = 0.35f * alpha),
            1.0f to Color.Transparent,
            center = moonCenter,
            radius = size.height * 0.34f
        ),
        radius = size.height * 0.34f,
        center = moonCenter
    )
    px(26.5f, 4.2f, 2.2f, 1.1f, Color(0xFFFFFDF0), alpha)
    px(25.6f, 5.2f, 1.9f, 2.2f, Color(0xFFFFFDF0), alpha)
    px(26.5f, 7.3f, 2.4f, 1.1f, Color(0xFFEAF7FF), alpha)

    // 4. Bioluminescent Cyan Halo radiating from the Right Mushrooms!
    val bioPulse = 0.90f + 0.10f * sin(twinklePhase)
    val bioCenter = Offset(size.width * 0.74f, size.height * 0.68f)
    drawCircle(
        brush = Brush.radialGradient(
            0.0f to Color(0xFF52FFE8).copy(alpha = 0.68f * alpha * bioPulse),
            0.48f to Color(0xFF1CCAC0).copy(alpha = 0.28f * alpha * bioPulse),
            1.0f to Color.Transparent,
            center = bioCenter,
            radius = size.height * 0.48f
        ),
        radius = size.height * 0.48f,
        center = bioCenter
    )

    // 5. Shadowed Night Meadow Ground & Cobble Base
    px(0f, 15.2f, 38f, 1.3f, Color(0xFF1F3C2C), alpha)
    px(0f, 16.4f, 38f, 1.2f, Color(0xFF13261C), alpha)
    px(0f, 17.5f, 38f, 1.8f, Color(0xFF1A2229), alpha)

    // 6. Shadowed Bush & Dimmed Red Mushroom on Left
    px(2.5f, 12.6f, 4.5f, 2.8f, Color(0xFF1C3028), alpha)
    px(11.3f, 11.4f, 1.3f, 4.0f, Color(0xFF6B6E78), alpha)
    px(9.3f, 9.2f, 5.2f, 2.3f, Color(0xFF7D272B), alpha)
    px(10.3f, 8.2f, 3.2f, 1.1f, Color(0xFF8C3135), alpha)
    px(10.2f, 9.8f, 0.9f, 0.8f, Color(0xFFB8C0CC), alpha)
    px(12.6f, 8.8f, 0.9f, 0.8f, Color(0xFFB8C0CC), alpha)

    // 7. Stone Lantern Silhouette with Glowing Warm Golden Window (Center-Right)
    px(19.6f, 7.6f, 3.8f, 1.0f, Color(0xFF28334A), alpha)
    px(20.2f, 8.6f, 2.6f, 2.4f, Color(0xFF1E283D), alpha)
    // Glowing golden lantern windows!
    px(20.7f, 9.2f, 0.7f, 1.1f, Color(0xFFFFE56B), alpha)
    px(21.7f, 9.2f, 0.7f, 1.1f, Color(0xFFFFD047), alpha)
    px(20.9f, 11.0f, 1.1f, 4.4f, Color(0xFF232E45), alpha)

    // 8. Bioluminescent Glowing Cyan Pixel Mushrooms (Right — Hero inside Night Orb!)
    // Tall Cyan Mushroom
    px(27.0f, 11.2f, 1.4f, 4.2f, Color(0xFF9EFFF4), alpha)
    px(25.2f, 9.4f, 5.0f, 2.0f, Color(0xFF4CF5E2), alpha)
    px(26.1f, 8.5f, 3.2f, 1.0f, Color(0xFF9EFFFA), alpha)
    px(26.5f, 9.0f, 1.2f, 0.8f, Color.White, alpha * 0.9f)
    // Secondary Cyan Mushroom
    px(30.6f, 12.2f, 1.1f, 3.2f, Color(0xFF7CFFEE), alpha)
    px(29.3f, 10.9f, 3.8f, 1.4f, Color(0xFF32E6D6), alpha)
    px(30.1f, 10.2f, 2.2f, 0.8f, Color(0xFFB2FFF8), alpha)
    // Small Front Cyan Mushroom
    px(24.6f, 13.0f, 0.9f, 2.4f, Color(0xFF70F5E6), alpha)
    px(23.7f, 12.1f, 2.6f, 1.0f, Color(0xFF56FFF0), alpha)

    // 9. Floating Emerald Fireflies near the meadow
    val ffDrift = sin(ambientPhase * 2f * PI).toFloat() * 0.8f
    px(7.2f + ffDrift, 12.6f - ffDrift * 0.5f, 0.75f, 0.75f, Color(0xFF7EFFA1), alpha * 0.92f)
    px(15.2f - ffDrift, 11.8f + ffDrift * 0.4f, 0.75f, 0.75f, Color(0xFF9EFFA6), alpha * 0.88f)
    px(17.8f + ffDrift * 0.6f, 13.6f, 0.7f, 0.7f, Color(0xFF64FFDA), alpha * 0.85f)
}

/**
 * Signature Pixel-Matrix Mosaic Dissolve Effect (peaks at transition midpoint = 0.5,
 * reproducing the exact LED/pixel-grid shimmer from frame 0:02 of the user's video).
 */
private fun DrawScope.drawPixelMosaicDissolve(
    intensity: Float,
    progress: Float,
    isInsideOrb: Boolean,
    orbCenter: Offset = Offset.Zero,
    orbRadius: Float = 0f
) {
    val cols = if (isInsideOrb) 16 else 28
    val rows = if (isInsideOrb) 12 else 14
    val cellW = size.width / cols
    val cellH = size.height / rows
    val gap = (cellW * 0.24f).coerceAtLeast(1f)

    // Subtle dark matrix backing so mosaic pixels pop crisply like the video!
    drawRect(color = Color(0xFF0B0B14).copy(alpha = intensity * 0.58f))

    val palette = listOf(
        Color(0xFFFFB88A), // warm sunlit coral-gold
        Color(0xFFFFE599), // starlight gold
        Color(0xFF52FFE8), // bioluminescent cyan
        Color(0xFFFF7A7A), // mushroom red spark
        Color(0xFF9C8CFF), // twilight lavender
        Color(0xFFFFFFFF)  // crisp white highlight
    )

    for (r in 0 until rows) {
        for (c in 0 until cols) {
            val hash = ((c * 31 + r * 17 + (progress * 14f).toInt()) % 11)
            val tileAlpha = (intensity * (0.35f + (hash % 5) * 0.14f)).coerceIn(0f, 0.92f)
            val tileColor = palette[(c * 3 + r * 5) % palette.size]

            val left = c * cellW + gap * 0.5f
            val top = r * cellH + gap * 0.5f

            if (isInsideOrb) {
                val dx = (left + cellW * 0.5f) - orbCenter.x
                val dy = (top + cellH * 0.5f) - orbCenter.y
                if (dx * dx + dy * dy > orbRadius * orbRadius) continue
            }

            if (hash % 2 == 0 || intensity > 0.72f) {
                drawRect(
                    color = tileColor.copy(alpha = tileAlpha),
                    topLeft = Offset(left, top),
                    size = Size((cellW - gap).coerceAtLeast(1f), (cellH - gap).coerceAtLeast(1f))
                )
            }
        }
    }
}

/**
 * Full-Screen Living Day ↔ Night Atmospheric Backdrop synced to the toggle button.
 * - Light (Day): Soft sunlit cerulean sky bloom at top + subtle drifting cloud wisps & sunlit pollen.
 * - Dark (Night): Deep starlit midnight sky + twinkling pixel cross-stars (+) & gentle firefly/cyan glow.
 */
@Composable
fun DayNightAtmosphereCanvas(
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val nightMix by animateFloatAsState(
        targetValue = if (isDark) 1f else 0f,
        animationSpec = tween(durationMillis = 680, easing = FastOutSlowInEasing),
        label = "atmosphere_night_mix"
    )
    val ambient = rememberInfiniteTransition(label = "bg_atmosphere_ambient")
    val twinkle by ambient.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bg_twinkle"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val dayAlpha = 1f - nightMix

        // Day Sunlit Sky Atmosphere
        if (dayAlpha > 0.01f) {
            drawRect(
                brush = Brush.verticalGradient(
                    0.0f to Color(0xFFD5EEFD).copy(alpha = 0.95f * dayAlpha),
                    0.28f to Color(0xFFEAF5FC).copy(alpha = 0.90f * dayAlpha),
                    0.65f to Color(0xFFF3F8F5).copy(alpha = 0.85f * dayAlpha),
                    1.0f to Color(0xFFEBF5E8).copy(alpha = 0.92f * dayAlpha)
                )
            )
            // Soft top-left sunlit halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF4C8).copy(alpha = 0.55f * dayAlpha),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.16f, size.height * 0.08f),
                    radius = size.width * 0.65f
                ),
                radius = size.width * 0.65f,
                center = Offset(size.width * 0.16f, size.height * 0.08f)
            )
        }

        // Night Starlit Bioluminescent Atmosphere
        if (nightMix > 0.01f) {
            drawRect(
                brush = Brush.verticalGradient(
                    0.0f to Color(0xFF060A17).copy(alpha = nightMix),
                    0.45f to Color(0xFF0B1226).copy(alpha = nightMix),
                    1.0f to Color(0xFF091522).copy(alpha = nightMix)
                )
            )
            // Subtle bioluminescent cyan aura at bottom-right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3AEBD8).copy(alpha = 0.14f * nightMix),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.85f, size.height * 0.82f),
                    radius = size.width * 0.72f
                ),
                radius = size.width * 0.72f,
                center = Offset(size.width * 0.85f, size.height * 0.82f)
            )

            // Delicate twinkling pixel cross-stars (+) in upper sky
            val starCoords = listOf(
                Triple(0.12f, 0.07f, 0.0f),
                Triple(0.32f, 0.11f, 1.4f),
                Triple(0.58f, 0.06f, 2.3f),
                Triple(0.84f, 0.10f, 0.9f),
                Triple(0.91f, 0.22f, 3.0f),
                Triple(0.08f, 0.25f, 1.8f),
                Triple(0.74f, 0.18f, 2.6f)
            )
            val s = 2.6.dp.toPx()
            starCoords.forEach { (fx, fy, phase) ->
                val cx = size.width * fx
                val cy = size.height * fy
                val a = (0.30f + 0.55f * sin(twinkle + phase)).coerceIn(0.15f, 0.85f) * nightMix
                val col = if (phase > 1.7f) Color(0xFFF8D66D) else Color(0xFFEAF7FF)
                drawRect(col.copy(alpha = a), Offset(cx - s * 0.5f, cy - s * 1.5f), Size(s, s * 3f))
                drawRect(col.copy(alpha = a), Offset(cx - s * 1.5f, cy - s * 0.5f), Size(s * 3f, s))
            }

            // Soft emerald firefly specks near lower half
            val drift = cos(twinkle) * 8.dp.toPx()
            drawCircle(
                color = Color(0xFF6EEB9B).copy(alpha = 0.35f * nightMix),
                radius = 3.dp.toPx(),
                center = Offset(size.width * 0.18f + drift, size.height * 0.76f)
            )
            drawCircle(
                color = Color(0xFF4CF5E2).copy(alpha = 0.32f * nightMix),
                radius = 2.5.dp.toPx(),
                center = Offset(size.width * 0.78f - drift, size.height * 0.68f)
            )
        }
    }
}
