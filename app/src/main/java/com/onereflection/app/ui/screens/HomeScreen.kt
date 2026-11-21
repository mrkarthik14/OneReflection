package com.onereflection.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HomeScreen(
    state: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onIncrementHydration: (Double) -> Unit
) {
    val oledMode = state.oledMode
    var alarmActive by remember { mutableStateOf(true) }
    var isPlayingVoice by remember { mutableStateOf(false) }
    var activeDay by remember { mutableIntStateOf(15) }

    LaunchedEffect(isPlayingVoice) {
        if (isPlayingVoice) {
            delay(3600L)
            isPlayingVoice = false
        }
    }

    val days = remember {
        listOf("MO" to 13, "TU" to 14, "WE" to 15, "TH" to 16, "FR" to 17, "SA" to 18, "SU" to 19)
    }

    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark
    val textSecondary = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
    val cardBg = if (oledMode) BrandColors.OledCard else Color.White
    val subtleCardBg = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow
    val cardBorder = if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.04f)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Greeting & Micro-badge Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Wednesday, May 15",
                            style = MaterialTheme.typography.bodySmall,
                            color = textSecondary
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(BrandColors.ButterYellow)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "In Flow",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandColors.ButterYellowText
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Hey Taigo, ready today?",
                        style = MaterialTheme.typography.headlineMedium,
                        color = textPrimary
                    )
                }

                Surface(
                    onClick = {
                        ChimePlayer.play(ChimeType.TIBETAN)
                        onOpenVoiceStudio()
                    },
                    shape = CircleShape,
                    color = BrandColors.PinkFixed,
                    modifier = Modifier
                        .size(44.dp)
                        .rotate(6f)
                        .testTag("home_streak_voice_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Open Voice Studio",
                            tint = BrandColors.PrimaryPlum,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // 2. Calendar Day-of-Week Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                days.forEach { (label, num) ->
                    val isActive = num == activeDay
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                when {
                                    isActive -> BrandColors.OnSurfaceDark
                                    oledMode -> BrandColors.OledCard
                                    else -> BrandColors.SurfaceContainerLow
                                }
                            )
                            .clickable {
                                ChimePlayer.play(ChimeType.CLICK)
                                activeDay = num
                            }
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.White.copy(alpha = 0.8f) else textSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = num.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isActive) Color.White else textPrimary
                        )
                        if (isActive) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(BrandColors.ButterYellow)
                            )
                        }
                    }
                }
            }
        }

        // 3. Hero Sync Score Bento Card
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = cardBg,
                border = BorderStroke(1.dp, cardBorder),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(BrandColors.PinkFixed)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = BrandColors.DeepPlumText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "daily sync score",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = BrandColors.DeepPlumText
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "8.9",
                                    style = MaterialTheme.typography.displayLarge,
                                    color = textPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "/ 10",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = textSecondary,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                            }

                            Text(
                                text = "4 of 6 focus moments completed today",
                                style = MaterialTheme.typography.bodySmall,
                                color = textSecondary
                            )
                        }

                        // Starburst Sticker Badge
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .rotate(-6f)
                                .clip(CircleShape)
                                .background(BrandColors.ButterYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(48.dp)) {
                                val path = Path()
                                val cx = size.width / 2f
                                val cy = size.height / 2f
                                val outerR = size.minDimension / 2f
                                val innerR = outerR * 0.56f
                                val points = 10
                                for (i in 0 until points * 2) {
                                    val r = if (i % 2 == 0) outerR else innerR
                                    val angle = i * PI / points - PI / 2
                                    val x = cx + (r * cos(angle)).toFloat()
                                    val y = cy + (r * sin(angle)).toFloat()
                                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                }
                                path.close()
                                drawPath(path, color = BrandColors.ButterYellowText.copy(alpha = 0.25f))
                            }
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Sync Star Badge",
                                tint = BrandColors.OnSurfaceDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 0 until 6) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            i < 4 -> BrandColors.PrimaryPlum
                                            oledMode -> Color(0xFF27272A)
                                            else -> BrandColors.OutlineVariant
                                        }
                                    )
                            )
                        }
                    }
                }
            }
        }

        // 4. Featured Voice Alarm Card
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AvatarImage(
                                url = AVATAR_MAYA,
                                initials = "MR",
                                contentDescription = "Maya R.",
                                size = 44.dp
                            )
                            Column {
                                Text(
                                    text = "Tomorrow, 07:30 AM",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = textPrimary
                                )
                                Text(
                                    text = "Voice Alarm by Maya R.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum
                                )
                            }
                        }

                        OneReflectionSwitch(
                            checked = alarmActive,
                            onCheckedChange = {
                                ChimePlayer.play(ChimeType.CLICK)
                                alarmActive = it
                            },
                            modifier = Modifier.testTag("home_alarm_toggle")
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (oledMode) BrandColors.OledBlack else Color.White,
                        border = BorderStroke(1.dp, cardBorder)
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
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "Recorded snippet",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum
                                    )
                                }
                                Text(
                                    text = "0:24",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = textSecondary
                                )
                            }

                            Text(
                                text = "\"Rise and shine sunny! Remember to drink your lemon water first thing.\"",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic,
                                color = textPrimary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    onClick = {
                                        if (!isPlayingVoice) {
                                            ChimePlayer.play(ChimeType.TIBETAN)
                                            isPlayingVoice = true
                                        } else {
                                            ChimePlayer.stop()
                                            isPlayingVoice = false
                                        }
                                    },
                                    shape = CircleShape,
                                    color = if (isPlayingVoice) BrandColors.PrimaryPlum else BrandColors.OnSurfaceDark,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .testTag("home_voice_play_btn")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isPlayingVoice) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play or pause voice note",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                WaveformVisualizer(
                                    isPlaying = isPlayingVoice,
                                    barCount = 20,
                                    height = 30.dp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Today's Highlights 2x2 Bento Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Highlights",
                        style = MaterialTheme.typography.titleLarge,
                        color = textPrimary
                    )
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                ChimePlayer.play(ChimeType.CLICK)
                                onNavigate(ScreenId.REMINDERS)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "See all",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tile 1: Butter Yellow Wake-up Note
                    Surface(
                        onClick = {
                            ChimePlayer.play(ChimeType.CLICK)
                            onNavigate(ScreenId.ALARMS)
                        },
                        shape = RoundedCornerShape(26.dp),
                        color = BrandColors.ButterYellow,
                        modifier = Modifier.weight(1f).height(154.dp).testTag("home_tile_wakeup")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.9f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("7:30 AM", style = MaterialTheme.typography.labelMedium, color = BrandColors.OnSurfaceDark)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(BrandColors.OnSurfaceDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                }
                            }
                            Column {
                                Text("Wake-up Note", style = MaterialTheme.typography.titleLarge, color = BrandColors.OnSurfaceDark)
                                Text("Maya left voice memo", style = MaterialTheme.typography.bodySmall, color = BrandColors.OnSurfaceDark.copy(alpha = 0.8f))
                            }
                        }
                    }

                    // Tile 2: Candy Pink Gratitude Card
                    Surface(
                        onClick = {
                            ChimePlayer.play(ChimeType.CLICK)
                            onNavigate(ScreenId.HABITS)
                        },
                        shape = RoundedCornerShape(26.dp),
                        color = BrandColors.CandyPink,
                        modifier = Modifier.weight(1f).height(154.dp).testTag("home_tile_gratitude")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.9f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("15 min", style = MaterialTheme.typography.labelMedium, color = BrandColors.OnSurfaceDark)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .rotate(-12f)
                                        .clip(CircleShape)
                                        .background(BrandColors.OnSurfaceDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }
                            Column {
                                Text("Gratitude", style = MaterialTheme.typography.titleLarge, color = BrandColors.OnSurfaceDark)
                                Text("3 moments pinned", style = MaterialTheme.typography.bodySmall, color = BrandColors.OnSurfaceDark.copy(alpha = 0.8f))
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tile 3: Daily Focus Tasks
                    Surface(
                        onClick = {
                            ChimePlayer.play(ChimeType.CLICK)
                            onNavigate(ScreenId.TASKS)
                        },
                        shape = RoundedCornerShape(26.dp),
                        color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh,
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier.weight(1f).height(154.dp).testTag("home_tile_tasks")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (oledMode) Color(0xFF27272A) else Color.White)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${state.completedTasks.size} / ${state.tasks.size + state.completedTasks.size} Done",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = textPrimary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Checklist,
                                    contentDescription = null,
                                    tint = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text("Daily Focus", style = MaterialTheme.typography.titleLarge, color = textPrimary)
                                Text("Next: Evening walk", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                            }
                        }
                    }

                    // Tile 4: Soft Lavender Hydration quick-log card
                    val cups = (state.hydrationLiters / 0.25).toInt()
                    Surface(
                        onClick = { onIncrementHydration(0.25) },
                        shape = RoundedCornerShape(26.dp),
                        color = BrandColors.LavenderSoft,
                        modifier = Modifier.weight(1f).height(154.dp).testTag("home_tile_hydration")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.9f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("$cups / 10 Cups", style = MaterialTheme.typography.labelMedium, color = BrandColors.LavenderText)
                                }
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = BrandColors.TertiarySlateBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text("Hydration", style = MaterialTheme.typography.titleLarge, color = BrandColors.LavenderText)
                                Text(
                                    text = "${state.hydrationLiters}L logged (+250ml)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BrandColors.LavenderMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Your People Strip
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your People",
                        style = MaterialTheme.typography.titleLarge,
                        color = textPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (oledMode) Color(0xFF27272A) else BrandColors.SurfaceContainerHigh)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text("3 online", style = MaterialTheme.typography.labelSmall, color = textSecondary)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HomePersonBubble("Maya", "Morning note", AVATAR_MAYA, "MR", BrandColors.CandyPink, textPrimary, textSecondary) {
                        onNavigate(ScreenId.CHATS)
                    }
                    HomePersonBubble("Leo", "Tasks done", AVATAR_LEO, "LC", BrandColors.LavenderContainer, textPrimary, textSecondary) {
                        onNavigate(ScreenId.CHATS)
                    }
                    HomePersonBubble("Sofia", "Awake", AVATAR_SOFIA, "SM", BrandColors.ButterYellow, textPrimary, textSecondary) {
                        onNavigate(ScreenId.CHATS)
                    }
                    Column(
                        modifier = Modifier
                            .width(72.dp)
                            .clickable {
                                ChimePlayer.play(ChimeType.CLICK)
                                onNavigate(ScreenId.PEOPLE)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Add friend",
                                tint = textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Add", style = MaterialTheme.typography.labelMedium, color = textPrimary)
                        Text("Connect", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                    }
                }
            }
        }

        // 7. Evening Reflection Quick Check-in
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = subtleCardBg,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(BrandColors.ButterYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = BrandColors.ButterYellowText,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text("Evening Reflection", style = MaterialTheme.typography.titleSmall, color = textPrimary)
                            Text("Ready at 8:00 PM", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                        }
                    }

                    Button(
                        onClick = {
                            ChimePlayer.play(ChimeType.CLICK)
                            onNavigate(ScreenId.QUIET_BREAKTHROUGH)
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (oledMode) Color(0xFF27272A) else Color.White,
                            contentColor = textPrimary
                        ),
                        modifier = Modifier.testTag("home_evening_preview_btn")
                    ) {
                        Text("Preview", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomePersonBubble(
    name: String,
    subtitle: String,
    avatarUrl: String,
    initials: String,
    ringColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable {
                ChimePlayer.play(ChimeType.CLICK)
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarImage(
            url = avatarUrl,
            initials = initials,
            contentDescription = name,
            size = 56.dp,
            ringColor = ringColor,
            ringWidth = 2.5.dp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(name, style = MaterialTheme.typography.labelMedium, color = textPrimary, maxLines = 1)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = textSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
