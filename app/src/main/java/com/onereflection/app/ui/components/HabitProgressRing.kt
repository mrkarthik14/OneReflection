package com.onereflection.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.model.HabitTone
import com.onereflection.app.model.MindfulHabit
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class HabitTonePalette(
    val primary: Color,
    val secondary: Color,
    val surfaceTint: Color,
    val track: Color
)

fun habitTonePalette(tone: HabitTone, oledMode: Boolean): HabitTonePalette = when (tone) {
    HabitTone.TERRACOTTA -> HabitTonePalette(
        primary = if (oledMode) OneReflectionColors.TerracottaLight else OneReflectionColors.Terracotta,
        secondary = OneReflectionColors.GoldLuminous,
        surfaceTint = if (oledMode) Color(0xFF2A1814) else OneReflectionColors.PeachMist,
        track = if (oledMode) Color(0xFF2D2420) else OneReflectionColors.Terracotta.copy(alpha = 0.14f)
    )
    HabitTone.SAGE -> HabitTonePalette(
        primary = if (oledMode) OneReflectionColors.SageLight else OneReflectionColors.SageDeep,
        secondary = Color(0xFF8FD3BE),
        surfaceTint = if (oledMode) Color(0xFF132520) else OneReflectionColors.SageMist,
        track = if (oledMode) Color(0xFF1E2D28) else OneReflectionColors.SageDeep.copy(alpha = 0.14f)
    )
    HabitTone.GOLD -> HabitTonePalette(
        primary = if (oledMode) OneReflectionColors.GoldLuminous else OneReflectionColors.BurnishedGold,
        secondary = Color(0xFFF6D68B),
        surfaceTint = if (oledMode) Color(0xFF2A2110) else OneReflectionColors.HoneyMist,
        track = if (oledMode) Color(0xFF2D2618) else OneReflectionColors.BurnishedGold.copy(alpha = 0.16f)
    )
    HabitTone.INDIGO -> HabitTonePalette(
        primary = if (oledMode) OneReflectionColors.IndigoLight else OneReflectionColors.IndigoDeep,
        secondary = Color(0xFFB3C3EC),
        surfaceTint = if (oledMode) Color(0xFF171E2E) else OneReflectionColors.IndigoMist,
        track = if (oledMode) Color(0xFF212838) else OneReflectionColors.IndigoDeep.copy(alpha = 0.14f)
    )
    HabitTone.PLUM -> HabitTonePalette(
        primary = if (oledMode) Color(0xFFD699BF) else OneReflectionColors.PlumAccent,
        secondary = OneReflectionColors.TerracottaLight,
        surfaceTint = if (oledMode) Color(0xFF291924) else OneReflectionColors.LilacMist,
        track = if (oledMode) Color(0xFF2E212B) else OneReflectionColors.PlumAccent.copy(alpha = 0.14f)
    )
}

/**
 * Reusable Jetpack Compose visual progress ring with spring-animated sweep gradient,
 * subtle perimeter cadence ticks, completion halo, and leading tip indicator dot.
 */
