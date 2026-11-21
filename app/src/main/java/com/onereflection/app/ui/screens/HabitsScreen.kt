package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.components.WaveformVisualizer
import com.onereflection.app.ui.theme.BrandColors
import com.onereflection.app.viewmodel.OneReflectionUiState
import kotlinx.coroutines.delay

@Composable
fun HabitsScreen(
    state: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onBack: () -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onIncrementHydration: (Double) -> Unit
) {
    BackHandler(onBack = onBack)

    val oledMode = state.oledMode
    var isPlayingJournal by remember { mutableStateOf(false) }
    var sprintActive by remember { mutableStateOf(false) }
    var walkChecked by remember { mutableStateOf(false) }

    LaunchedEffect(isPlayingJournal) {
        if (isPlayingJournal) {
            delay(3600L)
            isPlayingJournal = false
        }
    }

    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark
    val textSecondary = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
    val cardBorder = if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.06f)

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
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Eco, contentDescription = null, tint = BrandColors.ButterYellowText, modifier = Modifier.size(14.dp))
                        Text("MINDFUL FLOW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.ButterYellowText)
                    }
                    Surface(
                        onClick = { onNavigate(ScreenId.REMINDERS) },
                        shape = CircleShape,
                        color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Habit calendar", tint = textPrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Column {
                    Text("Daily Rituals & Habits", style = MaterialTheme.typography.headlineMedium, color = textPrimary)
                    Text("Tiny moments of focus, gentle rhythm.", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                }
            }
        }

        // 2. Hero Today's Balance Card
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BrandColors.PinkFixed,
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.06f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TODAY'S BALANCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.CandyPinkDeep)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("6", style = MaterialTheme.typography.displayLarge, color = BrandColors.DeepPlumText)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("/ 8", style = MaterialTheme.typography.titleLarge, color = BrandColors.CandyPinkDeep, modifier = Modifier.padding(bottom = 4.dp))
                            }
                            Text("Habits Completed Today", style = MaterialTheme.typography.bodySmall, color = BrandColors.CandyPinkDeep)
                        }

                        Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val stroke = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round)
                                drawArc(
                                    color = BrandColors.CandyPink.copy(alpha = 0.45f),
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = stroke
                                )
                                drawArc(
                                    color = BrandColors.PrimaryPlum,
                                    startAngle = -90f,
                                    sweepAngle = 270f,
                                    useCenter = false,
                                    style = stroke
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("75%", style = MaterialTheme.typography.titleMedium, color = BrandColors.DeepPlumText)
                                Text("SCORE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandColors.CandyPinkDeep)
                            }
                        }
                    }

                    // 7-Day Bubble Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.92f))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val bubbles = listOf(
                            Triple("M", "✓", BrandColors.ButterYellow),
                            Triple("T", "✓", BrandColors.CandyPink),
                            Triple("W", "✓", BrandColors.LavenderSoft),
                            Triple("T", "✓", BrandColors.ButterYellow),
                            Triple("F", "★", BrandColors.OnSurfaceDark),
                            Triple("S", "·", BrandColors.SurfaceContainerHigh),
                            Triple("S", "·", BrandColors.SurfaceContainerHigh)
                        )
                        bubbles.forEachIndexed { idx, (day, symbol, bg) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(day, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (idx == 4) BrandColors.PrimaryPlum else BrandColors.OnSurfaceVariant)
                                Box(
                                    modifier = Modifier.size(30.dp).clip(CircleShape).background(bg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = symbol,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (idx == 4) Color.White else BrandColors.OnSurfaceDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Mindful Hydration Card (Butter Yellow)
        item {
            val pct = ((state.hydrationLiters / 2.5) * 100).toInt().coerceAtMost(100)
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = BrandColors.ButterYellow,
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.9f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null, tint = BrandColors.ButterYellowText)
                            }
                            Column {
                                Text("Mindful Hydration", style = MaterialTheme.typography.titleMedium, color = BrandColors.OnSurfaceDark)
                                Text("Shared ping with Sofia", style = MaterialTheme.typography.bodySmall, color = BrandColors.ButterYellowText)
                            }
                        }
                        Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.9f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Text("Goal $pct%", style = MaterialTheme.typography.labelSmall, color = BrandColors.OnSurfaceDark)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.88f))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("CURRENT VOLUME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.OnSurfaceVariant)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("${state.hydrationLiters}", style = MaterialTheme.typography.headlineLarge, color = BrandColors.OnSurfaceDark)
                                Text(" / 2.5 L", style = MaterialTheme.typography.bodySmall, color = BrandColors.OnSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { onIncrementHydration(0.25) },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("habits_add_250ml")
                            ) {
                                Text("+250ml", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { onIncrementHydration(0.50) },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("habits_add_500ml")
                            ) {
                                Text("+500ml", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 4. Gratitude & Voice Journal Card (Candy Pink)
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = BrandColors.PinkFixedDim,
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.9f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = BrandColors.PrimaryPlum)
                            }
                            Column {
                                Text("Gratitude & Voice Journal", style = MaterialTheme.typography.titleMedium, color = BrandColors.DeepPlumText)
                                Text("Completed at 08:15 AM", style = MaterialTheme.typography.bodySmall, color = BrandColors.CandyPinkDeep)
                            }
                        }
                        Box(modifier = Modifier.size(30.dp).clip(CircleShape).background(BrandColors.OnSurfaceDark), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color.White.copy(alpha = 0.92f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = {
                                if (!isPlayingJournal) {
                                    ChimePlayer.play(ChimeType.SOLFEGGIO)
                                    isPlayingJournal = true
                                } else {
                                    ChimePlayer.stop()
                                    isPlayingJournal = false
                                }
                            },
                            shape = CircleShape,
                            color = if (isPlayingJournal) BrandColors.PrimaryPlum else BrandColors.OnSurfaceDark,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPlayingJournal) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play reflection",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Morning Reflection #42", style = MaterialTheme.typography.labelMedium, color = BrandColors.OnSurfaceDark)
                            WaveformVisualizer(isPlaying = isPlayingJournal, barCount = 16, height = 20.dp)
                        }
                        Text("0:35s", style = MaterialTheme.typography.bodySmall, color = BrandColors.OnSurfaceVariant)
                    }
                }
            }
        }

        // 5. 45m Deep Work Sprint Card (Lavender Blue)
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = BrandColors.LavenderSoft,
                border = BorderStroke(1.dp, BrandColors.TertiarySlateBlue.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("45m Deep Work Sprint", style = MaterialTheme.typography.titleMedium, color = BrandColors.LavenderText)
                            Text("Session 2 of 3 today • Streak: 12d", style = MaterialTheme.typography.bodySmall, color = BrandColors.LavenderMuted)
                        }
                        Button(
                            onClick = {
                                ChimePlayer.play(ChimeType.CLICK)
                                sprintActive = !sprintActive
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (sprintActive) BrandColors.PrimaryPlum else BrandColors.OnSurfaceDark,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("habits_sprint_toggle")
                        ) {
                            Icon(if (sprintActive) Icons.Default.Pause else Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (sprintActive) "Active (45m)" else "Start 45m", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // 6. Evening Sunset Walk Card
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Evening Sunset Walk", style = MaterialTheme.typography.titleMedium, color = textPrimary)
                            Text("4,820 / 6,000 steps • Sunset at 7:12 PM", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                        }
                        Surface(
                            onClick = {
                                ChimePlayer.play(ChimeType.CLICK)
                                walkChecked = !walkChecked
                            },
                            shape = CircleShape,
                            color = if (walkChecked) BrandColors.OnSurfaceDark else if (oledMode) Color(0xFF27272A) else Color.White,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Toggle walk",
                                    tint = if (walkChecked) Color.White else textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    LinearProgressIndicator(
                        progress = { if (walkChecked) 1f else 0.80f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = BrandColors.PrimaryPlum,
                        trackColor = if (oledMode) Color(0xFF27272A) else Color.White
                    )
                }
            }
        }

        // 7. Weekly Momentum Bar Chart
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Weekly Momentum", style = MaterialTheme.typography.titleMedium, color = textPrimary)
                            Text("Playful rhythms, zero guilt.", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                        }
                        Text("Past 7 Days", style = MaterialTheme.typography.labelSmall, color = textSecondary)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (oledMode) BrandColors.OledBlack else Color.White)
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val bars = listOf(
                            Triple("M", 0.70f, BrandColors.ButterYellow),
                            Triple("T", 0.85f, BrandColors.CandyPink),
                            Triple("W", 0.60f, BrandColors.LavenderSoft),
                            Triple("T", 0.90f, BrandColors.YellowFixed),
                            Triple("F", 0.75f, BrandColors.PrimaryPlum),
                            Triple("S", 0.40f, BrandColors.OutlineVariant),
                            Triple("S", 0.30f, BrandColors.OutlineVariant)
                        )
                        bars.forEach { (day, frac, color) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .weight(1f),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(frac)
                                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                                            .background(color)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(day, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textSecondary)
                            }
                        }
                    }
                }
            }
        }

        // 8. Primary Bottom CTA
        item {
            Button(
                onClick = onOpenVoiceStudio,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Habit Ritual", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
