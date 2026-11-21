package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.model.AlarmItem
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.OneReflectionLogoMark
import com.onereflection.app.ui.components.OneReflectionSwitch
import com.onereflection.app.ui.components.WaveformVisualizer
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors
import kotlinx.coroutines.delay

@Composable
fun AlarmsScreen(
    uiState: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onOpenBedtimeSync: () -> Unit,
    onToggleAlarm: (String) -> Unit,
    onPlayChime: (ChimeType) -> Unit,
    onStopChime: () -> Unit
) {
    BackHandler { onNavigate(ScreenId.HOME) }

    var playingAlarmId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(playingAlarmId) {
        if (playingAlarmId != null) {
            delay(5000L)
            playingAlarmId = null
            onStopChime()
        }
    }

    val oledMode = uiState.oledMode
    val bedStr = String.format("%02d:%02d", uiState.bedtimeMinutes / 60, uiState.bedtimeMinutes % 60)
    val wakeStr = String.format("%02d:%02d", uiState.wakeMinutes / 60, uiState.wakeMinutes % 60)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 148.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Editorial Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "VOICE WAKE-UP & HARMONY",
                        fontFamily = MonoFontFamily,
                        fontSize = 10.5.sp,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Alarms.",
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 32.sp,
                        letterSpacing = (-0.6).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Surface(
                    modifier = Modifier
                        .testTag("alarms_bedtime_sync_button")
                        .clip(CircleShape)
                        .clickable { onOpenBedtimeSync() },
                    shape = CircleShape,
                    color = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.SurfaceCard,
                    border = BorderStroke(1.dp, if (oledMode) OneReflectionColors.OledBorder else OneReflectionColors.HairlineLight)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Bedtime Sync",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Bedtime Sync",
                            fontFamily = BodyFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        // Signature Obsidian Bedtime Harmony Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("alarms_bedtime_banner")
                    .clip(RoundedCornerShape(26.dp))
                    .clickable { onOpenBedtimeSync() },
                shape = RoundedCornerShape(26.dp),
                color = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.InkPrimary,
                border = BorderStroke(1.dp, OneReflectionColors.GoldLuminous.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OneReflectionLogoMark(size = 44.dp, cornerRadius = 14.dp)
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "BEDTIME HARMONY",
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                letterSpacing = 1.sp,
                                color = OneReflectionColors.GoldLuminous
                            )
                            Text(
                                text = "$bedStr → $wakeStr",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = Color.White
                            )
                            Text(
                                text = if (uiState.syncSleepWithPartner) "Synced with Maya R. • Breakthrough on" else "Personal quiet horizon",
                                fontFamily = BodyFontFamily,
                                fontSize = 11.5.sp,
                                color = Color(0xFFA79F97)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bedtime,
                            contentDescription = "Edit bedtime",
                            tint = OneReflectionColors.GoldLuminous,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Alarm Cards
        items(uiState.alarms, key = { it.id }) { alarm ->
            AlarmEditorialCard(
                alarm = alarm,
                isPlaying = playingAlarmId == alarm.id,
                oledMode = oledMode,
                onToggle = { onToggleAlarm(alarm.id) },
                onTogglePlay = {
                    if (playingAlarmId == alarm.id) {
                        playingAlarmId = null
                        onStopChime()
                    } else {
                        playingAlarmId = alarm.id
                        onPlayChime(alarm.chimeSound)
                    }
                }
            )
        }

        // Editorial CTA to Record a New Voice Alarm
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("alarms_record_voice_button")
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onOpenVoiceStudio() },
                shape = RoundedCornerShape(24.dp),
                color = if (oledMode) OneReflectionColors.TerracottaLight else OneReflectionColors.Terracotta
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Record voice wake-up",
                        tint = if (oledMode) OneReflectionColors.InkPrimary else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Record Voice Wake-Up Alarm",
                        fontFamily = BodyFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (oledMode) OneReflectionColors.InkPrimary else Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun AlarmEditorialCard(
    alarm: AlarmItem,
    isPlaying: Boolean,
    oledMode: Boolean,
    onToggle: () -> Unit,
    onTogglePlay: () -> Unit
) {
    val isShared = alarm.category == "shared"
    val cardBg = when {
        oledMode -> OneReflectionColors.OledSurface
        isShared -> OneReflectionColors.SurfaceCard
        else -> OneReflectionColors.SurfaceCard
    }
    val accentColor = if (isShared) OneReflectionColors.Terracotta else OneReflectionColors.IndigoDeep
    val tintBg = when {
        oledMode -> OneReflectionColors.OledSurfaceElevated
        isShared -> OneReflectionColors.PeachMist
        else -> OneReflectionColors.IndigoMist
    }
    val borderColor = when {
        oledMode && alarm.enabled -> OneReflectionColors.GoldLuminous.copy(alpha = 0.25f)
        oledMode -> OneReflectionColors.OledBorder
        alarm.enabled && isShared -> OneReflectionColors.Terracotta.copy(alpha = 0.28f)
        else -> OneReflectionColors.HairlineLight
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (alarm.enabled) 1f else 0.58f)
            .testTag("alarm_card_${alarm.id}"),
        shape = RoundedCornerShape(26.dp),
        color = cardBg,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (!oledMode && alarm.enabled) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (oledMode) OneReflectionColors.GoldLuminous else accentColor)
                        )
                        Text(
                            text = alarm.title.uppercase(),
                            fontFamily = MonoFontFamily,
                            fontSize = 10.sp,
                            letterSpacing = 0.9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = alarm.time,
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                            letterSpacing = (-1.2).sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = alarm.period,
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = if (oledMode) OneReflectionColors.GoldLuminous else accentColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                OneReflectionSwitch(
                    checked = alarm.enabled,
                    onCheckedChange = onToggle,
                    activeTrackColor = if (isShared) OneReflectionColors.Terracotta else OneReflectionColors.SageDeep,
                    inactiveTrackColor = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.OutlineSoft,
                    testTag = "alarm_switch_${alarm.id}"
                )
            }

            // Day pills + chime badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    alarm.days.forEach { d ->
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(tintBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = d,
                                fontFamily = MonoFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (oledMode) OneReflectionColors.GoldLuminous else accentColor
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = "Chime sound",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = alarm.chimeSound.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontFamily = MonoFontFamily,
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Embedded voice snippet strip when present
            if (!alarm.voiceSnippetQuote.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = tintBg
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("alarm_play_${alarm.id}")
                                    .clip(CircleShape)
                                    .background(if (oledMode) OneReflectionColors.TerracottaLight else OneReflectionColors.InkPrimary)
                                    .clickable { onTogglePlay() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause snippet" else "Play snippet",
                                    tint = if (oledMode) OneReflectionColors.InkPrimary else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            WaveformVisualizer(
                                isPlaying = isPlaying,
                                barCount = 18,
                                height = 22.dp,
                                mixedColors = true,
                                modifier = Modifier.weight(1f)
                            )

                            if (!alarm.creatorName.isNullOrBlank()) {
                                AvatarImage(
                                    url = alarm.creatorAvatar,
                                    fallbackInitials = alarm.creatorName.take(2).uppercase(),
                                    contentDescription = alarm.creatorName,
                                    size = 28.dp
                                )
                            }
                        }
                        Text(
                            text = "\"${alarm.voiceSnippetQuote}\"",
                            fontFamily = DisplayFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
