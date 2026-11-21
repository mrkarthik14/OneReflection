package com.onereflection.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.R
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import kotlin.math.abs
import kotlin.math.roundToInt

private data class LiquidDockItem(
    val id: ScreenId,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val tag: String
)

/**
 * Floating Liquid-Glass Magnifying Lens Navigation Dock.
 *
 * Features silky Light ↔ Dark glass color interpolation, plush spring-driven magnifying lens
 * gliding & droplet squish, trailing elemental caustic aura, smooth icon crossfade & vertical lift,
 * and horizontal drag scrubbing across tabs.
 */
@Composable
fun LiquidGlassFloatingDock(
    currentScreen: ScreenId,
    oledMode: Boolean,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalElementalPalette.current
    val darkT = palette.darkProgress
    val items = remember {
        listOf(
            LiquidDockItem(ScreenId.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home_button"),
            LiquidDockItem(ScreenId.ALARMS, "Alarms", Icons.Filled.Schedule, Icons.Outlined.Schedule, "nav_alarms_button"),
            LiquidDockItem(ScreenId.TASKS, "Tasks", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, "nav_tasks_button"),
            LiquidDockItem(ScreenId.CHATS, "Chats", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline, "nav_chats_button"),
            LiquidDockItem(ScreenId.PEOPLE, "Circle", Icons.Filled.Group, Icons.Outlined.Group, "nav_circle_button")
        )
    }

    val selectedIndex = items.indexOfFirst { it.id == currentScreen }.let { if (it >= 0) it else 0 }

    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableFloatStateOf(selectedIndex.toFloat()) }

    val targetIndex = if (isDragging) dragFraction else selectedIndex.toFloat()
    val animatedIndex by animateFloatAsState(
        targetValue = targetIndex,
        animationSpec = if (isDragging) {
            spring(dampingRatio = 0.88f, stiffness = 920f)
        } else {
            spring(dampingRatio = 0.72f, stiffness = 285f)
        },
        label = "liquid_lens_position"
    )

    // Secondary slower-following caustic glow trail inside the glass track for authentic liquid inertia!
    val trailIndex by animateFloatAsState(
        targetValue = targetIndex,
        animationSpec = spring(dampingRatio = 0.80f, stiffness = 170f),
        label = "liquid_lens_trail"
    )

    // Dynamic liquid-droplet squish: contracts smoothly into a glossy droplet while dragging or mid-glide
    val travelDelta = abs(animatedIndex - targetIndex).coerceIn(0f, 1.2f)
    val trailDelta = abs(animatedIndex - trailIndex).coerceIn(0f, 1f)
    val squishFactor by animateFloatAsState(
        targetValue = if (isDragging) 0.95f else ((travelDelta * 0.9f) + (trailDelta * 0.55f)).coerceIn(0f, 0.88f),
        animationSpec = spring(dampingRatio = 0.66f, stiffness = 320f),
        label = "liquid_lens_squish"
    )

    val dockHeight = 64.dp
    val density = LocalDensity.current

    val ambientShadowColor = lerp(Color(0xFF94A3B8), palette.glowCore.copy(alpha = 0.42f), darkT)
    val spotShadowColor = lerp(Color(0xFF8C9AAD), Color.Black, darkT)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Matching Liquid-Glass Floating Voice Mic Orb cleanly above the right edge of the dock
        LiquidGlassVoiceButton(
            darkProgress = darkT,
            accentColor = palette.primary,
            accentBright = palette.primaryBright,
            onClick = onOpenVoiceStudio
        )

        // Floating Frosted Liquid-Glass Capsule Track
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(dockHeight)
                .shadow(
                    elevation = (14f + 4f * darkT).dp,
                    shape = CircleShape,
                    ambientColor = ambientShadowColor,
                    spotColor = spotShadowColor
                )
                .clip(CircleShape)
                .pointerInput(items.size) {
                    val slotWidthPx = size.width.toFloat() / items.size.toFloat()
                    detectHorizontalDragGestures(
                        onDragStart = { startOffset ->
                            isDragging = true
                            val raw = ((startOffset.x / slotWidthPx) - 0.5f)
                                .coerceIn(0f, (items.size - 1).toFloat())
                            dragFraction = raw
                        },
                        onDragEnd = {
                            isDragging = false
                            val snapped = dragFraction.roundToInt().coerceIn(0, items.size - 1)
                            onNavigate(items[snapped].id)
                        },
                        onDragCancel = {
                            isDragging = false
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val next = (dragFraction + dragAmount / slotWidthPx)
                                .coerceIn(0f, (items.size - 1).toFloat())
                            dragFraction = next
                        }
                    )
                }
        ) {
            val totalWidthPx = with(density) { maxWidth.toPx() }
            val dockHeightPx = with(density) { dockHeight.toPx() }
            val slotWidthDp = maxWidth / items.size
            val slotWidthPx = totalWidthPx / items.size

            // 1. Recessed Frosted Glass Capsule Track Background (smoothly interpolated Light ↔ Dark)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.height / 2f
                val trackTop = lerp(Color(0xEAE6E9EE), Color(0xE0141620), darkT)
                val trackBottom = lerp(Color(0xF4F3F5F8), Color(0xEB1D2232), darkT)

                // Main translucent capsule body
                drawRoundRect(
                    brush = Brush.verticalGradient(listOf(trackTop, trackBottom)),
                    cornerRadius = CornerRadius(r, r)
                )

                // Soft liquid elemental caustic glow trailing behind the sliding lens!
                val trailCenterX = (trailIndex + 0.5f) * slotWidthPx
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.glowCore.copy(alpha = (0.14f + 0.14f * darkT) + 0.12f * squishFactor),
                            Color.Transparent
                        ),
                        center = Offset(trailCenterX, size.height * 0.5f),
                        radius = slotWidthPx * 1.15f
                    ),
                    radius = slotWidthPx * 1.15f,
                    center = Offset(trailCenterX, size.height * 0.5f)
                )

                // Recessed inner top shadow (gives authentic carved glass trough depth)
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.11f + 0.31f * darkT),
                        0.28f to Color.Transparent,
                        0.82f to Color.Transparent,
                        1.0f to Color.White.copy(alpha = 0.72f - 0.60f * darkT)
                    ),
                    cornerRadius = CornerRadius(r, r)
                )

                // Subtle outer frosted bezel rim
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lerp(Color(0xFFCBD2DC).copy(alpha = 0.85f), palette.border.copy(alpha = 0.78f), darkT),
                            lerp(Color.White.copy(alpha = 0.95f), Color.White.copy(alpha = 0.22f), darkT)
                        )
                    ),
                    cornerRadius = CornerRadius(r, r),
                    style = Stroke(width = 1.25.dp.toPx())
                )
            }

            // 2. Sliding / Squishing Refractive Liquid-Glass Lens Thumb
            val baseLensWidthPx = slotWidthPx * 0.94f
            val lensWidthPx = baseLensWidthPx * (1f - 0.26f * squishFactor)
            val lensHeightPx = dockHeightPx * (0.84f + 0.08f * squishFactor)
            val lensCenterX = (animatedIndex + 0.5f) * slotWidthPx
            val lensLeftPx = (lensCenterX - lensWidthPx / 2f).coerceIn(
                4f,
                (totalWidthPx - lensWidthPx - 4f).coerceAtLeast(4f)
            )
            val lensTopPx = (dockHeightPx - lensHeightPx) / 2f

            Canvas(modifier = Modifier.fillMaxSize()) {
                val lensRect = Rect(
                    left = lensLeftPx,
                    top = lensTopPx,
                    right = lensLeftPx + lensWidthPx,
                    bottom = lensTopPx + lensHeightPx
                )
                val lensRadius = lensHeightPx / 2f

                // Soft levitation shadow under the liquid lens
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.14f + 0.36f * darkT),
                    topLeft = Offset(lensRect.left, lensRect.top + 3.dp.toPx()),
                    size = Size(lensRect.width, lensRect.height),
                    cornerRadius = CornerRadius(lensRadius, lensRadius)
                )

                // Prismatic chromatic dispersion aura (delicate cyan left + warm rose/elemental right edge)
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        0.0f to Color(0xFF7BE8FF).copy(alpha = 0.36f + 0.14f * squishFactor),
                        0.5f to palette.primaryBright.copy(alpha = 0.16f + 0.10f * darkT),
                        1.0f to Color(0xFFFFA8E2).copy(alpha = 0.34f + 0.14f * squishFactor),
                        startX = lensRect.left,
                        endX = lensRect.right
                    ),
                    topLeft = Offset(lensRect.left - 1.dp.toPx(), lensRect.top - 0.5.dp.toPx()),
                    size = Size(lensRect.width + 2.dp.toPx(), lensRect.height + 1.dp.toPx()),
                    cornerRadius = CornerRadius(lensRadius, lensRadius),
                    style = Stroke(width = 2.6.dp.toPx())
                )

                // Main liquid-glass lens fill smoothly interpolated Light ↔ Dark
                val lensPath = Path().apply {
                    addRoundRect(RoundRect(lensRect, CornerRadius(lensRadius, lensRadius)))
                }
                clipPath(lensPath) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                lerp(Color(0xF8FFFFFF), Color(0xD8293146), darkT),
                                lerp(Color(0xDCEAF0F6), Color(0xC2171C2B), darkT),
                                lerp(Color(0xF5FFFFFF), Color(0xDE222B40), darkT)
                            ),
                            startY = lensRect.top,
                            endY = lensRect.bottom
                        ),
                        topLeft = lensRect.topLeft,
                        size = lensRect.size
                    )

                    // Top specular curved liquid reflection band
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.92f - 0.50f * darkT),
                                Color.Transparent
                            ),
                            startY = lensRect.top,
                            endY = lensRect.top + lensRect.height * 0.44f
                        ),
                        topLeft = Offset(lensRect.left + 4.dp.toPx(), lensRect.top + 1.5.dp.toPx()),
                        size = Size((lensRect.width - 8.dp.toPx()).coerceAtLeast(1f), lensRect.height * 0.42f),
                        cornerRadius = CornerRadius(lensRadius, lensRadius)
                    )

                    // Inner lower edge caustic glow
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                palette.primaryBright.copy(alpha = 0.16f + 0.08f * darkT)
                            ),
                            startY = lensRect.top + lensRect.height * 0.65f,
                            endY = lensRect.bottom
                        ),
                        topLeft = lensRect.topLeft,
                        size = lensRect.size,
                        cornerRadius = CornerRadius(lensRadius, lensRadius)
                    )
                }

                // Crisp luminous crystal rim highlight around the liquid lens
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 1f - 0.28f * darkT),
                            lerp(Color(0xFFD5DCE6).copy(alpha = 0.75f), palette.primaryBright.copy(alpha = 0.34f), darkT),
                            Color.White.copy(alpha = 0.95f - 0.50f * darkT)
                        ),
                        startY = lensRect.top,
                        endY = lensRect.bottom
                    ),
                    topLeft = lensRect.topLeft,
                    size = lensRect.size,
                    cornerRadius = CornerRadius(lensRadius, lensRadius),
                    style = Stroke(width = 1.6.dp.toPx())
                )
            }

            // 3. Minimalist Tab Items with Real-Time Optical Lens Magnification, Lift & Smooth Icon Crossfade!
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { idx, item ->
                    val distance = abs(animatedIndex - idx.toFloat()).coerceIn(0f, 1f)
                    val focusFactor = (1f - distance).coerceIn(0f, 1f)
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val pressScale by animateFloatAsState(
                        targetValue = if (isPressed) 0.90f else 1f,
                        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
                        label = "dock_tab_press_$idx"
                    )
                    val magnifiedScale = (1f + 0.12f * focusFactor) * pressScale
                    val liftPx = with(density) { (-2.dp * focusFactor).toPx() }
                    val activeColor = lerp(Color(0xFF151922), Color.White, darkT)
                    val inactiveColor = lerp(Color(0xFF87909B), Color(0xFF8390A8), darkT)
                    val itemColor = lerp(inactiveColor, activeColor, focusFactor)

                    Box(
                        modifier = Modifier
                            .width(slotWidthDp)
                            .fillMaxHeight()
                            .testTag(item.tag)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onNavigate(item.id)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.graphicsLayer {
                                scaleX = magnifiedScale
                                scaleY = magnifiedScale
                                translationY = liftPx
                            },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // Smooth alpha crossfade between outlined and filled icon as the lens glides over!
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = item.inactiveIcon,
                                    contentDescription = if (focusFactor <= 0.5f) item.label else null,
                                    tint = itemColor.copy(alpha = (1f - focusFactor).coerceIn(0f, 1f)),
                                    modifier = Modifier.size(19.dp)
                                )
                                Icon(
                                    imageVector = item.activeIcon,
                                    contentDescription = if (focusFactor > 0.5f) item.label else null,
                                    tint = itemColor.copy(alpha = focusFactor.coerceIn(0f, 1f)),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            Text(
                                text = item.label,
                                fontFamily = BodyFontFamily,
                                fontSize = 10.5.sp,
                                fontWeight = if (focusFactor > 0.5f) FontWeight.Bold else FontWeight.Medium,
                                color = itemColor,
                                letterSpacing = 0.1.sp
                            )
                        }
                    }
                }
            }

            // 4. Subtle Top Specular Glass Glaze over the Lens Area
            Box(
                modifier = Modifier
                    .offset { IntOffset(lensLeftPx.roundToInt(), lensTopPx.roundToInt()) }
                    .width(with(density) { lensWidthPx.toDp() })
                    .height(with(density) { (lensHeightPx * 0.34f).toDp() })
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.30f - 0.16f * darkT),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun LiquidGlassVoiceButton(
    darkProgress: Float,
    accentColor: Color,
    accentBright: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = Spring.StiffnessMedium),
        label = "voice_pill_press"
    )
    val bgTop = lerp(Color(0xFAFFFFFF), Color(0xEB232B3E), darkProgress)
    val bgBottom = lerp(Color(0xE8EEF3F8), Color(0xF0151A28), darkProgress)
    val iconTint = lerp(accentColor, accentBright, darkProgress)
    val labelTint = lerp(Color(0xFF151922), Color.White, darkProgress)
    val spotShadow = lerp(Color(0xFF94A3B8), Color.Black, darkProgress)

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .testTag("fab_voice_studio")
            .shadow(
                elevation = (10f + 4f * darkProgress).dp,
                shape = CircleShape,
                ambientColor = accentBright,
                spotColor = spotShadow
            )
            .clip(CircleShape)
            .background(Brush.linearGradient(colors = listOf(bgTop, bgBottom)))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val r = size.height / 2f
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF7BE8FF).copy(alpha = 0.45f),
                        accentBright.copy(alpha = 0.72f),
                        Color(0xFFFFA8E2).copy(alpha = 0.45f)
                    )
                ),
                cornerRadius = CornerRadius(r, r),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = stringResource(R.string.cd_open_voice_studio),
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Voice",
                fontFamily = BodyFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = labelTint
            )
        }
    }
}
