package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ReminderEvent
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors

@Composable
fun RemindersScreen(
    uiState: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onToggleReminder: (String) -> Unit,
    onPlayChime: (ChimeType) -> Unit
) {
    BackHandler { onNavigate(ScreenId.HOME) }

    var rangeIndex by remember { mutableIntStateOf(0) }
    var selectedDay by remember { mutableIntStateOf(2) }
    val ranges = listOf("Day", "Week", "Month")
    val weekDays = listOf("M" to "13", "T" to "14", "W" to "15", "T" to "16", "F" to "17", "S" to "18", "S" to "19")

    val oledMode = uiState.oledMode
    val cardBg = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.SurfaceCard
    val borderColor = if (oledMode) OneReflectionColors.OledBorder else OneReflectionColors.HairlineLight
    val heroEvent = uiState.reminders.firstOrNull()
    val timelineEvents = if (uiState.reminders.size > 1) uiState.reminders.drop(1) else emptyList()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 148.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Editorial Header + Segmented Range Toggle
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SHARED CALENDAR & MOMENTS",
                        fontFamily = MonoFontFamily,
                        fontSize = 10.5.sp,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Moments.",
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 31.sp,
                        letterSpacing = (-0.6).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.SurfaceCardLow,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        ranges.forEachIndexed { idx, label ->
                            val active = rangeIndex == idx
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("reminders_range_${label.lowercase()}")
                                    .clip(CircleShape)
                                    .clickable {
                                        rangeIndex = idx
                                        onPlayChime(ChimeType.CLICK)
                                    },
                                shape = CircleShape,
                                color = when {
                                    active && oledMode -> OneReflectionColors.TerracottaLight
                                    active -> OneReflectionColors.InkPrimary
                                    else -> Color.Transparent
                                }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontFamily = BodyFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp,
                                        color = when {
                                            active && oledMode -> OneReflectionColors.InkPrimary
                                            active -> Color.White
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Weekly Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                weekDays.forEachIndexed { idx, (d, num) ->
                    val active = selectedDay == idx
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                selectedDay = idx
                                onPlayChime(ChimeType.CLICK)
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = when {
                            active -> OneReflectionColors.Terracotta
                            oledMode -> OneReflectionColors.OledSurface
                            else -> OneReflectionColors.SurfaceCard
                        },
                        border = BorderStroke(1.dp, if (active) Color.Transparent else borderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = d,
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                color = if (active) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = num,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (active) Color.White else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // Hero Featured Moment Card
        if (heroEvent != null) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reminders_hero_card"),
                    shape = RoundedCornerShape(28.dp),
                    color = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.PeachMist,
                    border = BorderStroke(1.dp, OneReflectionColors.Terracotta.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "UP NEXT • ${heroEvent.time}",
                                fontFamily = MonoFontFamily,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.9.sp,
                                color = if (oledMode) OneReflectionColors.TerracottaLight else OneReflectionColors.Terracotta
                            )

                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .testTag("reminder_toggle_${heroEvent.id}")
                                    .clip(CircleShape)
                                    .background(if (heroEvent.completed) OneReflectionColors.Terracotta else Color.Transparent)
                                    .border(2.dp, OneReflectionColors.Terracotta, CircleShape)
                                    .clickable { onToggleReminder(heroEvent.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (heroEvent.completed) {
                                    Icon(Icons.Filled.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        Text(
                            text = heroEvent.title,
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 23.sp,
                            textDecoration = if (heroEvent.completed) TextDecoration.LineThrough else TextDecoration.None,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = "Location",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = heroEvent.location,
                                fontFamily = BodyFontFamily,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (!heroEvent.partnerNote.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (oledMode) OneReflectionColors.OledSurfaceElevated else Color.White.copy(alpha = 0.85f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Mic,
                                        contentDescription = "Partner voice note",
                                        tint = OneReflectionColors.Terracotta,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "\"${heroEvent.partnerNote}\"",
                                        fontFamily = DisplayFontFamily,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Remaining Timeline Cards
        items(timelineEvents, key = { it.id }) { event ->
            ReminderTimelineCard(
                event = event,
                cardBg = cardBg,
                borderColor = borderColor,
                onToggle = { onToggleReminder(event.id) }
            )
        }

        // Quick Voice Reminder CTA
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reminders_add_voice_button")
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onOpenVoiceStudio() },
                shape = RoundedCornerShape(22.dp),
                color = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.InkPrimary,
                border = BorderStroke(1.dp, OneReflectionColors.GoldLuminous.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 15.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = "Add voice moment", tint = OneReflectionColors.GoldLuminous, modifier = Modifier.size(18.dp))
                    Text(
                        text = "  Schedule Voice Reminder",
                        fontFamily = BodyFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderTimelineCard(
    event: ReminderEvent,
    cardBg: Color,
    borderColor: Color,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (event.completed) 0.58f else 1f)
            .testTag("reminder_card_${event.id}"),
        shape = RoundedCornerShape(22.dp),
        color = cardBg,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .testTag("reminder_toggle_${event.id}")
                    .clip(CircleShape)
                    .background(if (event.completed) OneReflectionColors.SageDeep else Color.Transparent)
                    .border(2.dp, OneReflectionColors.SageDeep, CircleShape)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (event.completed) {
                    Icon(Icons.Filled.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(13.dp))
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = event.title,
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.5.sp,
                    textDecoration = if (event.completed) TextDecoration.LineThrough else TextDecoration.None,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                        Text(event.time, fontFamily = MonoFontFamily, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(event.location, fontFamily = BodyFontFamily, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
