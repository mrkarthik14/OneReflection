package com.onereflection.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.model.AVATAR_ELENA
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.theme.BrandColors
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun AvatarImage(
    url: String,
    initials: String,
    contentDescription: String,
    size: Dp,
    modifier: Modifier = Modifier,
    ringColor: Color = Color.White,
    ringWidth: Dp = 2.dp,
    fallbackBg: Color = BrandColors.PinkFixed
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(fallbackBg)
            .border(ringWidth, ringColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(CircleShape),
            loading = {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.labelLarge,
                    color = BrandColors.OnSurfaceDark
                )
            },
            error = {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.labelLarge,
                    color = BrandColors.OnSurfaceDark
                )
            }
        )
    }
}

enum class WaveformColorStyle {
    PRIMARY, SECONDARY, TERTIARY, WHITE, MIXED
}

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    barCount: Int = 18,
    height: Dp = 32.dp,
    style: WaveformColorStyle = WaveformColorStyle.PRIMARY,
    modifier: Modifier = Modifier
) {
    var phase by remember { mutableIntStateOf(0) }
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(110L)
            phase = (phase + 1) % 100
        }
    }

    val baseHeights = remember {
        listOf(0.28f, 0.55f, 0.85f, 0.42f, 0.95f, 0.68f, 0.75f, 0.48f, 0.62f, 0.88f, 0.52f, 0.35f, 0.70f, 0.45f, 0.80f, 0.60f)
    }

    Row(
        modifier = modifier
            .height(height)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val base = baseHeights[i % baseHeights.size]
            val fraction = if (isPlaying) {
                (base + sin(phase * 0.42 + i) * 0.32).toFloat().coerceIn(0.20f, 1.0f)
            } else {
                base
            }
            val barColor = when (style) {
                WaveformColorStyle.WHITE -> Color.White
                WaveformColorStyle.SECONDARY -> BrandColors.ButterYellowText
                WaveformColorStyle.TERTIARY -> BrandColors.LavenderMuted
                WaveformColorStyle.MIXED -> when (i % 3) {
                    0 -> BrandColors.PrimaryPlum
                    1 -> BrandColors.ButterYellow
                    else -> BrandColors.OnSurfaceDark
                }
                WaveformColorStyle.PRIMARY -> if (i < barCount / 3) BrandColors.PrimaryPlum else BrandColors.OutlineVariant
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(fraction)
                    .clip(CircleShape)
                    .background(barColor)
            )
        }
    }
}

