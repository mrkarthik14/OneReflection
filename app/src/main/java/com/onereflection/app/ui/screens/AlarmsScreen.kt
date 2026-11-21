package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.model.*
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.OneReflectionSwitch
import com.onereflection.app.ui.components.WaveformVisualizer
import com.onereflection.app.ui.theme.BrandColors
import com.onereflection.app.viewmodel.OneReflectionUiState
import kotlinx.coroutines.delay

@Composable
fun AlarmsScreen(
    state: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onBack: () -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onOpenBedtimeSync: () -> Unit,
    onToggleAlarm: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val oledMode = state.oledMode
    var activeDayIndex by remember { mutableIntStateOf(2) }
    var playingAlarmId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(playingAlarmId) {
        if (playingAlarmId != null) {
            delay(3400L)
            playingAlarmId = null
        }
    }

    val weekDays = remember {
        listOf("MON" to "14", "TUE" to "15", "WED" to "16", "THU" to "17", "FRI" to "18", "SAT" to "19")
    }

    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark
    val textSecondary = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
    val cardBorder = if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.06f)

    val heroAlarm = state.alarms.firstOrNull()
    val otherAlarms = state.alarms.drop(1)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Next Alarm Badge
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BrandColors.ButterYellow)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AlarmOn,
                            contentDescription = null,
                            tint = BrandColors.ButterYellowText,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Next alarm in 6h 45m",
                            style = MaterialTheme.typography.labelMedium,
                            color = BrandColors.ButterYellowText
                        )
                    }

                    Surface(
                        onClick = { ChimePlayer.play(ChimeType.SOLFEGGIO) },
                        shape = CircleShape,
                        color = BrandColors.ButterYellow.copy(alpha = 0.35f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Play Solfeggio Chime",
                                tint = BrandColors.ButterYellowText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = "Wake-up & Shared Alarms",
                        style = MaterialTheme.typography.headlineMedium,
                        color = textPrimary
                    )
                    Text(
                        text = "Start the day with voices you trust",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondary
                    )
                }
            }
        }

        // 2. Weekly Horizontal Calendar Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainer)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                weekDays.forEachIndexed { idx, (name, num) ->
                    val isActive = idx == activeDayIndex
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when {
                                    isActive -> BrandColors.ButterYellow
                                    idx == 0 && !oledMode -> Color.White
                                    else -> Color.Transparent
                                }
                            )
                            .clickable {
                                ChimePlayer.play(ChimeType.CLICK)
                                activeDayIndex = idx
                            }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = name,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) BrandColors.ButterYellowText else textSecondary
                        )
                        Text(
                            text = num,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isActive) BrandColors.ButterYellowText else textPrimary
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isActive -> BrandColors.ButterYellowText
                                        idx == 0 || idx == 3 || idx == 4 -> BrandColors.PrimaryPlum
                                        else -> Color.Transparent
                                    }
                                )
                        )
                    }
                }
            }
        }

        // 3. Featured Hero Alarm Card (Butter Yellow)
        if (heroAlarm != null) {
            item(key = heroAlarm.id) {
                val isPlayingHero = playingAlarmId == heroAlarm.id
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = BrandColors.ButterYellow,
                    border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.9f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "SHARED VOICE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandColors.OnSurfaceDark
                                        )
                                    }
                                    Text(
                                        text = "Tomorrow",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrandColors.ButterYellowText
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = heroAlarm.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = BrandColors.OnSurfaceDark
                                )
                            }

                            OneReflectionSwitch(
                                checked = heroAlarm.enabled,
                                onCheckedChange = { onToggleAlarm(heroAlarm.id) },
                                thumbColor = BrandColors.ButterYellow,
                                showIcon = true,
                                modifier = Modifier.testTag("alarm_hero_toggle")
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = heroAlarm.time,
                                    style = MaterialTheme.typography.displayLarge,
                                    color = BrandColors.OnSurfaceDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = heroAlarm.period,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = BrandColors.OnSurfaceDark.copy(alpha = 0.75f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                heroAlarm.days.forEach { d ->
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = d,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandColors.OnSurfaceDark
                                        )
                                    }
                                }
                            }
                        }

                        // Voice Clip Sub-Card
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color.White.copy(alpha = 0.95f),
                            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.06f))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                        AvatarImage(
                                            url = heroAlarm.creatorAvatar ?: AVATAR_MAYA,
                                            initials = (heroAlarm.creatorName ?: "MR").take(2),
                                            contentDescription = heroAlarm.creatorName ?: "Maya",
                                            size = 36.dp
                                        )
                                        Column {
                                            Text(
                                                text = "${heroAlarm.creatorName ?: "Maya"} (${heroAlarm.creatorRole ?: "Partner"})",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = BrandColors.OnSurfaceDark
                                            )
                                            Text(
                                                text = "Sent yesterday at 9:15 PM",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = BrandColors.OnSurfaceVariant
                                            )
                                        }
                                    }

                                    Surface(
                                        onClick = {
                                            if (!isPlayingHero) {
                                                ChimePlayer.play(ChimeType.TIBETAN)
                                                playingAlarmId = heroAlarm.id
                                            } else {
                                                ChimePlayer.stop()
                                                playingAlarmId = null
                                            }
                                        },
                                        shape = CircleShape,
                                        color = if (isPlayingHero) BrandColors.PrimaryPlum else BrandColors.OnSurfaceDark,
                                        modifier = Modifier.size(36.dp).testTag("alarm_hero_play")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isPlayingHero) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = "Play voice alarm snippet",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                if (heroAlarm.voiceSnippetQuote != null) {
                                    Text(
                                        text = "\"${heroAlarm.voiceSnippetQuote}\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontStyle = FontStyle.Italic,
                                        color = BrandColors.OnSurfaceDark
                                    )
                                }

                                WaveformVisualizer(
                                    isPlaying = isPlayingHero,
                                    barCount = 20,
                                    height = 24.dp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Upcoming Cycles Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Upcoming Cycles",
                        style = MaterialTheme.typography.titleLarge,
                        color = textPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (oledMode) Color(0xFF27272A) else BrandColors.SurfaceContainerHigh)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${otherAlarms.size} total",
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                    }
                }

                TextButton(onClick = onOpenBedtimeSync) {
                    Text(
                        text = "Customize",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum
                    )
                }
            }
        }

        // 5. Upcoming Alarm Cards
        items(otherAlarms, key = { it.id }) { alarm ->
            val cardColor = when (alarm.category) {
                "studio" -> BrandColors.LavenderSoft
                "sprint", "shared" -> BrandColors.CandyPink
                else -> if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow
            }
            val onCardColor = when (alarm.category) {
                "studio" -> BrandColors.LavenderText
                "sprint", "shared" -> BrandColors.OnSurfaceDark
                else -> textPrimary
            }
            val subColor = when (alarm.category) {
                "studio" -> BrandColors.LavenderMuted
                "sprint", "shared" -> BrandColors.CandyPinkDeep
                else -> textSecondary
            }
            val isPlaying = playingAlarmId == alarm.id

            Surface(
                shape = RoundedCornerShape(26.dp),
                color = cardColor,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = when (alarm.category) {
                                    "studio" -> "SYNC WITH STUDIO"
                                    "sprint" -> "BUDDY SPRINT"
                                    "shared" -> "SHARED VOICE"
                                    else -> "SELF CARE"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = subColor
                            )
                            Text(
                                text = alarm.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = onCardColor
                            )
                        }

                        OneReflectionSwitch(
                            checked = alarm.enabled,
                            onCheckedChange = { onToggleAlarm(alarm.id) },
                            thumbColor = if (alarm.category == "studio") BrandColors.LavenderSoft else if (alarm.category == "sprint") BrandColors.CandyPink else Color.White,
                            showIcon = alarm.category != "selfcare"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = alarm.time,
                                style = MaterialTheme.typography.headlineLarge,
                                color = onCardColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = alarm.period,
                                style = MaterialTheme.typography.bodySmall,
                                color = subColor,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.85f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (alarm.days.size >= 7) "Everyday" else if (alarm.days.size == 5) "Mon - Fri" else alarm.days.joinToString(", "),
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandColors.OnSurfaceDark
                            )
                        }
                    }

                    Surface(
                        onClick = {
                            val chime = when (alarm.chimeSound) {
                                "nordic" -> ChimeType.NORDIC
                                "solfeggio" -> ChimeType.SOLFEGGIO
                                else -> ChimeType.TIBETAN
                            }
                            if (!isPlaying) {
                                ChimePlayer.play(chime)
                                playingAlarmId = alarm.id
                            } else {
                                ChimePlayer.stop()
                                playingAlarmId = null
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (oledMode && alarm.category == "selfcare") BrandColors.OledBlack else Color.White.copy(alpha = 0.86f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (alarm.voiceSnippetQuote != null) Icons.Default.Mic else Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = BrandColors.PrimaryPlum,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = alarm.voiceSnippetQuote ?: "Chime: Nordic Glass Bell (Remote Sync)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (oledMode && alarm.category == "selfcare") Color.White else BrandColors.OnSurfaceDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = if (isPlaying) "Playing..." else (alarm.voiceSnippetDuration?.plus(" ▶") ?: "Test ▶"),
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandColors.PrimaryPlum
                            )
                        }
                    }
                }
            }
        }

        // 6. Bottom Collaborative Audio Dock
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Need a gentle push?", style = MaterialTheme.typography.labelLarge, color = textPrimary)
                        Text("Collaborative audio", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                ChimePlayer.play(ChimeType.TIBETAN)
                                onOpenVoiceStudio()
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandColors.OnSurfaceDark,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).height(48.dp).testTag("alarms_record_voice_btn")
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Record Voice", style = MaterialTheme.typography.labelMedium)
                        }
                        Button(
                            onClick = {
                                ChimePlayer.play(ChimeType.CLICK)
                                onNavigate(ScreenId.PEOPLE)
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (oledMode) Color(0xFF27272A) else Color.White,
                                contentColor = textPrimary
                            ),
                            modifier = Modifier.weight(1f).height(48.dp).testTag("alarms_ask_friend_btn")
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = null,
                                tint = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ask a Friend", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
