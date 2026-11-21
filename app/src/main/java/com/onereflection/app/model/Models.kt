package com.onereflection.app.model

import com.onereflection.app.domain.DailyStreakSummary
import com.onereflection.app.domain.FlowGardenGrowthState
import com.onereflection.app.domain.calculateDailyStreakSummary
import com.onereflection.app.domain.calculateFlowGardenGrowth

enum class ScreenId(val subtitle: String) {
    HOME("Home"),
    ALARMS("Alarms"),
    TASKS("Tasks & Shared Flows"),
    CHATS("Chats & Circles"),
    PEOPLE("Inner Circle & Key"),
    HABITS("Daily Rituals & Habits"),
    REMINDERS("Reminders & Calendar Planner"),
    QUIET_BREAKTHROUGH("Bedtime Quiet Hours"),
    SANCTUARY_TREE("Great Tree Harvest"),
    PROFILE("Profile & Harmony")
}

enum class CategoryColor(val label: String) {
    PINK("Candy Pink"),
    YELLOW("Butter Yellow"),
    LAVENDER("Lavender Blue"),
    MATCHA("Soft Matcha"),
    LILAC("Warm Lilac"),
    CYAN("Soft Cyan"),
    SLATE("Surface Slate")
}

enum class TaskPriority(val label: String, val weight: Int) {
    HIGH("High", 3),
    MEDIUM("Medium", 2),
    LOW("Low", 1);

    fun next(): TaskPriority = when (this) {
        HIGH -> MEDIUM
        MEDIUM -> LOW
        LOW -> HIGH
    }
}

enum class TaskSortMode {
    PRIORITY_DESC,
    PRIORITY_ASC,
    TIME,
    DEFAULT
}

enum class ChimeType {
    TIBETAN,
    NORDIC,
    SOLFEGGIO,
    WATER,
    CLICK,
    PIANO,
    RIVER
}

data class TaskCategory(
    val id: String,
    val name: String,
    val color: CategoryColor,
    val isDefault: Boolean = false
)

data class TaskAssignee(
    val name: String,
    val avatar: String? = null,
    val initials: String? = null,
    val completionPercent: Int = 0
)

data class TaskItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val categoryId: String,
    val dueTime: String,
    val completed: Boolean = false,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val progressPercent: Int = if (completed) 100 else 0,
    val carryOverNextDay: Boolean = false,
    val subtasksDone: Int? = null,
    val subtasksTotal: Int? = null,
    val voiceBriefQuote: String? = null,
    val voiceBriefDuration: String? = null,
    val codeBranch: String? = null,
    val streak: Int? = null,
    val assignees: List<TaskAssignee> = emptyList()
)

data class AlarmItem(
    val id: String,
    val title: String,
    val time: String,
    val period: String,
    val days: List<String>,
    val enabled: Boolean,
    val category: String,
    val creatorName: String? = null,
    val creatorRole: String? = null,
    val creatorAvatar: String? = null,
    val voiceSnippetQuote: String? = null,
    val voiceSnippetDuration: String? = null,
    val chimeSound: ChimeType = ChimeType.TIBETAN,
    val isBreakthrough: Boolean = false
)

data class ReminderEvent(
    val id: String,
    val title: String,
    val location: String,
    val time: String,
    val category: String,
    val completed: Boolean = false,
    val source: String = "onereflection",
    val partnerNote: String? = null
)

enum class HabitTone(val label: String) {
    TERRACOTTA("Terracotta"),
    SAGE("Sage"),
    GOLD("Burnished Gold"),
    INDIGO("Twilight Indigo"),
    PLUM("Warm Plum")
}

data class MindfulHabit(
    val id: String,
    val title: String,
    val subtitle: String,
    val unit: String,
    val currentValue: Int,
    val targetValue: Int,
    val stepValue: Int = 1,
    val streakDays: Int = 1,
    val tone: HabitTone = HabitTone.SAGE,
    val weeklyHistory: List<Boolean> = listOf(true, true, true, true, false, false, false)
) {
    val progress: Float
        get() = if (targetValue <= 0) 0f else (currentValue.toFloat() / targetValue.toFloat()).coerceIn(0f, 1f)

    val isCompleted: Boolean
        get() = targetValue > 0 && currentValue >= targetValue
}