@Composable
fun MindfulProgressRing(
    progress: Float,
    primaryColor: Color,
    secondaryColor: Color,
    trackColor: Color,
    size: Dp = 96.dp,
    strokeWidth: Dp = 9.dp,
    showTicks: Boolean = true,
    modifier: Modifier = Modifier,
    centerContent: @Composable BoxScope.() -> Unit
) {
    val clampedTarget = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedTarget,
        animationSpec = spring(
            dampingRatio = 0.74f,
            stiffness = Spring.StiffnessLow
        ),
        label = "mindful_progress_ring"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val inset = strokePx / 2f + (if (showTicks) 4.dp.toPx() else 1.dp.toPx())
            val arcDiameter = (this.size.minDimension - inset * 2f).coerceAtLeast(1f)
            val topLeft = Offset(
                x = (this.size.width - arcDiameter) / 2f,
                y = (this.size.height - arcDiameter) / 2f
            )
            val arcSize = Size(arcDiameter, arcDiameter)
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = arcDiameter / 2f

            // Subtle outer completion glow when goal is achieved
            if (animatedProgress >= 0.99f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.22f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = this.size.minDimension / 2f
                    ),
                    radius = this.size.minDimension / 2f,
                    center = center
                )
            }

            // Delicate outer ritual cadence ticks
            if (showTicks) {
                val tickCount = 24
                val outerR = radius + strokePx * 0.68f
                val innerR = radius + strokePx * 0.45f
                for (i in 0 until tickCount) {
                    val angleDeg = -90.0 + (i * (360.0 / tickCount))
                    val angleRad = angleDeg * PI / 180.0
                    val tickRatio = i.toFloat() / tickCount.toFloat()
                    val isLit = tickRatio <= animatedProgress && animatedProgress > 0.01f
                    val start = Offset(
                        x = center.x + (innerR * cos(angleRad)).toFloat(),
                        y = center.y + (innerR * sin(angleRad)).toFloat()
                    )
                    val end = Offset(
                        x = center.x + (outerR * cos(angleRad)).toFloat(),
                        y = center.y + (outerR * sin(angleRad)).toFloat()
                    )
                    drawLine(
                        color = if (isLit) primaryColor.copy(alpha = 0.55f) else trackColor.copy(alpha = 0.65f),
                        start = start,
                        end = end,
                        strokeWidth = 1.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // Base track ring
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Sweep-gradient active progress arc starting at 12 o'clock (-90°)
            if (animatedProgress > 0.004f) {
                val sweepAngle = (360f * animatedProgress).coerceAtLeast(2f)
                rotate(degrees = -90f, pivot = center) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            0.0f to primaryColor,
                            0.65f to secondaryColor,
                            1.0f to primaryColor,
                            center = center
                        ),
                        startAngle = 0f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                }

                // Luminous leading tip indicator dot
                val tipAngleRad = (-90.0 + sweepAngle) * PI / 180.0
                val tipOffset = Offset(
                    x = center.x + (radius * cos(tipAngleRad)).toFloat(),
                    y = center.y + (radius * sin(tipAngleRad)).toFloat()
                )
                drawCircle(
                    color = Color.White,
                    radius = strokePx * 0.28f,
                    center = tipOffset
                )
            }
        }

        centerContent()
    }
}

/**
 * Hero Mindful Harmony Card showcasing aggregate daily progress across all tracked habits.
 */
