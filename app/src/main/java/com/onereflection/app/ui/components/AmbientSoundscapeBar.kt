package com.onereflection.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.R
import com.onereflection.app.model.ElementalTheme
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.SanctuaryEase
import com.onereflection.app.ui.theme.elementalPaletteFor
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Animated miniature acoustic equalizer bars synced to the active Elemental Theme palette.
 */
@Composable
fun ElementalEqualizerBars(
    isPlaying: Boolean,
    color: Color,
    barCount: Int = 4,
    height: Dp = 14.dp,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "ambient_eq_bars")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_eq_phase"
    )
    val idleHeights = remember { listOf(0.32f, 0.56f, 0.40f, 0.28f, 0.48f) }

    Row(
        modifier = modifier.height(height),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 0 until barCount) {
            val fraction = if (isPlaying) {
                (0.28f + 0.72f * (0.5f + 0.5f * sin(phase + i * 1.15f))).coerceIn(0.22f, 1f)
            } else {
                idleHeights[i % idleHeights.size]
            }
            Box(
                modifier = Modifier
                    .width(2.4.dp)
                    .fillMaxHeight(fraction)
                    .clip(CircleShape)
                    .background(if (isPlaying) color else color.copy(alpha = 0.45f))
            )
        }
    }
}

/**
 * Sleek, unobtrusive Elemental Ambient Soundscape ribbon positioned directly beneath the top bar.
 * Shows the active Elemental Theme's paired soundscape (e.g., "Ember · Evening Crickets & Hearth",
 * "Breeze · Alpine Wind", "Rain · Monsoon Rain"), toggles background sound in one tap, and
 * expands an inline volume mixer + 7-world quick soundscape switcher.
 */
