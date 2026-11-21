package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.model.AvatarUrls
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.WaveformVisualizer
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors
import kotlinx.coroutines.delay

@Composable
fun ChatsScreen(
    uiState: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onPlayChime: (ChimeType) -> Unit,
    onStopChime: () -> Unit
) {
    BackHandler { onNavigate(ScreenId.HOME) }

    var activeFilter by remember { mutableStateOf("All Circles") }
    var isPlayingMaya by remember { mutableStateOf(false) }
    var playbackSpeedIndex by remember { mutableIntStateOf(0) }
    var sourdoughChecked by remember { mutableStateOf(false) }
    var walkieLive by remember { mutableStateOf(false) }

    val speeds = listOf("1.0x", "1.5x", "2.0x")

    LaunchedEffect(isPlayingMaya) {
        if (isPlayingMaya) {
            delay(5000L)
            isPlayingMaya = false
            onStopChime()
        }
    }

    val oledMode = uiState.oledMode
    val cardBg = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.SurfaceCard
    val borderColor = if (oledMode) OneReflectionColors.OledBorder else OneReflectionColors.HairlineLight
    val filters = listOf("All Circles", "Maya R.", "Studio Circle", "Family")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 148.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Editorial Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "VOICE-FIRST DISPATCHES",
                    fontFamily = MonoFontFamily,
                    fontSize = 10.5.sp,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Chats & Circles.",
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 31.sp,
                    letterSpacing = (-0.6).sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Circle Filter Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { label ->
                    val selected = activeFilter == label
                    Surface(
                        modifier = Modifier
                            .testTag("chats_filter_${label.lowercase().replace(" ", "_")}")
                            .clip(CircleShape)
                            .clickable { activeFilter = label },
                        shape = CircleShape,
                        color = when {
                            selected && oledMode -> OneReflectionColors.TerracottaLight
                            selected -> OneReflectionColors.InkPrimary
                            oledMode -> OneReflectionColors.OledSurface
                            else -> OneReflectionColors.SurfaceCard
                        },
                        border = BorderStroke(1.dp, if (selected) Color.Transparent else borderColor)
                    ) {
                        Text(
                            text = label,
                            fontFamily = BodyFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            color = when {
                                selected && oledMode -> OneReflectionColors.InkPrimary
                                selected -> Color.White
                                else -> MaterialTheme.colorScheme.onBackground
                            },
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp)
                        )
                    }
                }
            }
        }

        // Featured Maya Voice Dispatch Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chats_maya_dispatch_card"),
                shape = RoundedCornerShape(28.dp),
                color = cardBg,
                border = BorderStroke(1.dp, if (oledMode) OneReflectionColors.GoldLuminous.copy(alpha = 0.24f) else OneReflectionColors.Terracotta.copy(alpha = 0.25f)),
                shadowElevation = if (oledMode) 0.dp else 3.dp
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(11.dp)
                        ) {
                            AvatarImage(
                                url = AvatarUrls.MAYA,
                                fallbackInitials = "MR",
                                contentDescription = "Maya R.",
                                size = 44.dp,
                                ringColor = OneReflectionColors.Terracotta,
                                ringWidth = 1.6.dp
                            )
                            Column {
                                Text(
                                    text = "Maya R.",
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "VOICE DISPATCH • 2M AGO",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .testTag("chats_speed_toggle")
                                .clip(CircleShape)
                                .clickable {
                                    playbackSpeedIndex = (playbackSpeedIndex + 1) % speeds.size
                                    onPlayChime(ChimeType.CLICK)
                                },
                            shape = CircleShape,
                            color = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.PeachMist
                        ) {
                            Text(
                                text = speeds[playbackSpeedIndex],
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (oledMode) OneReflectionColors.TerracottaLight else OneReflectionColors.Terracotta,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Audio Player Strip
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.SurfaceWarm
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("chats_play_maya_button")
                                    .clip(CircleShape)
                                    .background(OneReflectionColors.Terracotta)
                                    .clickable {
                                        if (isPlayingMaya) {
                                            isPlayingMaya = false
                                            onStopChime()
                                        } else {
                                            isPlayingMaya = true
                                            onPlayChime(ChimeType.TIBETAN)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlayingMaya) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (isPlayingMaya) "Pause Maya voice note" else "Play Maya voice note",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            WaveformVisualizer(
                                isPlaying = isPlayingMaya,
                                barCount = 20,
                                height = 26.dp,
                                mixedColors = true,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "0:24",
                                fontFamily = MonoFontFamily,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "\"Hey! Picked up fresh rosemary sourdough on my way back. Should I set our bedtime horizon for 22:30 tonight?\"",
                        fontFamily = DisplayFontFamily,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Inline Actionable Task Chip
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("chats_inline_sourdough_check")
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                sourdoughChecked = !sourdoughChecked
                                onPlayChime(ChimeType.CLICK)
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = if (oledMode) Color(0xFF142722) else OneReflectionColors.SageMist
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (sourdoughChecked) OneReflectionColors.SageDeep else Color.Transparent)
                                    .border(1.8.dp, OneReflectionColors.SageDeep, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (sourdoughChecked) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                }
                            }
                            Text(
                                text = "Confirm organic sourdough & espresso pickup",
                                fontFamily = BodyFontFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = if (sourdoughChecked) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (oledMode) OneReflectionColors.SageMist else OneReflectionColors.SageDeep
                            )
                        }
                    }
                }
            }
        }

        // Signature Obsidian Live Walkie Channel Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chats_walkie_card"),
                shape = RoundedCornerShape(28.dp),
                color = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.InkPrimary,
                border = BorderStroke(1.dp, OneReflectionColors.GoldLuminous.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RadioButtonChecked,
                                contentDescription = "Live status",
                                tint = if (walkieLive) OneReflectionColors.TerracottaLight else OneReflectionColors.GoldLuminous,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (walkieLive) "TRANSMITTING LIVE" else "LIVE WALKIE CHANNEL",
                                fontFamily = MonoFontFamily,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = if (walkieLive) OneReflectionColors.TerracottaLight else OneReflectionColors.GoldLuminous
                            )
                        }
                        Text(
                            text = "3 IN ROOM",
                            fontFamily = MonoFontFamily,
                            fontSize = 10.sp,
                            color = Color(0xFFA79F97)
                        )
                    }

                    Text(
                        text = "Evening Sanctuary Lounge",
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 21.sp,
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                            AvatarImage(url = AvatarUrls.MAYA, fallbackInitials = "MR", contentDescription = "Maya", size = 36.dp, ringColor = OneReflectionColors.GoldLuminous, ringWidth = 1.5.dp)
                            AvatarImage(url = AvatarUrls.LEO, fallbackInitials = "LC", contentDescription = "Leo", size = 36.dp, ringColor = OneReflectionColors.InkPrimary, ringWidth = 1.5.dp)
                            AvatarImage(url = AvatarUrls.SOFIA, fallbackInitials = "SM", contentDescription = "Sofia", size = 36.dp, ringColor = OneReflectionColors.InkPrimary, ringWidth = 1.5.dp)
                        }

                        Surface(
                            modifier = Modifier
                                .testTag("chats_walkie_toggle_button")
                                .clip(CircleShape)
                                .clickable {
                                    walkieLive = !walkieLive
                                    if (walkieLive) onPlayChime(ChimeType.NORDIC) else onStopChime()
                                },
                            shape = CircleShape,
                            color = if (walkieLive) OneReflectionColors.Terracotta else OneReflectionColors.GoldLuminous
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (walkieLive) Icons.Filled.GraphicEq else Icons.Filled.Mic,
                                    contentDescription = "Toggle walkie",
                                    tint = if (walkieLive) Color.White else OneReflectionColors.InkPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (walkieLive) "Mute Mic" else "Tap to Talk",
                                    fontFamily = BodyFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = if (walkieLive) Color.White else OneReflectionColors.InkPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Studio Circle Update Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onOpenVoiceStudio() },
                shape = RoundedCornerShape(24.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AvatarImage(
                        url = AvatarUrls.LEO,
                        fallbackInitials = "LC",
                        contentDescription = "Leo Chen",
                        size = 44.dp
                    )
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Design Studio Circle",
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "14:20",
                                fontFamily = MonoFontFamily,
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Leo shared a 0:19 audio note on Q3 voice flows",
                            fontFamily = BodyFontFamily,
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
