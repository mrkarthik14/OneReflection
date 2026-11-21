package com.onereflection.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onereflection.app.model.ScreenId
import com.onereflection.app.model.ThemeApplyScope
import com.onereflection.app.model.ThemeModeSlot
import com.onereflection.app.ui.components.ElementalAmbientSoundscapeBar
import com.onereflection.app.ui.components.ElementalPageAtmosphereCanvas
import com.onereflection.app.ui.components.ElementalThemeSettingsSheet
import com.onereflection.app.ui.components.InAppNotificationOverlay
import com.onereflection.app.ui.components.OneReflectionBottomNav
import com.onereflection.app.ui.components.OneReflectionHeader
import com.onereflection.app.ui.modals.BedtimeSyncSheet
import com.onereflection.app.ui.modals.VoiceStudioSheet
import com.onereflection.app.ui.screens.AlarmsScreen
import com.onereflection.app.ui.screens.ChatsScreen
import com.onereflection.app.ui.screens.HabitsScreen
import com.onereflection.app.ui.screens.HomeScreen
import com.onereflection.app.ui.screens.PeopleScreen
import com.onereflection.app.ui.screens.ProfileScreen
import com.onereflection.app.ui.screens.QuietHoursScreen
import com.onereflection.app.ui.screens.RemindersScreen
import com.onereflection.app.ui.screens.SanctuaryTreeScreen
import com.onereflection.app.ui.screens.TasksScreen
import com.onereflection.app.ui.theme.OneReflectionTheme
import com.onereflection.app.ui.theme.SanctuaryPageEase
import com.onereflection.app.viewmodel.OneReflectionViewModel