@Composable
fun ElementalAmbientSoundscapeBar(
    activeTheme: ElementalTheme,
    enabled: Boolean,
    volume: Float,
    oledMode: Boolean,
    onToggleEnabled: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onSelectSoundscapeTheme: (ElementalTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalElementalPalette.current
    var mixerExpanded by remember { mutableStateOf(false) }

    val pillBg by animateColorAsState(
        targetValue = if (enabled) palette.primary else palette.surfaceElevated,
        animationSpec = tween(durationMillis = 360, easing = SanctuaryEase),
        label = "ambient_pill_bg"
    )
    val pillText by animateColorAsState(
        targetValue = if (enabled) palette.onPrimary else MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(durationMillis = 360, easing = SanctuaryEase),
        label = "ambient_pill_text"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.pageBgTop.copy(alpha = 0.78f))
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Active Soundscape Identity (tapping toggles inline mixer)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .testTag("ambient_sound_expand_mixer")
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { mixerExpanded = !mixerExpanded }
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = palette.surfaceTint,
                    border = BorderStroke(1.dp, palette.primary.copy(alpha = if (enabled) 0.55f else 0.25f))
                ) {
                    Box(
                        modifier = Modifier.size(26.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (enabled) {
                            ElementalEqualizerBars(
                                isPlaying = true,
                                color = palette.primary,
                                barCount = 3,
                                height = 12.dp
                            )
                        } else {
                            Icon(
                                imageVector = elementalIcon(activeTheme),
                                contentDescription = null,
                                tint = palette.primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                AnimatedContent(
                    targetState = activeTheme,
                    transitionSpec = {
                        (fadeIn(tween(260, easing = SanctuaryEase)) +
                            slideInVertically(tween(280, easing = SanctuaryEase)) { it / 2 })
                            .togetherWith(
                                fadeOut(tween(180)) +
                                    slideOutVertically(tween(200)) { -it / 2 }
                            )
                    },
                    label = "ambient_soundscape_title"
                ) { theme ->
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            text = if (enabled) {
                                "PLAYING · ${theme.displayName.uppercase()}"
                            } else {
                                "AMBIENT · ${theme.displayName.uppercase()}"
                            },
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp,
                            letterSpacing = 0.7.sp,
                            color = palette.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = theme.soundscapeTitle,
                            fontFamily = BodyFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Mixer Drawer Button
                Surface(
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("ambient_sound_mixer_button")
                        .clip(CircleShape)
                        .clickable { mixerExpanded = !mixerExpanded },
                    shape = CircleShape,
                    color = if (mixerExpanded) palette.surfaceTint else palette.surfaceCard,
                    border = BorderStroke(
                        1.dp,
                        if (mixerExpanded) palette.primary else palette.border.copy(alpha = 0.75f)
                    )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = stringResource(R.string.cd_expand_ambient_mixer),
                            tint = if (mixerExpanded) palette.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // 1-Tap Play / Pause Ambient Soundscape Pill
                Surface(
                    modifier = Modifier
                        .testTag("ambient_sound_toggle_pill")
                        .clip(CircleShape)
                        .clickable { onToggleEnabled() },
                    shape = CircleShape,
                    color = pillBg,
                    border = BorderStroke(1.dp, if (enabled) Color.Transparent else palette.border)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = if (enabled) {
                                Icons.AutoMirrored.Filled.VolumeUp
                            } else {
                                Icons.AutoMirrored.Filled.VolumeOff
                            },
                            contentDescription = stringResource(R.string.cd_toggle_ambient_sound),
                            tint = pillText,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (enabled) {
                                stringResource(R.string.ambient_sound_on)
                            } else {
                                stringResource(R.string.ambient_sound_off)
                            },
                            fontFamily = BodyFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = pillText
                        )
                    }
                }
            }
        }

        // Smooth Expandable Inline Mixer (Volume Slider + 7 Elemental Soundscape Quick Chips)
        AnimatedVisibility(
            visible = mixerExpanded,
            enter = fadeIn(tween(240, easing = SanctuaryEase)) + expandVertically(tween(280, easing = SanctuaryEase)),
            exit = fadeOut(tween(180)) + shrinkVertically(tween(220, easing = SanctuaryEase))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ambient_sound_mixer_panel"),
                shape = RoundedCornerShape(20.dp),
                color = palette.surfaceCard,
                border = BorderStroke(1.dp, palette.border),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeTheme.soundscapeTitle,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = activeTheme.soundscapeDescription,
                                fontFamily = BodyFontFamily,
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${(volume * 100f).roundToInt()}%",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = palette.primary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Ambient volume",
                            tint = palette.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Slider(
                            value = volume,
                            onValueChange = onVolumeChange,
                            valueRange = 0.05f..1.0f,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ambient_sound_volume_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = palette.primary,
                                activeTrackColor = palette.primary,
                                inactiveTrackColor = palette.border.copy(alpha = 0.6f)
                            )
                        )
                    }

                    // 7 Elemental Soundscapes Quick Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ElementalTheme.entries.forEach { theme ->
                            val selected = theme == activeTheme
                            val itemPal = elementalPaletteFor(theme, oledMode)
                            Surface(
                                modifier = Modifier
                                    .testTag("ambient_chip_${theme.name.lowercase()}")
                                    .clip(CircleShape)
                                    .clickable {
                                        onSelectSoundscapeTheme(theme)
                                        if (!enabled) onToggleEnabled()
                                    },
                                shape = CircleShape,
                                color = if (selected) itemPal.primary else palette.surfaceElevated,
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) Color.Transparent else palette.border
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = elementalIcon(theme),
                                        contentDescription = null,
                                        tint = if (selected) itemPal.onPrimary else itemPal.primary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "${theme.displayName} · ${theme.soundscapeShort}",
                                        fontFamily = BodyFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (selected) itemPal.onPrimary else MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full-featured Elemental Ambient Soundscape settings card used inside Profile Settings
 * and Elemental Theme Studio.
 */
@Composable
fun AmbientSoundscapeSettingsCard(
    activeTheme: ElementalTheme,
    enabled: Boolean,
    volume: Float,
    oledMode: Boolean,
    onToggleEnabled: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onSelectTheme: (ElementalTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = LocalElementalPalette.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ambient_sound_settings_card"),
        shape = RoundedCornerShape(26.dp),
        color = palette.surfaceCard,
        border = BorderStroke(1.dp, palette.border)
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
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = palette.surfaceTint,
                        border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.35f))
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (enabled) {
                                ElementalEqualizerBars(
                                    isPlaying = true,
                                    color = palette.primary,
                                    barCount = 4,
                                    height = 16.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Headphones,
                                    contentDescription = null,
                                    tint = palette.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "ADAPTIVE MINDFUL AUDIO",
                            fontFamily = MonoFontFamily,
                            fontSize = 9.5.sp,
                            letterSpacing = 0.9.sp,
                            color = palette.primary
                        )
                        Text(
                            text = stringResource(R.string.ambient_sound_studio_title),
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 19.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                OneReflectionSwitch(
                    checked = enabled,
                    onCheckedChange = onToggleEnabled,
                    activeTrackColor = palette.primary,
                    testTag = "settings_ambient_sound_switch"
                )
            }

            // Active Soundscape Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = palette.surfaceTint,
                border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.28f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = elementalIcon(activeTheme),
                        contentDescription = null,
                        tint = palette.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "${activeTheme.displayName} · ${activeTheme.soundscapeTitle}",
                            fontFamily = BodyFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = activeTheme.soundscapeDescription,
                            fontFamily = BodyFontFamily,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.GraphicEq,
                        contentDescription = null,
                        tint = if (enabled) palette.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Volume Control
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AMBIENT MIX LEVEL",
                        fontFamily = MonoFontFamily,
                        fontSize = 9.5.sp,
                        letterSpacing = 0.8.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(volume * 100f).roundToInt()}%",
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = palette.primary
                    )
                }
                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0.05f..1.0f,
                    modifier = Modifier.testTag("settings_ambient_volume_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = palette.primary,
                        activeTrackColor = palette.primary,
                        inactiveTrackColor = palette.border.copy(alpha = 0.65f)
                    )
                )
            }

            // Interactive 7-Element Soundscape Preview Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                ElementalTheme.entries.forEach { theme ->
                    val selected = theme == activeTheme
                    val itemPal = elementalPaletteFor(theme, oledMode)
                    Surface(
                        modifier = Modifier
                            .testTag("settings_soundscape_${theme.name.lowercase()}")
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onSelectTheme(theme)
                                if (!enabled) onToggleEnabled()
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) itemPal.primary else palette.surfaceElevated,
                        border = BorderStroke(1.dp, if (selected) Color.Transparent else palette.border)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = elementalIcon(theme),
                                    contentDescription = null,
                                    tint = if (selected) itemPal.onPrimary else itemPal.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = theme.displayName,
                                    fontFamily = BodyFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = if (selected) itemPal.onPrimary else MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Text(
                                text = theme.soundscapeShort,
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                color = if (selected) {
                                    itemPal.onPrimary.copy(alpha = 0.85f)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
