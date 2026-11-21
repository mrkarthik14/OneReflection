package com.onereflection.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.components.OneReflectionBottomNav
import com.onereflection.app.ui.components.OneReflectionHeader
import com.onereflection.app.ui.modals.BedtimeSyncSheet
import com.onereflection.app.ui.modals.VoiceStudioSheet
import com.onereflection.app.ui.screens.*
import com.onereflection.app.ui.theme.BrandColors
import com.onereflection.app.ui.theme.OneReflectionTheme
import com.onereflection.app.viewmodel.OneReflectionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: OneReflectionViewModel = viewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(state.oledMode) {
                enableEdgeToEdge(
                    statusBarStyle = if (state.oledMode) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
                    },
                    navigationBarStyle = if (state.oledMode) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
                    }
                )
            }

            OneReflectionTheme(oledMode = state.oledMode) {
                OneReflectionAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun OneReflectionAppRoot(viewModel: OneReflectionViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        val msg = state.snackbarMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val backgroundColor = if (state.oledMode) BrandColors.OledBlack else BrandColors.SurfaceWarm

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                OneReflectionHeader(
                    currentScreen = state.currentScreen,
                    oledMode = state.oledMode,
                    onNavigate = viewModel::navigateTo,
                    onToggleOled = viewModel::toggleOledMode,
                    onOpenVoiceStudio = viewModel::openVoiceStudio,
                    onOpenBedtimeSync = viewModel::openBedtimeSync
                )
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 96.dp)
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(modifier = Modifier.widthIn(max = 600.dp).fillMaxSize()) {
                    Crossfade(
                        targetState = state.currentScreen,
                        label = "screen_crossfade"
                    ) { screen ->
                        when (screen) {
                            ScreenId.HOME -> HomeScreen(
                                state = state,
                                onNavigate = viewModel::navigateTo,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onIncrementHydration = viewModel::incrementHydration
                            )
                            ScreenId.ALARMS -> AlarmsScreen(
                                state = state,
                                onNavigate = viewModel::navigateTo,
                                onBack = viewModel::navigateBackToHome,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onOpenBedtimeSync = viewModel::openBedtimeSync,
                                onToggleAlarm = viewModel::toggleAlarm
                            )
                            ScreenId.TASKS -> TasksScreen(
                                state = state,
                                onBack = viewModel::navigateBackToHome,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onToggleTask = viewModel::toggleTask,
                                onAddTask = viewModel::addTask,
                                onCyclePriority = viewModel::cycleTaskPriority,
                                onAddCategory = viewModel::addCategory,
                                onUpdateCategory = viewModel::updateCategory,
                                onDeleteCategory = viewModel::deleteCategory
                            )
                            ScreenId.CHATS -> ChatsScreen(
                                state = state,
                                onNavigate = viewModel::navigateTo,
                                onBack = viewModel::navigateBackToHome,
                                onOpenVoiceStudio = viewModel::openVoiceStudio
                            )
                            ScreenId.HABITS -> HabitsScreen(
                                state = state,
                                onNavigate = viewModel::navigateTo,
                                onBack = viewModel::navigateBackToHome,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onIncrementHydration = viewModel::incrementHydration
                            )
                            ScreenId.REMINDERS -> RemindersScreen(
                                state = state,
                                onBack = viewModel::navigateBackToHome,
                                onOpenVoiceStudio = viewModel::openVoiceStudio,
                                onToggleReminder = viewModel::toggleReminder
                            )
                            ScreenId.PEOPLE -> PeopleScreen(
                                state = state,
                                onNavigate = viewModel::navigateTo,
                                onBack = viewModel::navigateBackToHome,
                                onOpenBedtimeSync = viewModel::openBedtimeSync,
                                onDismissPendingRequest = viewModel::dismissPendingRequest
                            )
                            ScreenId.PROFILE -> ProfileScreen(
                                state = state,
                                onNavigate = viewModel::navigateTo,
                                onBack = viewModel::navigateBackToHome,
                                onOpenBedtimeSync = viewModel::openBedtimeSync
                            )
                            ScreenId.QUIET_BREAKTHROUGH -> QuietHoursScreen(
                                state = state,
                                onBack = viewModel::navigateBackToHome,
                                onToggleOled = viewModel::toggleOledMode,
                                onOpenBedtimeSync = viewModel::openBedtimeSync
                            )
                        }
                    }
                }
            }
        }

        // Floating Pill Bottom Navigation
        OneReflectionBottomNav(
            currentScreen = state.currentScreen,
            oledMode = state.oledMode,
            onNavigate = viewModel::navigateTo,
            onOpenQuickCreate = viewModel::openVoiceStudio,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (state.voiceStudioOpen) {
            VoiceStudioSheet(
                oledMode = state.oledMode,
                onDismiss = viewModel::closeVoiceStudio,
                onSaveVoiceNote = viewModel::saveVoiceAlarm
            )
        }

        if (state.bedtimeSyncOpen) {
            BedtimeSyncSheet(
                initialConfig = state.quietHours,
                oledMode = state.oledMode,
                onDismiss = viewModel::closeBedtimeSync,
                onSave = viewModel::updateQuietHours
            )
        }
    }
}
