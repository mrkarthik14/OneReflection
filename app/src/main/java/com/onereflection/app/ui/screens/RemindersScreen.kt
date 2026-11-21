package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.model.AVATAR_MAYA
import com.onereflection.app.model.ChimeType
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.theme.BrandColors
import com.onereflection.app.viewmodel.OneReflectionUiState

@Composable
fun RemindersScreen(
    state: OneReflectionUiState,
    onBack: () -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onToggleReminder: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val oledMode = state.oledMode
    var activeTab by remember { mutableStateOf("day") }
    var activeDay by remember { mutableIntStateOf(16) }
    var heroDone by remember { mutableStateOf(false) }

    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark
    val textSecondary = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
    val cardBorder = if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.06f)

    val days = remember {
        listOf(
            Triple("MON", 13, 1), Triple("TUE", 14, 2), Triple("WED", 15, 1),
            Triple("THU", 16, 3), Triple("FRI", 17, 1), Triple("SAT", 18, 1), Triple("SUN", 19, 1)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header
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
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandColors.ButterYellowText, modifier = Modifier.size(14.dp))
                        Text("SYNCHRONIZED PLANNER • MAY 2024", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.ButterYellowText)
                    }
                    Text("Live Sync", style = MaterialTheme.typography.labelSmall, color = textSecondary)
                }
                Text("Moments & Reminders.", style = MaterialTheme.typography.headlineLarge, color = textPrimary)
                Text("Gentle nudges, upcoming shared milestones, and timely reflections.", style = MaterialTheme.typography.bodySmall, color = textSecondary)
            }
        }

        // 2. View Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("day" to "Day", "week" to "Week", "month" to "Month").forEach { (id, label) ->
                        val sel = activeTab == id
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (sel) BrandColors.OnSurfaceDark else Color.Transparent)
                                .clickable {
                                    ChimePlayer.play(ChimeType.CLICK)
                                    activeTab = id
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(label, style = MaterialTheme.typography.labelMedium, color = if (sel) Color.White else textSecondary)
                        }
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = if (oledMode) BrandColors.OledCard else Color.White,
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = BrandColors.PrimaryPlum, modifier = Modifier.size(15.dp))
                        Text("May 13–19", style = MaterialTheme.typography.labelSmall, color = textPrimary)
                    }
                }
            }
        }

        // 3. Weekly Interactive Dot Strip
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainer)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                days.forEach { (label, num, dots) ->
                    val isActive = num == activeDay
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isActive) BrandColors.ButterYellow else Color.Transparent)
                            .clickable {
                                ChimePlayer.play(ChimeType.CLICK)
                                activeDay = num
                            }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isActive) BrandColors.ButterYellowText else textSecondary)
                        Text(num.toString(), style = MaterialTheme.typography.titleSmall, color = if (isActive) BrandColors.ButterYellowText else textPrimary)
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.padding(top = 3.dp)) {
                            repeat(dots) {
                                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(if (isActive) BrandColors.ButterYellowText else BrandColors.PrimaryPlum))
                            }
                        }
                    }
                }
            }
        }

        // 4. Hero Next-Up Bento Card (Candy Pink)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BrandColors.CandyPink,
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.9f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text("NEXT UP IN 45 MIN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.OnSurfaceDark)
                        }
                        Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.72f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text("Google Cal Sync", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.OnSurfaceDark)
                        }
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("11:30 AM", style = MaterialTheme.typography.headlineLarge, color = BrandColors.OnSurfaceDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("TODAY", style = MaterialTheme.typography.labelMedium, color = BrandColors.CandyPinkDeep, modifier = Modifier.padding(bottom = 5.dp))
                    }

                    Column {
                        Text("Dentist & Oral Wellness Checkup", style = MaterialTheme.typography.titleLarge, color = BrandColors.OnSurfaceDark)
                        Text("Dr. Turner's Clinic • Bring updated allergy & nightguard notes.", style = MaterialTheme.typography.bodySmall, color = BrandColors.OnSurfaceVariant)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.85f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AvatarImage(AVATAR_MAYA, "MR", "Maya", 30.dp)
                        Text("Maya will send a voice ping 15m prior to departure", style = MaterialTheme.typography.bodySmall, color = BrandColors.OnSurfaceDark, modifier = Modifier.weight(1f))
                        IconButton(onClick = { ChimePlayer.play(ChimeType.TIBETAN) }, modifier = Modifier.size(30.dp)) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Test ping", tint = BrandColors.PrimaryPlum, modifier = Modifier.size(18.dp))
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                ChimePlayer.play(ChimeType.CLICK)
                                heroDone = !heroDone
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                            modifier = Modifier.weight(1f).height(46.dp).testTag("reminders_hero_done")
                        ) {
                            Icon(if (heroDone) Icons.Default.TaskAlt else Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (heroDone) "Completed" else "Mark Done", style = MaterialTheme.typography.labelMedium)
                        }
                        Button(
                            onClick = { ChimePlayer.play(ChimeType.CLICK) },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BrandColors.OnSurfaceDark),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Icon(Icons.Default.Snooze, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+10m", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // 5. Timeline For Today
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Timeline For Today", style = MaterialTheme.typography.titleMedium, color = textPrimary)
                state.reminders.drop(1).forEachIndexed { idx, rem ->
                    val bg = when (idx) {
                        0 -> BrandColors.ButterYellow
                        1 -> BrandColors.LavenderSoft
                        else -> if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh
                    }
                    val fg = when (idx) {
                        0 -> BrandColors.OnSurfaceDark
                        1 -> BrandColors.LavenderText
                        else -> textPrimary
                    }
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = bg,
                        border = BorderStroke(1.dp, cardBorder)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.9f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                                        Text(rem.time, style = MaterialTheme.typography.labelSmall, color = BrandColors.OnSurfaceDark)
                                    }
                                    Text(rem.category.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = fg.copy(alpha = 0.75f))
                                }
                                Surface(
                                    onClick = { onToggleReminder(rem.id) },
                                    shape = CircleShape,
                                    color = if (rem.completed) BrandColors.OnSurfaceDark else Color.White,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Toggle reminder",
                                            tint = if (rem.completed) Color.White else BrandColors.OutlineVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = rem.title,
                                style = MaterialTheme.typography.titleMedium,
                                textDecoration = if (rem.completed) TextDecoration.LineThrough else TextDecoration.None,
                                color = fg
                            )
                            Text(rem.location, style = MaterialTheme.typography.bodySmall, color = fg.copy(alpha = 0.75f))
                        }
                    }
                }
            }
        }

        // 6. Upcoming Highlights Countdowns
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Upcoming Highlights", style = MaterialTheme.typography.titleMedium, color = textPrimary)
                    MilestoneRow("6", "Maya's Birthday", "May 22 • Surprise voice alarm scheduled", BrandColors.ButterYellow, BrandColors.ButterYellowText, oledMode, textPrimary, textSecondary)
                    MilestoneRow("12", "Product Sprint 14 Launch", "May 28 • Shared design team milestone", BrandColors.LavenderSoft, BrandColors.LavenderText, oledMode, textPrimary, textSecondary)
                }
            }
        }

        // 7. Bottom Actions
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onOpenVoiceStudio,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                    modifier = Modifier.weight(1f).height(52.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = BrandColors.YellowFixed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("New Reminder or Event", style = MaterialTheme.typography.labelLarge)
                }
                Surface(
                    onClick = onOpenVoiceStudio,
                    shape = CircleShape,
                    color = BrandColors.CandyPink,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Mic, contentDescription = "Voice reminder", tint = BrandColors.OnSurfaceDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneRow(
    daysCount: String,
    title: String,
    subtitle: String,
    badgeBg: Color,
    badgeText: Color,
    oledMode: Boolean,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (oledMode) BrandColors.OledBlack else Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(badgeBg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("IN", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = badgeText)
            Text(daysCount, style = MaterialTheme.typography.titleMedium, color = badgeText)
            Text("DAYS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = badgeText)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = textPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = textSecondary)
        }
    }
}
