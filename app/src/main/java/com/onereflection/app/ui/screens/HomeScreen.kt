package com.onereflection.app.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.R
import com.onereflection.app.model.AvatarUrls
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.ElementalTheme
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.ElementalDayPill
import com.onereflection.app.ui.components.HomeHabitRingSpotlightCard
import com.onereflection.app.ui.components.OneReflectionLogoMark
import com.onereflection.app.ui.components.OneReflectionSwitch
import com.onereflection.app.ui.components.WaveformVisualizer
import com.onereflection.app.ui.components.elementalIcon
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors
import com.onereflection.app.ui.theme.SanctuaryEase
import com.onereflection.app.ui.theme.formatLiters
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    uiState: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onPlayChime: (ChimeType) -> Unit,
    onStopChime: () -> Unit,
    onAddHydration: (Double) -> Unit,
    onIncrementHabit: (String) -> Unit,
    onSelectDay: (Int) -> Unit,
    onOpenThemeSettings: () -> Unit
) {
    var isPlayingPreview by remember { mutableStateOf(false) }
    var heroAlarmActive by remember { mutableStateOf(true) }

    LaunchedEffect(isPlayingPreview) {
        if (isPlayingPreview) {
            delay(5000L)
            isPlayingPreview = false
            onStopChime()
        }
    }

    // Subtle organic spring breath on hero surface whenever the user switches days
    val dayPulseScale = remember { Animatable(1f) }
    var lastDayIndex by remember { mutableIntStateOf(uiState.selectedDayIndex) }
    LaunchedEffect(uiState.selectedDayIndex) {
        if (uiState.selectedDayIndex != lastDayIndex) {
            lastDayIndex = uiState.selectedDayIndex
            dayPulseScale.snapTo(0.986f)
            dayPulseScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.62f, stiffness = 320f)
            )
        }
    }

    val oledMode = uiState.oledMode
    val palette = LocalElementalPalette.current
    val activeElement = uiState.activeElementalTheme
    val cardBg = palette.surfaceCard
    val borderColor = palette.border
    val nextAlarm = uiState.alarms.firstOrNull { it.enabled } ?: uiState.alarms.firstOrNull()
    val hydrationGoal = 2.5
    val hydrationProgress by animateFloatAsState(
        targetValue = (uiState.hydrationLiters / hydrationGoal).toFloat().coerceIn(0f, 1f),
        animationSpec = tween(420, easing = SanctuaryEase),
        label = "home_hydration"
    )
    val bedtimeHours = uiState.bedtimeMinutes / 60
    val bedtimeMins = uiState.bedtimeMinutes % 60
    val bedtimeFormatted = String.format("%02d:%02d", bedtimeHours, bedtimeMins)

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
    val dayHeaders = remember {
        listOf(
            "MONDAY • MAY 13",
            "TUESDAY • MAY 14",
            "WEDNESDAY • MAY 15",
            "THURSDAY • MAY 16",
            "FRIDAY • MAY 17",
            "SATURDAY • MAY 18",
            "SUNDAY • MAY 19"
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 164.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Editorial Greeting & Bedtime Horizon Pill
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    AnimatedContent(
                        targetState = uiState.selectedDayIndex,
                        transitionSpec = {
                            val dir = if (targetState >= initialState) 1 else -1
                            (fadeIn(tween(340, easing = SanctuaryEase)) +
                                slideInHorizontally(tween(360, easing = SanctuaryEase)) { dir * it / 4 })
                                .togetherWith(
                                    fadeOut(tween(220)) +
                                        slideOutHorizontally(tween(260, easing = SanctuaryEase)) { -dir * it / 4 }
                                )
                        },
                        label = "home_day_eyebrow"
                    ) { dayIdx ->
                        Text(
                            text = dayHeaders.getOrElse(dayIdx) { "THURSDAY • MAY 16" },
                            fontFamily = MonoFontFamily,
                            fontSize = 10.5.sp,
                            letterSpacing = 1.2.sp,
                            color = palette.primary
                        )
                    }
                    Text(
                        text = "Good morning, Elena.",
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 31.sp,
                        lineHeight = 35.sp,
                        letterSpacing = (-0.6).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(Modifier.width(10.dp))

                Surface(
                    modifier = Modifier
                        .testTag("home_bedtime_chip")
                        .clip(CircleShape)
                        .clickable { onNavigate(ScreenId.QUIET_BREAKTHROUGH) },
                    shape = CircleShape,
                    color = palette.heroStart,
                    border = BorderStroke(1.dp, palette.primaryBright.copy(alpha = 0.55f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bedtime,
                            contentDescription = "Quiet Hours",
                            tint = palette.primaryBright,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = bedtimeFormatted,
                            fontFamily = MonoFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 2. Interactive 7-Day Elemental Rhythm Strip (Tap any day -> smoothly transforms entire page!)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    days.forEachIndexed { idx, (dayLabel, num) ->
                        val dayTheme = uiState.dayThemes.getOrElse(idx) { ElementalTheme.BLOOM }
                        ElementalDayPill(
                            dayLabel = dayLabel,
                            dayNumber = num,
                            theme = dayTheme,
                            selected = idx == uiState.selectedDayIndex,
                            oledMode = oledMode,
                            testTag = "home_day_$idx",
                            onClick = { onSelectDay(idx) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Active Elemental Theme Strip + Customize Trigger with smooth crossfade + slide
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_customize_day_theme")
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onOpenThemeSettings() },
                    shape = RoundedCornerShape(18.dp),
                    color = palette.surfaceCard.copy(alpha = 0.92f - 0.07f * palette.darkProgress),
                    border = BorderStroke(1.dp, palette.border)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedContent(
                            targetState = activeElement,
                            transitionSpec = {
                                (fadeIn(tween(360, easing = SanctuaryEase)) +
                                    scaleIn(tween(380, easing = SanctuaryEase), initialScale = 0.92f))
                                    .togetherWith(
                                        fadeOut(tween(220)) +
                                            scaleOut(tween(220), targetScale = 0.94f)
                                    )
                            },
                            label = "home_active_element_badge"
                        ) { element ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(9.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(palette.surfaceTint),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = elementalIcon(element),
                                        contentDescription = element.displayName,
                                        tint = palette.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "${element.code} · ${element.displayName}",
                                        fontFamily = DisplayFontFamily,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = element.tagline,
                                        fontFamily = MonoFontFamily,
                                        fontSize = 9.sp,
                                        letterSpacing = 0.8.sp,
                                        color = palette.primary
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = palette.surfaceTint,
                            border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Palette,
                                    contentDescription = "Customize daily theme",
                                    tint = palette.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Customize",
                                    fontFamily = BodyFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = palette.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Elemental Hero Voice Alarm Card (smoothly breathes & morphs with Ember / Solar / Breeze / Bloom!)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = dayPulseScale.value
                        scaleY = dayPulseScale.value
                    }
                    .testTag("home_hero_alarm_card"),
                shape = RoundedCornerShape(30.dp),
                color = palette.heroEnd,
                border = BorderStroke(1.2.dp, palette.primaryBright.copy(alpha = 0.42f)),
                shadowElevation = (10f - 6f * palette.darkProgress).dp
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_sanctuary_hero_1795264045436),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(0.20f)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        palette.heroStart.copy(alpha = 0.90f),
                                        palette.heroEnd.copy(alpha = 0.96f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AvatarImage(
                                    url = nextAlarm?.creatorAvatar ?: AvatarUrls.MAYA,
                                    fallbackInitials = "MR",
                                    contentDescription = "Maya R.",
                                    size = 44.dp,
                                    ringColor = palette.primaryBright,
                                    ringWidth = 1.6.dp
                                )
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OneReflectionLogoMark(size = 16.dp, cornerRadius = 5.dp, showGoldRing = false)
                                        Text(
                                            text = "TRUSTED VOICE ALARM",
                                            fontFamily = MonoFontFamily,
                                            fontSize = 10.sp,
                                            letterSpacing = 1.sp,
                                            color = palette.primaryBright
                                        )
                                    }
                                    Text(
                                        text = "From ${nextAlarm?.creatorName ?: "Maya R."} • ${nextAlarm?.creatorRole ?: "Partner"}",
                                        fontFamily = BodyFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFEAE5E0)
                                    )
                                }
                            }

                            OneReflectionSwitch(
                                checked = heroAlarmActive,
                                onCheckedChange = {
                                    heroAlarmActive = !heroAlarmActive
                                    onPlayChime(ChimeType.CLICK)
                                },
                                activeTrackColor = palette.primary,
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f),
                                testTag = "home_hero_alarm_switch"
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = nextAlarm?.time ?: "07:30",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 46.sp,
                                    letterSpacing = (-1.5).sp,
                                    color = Color.White
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = nextAlarm?.period ?: "AM",
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = palette.primaryBright,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .testTag("home_open_alarms_pill")
                                    .clip(CircleShape)
                                    .clickable { onNavigate(ScreenId.ALARMS) },
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, palette.primaryBright.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = "${uiState.alarms.count { it.enabled }} active",
                                        fontFamily = BodyFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "View alarms",
                                        tint = palette.primaryBright,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.08f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .testTag("home_play_preview_button")
                                            .clip(CircleShape)
                                            .background(palette.primary)
                                            .clickable {
                                                if (isPlayingPreview) {
                                                    isPlayingPreview = false
                                                    onStopChime()
                                                } else {
                                                    isPlayingPreview = true
                                                    onPlayChime(ChimeType.TIBETAN)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isPlayingPreview) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                            contentDescription = if (isPlayingPreview) "Pause voice alarm" else "Play voice alarm",
                                            tint = palette.onPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    WaveformVisualizer(
                                        isPlaying = isPlayingPreview,
                                        barCount = 20,
                                        height = 26.dp,
                                        mixedColors = true,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Text(
                                        text = nextAlarm?.voiceSnippetDuration ?: "0:18",
                                        fontFamily = MonoFontFamily,
                                        fontSize = 11.sp,
                                        color = palette.primaryBright
                                    )
                                }

                                Text(
                                    text = "\"${nextAlarm?.voiceSnippetQuote ?: "Remember to drink your lemon water and stretch before coffee!"}\"",
                                    fontFamily = DisplayFontFamily,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 13.5.sp,
                                    lineHeight = 19.sp,
                                    color = Color(0xFFF3EDE6)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Asymmetric Editorial Bento Row: Shared Flows + Hydration Ritual
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag("home_tasks_bento")
                        .clip(RoundedCornerShape(26.dp))
                        .clickable { onNavigate(ScreenId.TASKS) },
                    shape = RoundedCornerShape(26.dp),
                    color = cardBg.copy(alpha = if (oledMode) 0.92f else 0.96f),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(palette.surfaceTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Shared Flows",
                                    tint = palette.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "${uiState.tasks.size} OPEN",
                                fontFamily = MonoFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Shared Flows",
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = uiState.tasks.firstOrNull()?.title ?: "All caught up for today",
                                fontFamily = BodyFontFamily,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        val totalTasks = (uiState.tasks.size + uiState.completedTasks.size).coerceAtLeast(1)
                        val doneFraction = uiState.completedTasks.size.toFloat() / totalTasks.toFloat()
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(palette.surfaceElevated)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(doneFraction.coerceIn(0.15f, 1f))
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(palette.primary)
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .testTag("home_hydration_bento")
                        .clip(RoundedCornerShape(26.dp))
                        .clickable { onNavigate(ScreenId.HABITS) },
                    shape = RoundedCornerShape(26.dp),
                    color = cardBg.copy(alpha = if (oledMode) 0.92f else 0.96f),
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(palette.surfaceTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.WaterDrop,
                                    contentDescription = "Hydration",
                                    tint = palette.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .testTag("home_add_hydration_button")
                                    .clip(CircleShape)
                                    .clickable { onAddHydration(0.25) },
                                shape = CircleShape,
                                color = palette.primary
                            ) {
                                Text(
                                    text = "+250ml",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = palette.onPrimary,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${formatLiters(uiState.hydrationLiters)}L",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = " / 2.5L",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 12.sp,
                                    color = palette.primary,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                            Text(
                                text = "Daily Rituals • ${uiState.dailyStreak.currentStreakDays}d streak",
                                fontFamily = BodyFontFamily,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(palette.surfaceElevated)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(hydrationProgress)
                                    .fillMaxHeight()
                                    .clip(CircleShape)
                                    .background(palette.secondary)
                            )
                        }
                    }
                }
            }
        }

        // 4b. Mindful Habit Progress Ring Spotlight (live ring + 7-day consistency sparkline + one-tap logging)
        item {
            HomeHabitRingSpotlightCard(
                habits = uiState.habits,
                oledMode = oledMode,
                onIncrementHabit = onIncrementHabit,
                onOpenHabits = { onNavigate(ScreenId.HABITS) },
                hydrationLiters = uiState.hydrationLiters
            )
        }

        // 5. Sanctuary Quick-Jump Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SanctuaryQuickChip(
                    title = "Great Tree",
                    subtitle = "Weekly & Monthly",
                    icon = Icons.Filled.Park,
                    tag = "home_great_tree_shortcut",
                    onClick = { onNavigate(ScreenId.SANCTUARY_TREE) }
                )
                SanctuaryQuickChip(
                    title = "Moments",
                    subtitle = "11:30 AM Next",
                    icon = Icons.Filled.CalendarMonth,
                    tag = "home_reminders_shortcut",
                    onClick = { onNavigate(ScreenId.REMINDERS) }
                )
                SanctuaryQuickChip(
                    title = "Voice Note",
                    subtitle = "Send to Circle",
                    icon = Icons.Filled.Mic,
                    tag = "home_voice_shortcut",
                    onClick = onOpenVoiceStudio
                )
                SanctuaryQuickChip(
                    title = "Rituals",
                    subtitle = "${uiState.habits.size} Habits Active",
                    icon = Icons.Filled.Eco,
                    tag = "home_habits_shortcut",
                    onClick = { onNavigate(ScreenId.HABITS) }
                )
                SanctuaryQuickChip(
                    title = "Quiet Hours",
                    subtitle = "Night Shield",
                    icon = Icons.Filled.Bedtime,
                    tag = "home_quiet_shortcut",
                    onClick = { onNavigate(ScreenId.QUIET_BREAKTHROUGH) }
                )
            }
        }

        // 6. Inner Circle Pulse Editorial Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = cardBg.copy(alpha = if (oledMode) 0.92f else 0.96f),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CIRCLE ACTIVITY",
                                fontFamily = MonoFontFamily,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Inner Circle Pulse",
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .testTag("home_open_chats_link")
                                .clip(CircleShape)
                                .clickable { onNavigate(ScreenId.CHATS) },
                            shape = CircleShape,
                            color = palette.surfaceTint
                        ) {
                            Text(
                                text = "Open Chats",
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = palette.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    CirclePulseRow(
                        name = "Sofia Miller",
                        detail = "7-day Hydration ritual completed",
                        avatarUrl = AvatarUrls.SOFIA,
                        initials = "SM",
                        streak = "7d"
                    )
                    CirclePulseRow(
                        name = "Leo Chen",
                        detail = "Synced Q3 studio sprint checklist",
                        avatarUrl = AvatarUrls.LEO,
                        initials = "LC",
                        streak = "11d"
                    )
                }
            }
        }
    }
}

@Composable
private fun SanctuaryQuickChip(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tag: String,
    onClick: () -> Unit
) {
    val palette = LocalElementalPalette.current
    Surface(
        modifier = Modifier
            .testTag(tag)
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        color = palette.surfaceCard,
        border = BorderStroke(1.dp, palette.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(palette.surfaceTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = palette.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontFamily = BodyFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CirclePulseRow(
    name: String,
    detail: String,
    avatarUrl: String,
    initials: String,
    streak: String
) {
    val palette = LocalElementalPalette.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(palette.surfaceElevated.copy(alpha = 0.65f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AvatarImage(
            url = avatarUrl,
            fallbackInitials = initials,
            contentDescription = name,
            size = 40.dp,
            ringColor = palette.border,
            ringWidth = 1.dp
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontFamily = BodyFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = detail,
                fontFamily = BodyFontFamily,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Surface(
            shape = CircleShape,
            color = palette.surfaceTint
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = "Streak",
                    tint = palette.primary,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = streak,
                    fontFamily = MonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = palette.primary
                )
            }
        }
    }
}
