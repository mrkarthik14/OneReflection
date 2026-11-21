package com.onereflection.app.ui.modals

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.ui.components.OneReflectionLogoMark
import com.onereflection.app.ui.components.OneReflectionSwitch
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedtimeSyncSheet(
    uiState: OneReflectionUiState,
    onDismiss: () -> Unit,
    onAdjustBedtime: (Int) -> Unit,
    onAdjustWake: (Int) -> Unit,
    onToggleSyncSleepWithPartner: () -> Unit,
    onPlayChime: (ChimeType) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedChime by remember { mutableStateOf(ChimeType.TIBETAN) }

    val oledMode = uiState.oledMode
    val containerBg = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.SurfaceWarm
    val cardBg = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.SurfaceCard
    val borderColor = if (oledMode) OneReflectionColors.OledBorder else OneReflectionColors.HairlineLight

    val bedtimeStr = String.format("%02d:%02d", uiState.bedtimeMinutes / 60, uiState.bedtimeMinutes % 60)
    val wakeStr = String.format("%02d:%02d", uiState.wakeMinutes / 60, uiState.wakeMinutes % 60)

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
            // Header
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
                            text = "CIRCADIAN RHYTHM & RULES",
                            fontFamily = MonoFontFamily,
                            fontSize = 9.5.sp,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Bedtime Sync",
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
                        .testTag("bedtime_sync_close")
                        .clip(CircleShape)
                        .background(cardBg)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close Bedtime Sync", modifier = Modifier.size(18.dp))
                }
            }

            // Bedtime & Wake Time Steppers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TimeStepperCard(
                    title = "BEDTIME",
                    timeText = bedtimeStr,
                    icon = Icons.Filled.Bedtime,
                    accent = OneReflectionColors.IndigoDeep,
                    cardBg = cardBg,
                    borderColor = borderColor,
                    minusTag = "bedtime_minus",
                    plusTag = "bedtime_plus",
                    onMinus = { onAdjustBedtime(-15) },
                    onPlus = { onAdjustBedtime(15) },
                    modifier = Modifier.weight(1f)
                )
                TimeStepperCard(
                    title = "WAKE UP",
                    timeText = wakeStr,
                    icon = Icons.Filled.WbSunny,
                    accent = OneReflectionColors.BurnishedGold,
                    cardBg = cardBg,
                    borderColor = borderColor,
                    minusTag = "wake_minus",
                    plusTag = "wake_plus",
                    onMinus = { onAdjustWake(-15) },
                    onPlus = { onAdjustWake(15) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Sync Sleep with Maya Toggle
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sync Quiet Horizon with Maya R.", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Allow partner whispers during sleep", fontFamily = BodyFontFamily, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    OneReflectionSwitch(
                        checked = uiState.syncSleepWithPartner,
                        onCheckedChange = onToggleSyncSleepWithPartner,
                        activeTrackColor = OneReflectionColors.Terracotta,
                        testTag = "bedtime_partner_sync_switch"
                    )
                }
            }

            // Audio Vibe Preview Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "MORNING CHIME VIBE",
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp,
                    letterSpacing = 0.9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        ChimeType.TIBETAN to "Tibetan",
                        ChimeType.PIANO to "Piano",
                        ChimeType.RIVER to "River"
                    ).forEach { (chime, label) ->
                        val active = selectedChime == chime
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bedtime_chime_${label.lowercase()}")
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    selectedChime = chime
                                    onPlayChime(chime)
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = if (active) OneReflectionColors.InkPrimary else cardBg,
                            border = BorderStroke(1.dp, if (active) OneReflectionColors.GoldLuminous.copy(alpha = 0.45f) else borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 11.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MusicNote,
                                    contentDescription = label,
                                    tint = if (active) OneReflectionColors.GoldLuminous else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = " $label",
                                    fontFamily = BodyFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (active) Color.White else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }

            // Save Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bedtime_sync_save_button")
                    .clip(RoundedCornerShape(22.dp))
                    .clickable {
                        onPlayChime(ChimeType.SOLFEGGIO)
                        onDismiss()
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
                        text = "Save Harmony Schedule",
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

@Composable
private fun TimeStepperCard(
    title: String,
    timeText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    cardBg: Color,
    borderColor: Color,
    minusTag: String,
    plusTag: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = cardBg,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(15.dp))
                Text(title, fontFamily = MonoFontFamily, fontSize = 9.5.sp, letterSpacing = 0.8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                text = timeText,
                fontFamily = MonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .testTag(minusTag)
                        .clip(CircleShape)
                        .background(OneReflectionColors.SurfaceCardLow)
                        .clickable { onMinus() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Minus 15m", tint = OneReflectionColors.InkPrimary, modifier = Modifier.size(16.dp))
                }
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .testTag(plusTag)
                        .clip(CircleShape)
                        .background(OneReflectionColors.InkPrimary)
                        .clickable { onPlus() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Plus 15m", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