@Composable
fun HabitHarmonySummaryCard(
    habits: List<MindfulHabit>,
    oledMode: Boolean,
    onAddHabitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalElementalPalette.current
    val completedCount = habits.count { it.isCompleted }
    val totalCount = habits.size.coerceAtLeast(1)
    val overallProgress = if (habits.isEmpty()) 0f else habits.map { it.progress }.average().toFloat()
    val overallPercent = (overallProgress * 100f).roundToInt()
    val bestStreak = habits.maxOfOrNull { it.streakDays } ?: 0

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habits_harmony_summary_card"),
        shape = RoundedCornerShape(30.dp),
        color = palette.heroEnd,
        border = BorderStroke(1.2.dp, palette.primaryBright.copy(alpha = 0.42f)),
        shadowElevation = if (oledMode) 0.dp else 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            MindfulProgressRing(
                progress = overallProgress,
                primaryColor = palette.primary,
                secondaryColor = palette.primaryBright,
                trackColor = Color.White.copy(alpha = 0.14f),
                size = 114.dp,
                strokeWidth = 10.dp,
                showTicks = true,
                modifier = Modifier.testTag("habits_overall_progress_ring")
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$overallPercent%",
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                    Text(
                        text = "$completedCount/$totalCount DONE",
                        fontFamily = MonoFontFamily,
                        fontSize = 9.sp,
                        letterSpacing = 0.7.sp,
                        color = palette.primaryBright
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OneReflectionLogoMark(size = 16.dp, cornerRadius = 5.dp, showGoldRing = false)
                    Text(
                        text = "DAILY MINDFUL RING",
                        fontFamily = MonoFontFamily,
                        fontSize = 9.5.sp,
                        letterSpacing = 1.sp,
                        color = OneReflectionColors.GoldLuminous
                    )
                }

                Text(
                    text = if (completedCount == totalCount && habits.isNotEmpty()) {
                        "Sanctuary Complete"
                    } else {
                        "Rhythm in Balance"
                    },
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 21.sp,
                    lineHeight = 25.sp,
                    color = Color.White
                )

                Text(
                    text = "Longest active streak: ${bestStreak}d across ${habits.size} rituals",
                    fontFamily = BodyFontFamily,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFFB5ACA3)
                )

                Surface(
                    modifier = Modifier
                        .testTag("habits_new_ritual_button")
                        .clip(CircleShape)
                        .clickable { onAddHabitClick() },
                    shape = CircleShape,
                    color = OneReflectionColors.Terracotta
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "New Mindful Habit",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "New Ritual",
                            fontFamily = BodyFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Habit Tracking Card with individual visual progress ring, 7-day cadence dots,
 * streak badge, and accessible - / +step tactile controls.
 */
@Composable
fun MindfulHabitTrackerCard(
    habit: MindfulHabit,
    oledMode: Boolean,
    canDelete: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = habitTonePalette(habit.tone, oledMode)
    val cardBg = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.SurfaceCard
    val borderColor = when {
        habit.isCompleted -> palette.primary.copy(alpha = 0.45f)
        oledMode -> OneReflectionColors.OledBorder
        else -> OneReflectionColors.HairlineLight
    }
    val pct = (habit.progress * 100f).roundToInt()
    val dayLetters = listOf("M", "T", "W", "T", "F", "S", "S")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_tracker_card_${habit.id}"),
        shape = RoundedCornerShape(26.dp),
        color = cardBg,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (oledMode) 0.dp else 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MindfulProgressRing(
                    progress = habit.progress,
                    primaryColor = palette.primary,
                    secondaryColor = palette.secondary,
                    trackColor = palette.track,
                    size = 76.dp,
                    strokeWidth = 7.dp,
                    showTicks = true,
                    modifier = Modifier.testTag("habit_ring_${habit.id}")
                ) {
                    if (habit.isCompleted) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(palette.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "${habit.title} completed",
                                tint = if (oledMode) OneReflectionColors.InkPrimary else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "$pct%",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = habit.title,
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = palette.surfaceTint
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = palette.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "${habit.streakDays}d",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = palette.primary
                                )
                            }
                        }
                    }

                    Text(
                        text = habit.subtitle,
                        fontFamily = BodyFontFamily,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${habit.currentValue}",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = palette.primary
                        )
                        Text(
                            text = "/ ${habit.targetValue} ${habit.unit}",
                            fontFamily = MonoFontFamily,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }
                }
            }

            // Bottom Control Row: 7-Day Cadence Dots + Stepper Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    dayLetters.forEachIndexed { idx, letter ->
                        val done = habit.weeklyHistory.getOrElse(idx) { false }
                        val isToday = idx == 3
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (isToday) 9.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            done -> palette.primary
                                            isToday -> palette.primary.copy(alpha = 0.28f)
                                            else -> palette.track
                                        }
                                    )
                            )
                            Text(
                                text = letter,
                                fontFamily = MonoFontFamily,
                                fontSize = 8.5.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (isToday) palette.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (canDelete) {
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .size(36.dp)
                                .testTag("habit_delete_${habit.id}")
                                .clip(CircleShape)
                                .clickable { onDelete() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Remove ${habit.title}",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(38.dp)
                            .testTag("habit_dec_${habit.id}")
                            .clip(CircleShape)
                            .clickable(enabled = habit.currentValue > 0) { onDecrement() },
                        shape = CircleShape,
                        color = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.SurfaceWarm,
                        border = BorderStroke(1.dp, if (oledMode) OneReflectionColors.OledBorder else OneReflectionColors.HairlineLight)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Remove,
                                contentDescription = "Decrease ${habit.title}",
                                tint = if (habit.currentValue > 0) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("habit_inc_${habit.id}")
                            .clip(CircleShape)
                            .clickable(enabled = !habit.isCompleted) { onIncrement() },
                        shape = CircleShape,
                        color = if (habit.isCompleted) palette.surfaceTint else palette.primary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (habit.isCompleted) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Done",
                                    tint = palette.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Done",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = palette.primary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add ${habit.stepValue} ${habit.unit}",
                                    tint = if (oledMode) OneReflectionColors.InkPrimary else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "${habit.stepValue} ${habit.unit}",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (oledMode) OneReflectionColors.InkPrimary else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Compact Home Dashboard Spotlight featuring the Mindful Habit Progress Ring
 * and a one-tap quick-log action for the next active habit.
 */
@Composable
fun HomeHabitRingSpotlightCard(
    habits: List<MindfulHabit>,
    oledMode: Boolean,
    onIncrementHabit: (String) -> Unit,
    onOpenHabits: () -> Unit,
    hydrationLiters: Double = 1.8,
    modifier: Modifier = Modifier
) {
    val palette = LocalElementalPalette.current
    val overallProgress = if (habits.isEmpty()) 0f else habits.map { it.progress }.average().toFloat()
    val overallPercent = (overallProgress * 100f).roundToInt()
    val completedCount = habits.count { it.isCompleted }
    val nextHabit = habits.firstOrNull { !it.isCompleted } ?: habits.firstOrNull()
    val cardBg = palette.surfaceCard
    val borderColor = palette.border

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_habit_ring_spotlight")
            .clip(RoundedCornerShape(26.dp))
            .clickable { onOpenHabits() },
        shape = RoundedCornerShape(26.dp),
        color = cardBg,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = (2f - 2f * palette.darkProgress).dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MindfulProgressRing(
                    progress = overallProgress,
                    primaryColor = palette.primary,
                    secondaryColor = palette.primaryBright,
                    trackColor = palette.surfaceElevated,
                    size = 82.dp,
                    strokeWidth = 7.5.dp,
                    showTicks = true,
                    modifier = Modifier.testTag("home_overall_habit_ring")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$overallPercent%",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "$completedCount/${habits.size}",
                            fontFamily = MonoFontFamily,
                            fontSize = 9.sp,
                            color = palette.primary
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MINDFUL HABIT RING",
                            fontFamily = MonoFontFamily,
                            fontSize = 9.5.sp,
                            letterSpacing = 0.9.sp,
                            color = palette.primary
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Daily Rituals",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Text(
                        text = nextHabit?.title ?: "All Rituals Complete",
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (nextHabit != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${nextHabit.currentValue}/${nextHabit.targetValue} ${nextHabit.unit} • ${nextHabit.streakDays}d streak",
                                fontFamily = MonoFontFamily,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (!nextHabit.isCompleted) {
                                Surface(
                                    modifier = Modifier
                                        .testTag("home_quick_log_habit")
                                        .clip(CircleShape)
                                        .clickable { onIncrementHabit(nextHabit.id) },
                                    shape = CircleShape,
                                    color = palette.primary
                                ) {
                                    Text(
                                        text = "+${nextHabit.stepValue} ${nextHabit.unit}",
                                        fontFamily = MonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        color = palette.onPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Live 7-Day Consistency Sparkline Complementing Today's Progress Ring
            MiniWeeklyTrendSparkline(
                habits = habits,
                hydrationLiters = hydrationLiters,
                modifier = Modifier.testTag("home_weekly_trend_sparkline")
            )
        }
    }
}
