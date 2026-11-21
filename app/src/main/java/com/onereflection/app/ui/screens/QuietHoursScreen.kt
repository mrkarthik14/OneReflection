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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.model.AvatarUrls
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.EnchantedDayNightToggle
import com.onereflection.app.ui.components.OneReflectionLogoMark
import com.onereflection.app.ui.components.WaveformVisualizer
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors
import kotlinx.coroutines.delay

@Composable
fun QuietHoursScreen(
    uiState: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onOpenBedtimeSync: () -> Unit,
    onToggleOled: () -> Unit,
    onPlayChime: (ChimeType) -> Unit,
    onStopChime: () -> Unit
) {
    BackHandler { onNavigate(ScreenId.HOME) }

    var isPlayingWhisper by remember { mutableStateOf(false) }
    var earphoneMode by remember { mutableStateOf(true) }
    var sentQuickReply by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isPlayingWhisper) {
        if (isPlayingWhisper) {
            delay(5000L)
            isPlayingWhisper = false
            onStopChime()
        }
    }

    val wakeFormatted = String.format("%02d:%02d", uiState.wakeMinutes / 60, uiState.wakeMinutes % 60)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060505)),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 148.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Deep Obsidian Header & Status Pill
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OneReflectionLogoMark(size = 36.dp, cornerRadius = 11.dp, showGoldRing = true)
                    Column {
                        Text(
                            text = "NIGHT SANCTUARY ACTIVE",
                            fontFamily = MonoFontFamily,
                            fontSize = 9.5.sp,
                            letterSpacing = 1.sp,
                            color = OneReflectionColors.GoldLuminous
                        )
                        Text(
                            text = "Quiet Hours.",
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 27.sp,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = OneReflectionColors.OledSurfaceElevated,
                    border = BorderStroke(1.dp, OneReflectionColors.GoldLuminous.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "01:24 AM",
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = OneReflectionColors.GoldLuminous,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // OLED Pure Black Mode Toggle Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = OneReflectionColors.OledSurface,
                border = BorderStroke(1.dp, OneReflectionColors.OledBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Bedtime,
                            contentDescription = "OLED Pure Black",
                            tint = OneReflectionColors.GoldLuminous,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "OLED Pure Black Shield",
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Zero-glare obsidian canvas across app",
                                fontFamily = BodyFontFamily,
                                fontSize = 11.5.sp,
                                color = Color(0xFF9C938B)
                            )
                        }
                    }

                    EnchantedDayNightToggle(
                        isDark = uiState.oledMode,
                        onToggle = onToggleOled,
                        width = 100.dp,
                        height = 48.dp,
                        testTag = "quiet_oled_switch"
                    )
                }
            }
        }

        // Trusted Whisper Breakthrough Card from Maya
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiet_maya_whisper_card"),
                shape = RoundedCornerShape(28.dp),
                color = OneReflectionColors.OledSurface,
                border = BorderStroke(1.dp, OneReflectionColors.TerracottaLight.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AvatarImage(
                            url = AvatarUrls.MAYA,
                            fallbackInitials = "MR",
                            contentDescription = "Maya R.",
                            size = 46.dp,
                            ringColor = OneReflectionColors.TerracottaLight,
                            ringWidth = 1.6.dp
                        )
                        Column {
                            Text(
                                text = "TRUSTED WHISPER BREAKTHROUGH",
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                letterSpacing = 0.9.sp,
                                color = OneReflectionColors.TerracottaLight
                            )
                            Text(
                                text = "Maya R. • Partner",
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = OneReflectionColors.OledSurfaceElevated
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
                                    .testTag("quiet_play_whisper_button")
                                    .clip(CircleShape)
                                    .background(OneReflectionColors.TerracottaLight)
                                    .clickable {
                                        if (isPlayingWhisper) {
                                            isPlayingWhisper = false
                                            onStopChime()
                                        } else {
                                            isPlayingWhisper = true
                                            onPlayChime(ChimeType.PIANO)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlayingWhisper) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = "Play quiet whisper",
                                    tint = OneReflectionColors.InkPrimary,
                                    modifier = Modifier.size(21.dp)
                                )
                            }

                            WaveformVisualizer(
                                isPlaying = isPlayingWhisper,
                                barCount = 18,
                                height = 26.dp,
                                mixedColors = true,
                                modifier = Modifier.weight(1f)
                            )

                            Text("0:11", fontFamily = MonoFontFamily, fontSize = 11.sp, color = OneReflectionColors.GoldLuminous)
                        }
                    }

                    Text(
                        text = "\"Landed safely at the airport — don't wake up, just letting you know I'm heading straight home.\"",
                        fontFamily = DisplayFontFamily,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFFE7DFD7)
                    )

                    // Audio Route Toggle (Earphone vs Soft Speaker)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quiet_route_earphone")
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    earphoneMode = true
                                    onPlayChime(ChimeType.CLICK)
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = if (earphoneMode) OneReflectionColors.GoldLuminous else OneReflectionColors.OledSurfaceElevated
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Headphones, contentDescription = "Earphone mode", tint = if (earphoneMode) OneReflectionColors.InkPrimary else Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Whisper Ear", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (earphoneMode) OneReflectionColors.InkPrimary else Color.White)
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quiet_route_speaker")
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    earphoneMode = false
                                    onPlayChime(ChimeType.CLICK)
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = if (!earphoneMode) OneReflectionColors.GoldLuminous else OneReflectionColors.OledSurfaceElevated
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.VolumeUp, contentDescription = "Soft speaker", tint = if (!earphoneMode) OneReflectionColors.InkPrimary else Color.White, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Soft Speaker", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (!earphoneMode) OneReflectionColors.InkPrimary else Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Gentle One-Tap Responses
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "GENTLE ONE-TAP REPLY",
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = Color(0xFF9C938B)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val replies = listOf("Heard & safe", "Back to sleep", "Whisper reply")
                    replies.forEach { reply ->
                        val active = sentQuickReply == reply
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    if (reply == "Whisper reply") {
                                        onOpenVoiceStudio()
                                    } else {
                                        sentQuickReply = reply
                                        onPlayChime(ChimeType.SOLFEGGIO)
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = if (active) OneReflectionColors.SageDeep else OneReflectionColors.OledSurface,
                            border = BorderStroke(1.dp, if (active) OneReflectionColors.SageLight else OneReflectionColors.OledBorder)
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (active) "Sent ✓" else reply,
                                    fontFamily = BodyFontFamily,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Wake Horizon Footer
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quiet_wake_horizon_button")
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onOpenBedtimeSync() },
                shape = RoundedCornerShape(22.dp),
                color = OneReflectionColors.OledSurface,
                border = BorderStroke(1.dp, OneReflectionColors.OledBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "WAKE HORIZON",
                            fontFamily = MonoFontFamily,
                            fontSize = 9.5.sp,
                            color = Color(0xFF9C938B)
                        )
                        Text(
                            text = "$wakeFormatted AM • Tibetan Dawn",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = "Voice Studio", tint = OneReflectionColors.TerracottaLight, modifier = Modifier.size(18.dp))
                        Icon(Icons.Filled.Tune, contentDescription = "Adjust wake horizon", tint = OneReflectionColors.GoldLuminous, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
