package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.R
import com.onereflection.app.model.AvatarUrls
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.ElementalTheme
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ScreenId
import com.onereflection.app.model.ThemeApplyScope
import com.onereflection.app.model.ThemeModeSlot
import com.onereflection.app.ui.components.AmbientSoundscapeSettingsCard
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.ElementalDayPill
import com.onereflection.app.ui.components.ElementalThemeCarousel
import com.onereflection.app.ui.components.EnchantedDayNightToggle
import com.onereflection.app.ui.components.OneReflectionLogoMark
import com.onereflection.app.ui.components.OneReflectionSwitch
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors

@Composable
fun ProfileScreen(
    uiState: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onOpenBedtimeSync: () -> Unit,
    onToggleOled: () -> Unit,
    onSelectDay: (Int) -> Unit,
    onAssignDayTheme: (Int, ElementalTheme) -> Unit,
    onApplyThemeWithScope: (ThemeApplyScope, ThemeModeSlot, ScreenId, ElementalTheme) -> Unit,
    onClearPageTheme: (ScreenId, ThemeModeSlot) -> Unit,
    onResetDayThemes: () -> Unit,
    onToggleAmbientSound: () -> Unit,
    onSetAmbientSoundVolume: (Float) -> Unit,
    onPlayChime: (ChimeType) -> Unit
) {
    BackHandler { onNavigate(ScreenId.HOME) }

    var selectedPersona by remember { mutableIntStateOf(0) }
    var spatialAudio by remember { mutableStateOf(true) }
    var gentleHaptics by remember { mutableStateOf(true) }
    var encryptedVault by remember { mutableStateOf(true) }

    val personas = listOf("Warm Velvet", "Crisp Studio", "Soft Whisper")
    val oledMode = uiState.oledMode
    val palette = LocalElementalPalette.current
    val cardBg = palette.surfaceCard
    val borderColor = palette.border
    val days = remember {
        listOf("M" to "13", "T" to "14", "W" to "15", "T" to "16", "F" to "17", "S" to "18", "S" to "19")
    }
    val dayFullNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val activeDay = uiState.selectedDayIndex
    val activeElement = uiState.activeElementalTheme

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 164.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Identity Banner with Sanctuary Artwork + Active Elemental Glow
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_hero_card"),
                shape = RoundedCornerShape(30.dp),
                color = palette.heroEnd,
                border = BorderStroke(1.2.dp, palette.primaryBright.copy(alpha = 0.45f))
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_sanctuary_hero_1795264045436),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(0.22f)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        palette.heroStart.copy(alpha = 0.88f),
                                        palette.heroEnd.copy(alpha = 0.96f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AvatarImage(
                            url = AvatarUrls.ELENA,
                            fallbackInitials = "EV",
                            contentDescription = "Elena Vance",
                            size = 76.dp,
                            ringColor = palette.primaryBright,
                            ringWidth = 2.dp
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Elena Vance",
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 26.sp,
                                color = Color.White
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OneReflectionLogoMark(size = 18.dp, cornerRadius = 6.dp, showGoldRing = false)
                                Text(
                                    text = "SANCTUARY SETTINGS • @ELENA.V",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp,
                                    color = palette.primaryBright
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatPill(value = "14d", label = "STREAK")
                            ProfileStatPill(value = "3", label = "CIRCLE")
                            ProfileStatPill(value = "${uiState.alarms.count { it.enabled }}", label = "ALARMS")
                        }
                    }
                }
            }
        }

        // Dual Light/Dark & Single-Page / Entire-App Elemental Theme Customizer Studio
        item {
            var scopeChoice by remember { mutableStateOf(ThemeApplyScope.ENTIRE_APP) }
            var modeChoice by remember(oledMode) {
                mutableStateOf(if (oledMode) ThemeModeSlot.DARK else ThemeModeSlot.LIGHT)
            }
            val lightSlotTheme = if (scopeChoice == ThemeApplyScope.SINGLE_PAGE) {
                uiState.pageLightThemes[ScreenId.PROFILE] ?: uiState.resolvedThemeFor(ScreenId.PROFILE, isDark = false)
            } else {
                if (uiState.useGlobalLightTheme) uiState.lightGlobalTheme
                else uiState.dayThemes.getOrElse(activeDay) { uiState.lightGlobalTheme }
            }
            val darkSlotTheme = if (scopeChoice == ThemeApplyScope.SINGLE_PAGE) {
                uiState.pageDarkThemes[ScreenId.PROFILE] ?: uiState.darkGlobalTheme
            } else {
                uiState.darkGlobalTheme
            }
            val shownTheme = if (modeChoice == ThemeModeSlot.DARK) darkSlotTheme else lightSlotTheme

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_elemental_studio_card"),
                shape = RoundedCornerShape(28.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "7 ELEMENTAL WORLDS · DUAL MODE",
                                fontFamily = MonoFontFamily,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp,
                                color = palette.primary
                            )
                            Text(
                                text = "${dayFullNames[activeDay]} • ${activeElement.displayName}",
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        EnchantedDayNightToggle(
                            isDark = oledMode,
                            onToggle = {
                                onToggleOled()
                                modeChoice = if (!oledMode) ThemeModeSlot.DARK else ThemeModeSlot.LIGHT
                            },
                            width = 76.dp,
                            height = 38.dp,
                            testTag = "profile_day_night_toggle"
                        )
                    }

                    // Scope Switcher: Entire App vs This Page Only
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isEntire = scopeChoice == ThemeApplyScope.ENTIRE_APP
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_scope_entire_app")
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { scopeChoice = ThemeApplyScope.ENTIRE_APP },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isEntire) palette.primary else palette.surfaceElevated,
                            border = BorderStroke(1.dp, if (isEntire) Color.Transparent else borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 9.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Public, contentDescription = null, tint = if (isEntire) palette.onPrimary else palette.primary, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = "Entire App",
                                    fontFamily = BodyFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isEntire) palette.onPrimary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        val isPage = scopeChoice == ThemeApplyScope.SINGLE_PAGE
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_scope_single_page")
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { scopeChoice = ThemeApplyScope.SINGLE_PAGE },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isPage) palette.primary else palette.surfaceElevated,
                            border = BorderStroke(1.dp, if (isPage) Color.Transparent else borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 9.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Layers, contentDescription = null, tint = if (isPage) palette.onPrimary else palette.primary, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = "This Page",
                                    fontFamily = BodyFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isPage) palette.onPrimary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    // Dual Light & Dark Theme Slot Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val lightSelected = modeChoice == ThemeModeSlot.LIGHT
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_slot_light")
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    modeChoice = ThemeModeSlot.LIGHT
                                    if (oledMode) onToggleOled()
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = if (lightSelected) palette.surfaceTint else palette.surfaceElevated,
                            border = BorderStroke(if (lightSelected) 1.6.dp else 1.dp, if (lightSelected) palette.primary else borderColor)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Filled.LightMode, contentDescription = null, tint = palette.primary, modifier = Modifier.size(13.dp))
                                    Text("LIGHT THEME", fontFamily = MonoFontFamily, fontSize = 8.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = lightSlotTheme.displayName,
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        val darkSelected = modeChoice == ThemeModeSlot.DARK
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_slot_dark")
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    modeChoice = ThemeModeSlot.DARK
                                    if (!oledMode) onToggleOled()
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = if (darkSelected) palette.surfaceTint else palette.surfaceElevated,
                            border = BorderStroke(if (darkSelected) 1.6.dp else 1.dp, if (darkSelected) palette.primary else borderColor)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Filled.DarkMode, contentDescription = null, tint = palette.primary, modifier = Modifier.size(13.dp))
                                    Text("DARK THEME", fontFamily = MonoFontFamily, fontSize = 8.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = darkSlotTheme.displayName,
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }

                    // 7-Day Selector inside Settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        days.forEachIndexed { idx, (lbl, num) ->
                            ElementalDayPill(
                                dayLabel = lbl,
                                dayNumber = num,
                                theme = uiState.dayThemes.getOrElse(idx) { ElementalTheme.BLOOM },
                                selected = idx == activeDay,
                                oledMode = oledMode,
                                testTag = "profile_day_pill_$idx",
                                onClick = { onSelectDay(idx) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // 7-Element Animated Showcase Carousel
                    ElementalThemeCarousel(
                        selectedTheme = shownTheme,
                        onSelectTheme = { chosen ->
                            onApplyThemeWithScope(scopeChoice, modeChoice, ScreenId.PROFILE, chosen)
                            if (scopeChoice == ThemeApplyScope.ENTIRE_APP && modeChoice == ThemeModeSlot.LIGHT) {
                                onAssignDayTheme(activeDay, chosen)
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_apply_theme_all")
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    if (scopeChoice == ThemeApplyScope.SINGLE_PAGE && uiState.hasCustomPageTheme(ScreenId.PROFILE, modeChoice == ThemeModeSlot.DARK)) {
                                        onClearPageTheme(ScreenId.PROFILE, modeChoice)
                                    } else {
                                        onApplyThemeWithScope(scopeChoice, modeChoice, ScreenId.PROFILE, shownTheme)
                                    }
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = palette.primary
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 11.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = palette.onPrimary, modifier = Modifier.size(15.dp))
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = if (scopeChoice == ThemeApplyScope.SINGLE_PAGE) {
                                        "Applied to Profile (${modeChoice.name})"
                                    } else {
                                        "Apply ${shownTheme.displayName} (${modeChoice.name})"
                                    },
                                    fontFamily = BodyFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = palette.onPrimary
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .testTag("profile_reset_day_themes")
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onResetDayThemes() },
                            shape = RoundedCornerShape(16.dp),
                            color = palette.surfaceElevated,
                            border = BorderStroke(1.dp, borderColor)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 11.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Reset themes", tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        // Adaptive Elemental Ambient Soundscapes Card
        item {
            AmbientSoundscapeSettingsCard(
                activeTheme = activeElement,
                enabled = uiState.ambientSoundEnabled,
                volume = uiState.ambientSoundVolume,
                oledMode = oledMode,
                onToggleEnabled = onToggleAmbientSound,
                onVolumeChange = onSetAmbientSoundVolume,
                onSelectTheme = { chosen ->
                    onApplyThemeWithScope(
                        ThemeApplyScope.ENTIRE_APP,
                        if (oledMode) ThemeModeSlot.DARK else ThemeModeSlot.LIGHT,
                        ScreenId.PROFILE,
                        chosen
                    )
                }
            )
        }

        // Voice Persona Acoustic EQ Selector
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.GraphicEq, contentDescription = null, tint = palette.primary, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Voice Persona EQ",
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        personas.forEachIndexed { idx, label ->
                            val selected = selectedPersona == idx
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_persona_$idx")
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        selectedPersona = idx
                                        onPlayChime(ChimeType.TIBETAN)
                                    },
                                shape = RoundedCornerShape(16.dp),
                                color = if (selected) palette.primary else palette.surfaceElevated,
                                border = BorderStroke(1.dp, if (selected) Color.Transparent else borderColor)
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 11.dp, horizontal = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontFamily = BodyFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = if (selected) palette.onPrimary else MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Harmony & Privacy Preferences
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "HARMONY & PRIVACY VAULT",
                        fontFamily = MonoFontFamily,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    ProfileToggleRow(
                        icon = Icons.Filled.Mic,
                        title = "Spatial Voice Clarity",
                        subtitle = "Warm acoustic EQ for wake-up memos",
                        checked = spatialAudio,
                        testTag = "profile_spatial_switch",
                        onToggle = {
                            spatialAudio = !spatialAudio
                            onPlayChime(ChimeType.CLICK)
                        }
                    )
                    ProfileToggleRow(
                        icon = Icons.Filled.NotificationsActive,
                        title = "Mindful Haptic Pulses",
                        subtitle = "Soft sunrise ramp before voice chimes",
                        checked = gentleHaptics,
                        testTag = "profile_haptics_switch",
                        onToggle = {
                            gentleHaptics = !gentleHaptics
                            onPlayChime(ChimeType.CLICK)
                        }
                    )
                    ProfileToggleRow(
                        icon = Icons.Filled.Lock,
                        title = "End-to-End Voice Vault",
                        subtitle = "Zero-knowledge keys for Inner Circle",
                        checked = encryptedVault,
                        testTag = "profile_vault_switch",
                        onToggle = {
                            encryptedVault = !encryptedVault
                            onPlayChime(ChimeType.CLICK)
                        }
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("profile_open_bedtime")
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onOpenBedtimeSync() },
                    shape = RoundedCornerShape(20.dp),
                    color = palette.surfaceTint,
                    border = BorderStroke(1.dp, palette.border)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Bedtime, contentDescription = null, tint = palette.primary, modifier = Modifier.size(17.dp))
                        Text("  Bedtime Rules", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = palette.primary)
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("profile_open_voice")
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onOpenVoiceStudio() },
                    shape = RoundedCornerShape(20.dp),
                    color = palette.primary
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = null, tint = palette.onPrimary, modifier = Modifier.size(17.dp))
                        Text("  Voice Studio", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = palette.onPrimary)
                    }
                }
            }
        }

        // Brand Footer
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Verified, contentDescription = null, tint = OneReflectionColors.SageDeep, modifier = Modifier.size(14.dp))
                Text(
                    text = "  OneReflection v2.4.1 • Elemental Sanctuary",
                    fontFamily = MonoFontFamily,
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileStatPill(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = MonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = Color.White
        )
        Text(
            text = label,
            fontFamily = MonoFontFamily,
            fontSize = 9.5.sp,
            letterSpacing = 0.8.sp,
            color = Color(0xFFB5ACA3)
        )
    }
}

@Composable
private fun ProfileToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    testTag: String,
    onToggle: () -> Unit
) {
    val palette = LocalElementalPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = palette.primary,
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = BodyFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                fontFamily = BodyFontFamily,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        OneReflectionSwitch(
            checked = checked,
            onCheckedChange = onToggle,
            activeTrackColor = palette.primary,
            testTag = testTag
        )
    }
}
