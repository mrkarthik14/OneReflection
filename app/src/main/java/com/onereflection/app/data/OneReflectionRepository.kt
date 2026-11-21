package com.onereflection.app.data

import android.content.Context
import com.onereflection.app.model.AlarmItem
import com.onereflection.app.model.AvatarUrls
import com.onereflection.app.model.CategoryColor
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.ElementalTheme
import com.onereflection.app.model.HabitTone
import com.onereflection.app.model.MindfulHabit
import com.onereflection.app.model.ReminderEvent
import com.onereflection.app.model.ScreenId
import com.onereflection.app.model.TaskAssignee
import com.onereflection.app.model.TaskCategory
import com.onereflection.app.model.TaskItem
import com.onereflection.app.model.TaskPriority
import org.json.JSONArray
import org.json.JSONObject

class OneReflectionRepository(context: Context) {
    private val prefs = context.getSharedPreferences("onereflection_prefs_v1", Context.MODE_PRIVATE)

    fun loadOledMode(): Boolean = prefs.getBoolean("oled_mode", false)
    fun saveOledMode(enabled: Boolean) = prefs.edit().putBoolean("oled_mode", enabled).apply()

    fun loadSelectedDayIndex(): Int = prefs.getInt("selected_day_index", 3).coerceIn(0, 6)
    fun saveSelectedDayIndex(index: Int) = prefs.edit().putInt("selected_day_index", index.coerceIn(0, 6)).apply()

    fun loadDayThemes(): List<ElementalTheme> {
        val raw = prefs.getString("day_themes_json", null) ?: return ElementalTheme.defaultWeek()
        return runCatching {
            val arr = JSONArray(raw)
            if (arr.length() != 7) return ElementalTheme.defaultWeek()
            (0 until 7).map { idx ->
                runCatching { ElementalTheme.valueOf(arr.getString(idx)) }
                    .getOrDefault(ElementalTheme.defaultWeek()[idx])
            }
        }.getOrElse { ElementalTheme.defaultWeek() }
    }

    fun saveDayThemes(themes: List<ElementalTheme>) {
        val arr = JSONArray()
        themes.take(7).forEach { arr.put(it.name) }
        prefs.edit().putString("day_themes_json", arr.toString()).apply()
    }

    fun loadLightGlobalTheme(): ElementalTheme {
        val raw = prefs.getString("light_global_theme", null) ?: return ElementalTheme.CHERRY_BLOSSOM
        return runCatching { ElementalTheme.valueOf(raw) }.getOrDefault(ElementalTheme.CHERRY_BLOSSOM)
    }

    fun saveLightGlobalTheme(theme: ElementalTheme) {
        prefs.edit().putString("light_global_theme", theme.name).apply()
    }

    fun loadDarkGlobalTheme(): ElementalTheme {
        val raw = prefs.getString("dark_global_theme", null) ?: return ElementalTheme.RAIN
        return runCatching { ElementalTheme.valueOf(raw) }.getOrDefault(ElementalTheme.RAIN)
    }

    fun saveDarkGlobalTheme(theme: ElementalTheme) {
        prefs.edit().putString("dark_global_theme", theme.name).apply()
    }

    fun loadUseGlobalLightTheme(): Boolean = prefs.getBoolean("use_global_light_theme", false)
    fun saveUseGlobalLightTheme(enabled: Boolean) {
        prefs.edit().putBoolean("use_global_light_theme", enabled).apply()
    }

