package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.model.AVATAR_ELENA
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.OneReflectionSwitch
import com.onereflection.app.ui.theme.BrandColors
import com.onereflection.app.viewmodel.OneReflectionUiState

@Composable
fun ProfileScreen(
    state: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onBack: () -> Unit,
    onOpenBedtimeSync: () -> Unit
) {
    BackHandler(onBack = onBack)

    val oledMode = state.oledMode
    var eqPreset by remember { mutableStateOf("studio") }
    var fadeEnabled by remember { mutableStateOf(true) }
    var morningNotesEnabled by remember { mutableStateOf(true) }
    var habitsVisibility by remember { mutableStateOf(true) }
    var noPressureNudges by remember { mutableStateOf(true) }
    var softHaptics by remember { mutableStateOf(true) }
    var cacheCleared by remember { mutableStateOf(false) }

    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark
    val textSecondary = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
    val cardBorder = if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.06f)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text("YOUR SPACE • PREFERENCES & IDENTITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                }
                Text("Profile & Harmony.", style = MaterialTheme.typography.headlineLarge, color = textPrimary)
                Text("Manage your voice persona, partner sync mesh, bedtime routines, and privacy boundaries.", style = MaterialTheme.typography.bodySmall, color = textSecondary)
            }
        }

        // 2. Hero Profile Card (Candy Pink)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BrandColors.CandyPink,
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        AvatarImage(AVATAR_ELENA, "EV", "Elena Vance", 76.dp, ringWidth = 3.dp)
                        Box(modifier = Modifier.clip(CircleShape).background(Color.White).padding(horizontal = 12.dp, vertical = 6.dp)) {
                            Text("Awake & Flowing", style = MaterialTheme.typography.labelSmall, color = BrandColors.OnSurfaceDark)
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Elena Vance", style = MaterialTheme.typography.headlineMedium, color = BrandColors.OnSurfaceDark)
                            Icon(Icons.Default.Verified, contentDescription = null, tint = BrandColors.PrimaryPlum, modifier = Modifier.size(18.dp))
                        }
                        Text("@elenavance • Member since Nov 2023", style = MaterialTheme.typography.bodySmall, color = BrandColors.CandyPinkDeep)
                    }

                    Surface(shape = RoundedCornerShape(18.dp), color = Color.White.copy(alpha = 0.94f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Paired with Maya R. (@mayar)", style = MaterialTheme.typography.labelMedium, color = BrandColors.OnSurfaceDark)
                                Text("Live Heartbeat & Morning Mesh Active", fontSize = 10.sp, color = BrandColors.OnSurfaceVariant)
                            }
                            Box(modifier = Modifier.clip(CircleShape).background(BrandColors.LavenderSoft).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Text("Mutual Consent", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.LavenderText)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onNavigate(ScreenId.PEOPLE) },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Identity Key", style = MaterialTheme.typography.labelMedium)
                        }
                        Button(
                            onClick = onOpenBedtimeSync,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BrandColors.OnSurfaceDark),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(Icons.Default.Bedtime, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Quiet Rules", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // 3. Voice Persona & Mic Quality (Butter Yellow)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BrandColors.ButterYellow,
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("ACOUSTIC IDENTITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.ButterYellowText)
                    Text("Voice Persona & Mic Quality", style = MaterialTheme.typography.titleLarge, color = BrandColors.OnSurfaceDark)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("studio" to "Crisp Studio", "vintage" to "Warm Vintage", "whisper" to "Whisper EQ").forEach { (id, label) ->
                            val sel = eqPreset == id
                            Surface(
                                onClick = {
                                    ChimePlayer.play(ChimeType.CLICK)
                                    eqPreset = id
                                },
                                shape = CircleShape,
                                color = if (sel) BrandColors.OnSurfaceDark else Color.White
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (sel) Color.White else BrandColors.OnSurfaceDark,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Surface(shape = RoundedCornerShape(18.dp), color = Color.White.copy(alpha = 0.94f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Gentle Fade-in on Alarms", style = MaterialTheme.typography.labelMedium, color = BrandColors.OnSurfaceDark)
                                Text("Gradual volume swells over 4 minutes", fontSize = 10.sp, color = BrandColors.OnSurfaceVariant)
                            }
                            OneReflectionSwitch(
                                checked = fadeEnabled,
                                onCheckedChange = {
                                    ChimePlayer.play(ChimeType.CLICK)
                                    fadeEnabled = it
                                },
                                thumbColor = BrandColors.ButterYellow
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.85f)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text("48kHz Lossless FLAC", style = MaterialTheme.typography.labelSmall, color = BrandColors.ButterYellowText)
                        }
                        Button(
                            onClick = { ChimePlayer.play(ChimeType.TIBETAN) },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                            modifier = Modifier.testTag("profile_test_ping_btn")
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Voice Ping", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // 4. Sync & Sharing Mesh (Lavender Blue)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BrandColors.LavenderSoft,
                border = BorderStroke(1.dp, BrandColors.TertiarySlateBlue.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("RESONANCE NETWORK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.LavenderMuted)
                    Text("Sync & Sharing Mesh", style = MaterialTheme.typography.titleLarge, color = BrandColors.LavenderText)

                    ProfileToggleRow("Morning Wake-up Voice Notes", morningNotesEnabled, BrandColors.TertiarySlateBlue) {
                        ChimePlayer.play(ChimeType.CLICK)
                        morningNotesEnabled = it
                    }
                    ProfileToggleRow("Shared Habit Streak Visibility", habitsVisibility, BrandColors.TertiarySlateBlue) {
                        ChimePlayer.play(ChimeType.CLICK)
                        habitsVisibility = it
                    }

                    Surface(
                        onClick = onOpenBedtimeSync,
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.88f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Quiet Hours Sync", style = MaterialTheme.typography.labelMedium, color = BrandColors.OnSurfaceDark)
                                Text("Bedtime silence locked", fontSize = 10.sp, color = BrandColors.OnSurfaceVariant)
                            }
                            Box(modifier = Modifier.clip(CircleShape).background(BrandColors.LavenderSoft).padding(horizontal = 10.dp, vertical = 5.dp)) {
                                Text("${state.quietHours.formattedBedtime()} - ${state.quietHours.formattedWake()}", style = MaterialTheme.typography.labelSmall, color = BrandColors.LavenderText)
                            }
                        }
                    }
                }
            }
        }

        // 5. Mindful Notifications & Privacy Vault
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Mindful Notifications & Privacy", style = MaterialTheme.typography.titleMedium, color = textPrimary)

                    ProfileToggleRow("No-Pressure Daily Nudges", noPressureNudges, BrandColors.PrimaryPlum) {
                        ChimePlayer.play(ChimeType.CLICK)
                        noPressureNudges = it
                    }
                    ProfileToggleRow("Soft Haptic Vibration Rhythms", softHaptics, BrandColors.PrimaryPlum) {
                        ChimePlayer.play(ChimeType.CLICK)
                        softHaptics = it
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (oledMode) BrandColors.OledBlack else Color.White
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Offline Audio Cache", style = MaterialTheme.typography.labelMedium, color = textPrimary)
                                Text(if (cacheCleared) "0 MB (Cleaned)" else "248 MB stored locally", fontSize = 10.sp, color = textSecondary)
                            }
                            Surface(
                                onClick = {
                                    ChimePlayer.play(ChimeType.CLICK)
                                    cacheCleared = true
                                },
                                shape = CircleShape,
                                color = if (cacheCleared) BrandColors.Matcha else BrandColors.SurfaceContainerHigh,
                                modifier = Modifier.testTag("profile_clear_cache_btn")
                            ) {
                                Text(
                                    text = if (cacheCleared) "Cleared ✓" else "Clear Cache",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandColors.OnSurfaceDark,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Version Footer
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Built for mindful human connection", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                Text("OneReflection v2.4.1 (Native Android)", fontSize = 10.sp, color = textSecondary.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun ProfileToggleRow(
    label: String,
    checked: Boolean,
    activeColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color.White.copy(alpha = 0.9f)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = BrandColors.OnSurfaceDark, modifier = Modifier.weight(1f))
            OneReflectionSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                checkedTrackColor = activeColor
            )
        }
    }
}
