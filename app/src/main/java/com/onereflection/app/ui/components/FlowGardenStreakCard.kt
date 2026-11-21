package com.onereflection.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.R
import com.onereflection.app.domain.DailyStreakSummary
import com.onereflection.app.domain.StreakDayNode
import com.onereflection.app.domain.StreakDayStatus
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors
import com.onereflection.app.ui.theme.SanctuaryEase

/**
 * Prominent Flow Garden Daily Streak Counter Hero Card.
 *
 * Displays the user's live consecutive daily habit streak with an animated elemental flame crest,
 * rolling digit counter, connected 7-day streak chain, personal best badge, and next milestone
 * progress bar. Fully harmonized with all 7 Elemental Themes and Light/Dark mode.
 */
@Composable
fun FlowGardenDailyStreakCard(
    streak: DailyStreakSummary,
    onActionClick: (() -> Unit)? = null,
    actionLabel: String? = null,
    modifier: Modifier = Modifier
) {
    val palette = LocalElementalPalette.current
    val infinite = rememberInfiniteTransition(label = "streak_flame_pulse")
    val pulse by infinite.animateFloat(
        initialValue = 0.84f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "streak_aura_pulse"
    )

    val milestoneAnim by animateFloatAsState(
        targetValue = streak.milestoneProgress.coerceIn(0.05f, 1f),
        animationSpec = tween(durationMillis = 720, easing = SanctuaryEase),
        label = "streak_milestone_progress"
    )

    val statusBadgeBg by animateColorAsState(
        targetValue = when {
            streak.isPerfectDay -> OneReflectionColors.GoldLuminous
            streak.todayGoalAchieved -> palette.primaryBright
            else -> Color.White.copy(alpha = 0.14f)
        },
        animationSpec = tween(360, easing = SanctuaryEase),
        label = "streak_status_bg"
    )
    val statusBadgeText by animateColorAsState(
        targetValue = when {
            streak.isPerfectDay || streak.todayGoalAchieved -> palette.heroEnd
            else -> Color.White
        },
        animationSpec = tween(360, easing = SanctuaryEase),
        label = "streak_status_txt"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("flow_garden_daily_streak_card"),
        shape = RoundedCornerShape(30.dp),
        color = palette.heroEnd,
        border = BorderStroke(
            width = 1.3.dp,
            color = if (streak.todayGoalAchieved) {
                palette.primaryBright.copy(alpha = 0.65f)
            } else {
                palette.primary.copy(alpha = 0.42f)
            }
        ),
        shadowElevation = (10f - 5f * palette.darkProgress).dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Layered Atmospheric Elemental Glow Backdrop
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRect(
                    brush = Brush.verticalGradient(
                        0.0f to palette.heroStart,
                        0.58f to palette.heroEnd,
                        1.0f to palette.heroEnd
                    )
                )
                val glowOrigin = Offset(size.width * 0.16f, size.height * 0.24f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            palette.glowCore.copy(alpha = (if (streak.todayGoalAchieved) 0.44f else 0.28f) * pulse),
                            palette.glowOuter.copy(alpha = 0.14f * pulse),
                            Color.Transparent
                        ),
                        center = glowOrigin,
                        radius = size.width * 0.62f
                    ),
                    radius = size.width * 0.62f,
                    center = glowOrigin
                )

                // Subtle organic orbital arc accent on right edge
                val arcRadius = size.width * 0.42f
                drawCircle(
                    color = palette.primaryBright.copy(alpha = 0.08f),
                    radius = arcRadius,
                    center = Offset(size.width * 0.94f, size.height * 0.14f),
                    style = Stroke(width = 1.4.dp.toPx())
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                // Top Eyebrow + Status Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.streak_eyebrow),
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        letterSpacing = 1.1.sp,
                        color = palette.primaryBright
                    )

                    Surface(
                        modifier = Modifier.testTag("streak_status_badge"),
                        shape = CircleShape,
                        color = statusBadgeBg,
                        border = if (!streak.todayGoalAchieved) {
                            BorderStroke(1.dp, palette.primaryBright.copy(alpha = 0.42f))
                        } else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (streak.isPerfectDay) {
                                    Icons.Filled.AutoAwesome
                                } else if (streak.todayGoalAchieved) {
                                    Icons.Filled.Check
                                } else {
                                    Icons.Filled.LocalFireDepartment
                                },
                                contentDescription = null,
                                tint = statusBadgeText,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = when {
                                    streak.isPerfectDay -> stringResource(R.string.streak_perfect_badge)
                                    streak.todayGoalAchieved -> stringResource(R.string.streak_secured_badge)
                                    else -> "${streak.habitsRemainingForStreak} MORE TODAY"
                                },
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 0.6.sp,
                                color = statusBadgeText
                            )
                        }
                    }
                }

                // Main Focal Counter Row: Animated Flame Crest + Rolling Day Counter + Personal Best
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    LivingStreakFlameEmblem(
                        isSecuredToday = streak.todayGoalAchieved,
                        pulse = pulse,
                        primaryColor = palette.primary,
                        brightColor = palette.primaryBright,
                        secondaryColor = OneReflectionColors.GoldLuminous
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AnimatedContent(
                                targetState = streak.currentStreakDays,
                                transitionSpec = {
                                    val dir = if (targetState >= initialState) -1 else 1
                                    (fadeIn(tween(260)) + slideInVertically(tween(300, easing = SanctuaryEase)) { dir * it / 2 })
                                        .togetherWith(
                                            fadeOut(tween(180)) + slideOutVertically(tween(220)) { -dir * it / 2 }
                                        )
                                },
                                label = "streak_day_count_roll"
                            ) { count ->
                                Text(
                                    text = "$count",
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 46.sp,
                                    lineHeight = 46.sp,
                                    letterSpacing = (-1.2).sp,
                                    color = Color.White,
                                    modifier = Modifier.testTag("streak_count_value")
                                )
                            }

                            Column(modifier = Modifier.padding(bottom = 6.dp)) {
                                Text(
                                    text = if (streak.currentStreakDays == 1) {
                                        stringResource(R.string.streak_day_singular)
                                    } else {
                                        stringResource(R.string.streak_days_label)
                                    },
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp,
                                    color = palette.primaryBright
                                )
                                Text(
                                    text = "${streak.completedHabitsToday}/${streak.totalHabitsCount} rituals complete today",
                                    fontFamily = BodyFontFamily,
                                    fontSize = 12.sp,
                                    color = Color(0xFFE6DFD8),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Personal Best Vertical Pill
                    Surface(
                        modifier = Modifier.testTag("streak_best_badge"),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.10f),
                        border = BorderStroke(1.dp, OneReflectionColors.GoldLuminous.copy(alpha = 0.32f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "Personal best streak",
                                tint = OneReflectionColors.GoldLuminous,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${streak.bestStreakDays}d",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "BEST",
                                fontFamily = MonoFontFamily,
                                fontSize = 8.sp,
                                letterSpacing = 0.7.sp,
                                color = Color(0xFFD2C7BE)
                            )
                        }
                    }
                }

                // Connected 7-Day Streak Chain Strip
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("streak_7day_chain"),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        streak.weekNodes.forEachIndexed { idx, node ->
                            StreakChainDayPill(
                                node = node,
                                primaryColor = palette.primary,
                                brightColor = palette.primaryBright,
                                onPrimaryColor = palette.onPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            if (idx < streak.weekNodes.lastIndex) {
                                val nextNode = streak.weekNodes[idx + 1]
                                val linkLit = (node.status == StreakDayStatus.ACHIEVED || node.status == StreakDayStatus.TODAY_ACHIEVED) &&
                                    (nextNode.status == StreakDayStatus.ACHIEVED || nextNode.status == StreakDayStatus.TODAY_ACHIEVED)
                                Box(
                                    modifier = Modifier
                                        .width(8.dp)
                                        .height(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (linkLit) palette.primaryBright.copy(alpha = 0.85f)
                                            else Color.White.copy(alpha = 0.15f)
                                        )
                                )
                            }
                        }
                    }
                }

                // Milestone Progress + Optional Quick Action CTA
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (streak.todayGoalAchieved) {
                                "Goal achieved! ${(streak.nextMilestoneDays - streak.currentStreakDays).coerceAtLeast(1)}d to ${streak.nextMilestoneDays}-day milestone"
                            } else {
                                "Complete ${streak.habitsRemainingForStreak} more ritual${if (streak.habitsRemainingForStreak == 1) "" else "s"} to reach Day ${streak.currentStreakDays + 1}"
                            },
                            fontFamily = BodyFontFamily,
                            fontSize = 12.sp,
                            color = Color(0xFFEBE4DD),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(Modifier.width(8.dp))

                        if (onActionClick != null && !actionLabel.isNullOrBlank()) {
                            Surface(
                                modifier = Modifier
                                    .testTag("streak_card_action_button")
                                    .clip(CircleShape)
                                    .clickable { onActionClick() },
                                shape = CircleShape,
                                color = palette.primaryBright
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = actionLabel,
                                        fontFamily = BodyFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = palette.heroEnd
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = actionLabel,
                                        tint = palette.heroEnd,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "${streak.currentStreakDays}/${streak.nextMilestoneDays}d",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = palette.primaryBright
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(milestoneAnim)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(palette.primary, palette.primaryBright, OneReflectionColors.GoldLuminous)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LivingStreakFlameEmblem(
    isSecuredToday: Boolean,
    pulse: Float,
    primaryColor: Color,
    brightColor: Color,
    secondaryColor: Color
) {
    val desc = stringResource(R.string.cd_streak_flame)
    Box(
        modifier = Modifier
            .size(60.dp)
            .semantics { contentDescription = desc },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width * 0.5f
            val cy = size.height * 0.5f
            val outerR = size.minDimension * 0.49f

            // Radiant breathing aura halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        (if (isSecuredToday) secondaryColor else brightColor).copy(alpha = 0.42f * pulse),
                        primaryColor.copy(alpha = 0.18f * pulse),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = outerR
                ),
                radius = outerR,
                center = Offset(cx, cy)
            )

            // Subtle circular crest border
            drawCircle(
                color = (if (isSecuredToday) secondaryColor else brightColor).copy(alpha = 0.48f),
                radius = size.minDimension * 0.39f,
                center = Offset(cx, cy),
                style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
            )

            // Stylized flame silhouette behind icon
            val flameH = size.height * (0.34f + 0.04f * pulse)
            val flameW = size.width * 0.24f
            val flamePath = Path().apply {
                moveTo(cx, cy - flameH * 0.62f)
                cubicTo(
                    cx + flameW * 0.95f, cy - flameH * 0.08f,
                    cx + flameW * 0.85f, cy + flameH * 0.55f,
                    cx, cy + flameH * 0.58f
                )
                cubicTo(
                    cx - flameW * 0.85f, cy + flameH * 0.55f,
                    cx - flameW * 0.95f, cy - flameH * 0.08f,
                    cx, cy - flameH * 0.62f
                )
                close()
            }
            drawPath(
                path = flamePath,
                brush = Brush.verticalGradient(
                    colors = listOf(secondaryColor.copy(alpha = 0.32f), primaryColor.copy(alpha = 0.22f))
                )
            )
        }

        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = if (isSecuredToday) secondaryColor else brightColor,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun StreakChainDayPill(
    node: StreakDayNode,
    primaryColor: Color,
    brightColor: Color,
    onPrimaryColor: Color,
    modifier: Modifier = Modifier
) {
    val isLit = node.status == StreakDayStatus.ACHIEVED || node.status == StreakDayStatus.TODAY_ACHIEVED
    val isToday = node.status == StreakDayStatus.TODAY_ACHIEVED || node.status == StreakDayStatus.TODAY_PENDING

    val circleBg by animateColorAsState(
        targetValue = when (node.status) {
            StreakDayStatus.TODAY_ACHIEVED -> OneReflectionColors.GoldLuminous
            StreakDayStatus.ACHIEVED -> primaryColor
            StreakDayStatus.TODAY_PENDING -> Color.White.copy(alpha = 0.16f)
            StreakDayStatus.MISSED -> Color.White.copy(alpha = 0.06f)
            StreakDayStatus.UPCOMING -> Color.White.copy(alpha = 0.04f)
        },
        animationSpec = tween(320, easing = SanctuaryEase),
        label = "streak_day_node_bg"
    )

    Column(
        modifier = modifier.testTag("streak_day_node_${node.dayIndex}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(circleBg)
                .then(
                    if (isToday) {
                        Modifier.border(
                            width = 1.4.dp,
                            color = if (isLit) Color.White else brightColor,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLit -> Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "${node.shortLabel} goal achieved",
                    tint = if (node.status == StreakDayStatus.TODAY_ACHIEVED) Color(0xFF1A1206) else onPrimaryColor,
                    modifier = Modifier.size(14.dp)
                )
                node.status == StreakDayStatus.TODAY_PENDING -> Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = "Today in progress",
                    tint = brightColor,
                    modifier = Modifier.size(13.dp)
                )
                else -> Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = if (node.status == StreakDayStatus.UPCOMING) 0.22f else 0.35f))
                )
            }
        }

        Text(
            text = node.shortLabel,
            fontFamily = MonoFontFamily,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
            fontSize = 9.sp,
            color = if (isToday) brightColor else Color(0xFFCCC3BB)
        )
    }
}