private fun dockScreenOrder(screen: ScreenId): Int = when (screen) {
    ScreenId.HOME -> 0
    ScreenId.ALARMS -> 1
    ScreenId.TASKS -> 2
    ScreenId.CHATS -> 3
    ScreenId.PEOPLE -> 4
    else -> -1
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: OneReflectionViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(uiState.oledMode, uiState.currentScreen) {
                val useDarkBars = uiState.oledMode || uiState.currentScreen == ScreenId.QUIET_BREAKTHROUGH
                enableEdgeToEdge(
                    statusBarStyle = if (useDarkBars) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
                    },
                    navigationBarStyle = if (useDarkBars) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
                    }
                )
            }

            OneReflectionTheme(
                oledMode = uiState.oledMode,
                elementalTheme = uiState.activeElementalTheme
            ) {
                OneReflectionAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun OneReflectionAppRoot(viewModel: OneReflectionViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.onAppForegrounded()
                Lifecycle.Event.ON_STOP -> viewModel.onAppBackgrounded()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Full-Screen Living Elemental + Day/Night Atmosphere Canvas behind every page!
        ElementalPageAtmosphereCanvas(
            theme = uiState.activeElementalTheme,
            isDark = uiState.oledMode || uiState.currentScreen == ScreenId.QUIET_BREAKTHROUGH,
            daySelectionKey = uiState.selectedDayIndex,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                OneReflectionHeader(
                    currentScreen = uiState.currentScreen,
                    oledMode = uiState.oledMode,
                    onNavigate = viewModel::navigate,
                    onToggleOled = viewModel::toggleOledMode,
                    onOpenVoiceStudio = viewModel::openVoiceStudio,
                    onOpenBedtimeSync = viewModel::openBedtimeSync,
                    onOpenThemeSettings = viewModel::openThemeSettings
                )

                ElementalAmbientSoundscapeBar(
                    activeTheme = uiState.activeElementalTheme,
                    enabled = uiState.ambientSoundEnabled,
                    volume = uiState.ambientSoundVolume,
                    oledMode = uiState.oledMode,
                    onToggleEnabled = viewModel::toggleAmbientSound,
                    onVolumeChange = viewModel::setAmbientSoundVolume,
                    onSelectSoundscapeTheme = { chosen ->
                        viewModel.applyThemeWithScope(
                            scope = ThemeApplyScope.ENTIRE_APP,
                            modeSlot = if (uiState.oledMode) ThemeModeSlot.DARK else ThemeModeSlot.LIGHT,
                            screen = uiState.currentScreen,
                            theme = chosen
                        )
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    AnimatedContent(
                        targetState = uiState.currentScreen,
                        transitionSpec = {
                            val fromIdx = dockScreenOrder(initialState)
                            val toIdx = dockScreenOrder(targetState)
                            when {
                                // Smooth directional glide + depth scale between primary Floating Dock pages
                                fromIdx >= 0 && toIdx >= 0 -> {
                                    val dir = if (toIdx >= fromIdx) 1 else -1
                                    (slideInHorizontally(tween(420, easing = SanctuaryPageEase)) { dir * (it * 0.16f).toInt() } +
                                        scaleIn(tween(420, easing = SanctuaryPageEase), initialScale = 0.965f) +
                                        fadeIn(tween(340, easing = SanctuaryPageEase)))
                                        .togetherWith(
                                            slideOutHorizontally(tween(320, easing = SanctuaryPageEase)) { -dir * (it * 0.10f).toInt() } +
                                                scaleOut(tween(300, easing = SanctuaryPageEase), targetScale = 0.975f) +
                                                fadeOut(tween(240, easing = SanctuaryPageEase))
                                        )
                                }
                                // Opening a secondary sanctuary screen (Habits, Moments, Profile, Quiet Hours)
                                toIdx < 0 -> {
                                    (slideInVertically(tween(420, easing = SanctuaryPageEase)) { (it * 0.07f).toInt() } +
                                        scaleIn(tween(420, easing = SanctuaryPageEase), initialScale = 0.955f) +
                                        fadeIn(tween(340, easing = SanctuaryPageEase)))
                                        .togetherWith(
                                            scaleOut(tween(300, easing = SanctuaryPageEase), targetScale = 0.97f) +
                                                fadeOut(tween(240, easing = SanctuaryPageEase))
                                        )
                                }
                                // Returning from a secondary screen back to a main dock sanctuary
                                else -> {
                                    (scaleIn(tween(400, easing = SanctuaryPageEase), initialScale = 0.965f) +
                                        fadeIn(tween(340, easing = SanctuaryPageEase)))
                                        .togetherWith(
                                            slideOutVertically(tween(320, easing = SanctuaryPageEase)) { (it * 0.06f).toInt() } +
                                                scaleOut(tween(300, easing = SanctuaryPageEase), targetScale = 0.96f) +
                                                fadeOut(tween(240, easing = SanctuaryPageEase))
                                        )
                                }
                            }
                        },
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            ScreenId.HOME -> HomeScreen(
                                uiState = uiState,
                                onNavigate = viewModel::navigate,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onPlayChime = viewModel::playChime,
                                onStopChime = viewModel::stopChime,
                                onAddHydration = viewModel::addHydration,
                                onIncrementHabit = viewModel::incrementHabit,
                                onSelectDay = viewModel::selectDay,
                                onOpenThemeSettings = viewModel::openThemeSettings
                            )

                            ScreenId.ALARMS -> AlarmsScreen(
                                uiState = uiState,
                                onNavigate = viewModel::navigate,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onOpenBedtimeSync = viewModel::openBedtimeSync,
                                onToggleAlarm = viewModel::toggleAlarm,
                                onPlayChime = viewModel::playChime,
                                onStopChime = viewModel::stopChime
                            )

                            ScreenId.TASKS -> TasksScreen(
                                uiState = uiState,
                                onNavigate = viewModel::navigate,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onToggleTask = viewModel::toggleTask,
                                onUpdateTaskProgress = viewModel::updateTaskProgress,
                                onScheduleTaskForNextDay = viewModel::scheduleTaskForNextDay,
                                onAddTask = viewModel::addTask,
                                onCyclePriority = viewModel::cycleTaskPriority,
                                onAddCategory = viewModel::addCategory,
                                onUpdateCategory = viewModel::updateCategory,
                                onDeleteCategory = viewModel::deleteCategory,
                                onPlayChime = viewModel::playChime
                            )

                            ScreenId.CHATS -> ChatsScreen(
                                uiState = uiState,
                                onNavigate = viewModel::navigate,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onPlayChime = viewModel::playChime,
                                onStopChime = viewModel::stopChime
                            )

                            ScreenId.HABITS -> HabitsScreen(
                                uiState = uiState,
                                onNavigate = viewModel::navigate,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onAddHydration = viewModel::addHydration,
                                onIncrementHabit = viewModel::incrementHabit,
                                onDecrementHabit = viewModel::decrementHabit,
                                onAddHabit = viewModel::addHabit,
                                onDeleteHabit = viewModel::deleteHabit,
                                onPlayChime = viewModel::playChime,
                                onStopChime = viewModel::stopChime
                            )

                            ScreenId.REMINDERS -> RemindersScreen(
                                uiState = uiState,
                                onNavigate = viewModel::navigate,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onToggleReminder = viewModel::toggleReminder,
                                onPlayChime = viewModel::playChime
                            )

                            ScreenId.PEOPLE -> PeopleScreen(
                                uiState = uiState,
                                onNavigate = viewModel::navigate,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onOpenBedtimeSync = viewModel::openBedtimeSync,
                                onPlayChime = viewModel::playChime
                            )

                            ScreenId.PROFILE -> ProfileScreen(
                                uiState = uiState,
                                onNavigate = viewModel::navigate,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onOpenBedtimeSync = viewModel::openBedtimeSync,
                                onToggleOled = viewModel::toggleOledMode,
                                onSelectDay = viewModel::selectDay,
                                onAssignDayTheme = viewModel::setDayTheme,
                                onApplyThemeWithScope = viewModel::applyThemeWithScope,
                                onClearPageTheme = viewModel::clearPageTheme,
                                onResetDayThemes = viewModel::resetDefaultDayThemes,
                                onToggleAmbientSound = viewModel::toggleAmbientSound,
                                onSetAmbientSoundVolume = viewModel::setAmbientSoundVolume,
                                onPlayChime = viewModel::playChime
                            )

                            ScreenId.QUIET_BREAKTHROUGH -> QuietHoursScreen(
                                uiState = uiState,
                                onNavigate = viewModel::navigate,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onOpenBedtimeSync = viewModel::openBedtimeSync,
                                onToggleOled = viewModel::toggleOledMode,
                                onPlayChime = viewModel::playChime,
                                onStopChime = viewModel::stopChime
                            )

                            ScreenId.SANCTUARY_TREE -> SanctuaryTreeScreen(
                                uiState = uiState,
                                onNavigate = viewModel::navigate,
                                onOpenThemeSettings = viewModel::openThemeSettings,
                                onPlayChime = viewModel::playChime,
                                onShowNotification = viewModel::showInfoNotification
                            )
                        }
                    }
                }
            }

            OneReflectionBottomNav(
                currentScreen = uiState.currentScreen,
                oledMode = uiState.oledMode,
                onNavigate = viewModel::navigate,
                onOpenVoiceStudio = viewModel::openVoiceStudio,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            InAppNotificationOverlay(
                notification = uiState.activeNotification,
                onActionClick = { taskId ->
                    viewModel.scheduleTaskForNextDay(taskId)
                },
                onDismiss = viewModel::dismissNotification,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }

    if (uiState.voiceStudioOpen) {
        VoiceStudioSheet(
            oledMode = uiState.oledMode,
            onDismiss = viewModel::closeVoiceStudio,
            onSave = viewModel::saveVoiceMemoAlarm,
            onPlayChime = viewModel::playChime
        )
    }

    if (uiState.bedtimeSyncOpen) {
        BedtimeSyncSheet(
            uiState = uiState,
            onDismiss = viewModel::closeBedtimeSync,
            onAdjustBedtime = viewModel::adjustBedtime,
            onAdjustWake = viewModel::adjustWake,
            onToggleSyncSleepWithPartner = viewModel::toggleSyncSleepWithPartner,
            onPlayChime = viewModel::playChime
        )
    }

    if (uiState.themeSettingsOpen) {
        ElementalThemeSettingsSheet(
            uiState = uiState,
            onToggleOled = viewModel::toggleOledMode,
            onSelectDay = viewModel::selectDay,
            onAssignThemeToDay = viewModel::setDayTheme,
            onApplyThemeWithScope = viewModel::applyThemeWithScope,
            onClearPageTheme = viewModel::clearPageTheme,
            onResetDefaults = viewModel::resetDefaultDayThemes,
            onToggleAmbientSound = viewModel::toggleAmbientSound,
            onSetAmbientSoundVolume = viewModel::setAmbientSoundVolume,
            onDismiss = viewModel::closeThemeSettings
        )
    }
}
