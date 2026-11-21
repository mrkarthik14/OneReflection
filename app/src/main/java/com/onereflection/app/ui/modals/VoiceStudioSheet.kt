package com.onereflection.app.ui.modals

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.onereflection.app.model.AvatarUrls
import com.onereflection.app.model.ChimeType
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.OneReflectionLogoMark
import com.onereflection.app.ui.components.OneReflectionSwitch
import com.onereflection.app.ui.components.WaveformVisualizer
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceStudioSheet(
    oledMode: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    onPlayChime: (ChimeType) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedMode by remember { mutableIntStateOf(0) }
    var selectedVibe by remember { mutableIntStateOf(0) }
    var isRecording by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var endToEndEnabled by remember { mutableStateOf(true) }
    var permissionMessage by remember { mutableStateOf<String?>(null) }

    val modes = listOf("Wake Alarm", "Circle Memo", "Whisper")
    val vibes = listOf("Warm Velvet", "Tibetan Dawn", "Soft Rain")

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            permissionMessage = null
            isRecording = true
            onPlayChime(ChimeType.TIBETAN)
        } else {
            permissionMessage = "Microphone permission needed to capture live voice."
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000L)
                elapsedSeconds += 1
            }
        }
    }

    val containerBg = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.SurfaceWarm
    val cardBg = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.SurfaceCard
    val borderColor = if (oledMode) OneReflectionColors.OledBorder else OneReflectionColors.HairlineLight
    val formattedTimer = String.format("0:%02d", elapsedSeconds)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = containerBg,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with OneReflection Logo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OneReflectionLogoMark(size = 34.dp, cornerRadius = 10.dp)
                    Column {
                        Text(
                            text = "ACOUSTIC SANCTUARY",
                            fontFamily = MonoFontFamily,
                            fontSize = 9.5.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Voice Studio",
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 23.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("voice_studio_close")
                        .clip(CircleShape)
                        .background(cardBg)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close Voice Studio", modifier = Modifier.size(18.dp))
                }
            }

            // Mode Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                modes.forEachIndexed { idx, label ->
                    val active = selectedMode == idx
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(CircleShape)
                            .clickable {
                                selectedMode = idx
                                onPlayChime(ChimeType.CLICK)
                            },
                        shape = CircleShape,
                        color = if (active) OneReflectionColors.InkPrimary else cardBg,
                        border = BorderStroke(1.dp, if (active) OneReflectionColors.GoldLuminous.copy(alpha = 0.4f) else borderColor)
                    ) {
                        Box(modifier = Modifier.padding(vertical = 9.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (active) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            // Destination Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AvatarImage(url = AvatarUrls.MAYA, fallbackInitials = "MR", contentDescription = "Maya R.", size = 40.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("DESTINATION", fontFamily = MonoFontFamily, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Maya R. • 07:30 AM Breakthrough", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    }
                }
            }

            // Central Recording Console
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = OneReflectionColors.InkPrimary,
                border = BorderStroke(1.dp, OneReflectionColors.GoldLuminous.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = formattedTimer,
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 34.sp,
                        color = if (isRecording) OneReflectionColors.TerracottaLight else Color.White
                    )

                    WaveformVisualizer(
                        isPlaying = isRecording,
                        barCount = 24,
                        height = 38.dp,
                        mixedColors = true
                    )

                    if (permissionMessage != null) {
                        Text(
                            text = permissionMessage!!,
                            fontFamily = BodyFontFamily,
                            fontSize = 11.5.sp,
                            color = OneReflectionColors.GoldLuminous
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("voice_studio_record_button")
                            .clip(CircleShape)
                            .background(if (isRecording) OneReflectionColors.Terracotta else OneReflectionColors.GoldLuminous)
                            .clickable {
                                if (isRecording) {
                                    isRecording = false
                                    onPlayChime(ChimeType.CLICK)
                                } else {
                                    val granted = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (granted) {
                                        permissionMessage = null
                                        isRecording = true
                                        onPlayChime(ChimeType.TIBETAN)
                                    } else {
                                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                            contentDescription = if (isRecording) "Stop recording" else "Start recording",
                            tint = if (isRecording) Color.White else OneReflectionColors.InkPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Vibe Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vibes.forEachIndexed { idx, label ->
                    val active = selectedVibe == idx
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                selectedVibe = idx
                                onPlayChime(ChimeType.SOLFEGGIO)
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = if (active) OneReflectionColors.PeachMist else cardBg,
                        border = BorderStroke(1.dp, if (active) OneReflectionColors.Terracotta else borderColor)
                    ) {
                        Box(modifier = Modifier.padding(vertical = 9.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = if (active) OneReflectionColors.TerracottaDeep else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            // E2E Encryption Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = OneReflectionColors.SageDeep, modifier = Modifier.size(16.dp))
                    Text("End-to-End Circle Encryption", fontFamily = BodyFontFamily, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                OneReflectionSwitch(
                    checked = endToEndEnabled,
                    onCheckedChange = { endToEndEnabled = !endToEndEnabled },
                    activeTrackColor = OneReflectionColors.SageDeep,
                    testTag = "voice_studio_e2e_switch"
                )
            }

            // Save Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_studio_save_button")
                    .clip(RoundedCornerShape(22.dp))
                    .clickable {
                        val finalDuration = if (elapsedSeconds > 0) formattedTimer else "0:14"
                        onSave("Rise gently — fresh pour-over coffee is waiting in the kitchen.", finalDuration)
                    },
                shape = RoundedCornerShape(22.dp),
                color = OneReflectionColors.Terracotta
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Send Voice Reflection",
                        fontFamily = BodyFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
        }
    }
}
