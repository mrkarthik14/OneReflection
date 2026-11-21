package com.onereflection.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.onereflection.app.data.AmbientSoundscapeEngine
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.data.OneReflectionRepository
import com.onereflection.app.domain.calculateDailyStreakSummary
import com.onereflection.app.domain.calculateFlowGardenGrowth
import com.onereflection.app.model.AlarmItem
import com.onereflection.app.model.AvatarUrls
import com.onereflection.app.model.CategoryColor
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.ElementalTheme
import com.onereflection.app.model.HabitTone
import com.onereflection.app.model.InAppNotification
import com.onereflection.app.model.MindfulHabit
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ScreenId
import com.onereflection.app.model.TaskAssignee
import com.onereflection.app.model.TaskCategory
import com.onereflection.app.model.TaskItem
import com.onereflection.app.model.TaskPriority
import com.onereflection.app.model.ThemeApplyScope
import com.onereflection.app.model.ThemeModeSlot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class OneReflectionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OneReflectionRepository(application)
    private val chimePlayer = ChimePlayer()
    private val ambientSoundscapeEngine = AmbientSoundscapeEngine()

    private val _uiState = MutableStateFlow(
        OneReflectionUiState(
            oledMode = repository.loadOledMode(),
            selectedDayIndex = repository.loadSelectedDayIndex(),
            dayThemes = repository.loadDayThemes(),
            lightGlobalTheme = repository.loadLightGlobalTheme(),
            darkGlobalTheme = repository.loadDarkGlobalTheme(),
            useGlobalLightTheme = repository.loadUseGlobalLightTheme(),
            pageLightThemes = repository.loadPageThemes(isDark = false),
            pageDarkThemes = repository.loadPageThemes(isDark = true),
            ambientSoundEnabled = repository.loadAmbientSoundEnabled(),
            ambientSoundVolume = repository.loadAmbientSoundVolume(),
            alarms = repository.defaultAlarms(),
            categories = repository.loadCategories(),
            tasks = repository.loadTasks(),
            completedTasks = repository.loadCompletedTasks(),
            reminders = repository.defaultReminders(),
            habits = repository.loadHabits(),
            hydrationLiters = repository.loadHydration()
        )
    )
    val uiState: StateFlow<OneReflectionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState
                .map { state ->
                    Triple(
                        state.ambientSoundEnabled,
                        state.activeElementalTheme,
                        state.ambientSoundVolume
                    )
                }
                .distinctUntilChanged()
                .collect { (enabled, theme, volume) ->
                    ambientSoundscapeEngine.update(
                        enabled = enabled,
                        theme = theme,
                        volume = volume
                    )
                }
        }
    }

    fun toggleAmbientSound() {
        _uiState.update { state ->
            val nextEnabled = !state.ambientSoundEnabled
            repository.saveAmbientSoundEnabled(nextEnabled)
            val activeTheme = state.activeElementalTheme
            if (nextEnabled) {
                chimePlayer.play(chimeForElement(activeTheme))
            } else {
                chimePlayer.play(ChimeType.CLICK)
            }
            val notification = if (nextEnabled) {
                InAppNotification(
                    id = System.currentTimeMillis(),
                    title = "${activeTheme.displayName} Soundscape Active",
                    message = "Now playing ${activeTheme.soundscapeTitle} — adapts smoothly as your Elemental Theme changes.",
                    badgeText = activeTheme.soundscapeShort.uppercase()
                )
            } else {
                state.activeNotification
            }
            state.copy(
                ambientSoundEnabled = nextEnabled,
                activeNotification = notification
            )
        }
    }

    fun setAmbientSoundVolume(volume: Float) {
        val clamped = volume.coerceIn(0.05f, 1.0f)
        repository.saveAmbientSoundVolume(clamped)
        _uiState.update { it.copy(ambientSoundVolume = clamped) }
    }

    fun onAppForegrounded() {
        ambientSoundscapeEngine.setForeground(true)
    }

    fun onAppBackgrounded() {
        ambientSoundscapeEngine.setForeground(false)
    }

    fun playChime(type: ChimeType) {
        chimePlayer.play(type)
    }

    fun stopChime() {
        chimePlayer.stop()
    }

    fun navigate(screen: ScreenId) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun toggleOledMode() {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val next = !state.oledMode
            repository.saveOledMode(next)
            state.copy(oledMode = next)
        }
    }

    fun openVoiceStudio() {
        chimePlayer.play(ChimeType.TIBETAN)
        _uiState.update { it.copy(voiceStudioOpen = true) }
    }

    fun closeVoiceStudio() {
        _uiState.update { it.copy(voiceStudioOpen = false) }
    }

    fun openBedtimeSync() {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { it.copy(bedtimeSyncOpen = true) }
    }

    fun closeBedtimeSync() {
        _uiState.update { it.copy(bedtimeSyncOpen = false) }
    }

    fun openThemeSettings() {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { it.copy(themeSettingsOpen = true) }
    }

    fun closeThemeSettings() {
        _uiState.update { it.copy(themeSettingsOpen = false) }
    }

    fun selectDay(index: Int) {
        val safe = index.coerceIn(0, 6)
        chimePlayer.play(ChimeType.CLICK)
        repository.saveSelectedDayIndex(safe)
        repository.saveUseGlobalLightTheme(false)
        _uiState.update { it.copy(selectedDayIndex = safe, useGlobalLightTheme = false) }
    }

    private fun chimeForElement(theme: ElementalTheme): ChimeType = when (theme) {
        ElementalTheme.EMBER -> ChimeType.TIBETAN
        ElementalTheme.SOLAR -> ChimeType.SOLFEGGIO
        ElementalTheme.BREEZE -> ChimeType.RIVER
        ElementalTheme.BLOOM -> ChimeType.PIANO
        ElementalTheme.CHERRY_BLOSSOM -> ChimeType.PIANO
        ElementalTheme.SNOW -> ChimeType.NORDIC
        ElementalTheme.RAIN -> ChimeType.WATER
    }

    fun setDayTheme(dayIndex: Int, theme: ElementalTheme) {
        val safe = dayIndex.coerceIn(0, 6)
        chimePlayer.play(chimeForElement(theme))
        _uiState.update { state ->
            val updated = state.dayThemes.mapIndexed { idx, existing ->
                if (idx == safe) theme else existing
            }
            repository.saveDayThemes(updated)
            repository.saveUseGlobalLightTheme(false)
            state.copy(dayThemes = updated, selectedDayIndex = safe, useGlobalLightTheme = false)
        }
    }

    fun applyThemeWithScope(
        scope: ThemeApplyScope,
        modeSlot: ThemeModeSlot,
        screen: ScreenId,
        theme: ElementalTheme
    ) {
        chimePlayer.play(chimeForElement(theme))
        _uiState.update { state ->
            when (scope) {
                ThemeApplyScope.ENTIRE_APP -> {
                    if (modeSlot == ThemeModeSlot.DARK) {
                        repository.saveDarkGlobalTheme(theme)
                        state.copy(
                            darkGlobalTheme = theme,
                            activeNotification = InAppNotification(
                                id = System.currentTimeMillis(),
                                title = "Dark Theme Updated",
                                message = "${theme.displayName} applied across the entire app in Dark Mode.",
                                badgeText = "DARK · APP"
                            )
                        )
                    } else {
                        val updatedDays = List(7) { theme }
                        repository.saveLightGlobalTheme(theme)
                        repository.saveUseGlobalLightTheme(true)
                        repository.saveDayThemes(updatedDays)
                        state.copy(
                            lightGlobalTheme = theme,
                            useGlobalLightTheme = true,
                            dayThemes = updatedDays,
                            activeNotification = InAppNotification(
                                id = System.currentTimeMillis(),
                                title = "Light Theme Updated",
                                message = "${theme.displayName} applied across the entire app in Light Mode.",
                                badgeText = "LIGHT · APP"
                            )
                        )
                    }
                }

                ThemeApplyScope.SINGLE_PAGE -> {
                    if (modeSlot == ThemeModeSlot.DARK) {
                        val updated = state.pageDarkThemes + (screen to theme)
                        repository.savePageThemes(isDark = true, themes = updated)
                        state.copy(
                            pageDarkThemes = updated,
                            activeNotification = InAppNotification(
                                id = System.currentTimeMillis(),
                                title = "${screen.subtitle} Dark Theme",
                                message = "${theme.displayName} applied to this page only in Dark Mode.",
                                badgeText = "DARK · PAGE"
                            )
                        )
                    } else {
                        val updated = state.pageLightThemes + (screen to theme)
                        repository.savePageThemes(isDark = false, themes = updated)
                        state.copy(
                            pageLightThemes = updated,
                            activeNotification = InAppNotification(
                                id = System.currentTimeMillis(),
                                title = "${screen.subtitle} Light Theme",
                                message = "${theme.displayName} applied to this page only in Light Mode.",
                                badgeText = "LIGHT · PAGE"
                            )
                        )
                    }
                }
            }
        }
    }

    fun clearPageTheme(screen: ScreenId, modeSlot: ThemeModeSlot) {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            if (modeSlot == ThemeModeSlot.DARK) {
                val updated = state.pageDarkThemes - screen
                repository.savePageThemes(isDark = true, themes = updated)
                state.copy(pageDarkThemes = updated)
            } else {
                val updated = state.pageLightThemes - screen
                repository.savePageThemes(isDark = false, themes = updated)
                state.copy(pageLightThemes = updated)
            }
        }
    }

    fun applyThemeToAllDays(theme: ElementalTheme) {
        applyThemeWithScope(
            scope = ThemeApplyScope.ENTIRE_APP,
            modeSlot = if (_uiState.value.oledMode) ThemeModeSlot.DARK else ThemeModeSlot.LIGHT,
            screen = _uiState.value.currentScreen,
            theme = theme
        )
    }

    fun resetDefaultDayThemes() {
        chimePlayer.play(ChimeType.CLICK)
        val defaults = ElementalTheme.defaultWeek()
        repository.saveDayThemes(defaults)
        repository.saveLightGlobalTheme(ElementalTheme.CHERRY_BLOSSOM)
        repository.saveDarkGlobalTheme(ElementalTheme.RAIN)
        repository.saveUseGlobalLightTheme(false)
        repository.savePageThemes(isDark = false, themes = emptyMap())
        repository.savePageThemes(isDark = true, themes = emptyMap())
        _uiState.update {
            it.copy(
                dayThemes = defaults,
                lightGlobalTheme = ElementalTheme.CHERRY_BLOSSOM,
                darkGlobalTheme = ElementalTheme.RAIN,
                useGlobalLightTheme = false,
                pageLightThemes = emptyMap(),
                pageDarkThemes = emptyMap(),
                activeNotification = InAppNotification(
                    id = System.currentTimeMillis(),
                    title = "Themes Reset",
                    message = "Restored 7-day elemental rhythm and cleared page overrides.",
                    badgeText = "RESET"
                )
            )
        }
    }

    fun dismissNotification() {
        _uiState.update { it.copy(activeNotification = null) }
    }

    fun showInfoNotification(title: String, message: String, badgeText: String = "SHARED") {
        _uiState.update {
            it.copy(
                activeNotification = InAppNotification(
                    id = System.currentTimeMillis(),
                    title = title,
                    message = message,
                    badgeText = badgeText
                )
            )
        }
    }

    fun saveVoiceMemoAlarm(quote: String, duration: String) {
        chimePlayer.play(ChimeType.TIBETAN)
        val newAlarm = AlarmItem(
            id = "alarm-${System.currentTimeMillis()}",
            title = "Voice Wake-up Memo",
            time = "07:30",
            period = "AM",
            days = listOf("M", "T", "W", "T", "F"),
            enabled = true,
            category = "shared",
            creatorName = "Elena (You)",
            voiceSnippetQuote = quote,
            voiceSnippetDuration = duration,
            chimeSound = ChimeType.TIBETAN,
            isBreakthrough = true
        )
        _uiState.update { it.copy(alarms = listOf(newAlarm) + it.alarms, voiceStudioOpen = false) }
    }

    fun toggleAlarm(id: String) {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            state.copy(alarms = state.alarms.map { if (it.id == id) it.copy(enabled = !it.enabled) else it })
        }
    }

    fun toggleTask(id: String) {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val active = state.tasks.find { it.id == id }
            if (active != null) {
                val updatedTasks = state.tasks.filterNot { it.id == id }
                val completedAssignees = active.assignees.map { it.copy(completionPercent = 100) }
                val updatedDone = state.completedTasks + active.copy(
                    completed = true,
                    progressPercent = 100,
                    carryOverNextDay = false,
                    assignees = completedAssignees
                )
                repository.saveTasks(updatedTasks)
                repository.saveCompletedTasks(updatedDone)
                state.copy(
                    tasks = updatedTasks,
                    completedTasks = updatedDone,
                    activeNotification = InAppNotification(
                        id = System.currentTimeMillis(),
                        title = "Task Harvested (100%)",
                        message = "\"${active.title}\" bloomed on your Great Tree!",
                        badgeText = "100% DONE"
                    )
                )
            } else {
                val done = state.completedTasks.find { it.id == id }
                if (done != null) {
                    val updatedDone = state.completedTasks.filterNot { it.id == id }
                    val reopenedAssignees = done.assignees.map { it.copy(completionPercent = 50) }
                    val updatedTasks = state.tasks + done.copy(
                        completed = false,
                        progressPercent = 50,
                        assignees = reopenedAssignees
                    )
                    repository.saveTasks(updatedTasks)
                    repository.saveCompletedTasks(updatedDone)
                    state.copy(tasks = updatedTasks, completedTasks = updatedDone)
                } else state
            }
        }
    }

    fun updateTaskProgress(taskId: String, percent: Int, triggerNotification: Boolean) {
        val clamped = percent.coerceIn(0, 100)
        _uiState.update { state ->
            val active = state.tasks.find { it.id == taskId } ?: return@update state
            if (clamped >= 100 && triggerNotification) {
                chimePlayer.play(ChimeType.SOLFEGGIO)
                val updatedTasks = state.tasks.filterNot { it.id == taskId }
                val completedAssignees = active.assignees.map { person ->
                    if (person.name.equals("Elena", ignoreCase = true)) person.copy(completionPercent = 100)
                    else person.copy(completionPercent = maxOf(person.completionPercent, 100))
                }
                val doneItem = active.copy(
                    completed = true,
                    progressPercent = 100,
                    carryOverNextDay = false,
                    assignees = completedAssignees
                )
                val updatedDone = state.completedTasks + doneItem
                repository.saveTasks(updatedTasks)
                repository.saveCompletedTasks(updatedDone)
                return@update state.copy(
                    tasks = updatedTasks,
                    completedTasks = updatedDone,
                    activeNotification = InAppNotification(
                        id = System.currentTimeMillis(),
                        title = "Flow Completed · 100%",
                        message = "\"${active.title}\" added a new branch to your Great Tree.",
                        badgeText = "100%"
                    )
                )
            }

            val updatedAssignees = if (active.assignees.isEmpty()) {
                listOf(TaskAssignee("Elena", AvatarUrls.ELENA, "EV", completionPercent = clamped))
            } else {
                val hasElena = active.assignees.any { it.name.equals("Elena", ignoreCase = true) }
                if (hasElena) {
                    active.assignees.map { person ->
                        if (person.name.equals("Elena", ignoreCase = true)) {
                            person.copy(completionPercent = clamped)
                        } else {
                            person
                        }
                    }
                } else {
                    active.assignees.mapIndexed { idx, person ->
                        if (idx == 0) person.copy(completionPercent = clamped) else person
                    }
                }
            }

            val updatedTasks = state.tasks.map { task ->
                if (task.id == taskId) {
                    task.copy(progressPercent = clamped, assignees = updatedAssignees)
                } else {
                    task
                }
            }
            repository.saveTasks(updatedTasks)

            val notification = if (triggerNotification && clamped in 1..99) {
                chimePlayer.play(if (clamped == 50) ChimeType.PIANO else ChimeType.CLICK)
                val remaining = 100 - clamped
                val title = when {
                    clamped == 50 -> "Halfway Completed (50%)"
                    clamped >= 75 -> "Almost There ($clamped%)"
                    else -> "Progress Saved ($clamped%)"
                }
                InAppNotification(
                    id = System.currentTimeMillis(),
                    title = title,
                    message = "\"${active.title}\" is $clamped% done. Finish the remaining $remaining% tomorrow?",
                    badgeText = "$clamped%",
                    actionLabel = "Finish Tomorrow",
                    actionTaskId = active.id
                )
            } else {
                state.activeNotification
            }

            state.copy(tasks = updatedTasks, activeNotification = notification)
        }
    }

    fun scheduleTaskForNextDay(taskId: String) {
        chimePlayer.play(ChimeType.TIBETAN)
        _uiState.update { state ->
            val target = state.tasks.find { it.id == taskId } ?: return@update state
            val nextCarry = !target.carryOverNextDay
            val updatedDue = if (nextCarry) "Tomorrow · 09:00 AM" else "Today · 05:00 PM"
            val updatedTasks = state.tasks.map { task ->
                if (task.id == taskId) {
                    task.copy(carryOverNextDay = nextCarry, dueTime = updatedDue)
                } else {
                    task
                }
            }
            repository.saveTasks(updatedTasks)
            val remaining = (100 - target.progressPercent).coerceAtLeast(0)
            state.copy(
                tasks = updatedTasks,
                activeNotification = InAppNotification(
                    id = System.currentTimeMillis(),
                    title = if (nextCarry) "Carried Over to Tomorrow" else "Returned to Today",
                    message = if (nextCarry) {
                        "\"${target.title}\" (${target.progressPercent}% done, $remaining% left) scheduled for tomorrow."
                    } else {
                        "\"${target.title}\" moved back to today's active queue."
                    },
                    badgeText = if (nextCarry) "NEXT DAY" else "TODAY"
                )
            )
        }
    }

    fun addTask(title: String, categoryId: String, priority: TaskPriority) {
        if (title.isBlank()) return
        chimePlayer.play(ChimeType.SOLFEGGIO)
        _uiState.update { state ->
            val cat = state.categories.find { it.id == categoryId } ?: state.categories.first()
            val task = TaskItem(
                id = "task-${System.currentTimeMillis()}",
                title = title.trim(),
                description = "Added via quick action",
                category = cat.name,
                categoryId = cat.id,
                dueTime = "05:00 PM",
                completed = false,
                progressPercent = 0,
                priority = priority,
                assignees = listOf(TaskAssignee("Elena", AvatarUrls.ELENA, "EV", completionPercent = 0))
            )
            val updated = listOf(task) + state.tasks
            repository.saveTasks(updated)
            state.copy(tasks = updated)
        }
    }

    fun cycleTaskPriority(id: String) {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val updatedTasks = state.tasks.map {
                if (it.id == id) it.copy(priority = it.priority.next()) else it
            }
            val updatedDone = state.completedTasks.map {
                if (it.id == id) it.copy(priority = it.priority.next()) else it
            }
            repository.saveTasks(updatedTasks)
            repository.saveCompletedTasks(updatedDone)
            state.copy(tasks = updatedTasks, completedTasks = updatedDone)
        }
    }

    fun addCategory(name: String, color: CategoryColor) {
        if (name.isBlank()) return
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val updated = state.categories + TaskCategory(
                id = "cat-${System.currentTimeMillis()}",
                name = name.trim(),
                color = color
            )
            repository.saveCategories(updated)
            state.copy(categories = updated)
        }
    }

    fun updateCategory(id: String, name: String, color: CategoryColor) {
        if (name.isBlank()) return
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val trimmed = name.trim()
            val updatedCats = state.categories.map {
                if (it.id == id) it.copy(name = trimmed, color = color) else it
            }
            val updatedTasks = state.tasks.map {
                if (it.categoryId == id) it.copy(category = trimmed) else it
            }
            val updatedDone = state.completedTasks.map {
                if (it.categoryId == id) it.copy(category = trimmed) else it
            }
            repository.saveCategories(updatedCats)
            repository.saveTasks(updatedTasks)
            repository.saveCompletedTasks(updatedDone)
            state.copy(categories = updatedCats, tasks = updatedTasks, completedTasks = updatedDone)
        }
    }

    fun deleteCategory(id: String) {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val remaining = state.categories.filterNot { it.id == id }
            if (remaining.isEmpty()) return@update state
            val fallback = remaining.first()
            val updatedTasks = state.tasks.map {
                if (it.categoryId == id) it.copy(categoryId = fallback.id, category = fallback.name) else it
            }
            val updatedDone = state.completedTasks.map {
                if (it.categoryId == id) it.copy(categoryId = fallback.id, category = fallback.name) else it
            }
            repository.saveCategories(remaining)
            repository.saveTasks(updatedTasks)
            repository.saveCompletedTasks(updatedDone)
            state.copy(categories = remaining, tasks = updatedTasks, completedTasks = updatedDone)
        }
    }

    fun addHydration(litersDelta: Double) {
        chimePlayer.play(ChimeType.WATER)
        _uiState.update { state ->
            val previousStage = state.flowGardenGrowth.stage
            val next = ((state.hydrationLiters + litersDelta) * 100.0).roundToInt() / 100.0
            repository.saveHydration(next)
            val nextGrowth = calculateFlowGardenGrowth(
                habits = state.habits,
                hydrationLiters = next,
                completedTasksCount = state.completedTasks.size,
                totalTasksCount = state.tasks.size + state.completedTasks.size
            )
            val notification = if (nextGrowth.stage.ordinal > previousStage.ordinal) {
                chimePlayer.play(ChimeType.SOLFEGGIO)
                InAppNotification(
                    id = System.currentTimeMillis(),
                    title = "Flow Garden Evolved · ${nextGrowth.stage.displayName}!",
                    message = "Your daily hydration nourished your plant into a ${nextGrowth.stage.displayName} (${nextGrowth.growthPercent}%).",
                    badgeText = nextGrowth.stage.displayName.uppercase()
                )
            } else {
                state.activeNotification
            }
            state.copy(hydrationLiters = next, activeNotification = notification)
        }
    }

    fun incrementHabit(id: String) {
        _uiState.update { state ->
            val previousStage = state.flowGardenGrowth.stage
            val previousStreak = state.dailyStreak
            var justCompleted = false
            val dayIdx = state.selectedDayIndex.coerceIn(0, 6)
            val updated = state.habits.map { habit ->
                if (habit.id != id) {
                    habit
                } else {
                    val wasComplete = habit.isCompleted
                    val nextValue = (habit.currentValue + habit.stepValue).coerceAtMost(habit.targetValue)
                    val nowComplete = nextValue >= habit.targetValue
                    if (!wasComplete && nowComplete) {
                        justCompleted = true
                    }
                    val updatedHistory = habit.weeklyHistory.mapIndexed { idx, done ->
                        if (idx == dayIdx) nowComplete else done
                    }
                    habit.copy(
                        currentValue = nextValue,
                        streakDays = if (!wasComplete && nowComplete) habit.streakDays + 1 else habit.streakDays,
                        weeklyHistory = updatedHistory
                    )
                }
            }
            val nextGrowth = calculateFlowGardenGrowth(
                habits = updated,
                hydrationLiters = state.hydrationLiters,
                completedTasksCount = state.completedTasks.size,
                totalTasksCount = state.tasks.size + state.completedTasks.size
            )
            val nextStreak = calculateDailyStreakSummary(
                habits = updated,
                selectedDayIndex = state.selectedDayIndex
            )
            val stageEvolved = nextGrowth.stage.ordinal > previousStage.ordinal
            val streakExtended = !previousStreak.todayGoalAchieved && nextStreak.todayGoalAchieved
            val perfectDayUnlocked = !previousStreak.isPerfectDay && nextStreak.isPerfectDay
            chimePlayer.play(
                if (justCompleted || stageEvolved || streakExtended || perfectDayUnlocked) {
                    ChimeType.SOLFEGGIO
                } else {
                    ChimeType.CLICK
                }
            )
            repository.saveHabits(updated)
            val notification = when {
                stageEvolved -> InAppNotification(
                    id = System.currentTimeMillis(),
                    title = "Flow Garden Evolved · ${nextGrowth.stage.displayName}!",
                    message = "Daily habit progress grew your plant to ${nextGrowth.stage.displayName} (${nextGrowth.growthPercent}% vitality).",
                    badgeText = nextGrowth.stage.displayName.uppercase()
                )
                perfectDayUnlocked -> InAppNotification(
                    id = System.currentTimeMillis(),
                    title = "Perfect Harmony · ${nextStreak.currentStreakDays}-Day Streak!",
                    message = "Every daily habit goal achieved today! Your Flow Garden is radiating full vitality.",
                    badgeText = "PERFECT DAY"
                )
                streakExtended -> InAppNotification(
                    id = System.currentTimeMillis(),
                    title = "Daily Streak Extended · ${nextStreak.currentStreakDays} Days!",
                    message = "Today's habit goal is secured (${nextStreak.completedHabitsToday}/${nextStreak.totalHabitsCount} rituals complete).",
                    badgeText = "${nextStreak.currentStreakDays}D STREAK"
                )
                else -> state.activeNotification
            }
            state.copy(habits = updated, activeNotification = notification)
        }
    }

    fun decrementHabit(id: String) {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            val dayIdx = state.selectedDayIndex.coerceIn(0, 6)
            val updated = state.habits.map { habit ->
                if (habit.id != id) {
                    habit
                } else {
                    val wasComplete = habit.isCompleted
                    val nextValue = (habit.currentValue - habit.stepValue).coerceAtLeast(0)
                    val nowComplete = nextValue >= habit.targetValue
                    val updatedHistory = habit.weeklyHistory.mapIndexed { idx, done ->
                        if (idx == dayIdx) nowComplete else done
                    }
                    habit.copy(
                        currentValue = nextValue,
                        streakDays = if (wasComplete && !nowComplete) (habit.streakDays - 1).coerceAtLeast(0) else habit.streakDays,
                        weeklyHistory = updatedHistory
                    )
                }
            }
            repository.saveHabits(updated)
            state.copy(habits = updated)
        }
    }

    fun addHabit(
        title: String,
        subtitle: String,
        unit: String,
        targetValue: Int,
        stepValue: Int,
        tone: HabitTone
    ) {
        if (title.isBlank()) return
        chimePlayer.play(ChimeType.SOLFEGGIO)
        val safeTarget = targetValue.coerceAtLeast(1)
        val safeStep = stepValue.coerceIn(1, safeTarget)
        val newHabit = MindfulHabit(
            id = "habit-${System.currentTimeMillis()}",
            title = title.trim(),
            subtitle = subtitle.trim().ifBlank { "Mindful daily intention" },
            unit = unit.trim().ifBlank { "min" },
            currentValue = 0,
            targetValue = safeTarget,
            stepValue = safeStep,
            streakDays = 1,
            tone = tone,
            weeklyHistory = listOf(false, false, false, false, false, false, false)
        )
        _uiState.update { state ->
            val updated = listOf(newHabit) + state.habits
            repository.saveHabits(updated)
            state.copy(habits = updated)
        }
    }

    fun deleteHabit(id: String) {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            if (state.habits.size <= 1) return@update state
            val updated = state.habits.filterNot { it.id == id }
            repository.saveHabits(updated)
            state.copy(habits = updated)
        }
    }

    fun toggleReminder(id: String) {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            state.copy(reminders = state.reminders.map {
                if (it.id == id) it.copy(completed = !it.completed) else it
            })
        }
    }

    fun adjustBedtime(deltaMinutes: Int) {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            var next = (state.bedtimeMinutes + deltaMinutes) % (24 * 60)
            if (next < 0) next += 24 * 60
            state.copy(bedtimeMinutes = next)
        }
    }

    fun adjustWake(deltaMinutes: Int) {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { state ->
            var next = (state.wakeMinutes + deltaMinutes) % (24 * 60)
            if (next < 0) next += 24 * 60
            state.copy(wakeMinutes = next)
        }
    }

    fun toggleSyncSleepWithPartner() {
        chimePlayer.play(ChimeType.CLICK)
        _uiState.update { it.copy(syncSleepWithPartner = !it.syncSleepWithPartner) }
    }

    override fun onCleared() {
        super.onCleared()
        chimePlayer.stop()
        ambientSoundscapeEngine.release()
    }
}