    fun loadPageThemes(isDark: Boolean): Map<ScreenId, ElementalTheme> {
        val key = if (isDark) "page_dark_themes_json" else "page_light_themes_json"
        val raw = prefs.getString(key, null) ?: return emptyMap()
        return runCatching {
            val obj = JSONObject(raw)
            val result = mutableMapOf<ScreenId, ElementalTheme>()
            val keys = obj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val screen = runCatching { ScreenId.valueOf(k) }.getOrNull()
                val theme = runCatching { ElementalTheme.valueOf(obj.getString(k)) }.getOrNull()
                if (screen != null && theme != null) {
                    result[screen] = theme
                }
            }
            result
        }.getOrElse { emptyMap() }
    }

    fun savePageThemes(isDark: Boolean, themes: Map<ScreenId, ElementalTheme>) {
        val key = if (isDark) "page_dark_themes_json" else "page_light_themes_json"
        val obj = JSONObject()
        themes.forEach { (screen, theme) -> obj.put(screen.name, theme.name) }
        prefs.edit().putString(key, obj.toString()).apply()
    }

    fun loadHydration(): Double {
        val raw = prefs.getFloat("hydration_liters", 1.8f).toDouble()
        return kotlin.math.round(raw * 100.0) / 100.0
    }
    fun saveHydration(liters: Double) = prefs.edit().putFloat("hydration_liters", liters.toFloat()).apply()

    fun loadAmbientSoundEnabled(): Boolean = prefs.getBoolean("ambient_sound_enabled", false)
    fun saveAmbientSoundEnabled(enabled: Boolean) =
        prefs.edit().putBoolean("ambient_sound_enabled", enabled).apply()

    fun loadAmbientSoundVolume(): Float =
        prefs.getFloat("ambient_sound_volume", 0.65f).coerceIn(0.05f, 1.0f)

    fun saveAmbientSoundVolume(volume: Float) =
        prefs.edit().putFloat("ambient_sound_volume", volume.coerceIn(0.05f, 1.0f)).apply()

    fun loadHabits(): List<MindfulHabit> {
        val raw = prefs.getString("mindful_habits_json", null) ?: return defaultHabits()
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                val historyArr = obj.optJSONArray("weeklyHistory")
                val history = if (historyArr != null && historyArr.length() == 7) {
                    (0 until 7).map { idx -> historyArr.optBoolean(idx, false) }
                } else {
                    listOf(true, true, true, false, false, false, false)
                }
                MindfulHabit(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    subtitle = obj.optString("subtitle", "Mindful daily ritual"),
                    unit = obj.optString("unit", "min"),
                    currentValue = obj.optInt("currentValue", 0),
                    targetValue = obj.optInt("targetValue", 10).coerceAtLeast(1),
                    stepValue = obj.optInt("stepValue", 1).coerceAtLeast(1),
                    streakDays = obj.optInt("streakDays", 1),
                    tone = runCatching { HabitTone.valueOf(obj.getString("tone")) }.getOrDefault(HabitTone.SAGE),
                    weeklyHistory = history
                )
            }
        }.getOrElse { defaultHabits() }
    }

    fun saveHabits(habits: List<MindfulHabit>) {
        val arr = JSONArray()
        habits.forEach { habit ->
            val historyArr = JSONArray()
            habit.weeklyHistory.forEach { historyArr.put(it) }
            arr.put(
                JSONObject().apply {
                    put("id", habit.id)
                    put("title", habit.title)
                    put("subtitle", habit.subtitle)
                    put("unit", habit.unit)
                    put("currentValue", habit.currentValue)
                    put("targetValue", habit.targetValue)
                    put("stepValue", habit.stepValue)
                    put("streakDays", habit.streakDays)
                    put("tone", habit.tone.name)
                    put("weeklyHistory", historyArr)
                }
            )
        }
        prefs.edit().putString("mindful_habits_json", arr.toString()).apply()
    }

    fun defaultHabits(): List<MindfulHabit> = listOf(
        MindfulHabit(
            id = "habit-breath",
            title = "Morning Breathwork",
            subtitle = "Box breathing before first screen",
            unit = "min",
            currentValue = 10,
            targetValue = 15,
            stepValue = 5,
            streakDays = 14,
            tone = HabitTone.TERRACOTTA,
            weeklyHistory = listOf(true, true, true, false, false, false, false)
        ),
        MindfulHabit(
            id = "habit-hydrate",
            title = "Mindful Hydration",
            subtitle = "Warm lemon water & mineral glasses",
            unit = "cups",
            currentValue = 7,
            targetValue = 10,
            stepValue = 1,
            streakDays = 21,
            tone = HabitTone.SAGE,
            weeklyHistory = listOf(true, true, true, true, false, false, false)
        ),
        MindfulHabit(
            id = "habit-focus",
            title = "Deep Focus Flow",
            subtitle = "Phone-free creative sprint blocks",
            unit = "min",
            currentValue = 30,
            targetValue = 45,
            stepValue = 15,
            streakDays = 9,
            tone = HabitTone.INDIGO,
            weeklyHistory = listOf(true, false, true, true, false, false, false)
        ),
        MindfulHabit(
            id = "habit-read",
            title = "Evening Reading",
            subtitle = "Analog pages before 22:30 bedtime",
            unit = "pages",
            currentValue = 20,
            targetValue = 20,
            stepValue = 5,
            streakDays = 12,
            tone = HabitTone.GOLD,
            weeklyHistory = listOf(true, true, true, true, false, false, false)
        )
    )

    fun loadCategories(): List<TaskCategory> {
        val raw = prefs.getString("categories_json", null) ?: return defaultCategories()
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                TaskCategory(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    color = CategoryColor.valueOf(obj.getString("color")),
                    isDefault = obj.optBoolean("isDefault", false)
                )
            }
        }.getOrElse { defaultCategories() }
    }

    fun saveCategories(categories: List<TaskCategory>) {
        val arr = JSONArray()
        categories.forEach { cat ->
            arr.put(
                JSONObject().apply {
                    put("id", cat.id)
                    put("name", cat.name)
                    put("color", cat.color.name)
                    put("isDefault", cat.isDefault)
                }
            )
        }
        prefs.edit().putString("categories_json", arr.toString()).apply()
    }

    fun loadTasks(): List<TaskItem> {
        val raw = prefs.getString("tasks_json", null) ?: return defaultTasks()
        return parseTasks(raw, defaultTasks())
    }

    fun saveTasks(tasks: List<TaskItem>) {
        prefs.edit().putString("tasks_json", serializeTasks(tasks)).apply()
    }

    fun loadCompletedTasks(): List<TaskItem> {
        val raw = prefs.getString("completed_tasks_json", null) ?: return defaultCompletedTasks()
        return parseTasks(raw, defaultCompletedTasks())
    }

    fun saveCompletedTasks(tasks: List<TaskItem>) {
        prefs.edit().putString("completed_tasks_json", serializeTasks(tasks)).apply()
    }

    private fun serializeTasks(tasks: List<TaskItem>): String {
        val arr = JSONArray()
        tasks.forEach { t ->
            arr.put(
                JSONObject().apply {
                    put("id", t.id)
                    put("title", t.title)
                    put("description", t.description)
                    put("category", t.category)
                    put("categoryId", t.categoryId)
                    put("dueTime", t.dueTime)
                    put("completed", t.completed)
                    put("priority", t.priority.name)
                    put("progressPercent", t.progressPercent)
                    put("carryOverNextDay", t.carryOverNextDay)
                    t.subtasksDone?.let { put("subtasksDone", it) }
                    t.subtasksTotal?.let { put("subtasksTotal", it) }
                    t.voiceBriefQuote?.let { put("voiceBriefQuote", it) }
                    t.voiceBriefDuration?.let { put("voiceBriefDuration", it) }
                    t.codeBranch?.let { put("codeBranch", it) }
                    t.streak?.let { put("streak", it) }
                    val assigneesArr = JSONArray()
                    t.assignees.forEach { a ->
                        assigneesArr.put(
                            JSONObject().apply {
                                put("name", a.name)
                                a.avatar?.let { put("avatar", it) }
                                a.initials?.let { put("initials", it) }
                                put("completionPercent", a.completionPercent)
                            }
                        )
                    }
                    put("assignees", assigneesArr)
                }
            )
        }
        return arr.toString()
    }

    private fun parseTasks(raw: String, fallback: List<TaskItem>): List<TaskItem> {
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                val completed = obj.optBoolean("completed", false)
                val progress = obj.optInt("progressPercent", if (completed) 100 else 0).coerceIn(0, 100)
                val assignees = mutableListOf<TaskAssignee>()
                val aArr = obj.optJSONArray("assignees")
                if (aArr != null) {
                    for (j in 0 until aArr.length()) {
                        val aObj = aArr.getJSONObject(j)
                        assignees.add(
                            TaskAssignee(
                                name = aObj.getString("name"),
                                avatar = aObj.optString("avatar").takeIf { it.isNotBlank() },
                                initials = aObj.optString("initials").takeIf { it.isNotBlank() },
                                completionPercent = aObj.optInt("completionPercent", progress).coerceIn(0, 100)
                            )
                        )
                    }
                }
                TaskItem(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    description = obj.optString("description", ""),
                    category = obj.getString("category"),
                    categoryId = obj.getString("categoryId"),
                    dueTime = obj.optString("dueTime", "Today"),
                    completed = completed,
                    priority = runCatching { TaskPriority.valueOf(obj.getString("priority")) }.getOrDefault(TaskPriority.MEDIUM),
                    progressPercent = progress,
                    carryOverNextDay = obj.optBoolean("carryOverNextDay", false),
                    subtasksDone = if (obj.has("subtasksDone")) obj.getInt("subtasksDone") else null,
                    subtasksTotal = if (obj.has("subtasksTotal")) obj.getInt("subtasksTotal") else null,
                    voiceBriefQuote = obj.optString("voiceBriefQuote").takeIf { it.isNotBlank() },
                    voiceBriefDuration = obj.optString("voiceBriefDuration").takeIf { it.isNotBlank() },
                    codeBranch = obj.optString("codeBranch").takeIf { it.isNotBlank() },
                    streak = if (obj.has("streak")) obj.getInt("streak") else null,
                    assignees = assignees
                )
            }
        }.getOrElse { fallback }
    }

    fun defaultCategories(): List<TaskCategory> = listOf(
        TaskCategory("cat-home", "Home & Living", CategoryColor.YELLOW, isDefault = true),
        TaskCategory("cat-work", "Design Studio", CategoryColor.LAVENDER, isDefault = true),
        TaskCategory("cat-wellness", "Mindful Rituals", CategoryColor.PINK, isDefault = true),
        TaskCategory("cat-errands", "Errands", CategoryColor.MATCHA, isDefault = true)
    )

    fun defaultTasks(): List<TaskItem> = listOf(
        TaskItem(
            id = "task-1",
            title = "Weekend Market & Fresh Groceries",
            description = "Pick up organic sourdough, heirloom tomatoes, oat milk, and roasted espresso beans.",
            category = "Home & Living",
            categoryId = "cat-home",
            dueTime = "01:00 PM",
            priority = TaskPriority.HIGH,
            progressPercent = 60,
            subtasksDone = 3,
            subtasksTotal = 5,
            voiceBriefQuote = "Grab the crusty rosemary loaf if they still have it!",
            voiceBriefDuration = "0:12",
            assignees = listOf(
                TaskAssignee("Elena", AvatarUrls.ELENA, "EV", completionPercent = 60),
                TaskAssignee("Maya", AvatarUrls.MAYA, "MR", completionPercent = 80)
            )
        ),
        TaskItem(
            id = "task-2",
            title = "Finalize Q3 Voice-First Flow Specs",
            description = "Polish haptic curve states and review bedtime breakthrough micro-copy with Leo.",
            category = "Design Studio",
            categoryId = "cat-work",
            dueTime = "03:15 PM",
            priority = TaskPriority.HIGH,
            progressPercent = 50,
            subtasksDone = 4,
            subtasksTotal = 6,
            codeBranch = "feat/voice-studio-v2",
            assignees = listOf(
                TaskAssignee("Leo", AvatarUrls.LEO, "LC", completionPercent = 75),
                TaskAssignee("Elena", AvatarUrls.ELENA, "EV", completionPercent = 50)
            )
        ),
        TaskItem(
            id = "task-3",
            title = "Evening Gratitude Reflection Audio",
            description = "Record a 60-second voice reflection before 22:30 Bedtime Horizon sync.",
            category = "Mindful Rituals",
            categoryId = "cat-wellness",
            dueTime = "09:45 PM",
            priority = TaskPriority.MEDIUM,
            progressPercent = 25,
            streak = 14,
            assignees = listOf(TaskAssignee("Elena", AvatarUrls.ELENA, "EV", completionPercent = 25))
        ),
        TaskItem(
            id = "task-4",
            title = "Pick up Dry Cleaning & Package",
            description = "Collect linen coat and studio parcel from Oak Street lockers.",
            category = "Errands",
            categoryId = "cat-errands",
            dueTime = "05:30 PM",
            priority = TaskPriority.LOW,
            progressPercent = 40,
            assignees = listOf(
                TaskAssignee("Maya", AvatarUrls.MAYA, "MR", completionPercent = 40),
                TaskAssignee("Elena", AvatarUrls.ELENA, "EV", completionPercent = 30)
            )
        )
    )

    fun defaultCompletedTasks(): List<TaskItem> = listOf(
        TaskItem(
            id = "task-done-1",
            title = "Morning Warm Lemon Water & Stretch",
            description = "10 minutes of mobility flow on the balcony.",
            category = "Mindful Rituals",
            categoryId = "cat-wellness",
            dueTime = "07:45 AM",
            completed = true,
            progressPercent = 100,
            priority = TaskPriority.MEDIUM,
            streak = 21,
            assignees = listOf(TaskAssignee("Elena", AvatarUrls.ELENA, "EV", completionPercent = 100))
        ),
        TaskItem(
            id = "task-done-2",
            title = "Sync Sprint 14 Design Tokens",
            description = "Published Candy Pink and OLED Pure Black color tokens.",
            category = "Design Studio",
            categoryId = "cat-work",
            dueTime = "10:15 AM",
            completed = true,
            progressPercent = 100,
            priority = TaskPriority.HIGH,
            assignees = listOf(
                TaskAssignee("Elena", AvatarUrls.ELENA, "EV", completionPercent = 100),
                TaskAssignee("Leo", AvatarUrls.LEO, "LC", completionPercent = 100)
            )
        )
    )

    fun defaultAlarms(): List<AlarmItem> = listOf(
        AlarmItem(
            id = "alarm-1",
            title = "Gentle Sunrise Wake-up",
            time = "07:30",
            period = "AM",
            days = listOf("M", "T", "W", "T", "F"),
            enabled = true,
            category = "shared",
            creatorName = "Maya R.",
            creatorRole = "Partner",
            creatorAvatar = AvatarUrls.MAYA,
            voiceSnippetQuote = "Good morning love! Remember to drink your lemon water and stretch before coffee.",
            voiceSnippetDuration = "0:18",
            chimeSound = ChimeType.TIBETAN,
            isBreakthrough = true
        ),
        AlarmItem(
            id = "alarm-2",
            title = "Design Standup Voice Nudge",
            time = "09:45",
            period = "AM",
            days = listOf("M", "W", "F"),
            enabled = true,
            category = "work",
            creatorName = "Leo Chen",
            creatorRole = "Studio Lead",
            creatorAvatar = AvatarUrls.LEO,
            voiceSnippetQuote = "Quick 10-minute prototype huddle in the lounge — bring the acoustic mockups!",
            voiceSnippetDuration = "0:11",
            chimeSound = ChimeType.NORDIC,
            isBreakthrough = false
        ),
        AlarmItem(
            id = "alarm-3",
            title = "Wind-down & Tea Horizon",
            time = "10:15",
            period = "PM",
            days = listOf("M", "T", "W", "T", "F", "S", "S"),
            enabled = false,
            category = "personal",
            creatorName = "Elena (You)",
            creatorRole = "Personal Ritual",
            creatorAvatar = AvatarUrls.ELENA,
            voiceSnippetQuote = "Screens down, chamomile ready, sync bedtime with Maya.",
            voiceSnippetDuration = "0:09",
            chimeSound = ChimeType.RIVER,
            isBreakthrough = false
        )
    )

    fun defaultReminders(): List<ReminderEvent> = listOf(
        ReminderEvent(
            id = "rem-1",
            title = "Sourdough & Market Errand",
            location = "Tartine Corner Bakery",
            time = "01:00 PM",
            category = "Shared Errand",
            completed = false,
            partnerNote = "Maya added: +2 cardamom buns"
        ),
        ReminderEvent(
            id = "rem-2",
            title = "Studio Design Review",
            location = "Studio North • Room B",
            time = "03:15 PM",
            category = "Design Studio",
            completed = false,
            partnerNote = "Leo shared Figma prototype link"
        ),
        ReminderEvent(
            id = "rem-3",
            title = "Golden Hour Sunset Walk",
            location = "Botanical Loop Trail",
            time = "05:45 PM",
            category = "Mindful Ritual",
            completed = false
        )
    )
}
