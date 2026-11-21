package com.onereflection.app.ui.modals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.onereflection.app.model.AVATAR_LEO
import com.onereflection.app.model.AVATAR_MAYA
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.QuietHoursConfig
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.OneReflectionSwitch
import com.onereflection.app.ui.theme.BrandColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedtimeSyncSheet(
    initialConfig: QuietHoursConfig,
    oledMode: Boolean,
    onDismiss: () -> Unit,
    onSave: (QuietHoursConfig) -> Unit
) {
    var bedtimeMinutes by remember { mutableIntStateOf(initialConfig.bedtimeMinutes) }
    var wakeMinutes by remember { mutableIntStateOf(initialConfig.wakeMinutes) }
    var syncWithPartner by remember { mutableStateOf(initialConfig.syncWithPartner) }
    var mayaEmergency by remember { mutableStateOf(true) }
    var mayaWhispers by remember { mutableStateOf(true) }
    var mayaWakeQueue by remember { mutableStateOf(true) }
    var sensitivity by remember { mutableStateOf("gentle") }
    var leoEmergency by remember { mutableStateOf(true) }
    var selectedVibe by remember { mutableStateOf(initialConfig.audioVibe) }
    var mutedHaptics by remember { mutableStateOf(initialConfig.mutedHaptics) }

    val sheetBg = if (oledMode) Color(0xFF0A0A0C) else Color.White
    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark
    val textSecondary = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
    val cardBg = if (oledMode) BrandColors.OledCard else Color.White
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
                    Icon(Icons.Default.Bedtime, contentDescription = null, tint = BrandColors.LavenderText, modifier = Modifier.size(14.dp))
                    Text("BEDTIME HARMONY • MUTUAL REST", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.LavenderText)
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close modal", tint = textPrimary)
                }
            }

            Column {
                Text("Quiet Hours & Bedtime Sync.", style = MaterialTheme.typography.headlineSmall, color = textPrimary)
                Text("Protect restorative sleep while keeping delicate audio bridges open.", style = MaterialTheme.typography.bodySmall, color = textSecondary)
            }

            // Global Rest Horizon
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = BrandColors.PinkFixed.copy(alpha = if (oledMode) 0.16f else 0.4f),
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("GLOBAL REST HORIZON", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (oledMode) BrandColors.CandyPink else BrandColors.CandyPinkDeep)
                        Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.9f)).padding(horizontal = 9.dp, vertical = 3.dp)) {
                            Text("Auto-Adaptive", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.OnSurfaceDark)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TimeStepperTile(
                            label = "Bedtime",
                            formattedTime = QuietHoursConfig.formatMinutes(bedtimeMinutes),
                            cardBg = cardBg,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onDecrement = {
                                ChimePlayer.play(ChimeType.CLICK)
                                bedtimeMinutes -= 15
                            },
                            onIncrement = {
                                ChimePlayer.play(ChimeType.CLICK)
                                bedtimeMinutes += 15
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TimeStepperTile(
                            label = "Wake Horizon",
                            formattedTime = QuietHoursConfig.formatMinutes(wakeMinutes),
                            cardBg = cardBg,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            onDecrement = {
                                ChimePlayer.play(ChimeType.CLICK)
                                wakeMinutes -= 15
                            },
                            onIncrement = {
                                ChimePlayer.play(ChimeType.CLICK)
                                wakeMinutes += 15
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Surface(shape = RoundedCornerShape(18.dp), color = cardBg, border = BorderStroke(1.dp, cardBorder)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Sync Sleep State with Maya", style = MaterialTheme.typography.labelMedium, color = textPrimary)
                                Text("Calibrates quiet hours when either partner dozes off", fontSize = 10.sp, color = textSecondary)
                            }
                            OneReflectionSwitch(
                                checked = syncWithPartner,
                                onCheckedChange = { syncWithPartner = it },
                                checkedTrackColor = BrandColors.PrimaryPlum
                            )
                        }
                    }
                }
            }

            // Maya Breakthrough Rules
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = BrandColors.ButterYellow.copy(alpha = if (oledMode) 0.16f else 0.28f),
                border = BorderStroke(1.dp, BrandColors.ButterYellowText.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AvatarImage(AVATAR_MAYA, "MR", "Maya R.", 40.dp, ringColor = BrandColors.ButterYellow)
                        Column {
                            Text("Maya R. • Primary Partner", style = MaterialTheme.typography.titleSmall, color = textPrimary)
                            Text("Awake & Synced via Oura Loop", fontSize = 10.sp, color = textSecondary)
                        }
                    }

                    BreakthroughToggle("Emergency Breakthrough", "Bypass silence with high-priority chime", mayaEmergency, cardBg, textPrimary, textSecondary) { mayaEmergency = it }
                    BreakthroughToggle("Gentle Voice Whispers", "Soft audio capped at 20% max volume", mayaWhispers, cardBg, textPrimary, textSecondary) { mayaWhispers = it }
                    BreakthroughToggle("Wake-Up Audio Queue", "Plant warm morning greetings for 07:00", mayaWakeQueue, cardBg, textPrimary, textSecondary) { mayaWakeQueue = it }

                    // Sensitivity Threshold
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(CircleShape).background(cardBg).padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("critical" to "Critical Only", "gentle" to "Gentle Check-ins", "all" to "All Alarms").forEach { (id, label) ->
                            val sel = sensitivity == id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(if (sel) BrandColors.OnSurfaceDark else Color.Transparent)
                                    .clickable {
                                        ChimePlayer.play(ChimeType.CLICK)
                                        sensitivity = id
                                    }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (sel) Color.White else textSecondary)
                            }
                        }
                    }
                }
            }

            // Leo Chen Breakthrough Rules
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = BrandColors.LavenderSoft.copy(alpha = if (oledMode) 0.16f else 0.35f),
                border = BorderStroke(1.dp, BrandColors.TertiarySlateBlue.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AvatarImage(AVATAR_LEO, "LC", "Leo Chen", 40.dp, ringColor = BrandColors.LavenderContainer)
                        Column {
                            Text("Leo Chen • Studio Co-founder", style = MaterialTheme.typography.titleSmall, color = textPrimary)
                            Text("Design Sprint & Releases", fontSize = 10.sp, color = textSecondary)
                        }
                    }
                    BreakthroughToggle("Emergency Breakthrough", "Requires double-ping verification", leoEmergency, cardBg, textPrimary, textSecondary) { leoEmergency = it }
                }
            }

            // Audio Vibe & Haptics
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Audio Vibe & Haptic Texture", style = MaterialTheme.typography.titleSmall, color = textPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            Triple("tibetan", "Tibetan Bowl", ChimeType.TIBETAN),
                            Triple("piano", "Warm Piano", ChimeType.PIANO),
                            Triple("river", "River Glow", ChimeType.RIVER)
                        ).forEach { (id, label, chime) ->
                            val sel = selectedVibe == id
                            Surface(
                                onClick = {
                                    ChimePlayer.play(chime)
                                    selectedVibe = id
                                },
                                shape = CircleShape,
                                color = if (sel) BrandColors.OnSurfaceDark else cardBg,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (sel) Color.White else textPrimary)
                                }
                            }
                        }
                    }
                    BreakthroughToggle("Muted Vibration Pulses", "Micro-taps only; avoids harsh buzzing", mutedHaptics, cardBg, textPrimary, textSecondary) { mutedHaptics = it }
                }
            }

            Button(
                onClick = {
                    onSave(
                        QuietHoursConfig(
                            bedtimeMinutes = bedtimeMinutes,
                            wakeMinutes = wakeMinutes,
                            syncWithPartner = syncWithPartner,
                            audioVibe = selectedVibe,
                            mutedHaptics = mutedHaptics
                        )
                    )
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(52.dp).testTag("bedtime_save_btn")
            ) {
                Icon(Icons.Default.Bedtime, contentDescription = null, tint = BrandColors.ButterYellow, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Bedtime Sync & Exceptions", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun TimeStepperTile(
    label: String,
    formattedTime: String,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = cardBg,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecondary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(formattedTime, style = MaterialTheme.typography.headlineSmall, color = textPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(onClick = onDecrement, shape = CircleShape, color = BrandColors.SurfaceContainerHigh, modifier = Modifier.size(28.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text("-", fontWeight = FontWeight.Bold, color = BrandColors.OnSurfaceDark) }
                    }
                    Surface(onClick = onIncrement, shape = CircleShape, color = BrandColors.SurfaceContainerHigh, modifier = Modifier.size(28.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text("+", fontWeight = FontWeight.Bold, color = BrandColors.OnSurfaceDark) }
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakthroughToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(shape = RoundedCornerShape(16.dp), color = cardBg) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = textPrimary)
                Text(subtitle, fontSize = 10.sp, color = textSecondary)
            }
            OneReflectionSwitch(
                checked = checked,
                onCheckedChange = {
                    ChimePlayer.play(ChimeType.CLICK)
                    onCheckedChange(it)
                },
                checkedTrackColor = BrandColors.ButterYellow,
                thumbColor = if (checked) BrandColors.OnSurfaceDark else Color.White
            )
        }
    }
}