@Composable
fun OneReflectionSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkedTrackColor: Color = BrandColors.OnSurfaceDark,
    uncheckedTrackColor: Color = Color(0xFFB5B2B2),
    thumbColor: Color = Color.White,
    showIcon: Boolean = false
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 2.dp,
        animationSpec = spring(stiffness = 500f),
        label = "switch_offset"
    )
    Box(
        modifier = modifier
            .width(48.dp)
            .height(28.dp)
            .clip(CircleShape)
            .background(if (checked) checkedTrackColor else uncheckedTrackColor)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(24.dp)
                .clip(CircleShape)
                .background(thumbColor),
            contentAlignment = Alignment.Center
        ) {
            if (showIcon) {
                Icon(
                    imageVector = if (checked) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = BrandColors.OnSurfaceDark,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Composable
fun OneReflectionHeader(
    currentScreen: ScreenId,
    oledMode: Boolean,
    onNavigate: (ScreenId) -> Unit,
    onToggleOled: () -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onOpenBedtimeSync: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    var notificationsOpen by remember { mutableStateOf(false) }

    val headerBg = if (oledMode) BrandColors.OledBlack.copy(alpha = 0.96f) else BrandColors.SurfaceWarm.copy(alpha = 0.94f)
    val borderColor = if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.05f)
    val textColor = if (oledMode) Color.White else BrandColors.OnSurfaceDark

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(headerBg)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        ChimePlayer.play(ChimeType.CLICK)
                        menuOpen = !menuOpen
                        notificationsOpen = false
                    }
                    .padding(vertical = 6.dp, horizontal = 4.dp)
                    .testTag("header_brand_switcher"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(BrandColors.CandyPink, BrandColors.ButterYellow)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BrandColors.OnSurfaceDark,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "OneReflection",
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = if (menuOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Screen Navigator",
                            tint = textColor.copy(alpha = 0.65f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = currentScreen.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = onToggleOled,
                    shape = CircleShape,
                    color = if (oledMode) Color(0xFF313030) else BrandColors.OnSurfaceDark,
                    border = if (oledMode) BorderStroke(1.dp, BrandColors.ButterYellow.copy(alpha = 0.45f)) else null,
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("header_oled_toggle")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = if (oledMode) Icons.Default.WbSunny else Icons.Default.DarkMode,
                            contentDescription = "Toggle OLED Pure Black Mode",
                            tint = if (oledMode) BrandColors.ButterYellow else Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "OLED",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (oledMode) BrandColors.ButterYellow else Color.White
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(BrandColors.ButterYellow)
                        )
                    }
                }

                Box(contentAlignment = Alignment.TopEnd) {
                    IconButton(
                        onClick = {
                            ChimePlayer.play(ChimeType.CLICK)
                            notificationsOpen = !notificationsOpen
                            menuOpen = false
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("header_notifications_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = if (oledMode) Color(0xFFD4D4D8) else BrandColors.OnSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp, end = 8.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(BrandColors.PrimaryPlum)
                    )
                }

                AvatarImage(
                    url = AVATAR_ELENA,
                    initials = "EV",
                    contentDescription = "Elena Vance Profile",
                    size = 36.dp,
                    ringColor = if (currentScreen == ScreenId.PROFILE) BrandColors.CandyPink else Color.Transparent,
                    modifier = Modifier
                        .clickable {
                            ChimePlayer.play(ChimeType.CLICK)
                            menuOpen = false
                            notificationsOpen = false
                            onNavigate(ScreenId.PROFILE)
                        }
                        .testTag("header_profile_avatar")
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = borderColor)

        AnimatedVisibility(
            visible = notificationsOpen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(24.dp),
                color = if (oledMode) BrandColors.OledCard else Color.White,
                border = BorderStroke(1.dp, borderColor),
                shadowElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MINDFUL NOTIFICATIONS",
                            style = MaterialTheme.typography.labelMedium,
                            color = textColor
                        )
                        Text(
                            text = "Live",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandColors.PrimaryPlum
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrandColors.CandyPink.copy(alpha = 0.22f))
                            .clickable {
                                notificationsOpen = false
                                onNavigate(ScreenId.ALARMS)
                            }
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = BrandColors.PrimaryPlum,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text("Maya sent a wake-up memo", style = MaterialTheme.typography.labelMedium, color = textColor)
                            Text("\"Remember to drink lemon water...\"", style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.65f))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrandColors.ButterYellow.copy(alpha = 0.24f))
                            .clickable {
                                notificationsOpen = false
                                onNavigate(ScreenId.HABITS)
                            }
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = BrandColors.ButterYellowText,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text("Sofia completed Hydration", style = MaterialTheme.typography.labelMedium, color = textColor)
                            Text("7-Day Streak achieved!", style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.65f))
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = menuOpen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (oledMode) Color(0xFF0A0A0C) else Color.White,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXPLORE ONEREFLECTION SCREENS",
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.6f)
                        )
                        IconButton(
                            onClick = { menuOpen = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close navigator",
                                tint = textColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    val allScreens = ScreenId.entries
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        allScreens.chunked(2).forEach { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                pair.forEach { screen ->
                                    val isCurrent = screen == currentScreen
                                    Surface(
                                        onClick = {
                                            ChimePlayer.play(ChimeType.CLICK)
                                            menuOpen = false
                                            onNavigate(screen)
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isCurrent) {
                                            BrandColors.OnSurfaceDark
                                        } else if (oledMode) {
                                            BrandColors.OledCard
                                        } else {
                                            BrandColors.SurfaceContainerLow
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("drawer_nav_${screen.name.lowercase()}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = screenIcon(screen),
                                                contentDescription = null,
                                                tint = if (isCurrent) Color.White else textColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = screen.navLabel,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (isCurrent) Color.White else textColor,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (screen.badge != null) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(CircleShape)
                                                        .background(BrandColors.CandyPink)
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = screen.badge,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = BrandColors.OnSurfaceDark
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                ChimePlayer.play(ChimeType.TIBETAN)
                                menuOpen = false
                                onOpenVoiceStudio()
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandColors.CandyPink,
                                contentColor = BrandColors.OnSurfaceDark
                            ),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Voice Studio", style = MaterialTheme.typography.labelMedium)
                        }
                        Button(
                            onClick = {
                                ChimePlayer.play(ChimeType.CLICK)
                                menuOpen = false
                                onOpenBedtimeSync()
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandColors.ButterYellow,
                                contentColor = BrandColors.ButterYellowText
                            ),
                            modifier = Modifier.weight(1f).height(42.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bedtime Rules", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

private fun screenIcon(screen: ScreenId): ImageVector = when (screen) {
    ScreenId.HOME -> Icons.Default.Home
    ScreenId.ALARMS -> Icons.Default.Alarm
    ScreenId.TASKS -> Icons.Default.CheckCircle
    ScreenId.CHATS -> Icons.Default.ChatBubble
    ScreenId.REMINDERS -> Icons.Default.CalendarMonth
    ScreenId.HABITS -> Icons.Default.Eco
    ScreenId.QUIET_BREAKTHROUGH -> Icons.Default.Bedtime
    ScreenId.PEOPLE -> Icons.Default.Group
    ScreenId.PROFILE -> Icons.Default.Person
}

@Composable
fun OneReflectionBottomNav(
    currentScreen: ScreenId,
    oledMode: Boolean,
    onNavigate: (ScreenId) -> Unit,
    onOpenQuickCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 460.dp)
                .fillMaxWidth()
                .height(66.dp)
                .shadow(14.dp, CircleShape),
            shape = CircleShape,
            color = if (oledMode) BrandColors.OledSurface else BrandColors.OnSurfaceDark,
            border = BorderStroke(
                1.dp,
                if (oledMode) BrandColors.OledBorder else Color.White.copy(alpha = 0.10f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BottomNavItem(
                    label = "Home",
                    icon = if (currentScreen == ScreenId.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    selected = currentScreen == ScreenId.HOME,
                    onClick = {
                        ChimePlayer.play(ChimeType.CLICK)
                        onNavigate(ScreenId.HOME)
                    },
                    tag = "nav_home"
                )
                BottomNavItem(
                    label = "Alarms",
                    icon = if (currentScreen == ScreenId.ALARMS) Icons.Filled.Schedule else Icons.Outlined.Schedule,
                    selected = currentScreen == ScreenId.ALARMS,
                    onClick = {
                        ChimePlayer.play(ChimeType.CLICK)
                        onNavigate(ScreenId.ALARMS)
                    },
                    tag = "nav_alarms"
                )

                Spacer(modifier = Modifier.width(58.dp))

                BottomNavItem(
                    label = "Tasks",
                    icon = if (currentScreen == ScreenId.TASKS) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    selected = currentScreen == ScreenId.TASKS,
                    onClick = {
                        ChimePlayer.play(ChimeType.CLICK)
                        onNavigate(ScreenId.TASKS)
                    },
                    tag = "nav_tasks"
                )
                BottomNavItem(
                    label = "Chats",
                    icon = if (currentScreen == ScreenId.CHATS) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                    selected = currentScreen == ScreenId.CHATS,
                    onClick = {
                        ChimePlayer.play(ChimeType.CLICK)
                        onNavigate(ScreenId.CHATS)
                    },
                    tag = "nav_chats"
                )
                BottomNavItem(
                    label = "Circle",
                    icon = if (currentScreen == ScreenId.PEOPLE) Icons.Filled.Group else Icons.Outlined.Group,
                    selected = currentScreen == ScreenId.PEOPLE,
                    onClick = {
                        ChimePlayer.play(ChimeType.CLICK)
                        onNavigate(ScreenId.PEOPLE)
                    },
                    tag = "nav_people"
                )
            }
        }

        Surface(
            onClick = {
                ChimePlayer.play(ChimeType.TIBETAN)
                onOpenQuickCreate()
            },
            shape = CircleShape,
            color = BrandColors.CandyPink,
            shadowElevation = 10.dp,
            modifier = Modifier
                .offset(y = (-20).dp)
                .size(56.dp)
                .testTag("fab_voice_studio")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create new voice entry or task",
                    tint = BrandColors.OnSurfaceDark,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        modifier = Modifier
            .widthIn(min = 48.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color.White else BrandColors.SurfaceContainerHighest.copy(alpha = 0.6f),
            modifier = Modifier.size(21.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else BrandColors.SurfaceContainerHighest.copy(alpha = 0.6f)
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(BrandColors.CandyPink)
            )
        }
    }
}
