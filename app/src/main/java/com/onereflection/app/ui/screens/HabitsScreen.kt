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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.HabitTone
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.components.FlowGardenDailyStreakCard
import com.onereflection.app.ui.components.HabitHarmonySummaryCard
import com.onereflection.app.ui.components.MindfulHabitTrackerCard
import com.onereflection.app.ui.components.MindfulProgressRing
import com.onereflection.app.ui.components.WaveformVisualizer
import com.onereflection.app.ui.components.WeeklyConsistencyTrendCard
import com.onereflection.app.ui.components.habitTonePalette
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors
import com.onereflection.app.ui.theme.formatLiters
import kotlinx.coroutines.delay

@Composable
fun HabitsScreen(
    uiState: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onAddHydration: (Double) -> Unit,
    onIncrementHabit: (String) -> Unit,
    onDecrementHabit: (String) -> Unit,
    onAddHabit: (String, String, String, Int, Int, HabitTone) -> Unit,
    onDeleteHabit: (String) -> Unit,
    onPlayChime: (ChimeType) -> Unit,
    onStopChime: () -> Unit
) {
    BackHandler { onNavigate(ScreenId.HOME) }

    var playingJournal by remember { mutableStateOf(false) }
    var showAddHabitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(playingJournal) {
        if (playingJournal) {
            delay(5000L)
            playingJournal = false
            onStopChime()
        }
    }

    val oledMode = uiState.oledMode
    val cardBg = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.SurfaceCard
    val borderColor = if (oledMode) OneReflectionColors.OledBorder else OneReflectionColors.HairlineLight
    val hydrationProgress = (uiState.hydrationLiters / 2.5).toFloat().coerceIn(0f, 1f)
    val dailyStreak = uiState.dailyStreak

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 164.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Editorial Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "MINDFUL RITUALS & RHYTHM",
                        fontFamily = MonoFontFamily,
                        fontSize = 10.5.sp,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Daily Rituals.",
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 31.sp,
                        letterSpacing = (-0.6).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = if (oledMode) Color(0xFF2B2110) else OneReflectionColors.HoneyMist,
                    border = BorderStroke(1.dp, OneReflectionColors.BurnishedGold.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = if (oledMode) OneReflectionColors.GoldLuminous else OneReflectionColors.BurnishedGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "${dailyStreak.currentStreakDays}d Streak",
                            fontFamily = MonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (oledMode) OneReflectionColors.GoldLuminous else OneReflectionColors.HoneyInk
                        )
                    }
                }
            }
        }

        // 0a. Prominent Flow Garden Daily Streak Counter Hero Card
        item {
            FlowGardenDailyStreakCard(
                streak = dailyStreak,
                onActionClick = { onNavigate(ScreenId.SANCTUARY_TREE) },
                actionLabel = "Great Tree"
            )
        }

        // 0b. Live Flow Garden Plant Growth Stage Card (Seedling -> Sprout -> Mature Tree)
        item {
            val palette = LocalElementalPalette.current
            val gardenGrowth = uiState.flowGardenGrowth
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("habits_flow_garden_stage_card")
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { onNavigate(ScreenId.SANCTUARY_TREE) },
                shape = RoundedCornerShape(24.dp),
                color = palette.surfaceCard.copy(alpha = 0.96f),
                border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(palette.surfaceTint),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Park,
                                    contentDescription = "Flow Garden Stage",
                                    tint = palette.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "FLOW GARDEN · ${gardenGrowth.stage.badgeLabel}",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 9.5.sp,
                                    letterSpacing = 0.8.sp,
                                    color = palette.primary
                                )
                                Text(
                                    text = "${gardenGrowth.stage.displayName} (${gardenGrowth.growthPercent}%)",
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = palette.primary
                        ) {
                            Text(
                                text = "View Tree",
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = palette.onPrimary,
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Progress bar toward Mature Tree
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(CircleShape)
                            .background(palette.surfaceElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(gardenGrowth.growthScore.coerceIn(0.06f, 1f))
                                .height(7.dp)
                                .clip(CircleShape)
                                .background(palette.primary)
                        )
                    }

                    Text(
                        text = if (gardenGrowth.nextStage != null) {
                            "Complete daily rituals to grow into ${gardenGrowth.nextStage.displayName} (+${gardenGrowth.percentToNextStage}% needed)."
                        } else {
                            "Your daily habits have grown a thriving Mature Tree in your Flow Garden!"
                        },
                        fontFamily = BodyFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 1. Overall Daily Mindful Progress Ring Hero Card
        item {
            HabitHarmonySummaryCard(
                habits = uiState.habits,
                oledMode = oledMode,
                onAddHabitClick = { showAddHabitDialog = true }
            )
        }

        // 1b. Rolling 7-Day Completion Consistency Trend Visualization (syncs live with rings!)
        item {
            WeeklyConsistencyTrendCard(
                habits = uiState.habits,
                hydrationLiters = uiState.hydrationLiters,
                oledMode = oledMode
            )
        }

        // 2. Section Label for Individual Tracked Habits
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE RITUAL RINGS (${uiState.habits.size})",
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${uiState.habits.count { it.isCompleted }} completed today",
                    fontFamily = BodyFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 3. Individual Interactive Habit Tracking Cards with Visual Progress Rings
        items(uiState.habits, key = { it.id }) { habit ->
            MindfulHabitTrackerCard(
                habit = habit,
                oledMode = oledMode,
                canDelete = uiState.habits.size > 1,
                onIncrement = { onIncrementHabit(habit.id) },
                onDecrement = { onDecrementHabit(habit.id) },
                onDelete = { onDeleteHabit(habit.id) }
            )
        }

        // 4. Hydration Sanctuary Visual Progress Ring Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("habits_hydration_card"),
                shape = RoundedCornerShape(28.dp),
                color = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.SageMist,
                border = BorderStroke(
                    1.dp,
                    if (oledMode) OneReflectionColors.SageLight.copy(alpha = 0.3f) else OneReflectionColors.SageDeep.copy(alpha = 0.22f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    MindfulProgressRing(
                        progress = hydrationProgress,
                        primaryColor = if (oledMode) OneReflectionColors.SageLight else OneReflectionColors.SageDeep,
                        secondaryColor = Color(0xFF8FD3BE),
                        trackColor = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.SageDeep.copy(alpha = 0.14f),
                        size = 104.dp,
                        strokeWidth = 9.dp,
                        showTicks = true,
                        modifier = Modifier.testTag("habits_hydration_progress_ring")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${formatLiters(uiState.hydrationLiters)}L",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (oledMode) Color.White else OneReflectionColors.InkPrimary
                            )
                            Text(
                                text = "of 2.5L",
                                fontFamily = MonoFontFamily,
                                fontSize = 10.sp,
                                color = if (oledMode) OneReflectionColors.SageLight else OneReflectionColors.SageDeep
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.WaterDrop,
                                contentDescription = "Hydration",
                                tint = if (oledMode) OneReflectionColors.SageLight else OneReflectionColors.SageDeep,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "HYDRATION SANCTUARY",
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                letterSpacing = 0.8.sp,
                                color = if (oledMode) OneReflectionColors.SageLight else OneReflectionColors.SageDeep
                            )
                        }
                        Text(
                            text = "Steady & Nourished",
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = if (oledMode) Color.White else OneReflectionColors.InkPrimary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                modifier = Modifier
                                    .testTag("habits_add_250ml_button")
                                    .clip(CircleShape)
                                    .clickable { onAddHydration(0.25) },
                                shape = CircleShape,
                                color = if (oledMode) OneReflectionColors.SageLight else OneReflectionColors.SageDeep
                            ) {
                                Text(
                                    text = "+250ml",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (oledMode) OneReflectionColors.InkPrimary else Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                            Surface(
                                modifier = Modifier
                                    .testTag("habits_add_500ml_button")
                                    .clip(CircleShape)
                                    .clickable { onAddHydration(0.5) },
                                shape = CircleShape,
                                color = if (oledMode) OneReflectionColors.OledSurfaceElevated else Color.White,
                                border = BorderStroke(1.dp, OneReflectionColors.SageDeep.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = "+500ml",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (oledMode) OneReflectionColors.SageLight else OneReflectionColors.SageDeep,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Gratitude Voice Journal Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "VOICE GRATITUDE LOG",
                                fontFamily = MonoFontFamily,
                                fontSize = 10.sp,
                                letterSpacing = 0.9.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Morning Reflection",
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .testTag("habits_record_gratitude")
                                .clip(CircleShape)
                                .clickable { onOpenVoiceStudio() },
                            shape = CircleShape,
                            color = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.PeachMist
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Filled.Mic, contentDescription = "Record", tint = OneReflectionColors.Terracotta, modifier = Modifier.size(14.dp))
                                Text("Record", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = if (oledMode) OneReflectionColors.TerracottaLight else OneReflectionColors.Terracotta)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.SurfaceWarm
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
                                    .size(38.dp)
                                    .testTag("habits_play_journal")
                                    .clip(CircleShape)
                                    .background(OneReflectionColors.Terracotta)
                                    .clickable {
                                        if (playingJournal) {
                                            playingJournal = false
                                            onStopChime()
                                        } else {
                                            playingJournal = true
                                            onPlayChime(ChimeType.RIVER)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (playingJournal) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = "Play reflection",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            WaveformVisualizer(
                                isPlaying = playingJournal,
                                barCount = 18,
                                height = 24.dp,
                                mixedColors = true,
                                modifier = Modifier.weight(1f)
                            )
                            Text("0:42", fontFamily = MonoFontFamily, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Text(
                        text = "\"Grateful for quiet espresso mornings and finishing the sanctuary wireframes without rushing.\"",
                        fontFamily = DisplayFontFamily,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showAddHabitDialog) {
        AddMindfulHabitDialog(
            oledMode = oledMode,
            onDismiss = { showAddHabitDialog = false },
            onCreate = { title, subtitle, unit, target, step, tone ->
                onAddHabit(title, subtitle, unit, target, step, tone)
                showAddHabitDialog = false
            }
        )
    }
}

@Composable
private fun AddMindfulHabitDialog(
    oledMode: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String, String, Int, Int, HabitTone) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("min") }
    var targetText by remember { mutableStateOf("20") }
    var stepText by remember { mutableStateOf("5") }
    var selectedTone by remember { mutableStateOf(HabitTone.TERRACOTTA) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Mindful Ritual",
                fontFamily = DisplayFontFamily,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Ritual name") },
                    placeholder = { Text("e.g., Sunset Walk") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_habit_title_input")
                )
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Intention") },
                    placeholder = { Text("Unplug and breathe outdoors") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetText,
                        onValueChange = { targetText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Goal") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stepText,
                        onValueChange = { stepText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Step") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "RING TONE",
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HabitTone.entries.forEach { tone ->
                        val pal = habitTonePalette(tone, oledMode)
                        val active = selectedTone == tone
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("add_habit_tone_${tone.name.lowercase()}")
                                .clip(CircleShape)
                                .background(pal.primary)
                                .border(
                                    width = if (active) 2.5.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    shape = CircleShape
                                )
                                .clickable { selectedTone = tone }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goal = targetText.toIntOrNull()?.coerceAtLeast(1) ?: 20
                    val step = stepText.toIntOrNull()?.coerceIn(1, goal) ?: 5
                    if (title.isNotBlank()) {
                        onCreate(title, subtitle, unit, goal, step, selectedTone)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OneReflectionColors.Terracotta),
                modifier = Modifier.testTag("add_habit_confirm_button")
            ) {
                Text("Create Ritual", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
