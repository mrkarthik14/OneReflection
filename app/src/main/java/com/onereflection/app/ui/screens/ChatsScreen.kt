package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.model.*
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.WaveformVisualizer
import com.onereflection.app.ui.theme.BrandColors
import com.onereflection.app.viewmodel.OneReflectionUiState
import kotlinx.coroutines.delay

@Composable
fun ChatsScreen(
    state: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onBack: () -> Unit,
    onOpenVoiceStudio: () -> Unit
) {
    BackHandler(onBack = onBack)

    val oledMode = state.oledMode
    var searchQuery by remember { mutableStateOf("") }
    var activeFilter by remember { mutableStateOf("all") }
    var isPlayingDispatch by remember { mutableStateOf(false) }
    var speedMultiplier by remember { mutableStateOf("1.5x") }
    var sourdoughChecked by remember { mutableStateOf(false) }
    var walkieActive by remember { mutableStateOf(false) }

    LaunchedEffect(isPlayingDispatch) {
        if (isPlayingDispatch) {
            delay(4200L)
            isPlayingDispatch = false
        }
    }

    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark
    val textSecondary = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
    val cardBorder = if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.05f)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header & Search
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BrandColors.ButterYellow)
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "IN SYNC • SHARED VOICES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandColors.ButterYellowText
                        )
                    }
                    Surface(
                        onClick = { ChimePlayer.play(ChimeType.NORDIC) },
                        shape = CircleShape,
                        color = BrandColors.PinkFixed,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.GraphicEq, contentDescription = "Acoustic ping", tint = BrandColors.PrimaryPlum, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Column {
                    Text("Chats & Circles", style = MaterialTheme.typography.headlineLarge, color = textPrimary)
                    Text("Gentle whispers, quick pings, and shared daily rhythms.", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search voice notes, circles, rituals...", style = MaterialTheme.typography.bodySmall) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    shape = CircleShape,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("all" to "All Circles", "memos" to "Voice Memos", "tasks" to "Shared Tasks", "family" to "Family").forEach { (id, label) ->
                        val sel = activeFilter == id
                        Surface(
                            onClick = {
                                ChimePlayer.play(ChimeType.CLICK)
                                activeFilter = id
                            },
                            shape = CircleShape,
                            color = if (sel) BrandColors.OnSurfaceDark else if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (sel) Color.White else textSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Active Circle Presence
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Your Circle", style = MaterialTheme.typography.titleMedium, color = textPrimary)
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(BrandColors.ButterYellow)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("5 online", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.ButterYellowText)
                        }
                    }
                    Text(
                        text = "Manage ›",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum,
                        modifier = Modifier.clickable { onNavigate(ScreenId.PEOPLE) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onNavigate(ScreenId.PEOPLE) }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (oledMode) BrandColors.OledCard else Color.White,
                            border = BorderStroke(1.dp, cardBorder),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PersonAdd, contentDescription = "Invite", tint = textPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Invite", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                    }
                    CircleAvatarBadge("Maya", "Awake", AVATAR_MAYA, "MR", BrandColors.CandyPink, textPrimary, textSecondary)
                    CircleAvatarBadge("Leo", "Sprint", AVATAR_LEO, "LC", BrandColors.LavenderContainer, textPrimary, textSecondary)
                    CircleAvatarBadge("Sofia", "Listening", AVATAR_SOFIA, "SM", BrandColors.ButterYellow, textPrimary, textSecondary)
                }
            }
        }

        // 3. Featured Maya R. Candy Pink Bento Card
        if (searchQuery.isBlank() || "maya sourdough morning voice".contains(searchQuery.lowercase())) {
            item {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = BrandColors.CandyPink,
                    border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
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
                                AvatarImage(AVATAR_MAYA, "MR", "Maya R.", 44.dp)
                                Column {
                                    Text("Maya R.", style = MaterialTheme.typography.titleMedium, color = BrandColors.OnSurfaceDark)
                                    Text("Partner • In Deep Flow", style = MaterialTheme.typography.bodySmall, color = BrandColors.CandyPinkDeep)
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(BrandColors.OnSurfaceDark)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text("1 new memo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        // Voice Note Waveform Player
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.92f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = BrandColors.PrimaryPlum, modifier = Modifier.size(16.dp))
                                        Text("Morning Voice Dispatch", style = MaterialTheme.typography.labelMedium, color = BrandColors.OnSurfaceDark)
                                    }
                                    Text("0:38 / 1:12", style = MaterialTheme.typography.bodySmall, color = BrandColors.OnSurfaceVariant)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        onClick = {
                                            if (!isPlayingDispatch) {
                                                ChimePlayer.play(ChimeType.TIBETAN)
                                                isPlayingDispatch = true
                                            } else {
                                                ChimePlayer.stop()
                                                isPlayingDispatch = false
                                            }
                                        },
                                        shape = CircleShape,
                                        color = if (isPlayingDispatch) BrandColors.PrimaryPlum else BrandColors.OnSurfaceDark,
                                        modifier = Modifier.size(40.dp).testTag("chats_maya_play")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isPlayingDispatch) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = "Play voice dispatch",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    WaveformVisualizer(
                                        isPlaying = isPlayingDispatch,
                                        barCount = 18,
                                        height = 30.dp,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Surface(
                                        onClick = {
                                            ChimePlayer.play(ChimeType.CLICK)
                                            speedMultiplier = when (speedMultiplier) {
                                                "1.0x" -> "1.5x"
                                                "1.5x" -> "2.0x"
                                                else -> "1.0x"
                                            }
                                        },
                                        shape = CircleShape,
                                        color = BrandColors.SurfaceContainerHigh
                                    ) {
                                        Text(
                                            text = speedMultiplier,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandColors.OnSurfaceDark,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = "“Sending gentle morning energy! I also attached our dinner sourdough grocery to-do.”",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontStyle = FontStyle.Italic,
                                    color = BrandColors.OnSurfaceDark
                                )
                            }
                        }

                        // Embedded Shared Task Chip
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color.White.copy(alpha = 0.95f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
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
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(BrandColors.ButterYellow),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = BrandColors.ButterYellowText, modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text(
                                            text = "Pick up artisan sourdough",
                                            style = MaterialTheme.typography.labelMedium,
                                            textDecoration = if (sourdoughChecked) TextDecoration.LineThrough else TextDecoration.None,
                                            color = if (sourdoughChecked) BrandColors.OnSurfaceVariant else BrandColors.OnSurfaceDark
                                        )
                                        Text("Shared To-Do • 2 items remaining", fontSize = 10.sp, color = BrandColors.OnSurfaceVariant)
                                    }
                                }

                                Surface(
                                    onClick = {
                                        ChimePlayer.play(ChimeType.CLICK)
                                        sourdoughChecked = !sourdoughChecked
                                    },
                                    shape = CircleShape,
                                    color = if (sourdoughChecked) BrandColors.OnSurfaceDark else BrandColors.SurfaceContainerHigh,
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Toggle sourdough task",
                                            tint = if (sourdoughChecked) Color.White else BrandColors.OnSurfaceDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Delivered 2m ago", style = MaterialTheme.typography.bodySmall, color = BrandColors.CandyPinkDeep)
                            Button(
                                onClick = onOpenVoiceStudio,
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("Reply", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }

        // 4. Recent Channels
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Recent Channels", style = MaterialTheme.typography.titleMedium, color = textPrimary)
                    Text("UPDATED NOW", style = MaterialTheme.typography.labelSmall, color = textSecondary)
                }

                // Studio Dev Sync (Lavender)
                if (activeFilter == "all" || activeFilter == "memos" || activeFilter == "tasks") {
                    ChannelCard(
                        title = "Studio Dev Sync",
                        subtitle = "Sprint 14 Sync Hub",
                        time = "11:20 AM",
                        message = "Leo: “Compose migration looks solid on staging...”",
                        badge = "0:45s",
                        bg = BrandColors.LavenderSoft,
                        textColor = BrandColors.LavenderText,
                        subColor = BrandColors.LavenderMuted,
                        onClick = { ChimePlayer.play(ChimeType.NORDIC) }
                    )
                }

                // Sofia Miller (Butter Yellow)
                if (activeFilter == "all" || activeFilter == "tasks") {
                    ChannelCard(
                        title = "Sofia Miller",
                        subtitle = "Mindful Rhythm Buddy",
                        time = "Yesterday",
                        message = "Shared a completed ritual: Hydration Goal (+500ml)",
                        badge = "7d Streak",
                        bg = BrandColors.ButterYellow,
                        textColor = BrandColors.OnSurfaceDark,
                        subColor = BrandColors.ButterYellowText,
                        onClick = { ChimePlayer.play(ChimeType.WATER) }
                    )
                }

                // Mom & Family Hub
                if (activeFilter == "all" || activeFilter == "family") {
                    ChannelCard(
                        title = "Mom & Family Hub",
                        subtitle = "Scheduled Morning Routine",
                        time = "May 14",
                        message = "Scheduled Voice Wake-up set for Sunday 08:30 AM",
                        badge = "Ready",
                        bg = if (oledMode) BrandColors.OledCard else Color.White,
                        textColor = textPrimary,
                        subColor = textSecondary,
                        onClick = { ChimePlayer.play(ChimeType.SOLFEGGIO) }
                    )
                }
            }
        }

        // 5. Hold to Talk Walkie-Talkie Dock
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh,
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(CircleShape)
                                .background(if (walkieActive) BrandColors.PrimaryPlum else BrandColors.OnSurfaceDark)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            ChimePlayer.play(ChimeType.CLICK)
                                            walkieActive = true
                                            tryAwaitRelease()
                                            ChimePlayer.play(ChimeType.SOLFEGGIO)
                                            walkieActive = false
                                        }
                                    )
                                }
                                .testTag("chats_walkie_talkie_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(26.dp).clip(CircleShape).background(BrandColors.CandyPink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = null, tint = BrandColors.OnSurfaceDark, modifier = Modifier.size(15.dp))
                                }
                                Text(
                                    text = if (walkieActive) "Transmitting Live..." else "Hold to Talk",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            onClick = { onNavigate(ScreenId.PEOPLE) },
                            shape = CircleShape,
                            color = if (oledMode) Color(0xFF27272A) else Color.White,
                            modifier = Modifier.height(50.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.AddCircle, contentDescription = null, tint = textPrimary, modifier = Modifier.size(16.dp))
                                Text("New Circle", style = MaterialTheme.typography.labelMedium, color = textPrimary)
                            }
                        }
                    }
                }
                Text(
                    text = "End-to-end encrypted audio bubbles • Keep audio notes mindful & sweet",
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun CircleAvatarBadge(
    name: String,
    status: String,
    url: String,
    initials: String,
    ringColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AvatarImage(url, initials, name, 56.dp, ringColor = ringColor, ringWidth = 2.5.dp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, style = MaterialTheme.typography.labelMedium, color = textPrimary)
        Text(status, fontSize = 10.sp, color = textSecondary)
    }
}

@Composable
private fun ChannelCard(
    title: String,
    subtitle: String,
    time: String,
    message: String,
    badge: String,
    bg: Color,
    textColor: Color,
    subColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = bg,
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.06f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = textColor)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = subColor)
                }
                Text(time, fontSize = 10.sp, color = subColor)
            }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White.copy(alpha = 0.85f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandColors.OnSurfaceDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BrandColors.PinkFixed)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.PrimaryPlum)
                    }
                }
            }
        }
    }
}