enum class ElementalTheme(
    val code: String,
    val displayName: String,
    val tagline: String,
    val soundscapeTitle: String,
    val soundscapeShort: String,
    val soundscapeDescription: String
) {
    EMBER(
        code = "01",
        displayName = "Ember",
        tagline = "FIRE  ·  COURAGE",
        soundscapeTitle = "Evening Crickets & Hearth",
        soundscapeShort = "Crickets",
        soundscapeDescription = "Warm twilight crickets & soft glowing embers"
    ),
    SOLAR(
        code = "02",
        displayName = "Solar",
        tagline = "SUN  ·  VITALITY",
        soundscapeTitle = "Sunlit Meadow & Songbirds",
        soundscapeShort = "Songbirds",
        soundscapeDescription = "Golden morning warmth & gentle birdsong"
    ),
    BREEZE(
        code = "03",
        displayName = "Breeze",
        tagline = "AIR  ·  CLARITY",
        soundscapeTitle = "Alpine Wind & Air Currents",
        soundscapeShort = "Wind",
        soundscapeDescription = "Sweeping valley wind & rustling leaves"
    ),
    BLOOM(
        code = "04",
        displayName = "Bloom",
        tagline = "HEART  ·  WARMTH",
        soundscapeTitle = "Lotus Garden & Dew Chimes",
        soundscapeShort = "Garden Chimes",
        soundscapeDescription = "Serene botanical breeze & soft dew chimes"
    ),
    CHERRY_BLOSSOM(
        code = "05",
        displayName = "Cherry Blossom",
        tagline = "SAKURA  ·  SERENITY",
        soundscapeTitle = "Sakura Grove & Bamboo Wind",
        soundscapeShort = "Sakura Breeze",
        soundscapeDescription = "Spring petal drift & hollow bamboo chimes"
    ),
    SNOW(
        code = "06",
        displayName = "Snow",
        tagline = "FROST  ·  STILLNESS",
        soundscapeTitle = "Arctic Stillness & Frost",
        soundscapeShort = "Frost Wind",
        soundscapeDescription = "Hushed winter air & crystalline frost shimmer"
    ),
    RAIN(
        code = "07",
        displayName = "Rain",
        tagline = "MONSOON  ·  CLARITY",
        soundscapeTitle = "Monsoon Rain & Droplets",
        soundscapeShort = "Rain",
        soundscapeDescription = "Steady rainfall patter & soft water ripples"
    );

    companion object {
        fun defaultWeek(): List<ElementalTheme> = listOf(
            EMBER,           // Mon 13
            SOLAR,           // Tue 14
            BREEZE,          // Wed 15
            BLOOM,           // Thu 16
            CHERRY_BLOSSOM,  // Fri 17
            SNOW,            // Sat 18
            RAIN             // Sun 19
        )
    }
}

enum class ThemeApplyScope {
    ENTIRE_APP,
    SINGLE_PAGE
}

enum class ThemeModeSlot {
    LIGHT,
    DARK
}

data class InAppNotification(
    val id: Long,
    val title: String,
    val message: String,
    val badgeText: String = "",
    val actionLabel: String? = null,
    val actionTaskId: String? = null
)

