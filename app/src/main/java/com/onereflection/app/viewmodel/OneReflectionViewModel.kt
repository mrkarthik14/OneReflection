package com.onereflection.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.data.OneReflectionRepository
import com.onereflection.app.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OneReflectionUiState(
    val currentScreen: ScreenId = ScreenId.HOME,
    val oledMode: Boolean = false,
    val voiceStudioOpen: Boolean = false,
    val bedtimeSyncOpen: Boolean = false,
    val alarms: List<AlarmItem> = OneReflectionRepository.defaultAlarms(),
    val categories: List<TaskCategory> = OneReflectionRepository.defaultCategories(),
    val tasks: List<TaskItem> = OneReflectionRepository.defaultTasks(),
    val completedTasks: List<TaskItem> = OneReflectionRepository.defaultCompletedTasks(),
    val reminders: List<ReminderEvent> = OneReflectionRepository.defaultReminders(),
    val hydrationLiters: Double = 1.8,
    val quietHours: QuietHoursConfig = QuietHoursConfig(),
    val profile: UserProfile = UserProfile(),
    val pendingRequestDismissed: Boolean = false,
    val snackbarMessage: String? = null
)

class OneReflectionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OneReflectionRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(
        OneReflectionUiState(
            oledMode = repository.loadOledMode(),
            categories = repository.loadCategories(),
            tasks = repository.loadTasks(),
            completedTasks = repository.loadCompletedTasks(),
            hydrationLiters = repository.loadHydrationLiters()
        )
    )
    val uiState: StateFlow<OneReflectionUiState> = _uiState.asStateFlow()

    fun navigateTo(screen: ScreenId) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun navigateBackToHome() {
        _uiState.update { it.copy(currentScreen = ScreenId.HOME) }
    }

    fun toggleOledMode() {
        ChimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val next = !state.oledMode
            repository.saveOledMode(next)
            state.copy(oledMode = next)
        }
    }

    fun openVoiceStudio() {
        _uiState.update { it.copy(voiceStudioOpen = true) }
    }

    fun closeVoiceStudio() {
        _uiState.update { it.copy(voiceStudioOpen = false) }
    }

    fun openBedtimeSync() {
        _uiState.update { it.copy(bedtimeSyncOpen = true) }
    }

    fun closeBedtimeSync() {
        _uiState.update { it.copy(bedtimeSyncOpen = false) }
    }

    fun toggleAlarm(id: String) {
        ChimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            state.copy(
                alarms = state.alarms.map { alarm ->
                    if (alarm.id == id) alarm.copy(enabled = !alarm.enabled) else alarm
                }
            )
        }
    }

    fun saveVoiceAlarm(quote: String, duration: String) {
        ChimePlayer.play(ChimeType.TIBETAN)
        val newAlarm = AlarmItem(
            id = "alarm-${System.currentTimeMillis()}",
            title = "Voice Wake-up Memo",
            time = "07:30",
            period = "AM",
            days = listOf("M", "T", "W", "T", "F"),
            enabled = true,
            category = "shared",
            creatorName = "Elena (You)",
            creatorRole = "Personal Dispatch",
            creatorAvatar = AVATAR_ELENA,
            voiceSnippetQuote = quote,
            voiceSnippetDuration = duration,
            chimeSound = "tibetan",
            isBreakthrough = true
        )
        _uiState.update { state ->
            state.copy(
                alarms = listOf(newAlarm) + state.alarms,
                voiceStudioOpen = false,
                snackbarMessage = "Voice note attached to 07:30 AM Morning Alarm"
            )
        }
    }

    fun toggleTask(id: String) {
        ChimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val active = state.tasks.find { it.id == id }
            if (active != null) {
                val updatedActive = state.tasks.filterNot { it.id == id }
                val updatedDone = listOf(active.copy(completed = true)) + state.completedTasks
                repository.saveTasks(updatedActive)
                repository.saveCompletedTasks(updatedDone)
                state.copy(tasks = updatedActive, completedTasks = updatedDone)
            } else {
                val done = state.completedTasks.find { it.id == id } ?: return@update state
                val updatedDone = state.completedTasks.filterNot { it.id == id }
                val updatedActive = state.tasks + done.copy(completed = false)
                repository.saveTasks(updatedActive)
                repository.saveCompletedTasks(updatedDone)
                state.copy(tasks = updatedActive, completedTasks = updatedDone)
            }
        }
    }

    fun addTask(title: String, categoryId: String, priority: TaskPriority) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        ChimePlayer.play(ChimeType.SOLFEGGIO)
        _uiState.update { state ->
            val cat = state.categories.find { it.id == categoryId }
                ?: state.categories.firstOrNull()
                ?: OneReflectionRepository.defaultCategories().first()
            val newTask = TaskItem(
                id = "task-${System.currentTimeMillis()}",
                title = trimmed,
                description = "Added via quick action",
                category = cat.name,
                categoryId = cat.id,
                dueTime = "05:00 PM",
                completed = false,
                priority = priority
            )
            val updated = listOf(newTask) + state.tasks
            repository.saveTasks(updated)
            state.copy(tasks = updated)
        }
    }

    fun cycleTaskPriority(id: String) {
        ChimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val updatedTasks = state.tasks.map { t ->
                if (t.id == id) t.copy(priority = t.priority.next()) else t
            }
            val updatedDone = state.completedTasks.map { t ->
                if (t.id == id) t.copy(priority = t.priority.next()) else t
            }
            repository.saveTasks(updatedTasks)
            repository.saveCompletedTasks(updatedDone)
            state.copy(tasks = updatedTasks, completedTasks = updatedDone)
        }
    }

    fun addCategory(name: String, color: CategoryColor) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        ChimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val newCat = TaskCategory(
                id = "cat-${System.currentTimeMillis()}",
                name = trimmed,
                color = color,
                isDefault = false
            )
            val updated = state.categories + newCat
            repository.saveCategories(updated)
            state.copy(categories = updated)
        }
    }

    fun updateCategory(id: String, name: String, color: CategoryColor) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        ChimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val updatedCats = state.categories.map { c ->
                if (c.id == id) c.copy(name = trimmed, color = color) else c
            }
            val updatedTasks = state.tasks.map { t ->
                if (t.categoryId == id) t.copy(category = trimmed) else t
            }
            val updatedDone = state.completedTasks.map { t ->
                if (t.categoryId == id) t.copy(category = trimmed) else t
            }
            repository.saveCategories(updatedCats)
            repository.saveTasks(updatedTasks)
            repository.saveCompletedTasks(updatedDone)
            state.copy(categories = updatedCats, tasks = updatedTasks, completedTasks = updatedDone)
        }
    }

    fun deleteCategory(id: String) {
        ChimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            if (state.categories.size <= 1) return@update state
            val updatedCats = state.categories.filterNot { it.id == id }
            val fallback = updatedCats.first()
            val updatedTasks = state.tasks.map { t ->
                if (t.categoryId == id) t.copy(categoryId = fallback.id, category = fallback.name) else t
            }
            val updatedDone = state.completedTasks.map { t ->
                if (t.categoryId == id) t.copy(categoryId = fallback.id, category = fallback.name) else t
            }
            repository.saveCategories(updatedCats)
            repository.saveTasks(updatedTasks)
            repository.saveCompletedTasks(updatedDone)
            state.copy(categories = updatedCats, tasks = updatedTasks, completedTasks = updatedDone)
        }
    }

    fun incrementHydration(liters: Double) {
        ChimePlayer.play(ChimeType.WATER)
        _uiState.update { state ->
            val next = OneReflectionRepository.roundTwoDecimals(state.hydrationLiters + liters)
            repository.saveHydrationLiters(next)
            state.copy(
                hydrationLiters = next,
                snackbarMessage = "+${(liters * 1000).toInt()}ml hydration logged (${next}L)"
            )
        }
    }

    fun toggleReminder(id: String) {
        ChimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            state.copy(
                reminders = state.reminders.map { r ->
                    if (r.id == id) r.copy(completed = !r.completed) else r
                }
            )
        }
    }

    fun dismissPendingRequest(accepted: Boolean) {
        ChimePlayer.play(if (accepted) ChimeType.SOLFEGGIO else ChimeType.CLICK)
        _uiState.update {
            it.copy(
                pendingRequestDismissed = true,
                snackbarMessage = if (accepted) "Julian Vance added to your trusted circle" else "Request dismissed"
            )
        }
    }

    fun updateQuietHours(config: QuietHoursConfig) {
        ChimePlayer.play(ChimeType.TIBETAN)
        _uiState.update {
            it.copy(
                quietHours = config,
                bedtimeSyncOpen = false,
                snackbarMessage = "Bedtime Sync saved (${config.formattedBedtime()} – ${config.formattedWake()})"
            )
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
