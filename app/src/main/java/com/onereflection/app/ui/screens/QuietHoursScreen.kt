package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.model.AVATAR_MAYA
import com.onereflection.app.model.ChimeType
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.OneReflectionSwitch
import com.onereflection.app.ui.components.WaveformVisualizer
import com.onereflection.app.ui.theme.BrandColors
import com.onereflection.app.viewmodel.OneReflectionUiState

@Composable
fun QuietHoursScreen(
    state: OneReflectionUiState,
    onBack: () -> Unit,
    onToggleOled: () -> Unit,
    onOpenBedtimeSync: () -> Unit
) {
    BackHandler(onBack = onBack)

    val oledMode = state.oledMode
    var isPlayingWhisper by remember { mutableStateOf(true) }
    var bedsideSpeaker by remember { mutableStateOf(true) }
    var radiatorOff by remember { mutableStateOf(false) }
    var silenceRestOfNight by remember { mutableStateOf(false) }
    var whisperingBack by remember { mutableStateOf(false) }
    var sleepingAckSent by remember { mutableStateOf(false) }

    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark
    val textSecondary = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
    val cardBorder = if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.06f)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Ambient Night Status Pill Bar
        item {
            Surface(
                shape = CircleShape,
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Bedtime, contentDescription = null, tint = BrandColors.ButterYellow, modifier = Modifier.size(18.dp))
                        Text("01:24 AM", style = MaterialTheme.typography.titleSmall, color = textPrimary)
                        Text("Quiet Hours Active", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                    }
                    Box(modifier = Modifier.clip(CircleShape).background(BrandColors.ButterYellow).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text("AUDIO PASS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.ButterYellowText)
                    }
                }
            }
        }

        // 2. OLED 0-Nit Night Mode Banner
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier.size(38.dp).clip(CircleShape).background(BrandColors.OnSurfaceDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.DarkMode, contentDescription = null, tint = BrandColors.ButterYellow, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("OLED Pure Black (#000000)", style = MaterialTheme.typography.labelLarge, color = textPrimary)
                            Text("Zero-nit panel sleep • Melatonin & battery safe", fontSize = 10.sp, color = textSecondary)
                        }
                    }

                    OneReflectionSwitch(
                        checked = oledMode,
                        onCheckedChange = { onToggleOled() },
                        checkedTrackColor = BrandColors.ButterYellow,
                        thumbColor = if (oledMode) BrandColors.OnSurfaceDark else Color.White,
                        modifier = Modifier.testTag("quiet_oled_switch")
                    )
                }
            }
        }

        // 3. Breakthrough Rule Banner
        item {
            Surface(
                onClick = onOpenBedtimeSync,
                shape = RoundedCornerShape(24.dp),
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.clip(CircleShape).background(BrandColors.PinkFixed).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text("VERIFIED PARTNER PERMISSION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.DeepPlumText)
                        }
                        Text("Max 20%", style = MaterialTheme.typography.labelSmall, color = textSecondary)
                    }
                    Text(
                        text = "Maya R. bypassed quiet mode via your granular Gentle Voice Whispers bedtime rule.",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondary
                    )
                }
            }
        }

        // 4. Partner Presence Card
        item {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = if (oledMode) BrandColors.OledCard else Color.White,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                        AvatarImage(AVATAR_MAYA, "MR", "Maya R.", 50.dp, ringColor = BrandColors.ButterYellow, ringWidth = 2.5.dp)
                        Column {
                            Text("Maya R. • Primary", style = MaterialTheme.typography.titleMedium, color = textPrimary)
                            Text("Awake in Brooklyn • Oura Synced", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                            Text("Mutual Rest Exception Granted", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (oledMode) BrandColors.LavenderSoft else BrandColors.TertiarySlateBlue)
                        }
                    }

                    Surface(
                        onClick = {
                            if (!isPlayingWhisper) {
                                ChimePlayer.play(ChimeType.TIBETAN)
                                isPlayingWhisper = true
                            } else {
                                ChimePlayer.stop()
                                isPlayingWhisper = false
                            }
                        },
                        shape = CircleShape,
                        color = if (isPlayingWhisper) BrandColors.PrimaryPlum else BrandColors.SurfaceContainerHigh,
                        modifier = Modifier.size(42.dp).testTag("quiet_whisper_play")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isPlayingWhisper) Icons.Default.Pause else Icons.Default.GraphicEq,
                                contentDescription = "Play or pause whisper",
                                tint = if (isPlayingWhisper) Color.White else BrandColors.OnSurfaceDark
                            )
                        }
                    }
                }
            }
        }

        // 5. Live Incoming Voice Breakthrough Player
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BrandColors.LavenderSoft.copy(alpha = if (oledMode) 0.16f else 0.45f),
                border = BorderStroke(1.dp, BrandColors.TertiarySlateBlue.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Maya is whispering...", style = MaterialTheme.typography.titleMedium, color = textPrimary)
                        Box(modifier = Modifier.clip(CircleShape).background(if (oledMode) BrandColors.OledCard else Color.White).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text("00:08 / 00:27", style = MaterialTheme.typography.labelSmall, color = textPrimary)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (oledMode) BrandColors.OledBlack else Color.White.copy(alpha = 0.9f)
                    ) {
                        WaveformVisualizer(
                            isPlaying = isPlayingWhisper,
                            barCount = 22,
                            height = 38.dp,
                            modifier = Modifier.fillMaxWidth().padding(12.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (oledMode) BrandColors.OledCard else Color.White
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Instant Bedside Transcription", style = MaterialTheme.typography.labelSmall, color = if (oledMode) BrandColors.LavenderSoft else BrandColors.TertiarySlateBlue)
                                Text("Soft Whisper EQ", fontSize = 10.sp, color = textSecondary)
                            }
                            Text(
                                text = "“Hey love, sorry to wake you gently — just wanted to make sure you turned the radiator off before deep sleep, it was rattling earlier...”",
                                style = MaterialTheme.typography.bodySmall,
                                fontStyle = FontStyle.Italic,
                                color = textPrimary
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Audio Profile: Calm & Low Gain", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                        Surface(
                            onClick = {
                                ChimePlayer.play(ChimeType.CLICK)
                                bedsideSpeaker = !bedsideSpeaker
                            },
                            shape = CircleShape,
                            color = if (bedsideSpeaker) (if (oledMode) BrandColors.OledCard else Color.White) else BrandColors.ButterYellow
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (bedsideSpeaker) Icons.Default.Speaker else Icons.Default.Headphones,
                                    contentDescription = null,
                                    tint = if (bedsideSpeaker) textPrimary else BrandColors.ButterYellowText,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = if (bedsideSpeaker) "Bedside Speaker" else "Earphone Mode",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (bedsideSpeaker) textPrimary else BrandColors.ButterYellowText
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Tactile Gentle Response Controls
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("GENTLE RESPONSE ACTIONS", style = MaterialTheme.typography.labelSmall, color = textSecondary)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (whisperingBack) BrandColors.PrimaryPlum else BrandColors.CandyPink)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        ChimePlayer.play(ChimeType.CLICK)
                                        whisperingBack = true
                                        tryAwaitRelease()
                                        ChimePlayer.play(ChimeType.TIBETAN)
                                        whisperingBack = false
                                    }
                                )
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = BrandColors.PrimaryPlum)
                            }
                            Text(if (whisperingBack) "Recording..." else "Whisper Back", style = MaterialTheme.typography.titleSmall, color = if (whisperingBack) Color.White else BrandColors.OnSurfaceDark)
                            Text("Hold for 15s note", fontSize = 10.sp, color = if (whisperingBack) Color.White.copy(alpha = 0.85f) else BrandColors.CandyPinkDeep)
                        }
                    }

                    Surface(
                        onClick = {
                            ChimePlayer.play(ChimeType.SOLFEGGIO)
                            sleepingAckSent = !sleepingAckSent
                        },
                        shape = RoundedCornerShape(24.dp),
                        color = BrandColors.ButterYellow,
                        modifier = Modifier.weight(1f).testTag("quiet_ok_sleeping")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Bedtime, contentDescription = null, tint = BrandColors.ButterYellowText)
                            }
                            Text(if (sleepingAckSent) "Sent & Muted ✓" else "I'm OK • Sleeping", style = MaterialTheme.typography.titleSmall, color = BrandColors.ButterYellowText)
                            Text("Auto-replies & mutes", fontSize = 10.sp, color = BrandColors.ButterYellowText.copy(alpha = 0.8f))
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            ChimePlayer.play(ChimeType.CLICK)
                            radiatorOff = !radiatorOff
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (radiatorOff) BrandColors.PrimaryPlum else if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh,
                            contentColor = if (radiatorOff) Color.White else textPrimary
                        ),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text(if (radiatorOff) "Radiator Off ✓" else "Turn Radiator Off", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            ChimePlayer.play(ChimeType.CLICK)
                            silenceRestOfNight = !silenceRestOfNight
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (silenceRestOfNight) BrandColors.OnSurfaceDark else if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                            contentColor = if (silenceRestOfNight) Color.White else textSecondary
                        ),
                        modifier = Modifier.weight(1f).height(44.dp)
                    ) {
                        Text(if (silenceRestOfNight) "Audio Silenced" else "Silence Night", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 7. Sleep Horizon Footer
        item {
            Surface(
                shape = CircleShape,
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Wake Horizon in 5h 36m (${state.quietHours.formattedWake()})", style = MaterialTheme.typography.bodySmall, color = textPrimary)
                    Box(modifier = Modifier.clip(CircleShape).background(BrandColors.LavenderSoft).padding(horizontal = 10.dp, vertical = 4.dp)) {
                        Text("Deep Rest", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.LavenderText)
                    }
                }
            }
        }
    }
}
