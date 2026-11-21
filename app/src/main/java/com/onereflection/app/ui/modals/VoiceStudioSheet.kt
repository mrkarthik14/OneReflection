package com.onereflection.app.ui.modals

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.model.AVATAR_MAYA
import com.onereflection.app.model.ChimeType
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.OneReflectionSwitch
import com.onereflection.app.ui.components.WaveformColorStyle
import com.onereflection.app.ui.components.WaveformVisualizer
import com.onereflection.app.ui.theme.BrandColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceStudioSheet(
    oledMode: Boolean,
    onDismiss: () -> Unit,
    onSaveVoiceNote: (quote: String, duration: String) -> Unit
) {
    var selectedMode by remember { mutableStateOf("alarm") }
    var isRecording by remember { mutableStateOf(true) }
    var seconds by remember { mutableIntStateOf(31) }
    var vibeFilter by remember { mutableStateOf("natural") }
    var encryptedBubble by remember { mutableStateOf(true) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        isRecording = true
    }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(1000L)
            seconds = if (seconds >= 90) 31 else seconds + 1
        }
    }

    val formattedTime = "%02d:%02d".format(seconds / 60, seconds % 60)
    val sheetBg = if (oledMode) Color(0xFF0A0A0C) else Color.White
    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark
    val textSecondary = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
    val subtleBg = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow
    val cardBorder = if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.06f)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = sheetBg,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(BrandColors.LavenderSoft)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = BrandColors.LavenderText, modifier = Modifier.size(14.dp))
                    Text("VOICE STUDIO • DISPATCH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.LavenderText)
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Dismiss Voice Studio", tint = textPrimary)
                }
            }

            Column {
                Text("Record a gentle voice note.", style = MaterialTheme.typography.headlineSmall, color = textPrimary)
                Text("Attach your warmth to an alarm, daily habit, or shared to-do.", style = MaterialTheme.typography.bodySmall, color = textSecondary)
            }

            // Mode Selector Pills
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("alarm", "Morning Alarm", BrandColors.ButterYellow to BrandColors.ButterYellowText),
                    Triple("reminder", "Reminder Ping", BrandColors.PinkFixed to BrandColors.PrimaryPlum),
                    Triple("habit", "Daily Habit", BrandColors.LavenderSoft to BrandColors.LavenderText),
                    Triple("chat", "Circle Chat", BrandColors.SurfaceContainerHigh to BrandColors.OnSurfaceDark)
                ).forEach { (id, label, colors) ->
                    val active = selectedMode == id
                    Surface(
                        onClick = {
                            ChimePlayer.play(ChimeType.CLICK)
                            selectedMode = id
                        },
                        shape = CircleShape,
                        color = if (active) colors.first else subtleBg
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (active) colors.second else textSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                        )
                    }
                }
            }

            // Destination Assignment Bento Card (Candy Pink)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = BrandColors.CandyPink,
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("DESTINATION & SCHEDULE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.CandyPinkDeep)
                        Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.9f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text("Tomorrow • 07:30 AM", style = MaterialTheme.typography.labelSmall, color = BrandColors.OnSurfaceDark)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.95f))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AvatarImage(AVATAR_MAYA, "MR", "Maya", 42.dp)
                            Column {
                                Text("Maya (Partner)", style = MaterialTheme.typography.labelLarge, color = BrandColors.OnSurfaceDark)
                                Text("Alarm: \"Rise gently with tea\"", fontSize = 10.sp, color = BrandColors.OnSurfaceVariant)
                            }
                        }
                        Box(modifier = Modifier.clip(CircleShape).background(BrandColors.SurfaceContainerLow).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("Wake-up Sync", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.PrimaryPlum)
                        }
                    }
                }
            }

            // Expressive Waveform & Timer Card
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = subtleBg,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(if (isRecording) Color(0xFFEF4444) else textSecondary))
                            Text(if (isRecording) "Recording Live • 48kHz HD" else "Paused • 48kHz HD", style = MaterialTheme.typography.labelSmall, color = textPrimary)
                        }
                        Text("Crisp Voice EQ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(formattedTime, style = MaterialTheme.typography.displayLarge, color = textPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("/ 01:30", style = MaterialTheme.typography.titleMedium, color = textSecondary, modifier = Modifier.padding(bottom = 5.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (oledMode) BrandColors.OledBlack else Color.White,
                        border = BorderStroke(1.dp, cardBorder)
                    ) {
                        WaveformVisualizer(
                            isPlaying = isRecording,
                            barCount = 24,
                            height = 48.dp,
                            style = WaveformColorStyle.MIXED,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }

                    // Audio Vibe Filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("natural" to "Natural", "radio" to "Warm Radio", "echo" to "Gentle Echo").forEach { (id, label) ->
                            val sel = vibeFilter == id
                            Surface(
                                onClick = {
                                    ChimePlayer.play(ChimeType.CLICK)
                                    vibeFilter = id
                                },
                                shape = CircleShape,
                                color = if (sel) BrandColors.OnSurfaceDark else if (oledMode) Color(0xFF27272A) else Color.White,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (sel) Color.White else textSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Recording Controls Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        onClick = {
                            ChimePlayer.play(ChimeType.CLICK)
                            seconds = 0
                            isRecording = true
                        },
                        shape = CircleShape,
                        color = if (oledMode) Color(0xFF27272A) else BrandColors.SurfaceContainerHigh,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Delete, contentDescription = "Restart recording", tint = textPrimary)
                        }
                    }
                    Text("Restart", fontSize = 10.sp, color = textSecondary)
                }

                Surface(
                    onClick = {
                        ChimePlayer.play(ChimeType.CLICK)
                        if (!isRecording) {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else {
                            isRecording = false
                        }
                    },
                    shape = CircleShape,
                    color = if (isRecording) BrandColors.OnSurfaceDark else BrandColors.PrimaryPlum,
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(68.dp).testTag("voice_studio_record_toggle")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Pause else Icons.Default.Mic,
                            contentDescription = "Toggle recording",
                            tint = if (isRecording) BrandColors.CandyPink else Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        onClick = {
                            ChimePlayer.play(ChimeType.CLICK)
                            isRecording = !isRecording
                        },
                        shape = CircleShape,
                        color = BrandColors.ButterYellow,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isRecording) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Pause or resume",
                                tint = BrandColors.ButterYellowText
                            )
                        }
                    }
                    Text(if (isRecording) "Pause" else "Resume", fontSize = 10.sp, color = textSecondary)
                }
            }

            // Encrypted Bubble Toggle
            Surface(shape = RoundedCornerShape(18.dp), color = subtleBg, border = BorderStroke(1.dp, cardBorder)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("End-to-End Encrypted Bubble", style = MaterialTheme.typography.labelMedium, color = textPrimary)
                        Text("Maya will receive this gently at wake-up time.", fontSize = 10.sp, color = textSecondary)
                    }
                    OneReflectionSwitch(
                        checked = encryptedBubble,
                        onCheckedChange = { encryptedBubble = it },
                        thumbColor = BrandColors.ButterYellow
                    )
                }
            }

            // Attach & Draft Actions
            Button(
                onClick = {
                    onSaveVoiceNote(
                        "Rise and shine sunny! Remember to drink your lemon water first thing.",
                        formattedTime
                    )
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(54.dp).testTag("voice_studio_attach_btn")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandColors.CandyPink, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Attach to Morning Alarm", style = MaterialTheme.typography.titleSmall)
            }

            OutlinedButton(
                onClick = onDismiss,
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth().height(46.dp)
            ) {
                Text("Save to Voice Drafts", style = MaterialTheme.typography.labelMedium, color = textPrimary)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
