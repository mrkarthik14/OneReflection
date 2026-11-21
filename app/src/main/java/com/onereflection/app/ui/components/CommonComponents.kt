package com.onereflection.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.onereflection.app.R
import com.onereflection.app.model.AvatarUrls
import com.onereflection.app.model.InAppNotification
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors
import com.onereflection.app.ui.theme.SanctuaryEase
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun OneReflectionLogoMark(
    size: Dp = 38.dp,
    cornerRadius: Dp = 12.dp,
    showGoldRing: Boolean = true,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(OneReflectionColors.InkPrimary)
            .then(
                if (showGoldRing) {
                    Modifier.border(
                        width = 1.2.dp,
                        color = OneReflectionColors.GoldLuminous.copy(alpha = 0.55f),
                        shape = shape
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_onereflection_logo_1795264043993),
            contentDescription = stringResource(R.string.app_name),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(size)
        )
    }
}

@Composable
fun AvatarImage(
    url: String?,
    fallbackInitials: String,
    contentDescription: String,
    size: Dp = 44.dp,
    ringColor: Color? = null,
    ringWidth: Dp = 2.dp,
    fallbackBg: Color = OneReflectionColors.PeachMist,
    fallbackText: Color = OneReflectionColors.TerracottaDeep,
    modifier: Modifier = Modifier
) {
    val ringModifier = if (ringColor != null) {
        Modifier.border(ringWidth, ringColor, CircleShape)
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(ringModifier)
            .background(fallbackBg),
        contentAlignment = Alignment.Center
    ) {
        if (!url.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = url,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                error = {
                    Text(
                        text = fallbackInitials,
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.34f).sp,
                        color = fallbackText
                    )
                },
                loading = {
                    Text(
                        text = fallbackInitials,
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.34f).sp,
                        color = fallbackText
                    )
                }
            )
        } else {
            Text(
                text = fallbackInitials,
                fontFamily = DisplayFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.34f).sp,
                color = fallbackText
            )
        }
    }
}

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    barCount: Int = 20,
    height: Dp = 28.dp,
    mixedColors: Boolean = false,
    modifier: Modifier = Modifier
) {
    var pulsePhase by remember { mutableIntStateOf(0) }
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(95L)
            pulsePhase = (pulsePhase + 1) % 100
        }
    }
    val baseHeights = remember {
        listOf(30, 58, 86, 44, 96, 70, 78, 48, 64, 90, 54, 36, 72, 46, 82, 62)
    }

    Row(
        modifier = modifier.height(height),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        for (i in 0 until barCount) {
            val base = baseHeights[i % baseHeights.size]
            val fraction = if (isPlaying) {
                ((base + sin(pulsePhase * 0.48 + i) * 34.0) / 100.0).toFloat().coerceIn(0.22f, 1f)
            } else {
                (base / 100f).coerceIn(0.22f, 1f)
            }
            val barColor = when {
                mixedColors && i % 3 == 0 -> OneReflectionColors.Terracotta
                mixedColors && i % 3 == 1 -> OneReflectionColors.GoldLuminous
                mixedColors -> OneReflectionColors.SageLight
                i < 7 -> OneReflectionColors.Terracotta
                else -> OneReflectionColors.OutlineSoft
            }
            Box(
                modifier = Modifier
                    .width(3.5.dp)
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
    onCheckedChange: () -> Unit,
    activeTrackColor: Color = OneReflectionColors.Terracotta,
    inactiveTrackColor: Color = Color(0xFFC9C1B8),
    thumbColor: Color = Color.White,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.68f, stiffness = Spring.StiffnessMediumLow),
        label = "switch_thumb"
    )
    val trackColor by animateColorAsState(
        targetValue = if (checked) activeTrackColor else inactiveTrackColor,
        animationSpec = tween(durationMillis = 320, easing = SanctuaryEase),
        label = "switch_track"
    )
    Box(
        modifier = modifier
            .testTag(testTag)
            .size(width = 48.dp, height = 28.dp)
            .clip(CircleShape)
            .background(trackColor)
            .clickable { onCheckedChange() }
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(22.dp)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}

private data class NavDrawerItem(
    val id: ScreenId,
    val label: String,
    val icon: ImageVector,
    val badge: String? = null
)

@Composable
fun OneReflectionHeader(
    currentScreen: ScreenId,
    oledMode: Boolean,
    onNavigate: (ScreenId) -> Unit,
    onToggleOled: () -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onOpenBedtimeSync: () -> Unit,
    onOpenThemeSettings: () -> Unit
) {
    var menuOpen by remember { mutableStateOf(false) }
    var notificationsOpen by remember { mutableStateOf(false) }

    val navItems = remember {
        listOf(
            NavDrawerItem(ScreenId.HOME, "Home", Icons.Filled.Home),
            NavDrawerItem(ScreenId.ALARMS, "Alarms", Icons.Filled.Alarm, "07:30"),
            NavDrawerItem(ScreenId.TASKS, "Tasks", Icons.Filled.CheckCircle, "4"),
            NavDrawerItem(ScreenId.SANCTUARY_TREE, "Great Tree", Icons.Filled.Park, "New"),
            NavDrawerItem(ScreenId.CHATS, "Chats", Icons.Filled.ChatBubble, "Live"),
            NavDrawerItem(ScreenId.HABITS, "Rituals", Icons.Filled.Eco),
            NavDrawerItem(ScreenId.REMINDERS, "Moments", Icons.Filled.CalendarMonth),
            NavDrawerItem(ScreenId.QUIET_BREAKTHROUGH, "Quiet", Icons.Filled.Bedtime, "Night"),
            NavDrawerItem(ScreenId.PEOPLE, "Circle", Icons.Filled.Group),
            NavDrawerItem(ScreenId.PROFILE, "Profile", Icons.Filled.Person)
        )
    }

    val palette = LocalElementalPalette.current
    val headerBg = palette.pageBgTop.copy(alpha = 0.86f)
    val borderColor = palette.border.copy(alpha = 0.72f)
    val chevronRotation by animateFloatAsState(
        targetValue = if (menuOpen) 180f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f),
        label = "header_chevron_rot"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(headerBg)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo + Editorial Wordmark + Screen Switcher
            Row(
                modifier = Modifier
                    .testTag("header_navigator_button")
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        notificationsOpen = false
                        menuOpen = !menuOpen
                    }
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OneReflectionLogoMark(size = 38.dp, cornerRadius = 12.dp)

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.app_name),
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            letterSpacing = (-0.4).sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Icon(
                            imageVector = Icons.Filled.ExpandMore,
                            contentDescription = stringResource(R.string.cd_toggle_navigator),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(18.dp)
                                .graphicsLayer { rotationZ = chevronRotation }
                        )
                    }
                    AnimatedContent(
                        targetState = currentScreen.subtitle.uppercase(),
                        transitionSpec = {
                            (fadeIn(tween(260, easing = SanctuaryEase)) +
                                slideInVertically(tween(280, easing = SanctuaryEase)) { it / 2 })
                                .togetherWith(
                                    fadeOut(tween(180)) +
                                        slideOutVertically(tween(200)) { -it / 2 }
                                )
                        },
                        label = "header_screen_subtitle"
                    ) { subtitle ->
                        Text(
                            text = subtitle,
                            fontFamily = MonoFontFamily,
                            fontSize = 9.5.sp,
                            letterSpacing = 0.8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Right Controls: Theme Studio + Enchanted Day/Night Toggle + Notifications + Profile
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("header_theme_studio_button")
                        .clip(CircleShape)
                        .clickable {
                            menuOpen = false
                            notificationsOpen = false
                            onOpenThemeSettings()
                        },
                    shape = CircleShape,
                    color = palette.surfaceTint,
                    border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.35f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = "Customize Page or App Theme",
                            tint = palette.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                EnchantedDayNightToggle(
                    isDark = oledMode,
                    onToggle = onToggleOled,
                    width = 70.dp,
                    height = 34.dp,
                    testTag = "oled_toggle_button"
                )

                Surface(
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("notifications_button")
                        .clip(CircleShape)
                        .clickable {
                            menuOpen = false
                            notificationsOpen = !notificationsOpen
                        },
                    shape = CircleShape,
                    color = palette.surfaceCard,
                    border = BorderStroke(1.dp, borderColor)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = stringResource(R.string.cd_notifications),
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(16.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 7.dp, end = 7.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(palette.primary)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .testTag("header_profile_button")
                        .clip(CircleShape)
                        .clickable {
                            menuOpen = false
                            notificationsOpen = false
                            onNavigate(ScreenId.PROFILE)
                        }
                ) {
                    AvatarImage(
                        url = AvatarUrls.ELENA,
                        fallbackInitials = "EV",
                        contentDescription = stringResource(R.string.cd_profile),
                        size = 34.dp,
                        ringColor = palette.primary,
                        ringWidth = 1.6.dp
                    )
                }
            }
        }

        HorizontalDivider(color = borderColor)

        AnimatedVisibility(
            visible = notificationsOpen,
            enter = fadeIn(tween(260, easing = SanctuaryEase)) + expandVertically(tween(300, easing = SanctuaryEase)),
            exit = fadeOut(tween(180)) + shrinkVertically(tween(220, easing = SanctuaryEase))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(24.dp),
                color = palette.surfaceCard,
                border = BorderStroke(1.dp, borderColor),
                shadowElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT WHISPERS & PULSE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Dismiss",
                            fontFamily = BodyFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { notificationsOpen = false }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.PeachMist)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = null, tint = OneReflectionColors.Terracotta, modifier = Modifier.size(18.dp))
                        Column {
                            Text("Maya left a wake-up reflection", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("\"Remember to drink lemon water...\"", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.SageMist)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.WaterDrop, contentDescription = null, tint = OneReflectionColors.SageDeep, modifier = Modifier.size(18.dp))
                        Column {
                            Text("Sofia completed Hydration", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("7-day mindful streak achieved", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = menuOpen,
            enter = fadeIn(tween(260, easing = SanctuaryEase)) + expandVertically(tween(300, easing = SanctuaryEase)),
            exit = fadeOut(tween(180)) + shrinkVertically(tween(220, easing = SanctuaryEase))
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = palette.surfaceCard,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            OneReflectionLogoMark(size = 24.dp, cornerRadius = 7.dp, showGoldRing = false)
                            Text(
                                text = "SANCTUARY NAVIGATOR",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(palette.surfaceElevated)
                                .clickable { menuOpen = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Close screen drawer", modifier = Modifier.size(16.dp))
                        }
                    }

                    navItems.chunked(3).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { item ->
                                val selected = currentScreen == item.id
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("drawer_nav_${item.id.name.lowercase()}")
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable {
                                            menuOpen = false
                                            onNavigate(item.id)
                                        },
                                    shape = RoundedCornerShape(18.dp),
                                    color = if (selected) palette.primary else palette.surfaceElevated,
                                    border = if (!selected) BorderStroke(1.dp, borderColor) else null
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = if (selected) palette.onPrimary else MaterialTheme.colorScheme.onBackground,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = item.label,
                                            fontFamily = BodyFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = if (selected) palette.onPrimary else MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("drawer_open_voice_studio")
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    menuOpen = false
                                    onOpenVoiceStudio()
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = palette.heroStart
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 11.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Mic, contentDescription = null, tint = palette.primaryBright, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Voice Studio", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("drawer_open_bedtime_sync")
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    menuOpen = false
                                    onOpenBedtimeSync()
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = palette.surfaceTint,
                            border = BorderStroke(1.dp, palette.border)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 11.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Tune, contentDescription = null, tint = palette.primary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Bedtime Rules", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = palette.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OneReflectionBottomNav(
    currentScreen: ScreenId,
    oledMode: Boolean,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    LiquidGlassFloatingDock(
        currentScreen = currentScreen,
        oledMode = oledMode,
        onNavigate = onNavigate,
        onOpenVoiceStudio = onOpenVoiceStudio,
        modifier = modifier
    )
}

@Composable
fun InAppNotificationOverlay(
    notification: InAppNotification?,
    onActionClick: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalElementalPalette.current

    LaunchedEffect(notification?.id) {
        if (notification != null) {
            delay(4800L)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = notification != null,
        enter = fadeIn(tween(260, easing = SanctuaryEase)) +
            slideInVertically(tween(340, easing = SanctuaryEase)) { -it },
        exit = fadeOut(tween(200)) +
            slideOutVertically(tween(240, easing = SanctuaryEase)) { -it },
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        val current = notification ?: return@AnimatedVisibility
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("in_app_notification_banner"),
            shape = RoundedCornerShape(22.dp),
            color = palette.heroStart.copy(alpha = 0.96f),
            border = BorderStroke(1.2.dp, palette.primaryBright.copy(alpha = 0.65f)),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (current.badgeText.isNotBlank()) {
                            Surface(
                                shape = CircleShape,
                                color = palette.primary
                            ) {
                                Text(
                                    text = current.badgeText,
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp,
                                    color = palette.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Text(
                            text = current.title,
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .testTag("in_app_notification_dismiss")
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Dismiss notification",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = current.message,
                    fontFamily = BodyFontFamily,
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp,
                    color = Color(0xFFEDE8E3)
                )

                val actionLabel = current.actionLabel
                val actionTaskId = current.actionTaskId
                if (!actionLabel.isNullOrBlank() && !actionTaskId.isNullOrBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            modifier = Modifier
                                .testTag("in_app_notification_action")
                                .clip(CircleShape)
                                .clickable { onActionClick(actionTaskId) },
                            shape = CircleShape,
                            color = palette.primaryBright
                        ) {
                            Text(
                                text = actionLabel,
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = palette.heroEnd,
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