data class OneReflectionUiState(
    val currentScreen: ScreenId = ScreenId.HOME,
    val oledMode: Boolean = false,
    val voiceStudioOpen: Boolean = false,
    val bedtimeSyncOpen: Boolean = false,
    val themeSettingsOpen: Boolean = false,
    val selectedDayIndex: Int = 3,
    val dayThemes: List<ElementalTheme> = ElementalTheme.defaultWeek(),
    val lightGlobalTheme: ElementalTheme = ElementalTheme.CHERRY_BLOSSOM,
    val darkGlobalTheme: ElementalTheme = ElementalTheme.RAIN,
    val useGlobalLightTheme: Boolean = false,
    val pageLightThemes: Map<ScreenId, ElementalTheme> = emptyMap(),
    val pageDarkThemes: Map<ScreenId, ElementalTheme> = emptyMap(),
    val ambientSoundEnabled: Boolean = false,
    val ambientSoundVolume: Float = 0.65f,
    val activeNotification: InAppNotification? = null,
    val alarms: List<AlarmItem> = emptyList(),
    val categories: List<TaskCategory> = emptyList(),
    val tasks: List<TaskItem> = emptyList(),
    val completedTasks: List<TaskItem> = emptyList(),
    val reminders: List<ReminderEvent> = emptyList(),
    val habits: List<MindfulHabit> = emptyList(),
    val hydrationLiters: Double = 1.8,
    val bedtimeMinutes: Int = 22 * 60 + 30,
    val wakeMinutes: Int = 7 * 60,
    val syncSleepWithPartner: Boolean = true
) {
    fun resolvedThemeFor(screen: ScreenId, isDark: Boolean): ElementalTheme {
        return if (isDark) {
            pageDarkThemes[screen] ?: darkGlobalTheme
        } else {
            pageLightThemes[screen]
                ?: if (useGlobalLightTheme) lightGlobalTheme
                else dayThemes.getOrElse(selectedDayIndex) { lightGlobalTheme }
        }
    }

    fun hasCustomPageTheme(screen: ScreenId, isDark: Boolean): Boolean =
        if (isDark) pageDarkThemes.containsKey(screen) else pageLightThemes.containsKey(screen)

    val activeElementalTheme: ElementalTheme
        get() = resolvedThemeFor(currentScreen, oledMode)

    val flowGardenGrowth: FlowGardenGrowthState
        get() = calculateFlowGardenGrowth(
            habits = habits,
            hydrationLiters = hydrationLiters,
            completedTasksCount = completedTasks.size,
            totalTasksCount = tasks.size + completedTasks.size,
            periodDays = 7
        )

    val dailyStreak: DailyStreakSummary
        get() = calculateDailyStreakSummary(
            habits = habits,
            selectedDayIndex = selectedDayIndex
        )
}

object AvatarUrls {
    const val ELENA = "https://lh3.googleusercontent.com/aida-public/AB6AXuBE7jpIocCISc-CQYfgAnahVZmLt7VB9UJKlTFKJX9JEBM0KeqWHrnY0EOlX7Xr016w-StCO-DfHViu0fGfhqLc5xXymxW-Idb1KIskGYbnQtfZt9R24CzmTiiG97t1oqT6BRybyaDZrINztAvlimWlhHUhwdR_IyfwE_j6uA1V6l9K1-IiXMA6jRa5-JRXxNhlmHpahWf3zorVKk7seurAn3RnMPBzb_UQpUlVYYBvZMwd1oKmMCq0"
    const val MAYA = "https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
    const val LEO = "https://lh3.googleusercontent.com/aida-public/AB6AXuB-p-Cg99Ksi_ssT1yEzjo7dZ74bX9c286fmjBrB3JDt9C2-RVpWDUyQS3BcXmbfuc_5a3KPKxqZQ6HMO9VrNvDKyO_cDtWwfK8dUOgy7u-ooaZG5Oqa4SKmjHSsIEeLJKvVrTv2jg4kbeGT94tWbu4b9PvkRK2IieztVYz6V_0FOwskVywC5epxeiRXKV9YTW6j7Azw-udLYcHbZl6lbAB_u_ruM4C-5pOAs3oT2TtxmP9_QheqmgU"
    const val SOFIA = "https://lh3.googleusercontent.com/aida-public/AB6AXuCYk_21N695Jts_2y9t9DTRLnqBKYt37JiJRzq1NIv0wmakZo9kLQtkH6vCfxtcSLGR9nDpaUP93NGFX-UDhJG5T_e0PVL7nt_w8niC5mp9N_a2qPlrje-kCSNx0H5uymVl_VPTzeODHeDotNL1q3m9lrveFP7tMaYabytMVrSx117nqmfZg3JNVXmAf02qSYvj_V8UqYymiuasWbegnQAywv2jec0DFhoSdfKvTlpFL7f0fHQG4704"
}
