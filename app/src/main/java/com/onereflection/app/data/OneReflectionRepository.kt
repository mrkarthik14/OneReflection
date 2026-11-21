package com.onereflection.app.data

import android.content.Context
import android.content.SharedPreferences
import com.onereflection.app.model.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.roundToInt

class OneReflectionRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("onereflection_store", Context.MODE_PRIVATE)

    fun loadOledMode(): Boolean = prefs.getBoolean("oled_mode", false)

    fun saveOledMode(enabled: Boolean) {
        prefs.edit().putBoolean("oled_mode", enabled).apply()
    }

    fun loadHydrationLiters(): Double =
        prefs.getFloat("hydration_liters", 1.8f).toDouble()

    fun saveHydrationLiters(liters: Double) {
        prefs.edit().putFloat("hydration_liters", liters.toFloat()).apply()
    }

    fun loadCategories(): List<TaskCategory> {
        val raw = prefs.getString("categories_json", null) ?: return defaultCategories()
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                TaskCategory(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    color = runCatching { CategoryColor.valueOf(obj.getString("color")) }
                        .getOrDefault(CategoryColor.SLATE),
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
        return parseTasksJson(raw, defaultTasks())
    }

    fun saveTasks(tasks: List<TaskItem>) {
        prefs.edit().putString("tasks_json", serializeTasksJson(tasks)).apply()
    }

    fun loadCompletedTasks(): List<TaskItem> {
        val raw = prefs.getString("completed_tasks_json", null) ?: return defaultCompletedTasks()
        return parseTasksJson(raw, defaultCompletedTasks())
    }

    fun saveCompletedTasks(tasks: List<TaskItem>) {
        prefs.edit().putString("completed_tasks_json", serializeTasksJson(tasks)).apply()
    }

    private fun serializeTasksJson(tasks: List<TaskItem>): String {
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
                            }
                        )
                    }
                    put("assignees", assigneesArr)
                }
            )
        }
        return arr.toString()
    }

    private fun parseTasksJson(raw: String, fallback: List<TaskItem>): List<TaskItem> {
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                val assigneesArr = obj.optJSONArray("assignees")
                val assignees = if (assigneesArr != null) {
                    (0 until assigneesArr.length()).map { j ->
                        val aObj = assigneesArr.getJSONObject(j)
                        TaskAssignee(
                            name = aObj.getString("name"),
                            avatar = aObj.optString("avatar").ifBlank { null },
                            initials = aObj.optString("initials").ifBlank { null }
                        )
                    }
                } else emptyList()

                TaskItem(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    description = obj.optString("description", ""),
                    category = obj.optString("category", "Work"),
                    categoryId = obj.optString("categoryId", "cat-work"),
                    dueTime = obj.optString("dueTime", "05:00 PM"),
                    completed = obj.optBoolean("completed", false),
                    priority = runCatching { TaskPriority.valueOf(obj.optString("priority", "MEDIUM")) }
                        .getOrDefault(TaskPriority.MEDIUM),
                    subtasksDone = if (obj.has("subtasksDone")) obj.getInt("subtasksDone") else null,
                    subtasksTotal = if (obj.has("subtasksTotal")) obj.getInt("subtasksTotal") else null,
                    voiceBriefQuote = obj.optString("voiceBriefQuote").ifBlank { null },
                    voiceBriefDuration = obj.optString("voiceBriefDuration").ifBlank { null },
                    assignees = assignees,
                    codeBranch = obj.optString("codeBranch").ifBlank { null },
                    streak = if (obj.has("streak")) obj.getInt("streak") else null
                )
            }
        }.getOrElse { fallback }
    }

    companion object {
        fun defaultCategories(): List<TaskCategory> = listOf(
            TaskCategory("cat-work", "Work", CategoryColor.SLATE, true),
            TaskCategory("cat-personal", "Personal", CategoryColor.MATCHA, true),
            TaskCategory("cat-health", "Health", CategoryColor.PINK, true),
            TaskCategory("cat-studio", "Studio Dev", CategoryColor.LAVENDER, true),
            TaskCategory("cat-errand", "Shared Errands", CategoryColor.YELLOW, true),
            TaskCategory("cat-ritual", "Daily Ritual", CategoryColor.CYAN, true)
        )

        fun defaultAlarms(): List<AlarmItem> = listOf(
            AlarmItem(
                id = "alarm-1",
                title = "Morning Sunshine",
                time = "07:30",
                period = "AM",
                days = listOf("M", "T", "W", "T", "F"),
                enabled = true,
                category = "shared",
                creatorName = "Maya R.",
                creatorRole = "Partner",
                creatorAvatar = AVATAR_MAYA,
                voiceSnippetQuote = "Wake up lazybones, coffee is on me! Let's crush today.",
                voiceSnippetDuration = "0:24",
                chimeSound = "tibetan",
                isBreakthrough = true
            ),
            AlarmItem(
                id = "alarm-2",
                title = "Team Standup Ping",
                time = "08:45",
                period = "AM",
                days = listOf("M", "T", "W", "T", "F"),
                enabled = true,
                category = "studio",
                chimeSound = "nordic"
            ),
            AlarmItem(
                id = "alarm-3",
                title = "Shared Focus Sprint",
                time = "02:30",
                period = "PM",
                days = listOf("W"),
                enabled = true,
                category = "sprint",
                creatorName = "Leo",
                voiceSnippetQuote = "Time to lock in 45m deep code",
                voiceSnippetDuration = "0:08",
                chimeSound = "nordic"
            ),
            AlarmItem(
                id = "alarm-4",
                title = "Wind Down & Reflection",
                time = "10:30",
                period = "PM",
                days = listOf("M", "T", "W", "T", "F", "S", "S"),
                enabled = false,
                category = "selfcare",
                voiceSnippetQuote = "Gentle Solfeggio Chime + 30s Audio Note",
                chimeSound = "solfeggio"
            )
        )

        fun defaultTasks(): List<TaskItem> = listOf(
            TaskItem(
                id = "task-1",
                title = "Design Critique & Prototype",
                description = "Screen 3 Bento review & gesture refinement with Maya R.",
                category = "Work",
                categoryId = "cat-work",
                dueTime = "11:30 AM",
                priority = TaskPriority.HIGH,
                subtasksDone = 3,
                subtasksTotal = 4,
                voiceBriefQuote = "Screen 3 Bento review & gesture refinement",
                voiceBriefDuration = "0:14s",
                assignees = listOf(
                    TaskAssignee("Elena", avatar = AVATAR_ELENA),
                    TaskAssignee("Maya", avatar = AVATAR_MAYA)
                )
            ),
            TaskItem(
                id = "task-2",
                title = "Daily Botanical Care & Terrace Water",
                description = "Mindful hydration cycle for balcony greenery & herbs.",
                category = "Daily Ritual",
                categoryId = "cat-ritual",
                dueTime = "01:00 PM",
                priority = TaskPriority.LOW,
                streak = 14
            ),
            TaskItem(
                id = "task-3",
                title = "OneReflection Kotlin Compose Sync",
                description = "Architecture review of the reactive shared store state.",
                category = "Studio Dev",
                categoryId = "cat-studio",
                dueTime = "04:15 PM",
                priority = TaskPriority.HIGH,
                codeBranch = "feat/sync-v3",
                assignees = listOf(
                    TaskAssignee("Leo", initials = "LS"),
                    TaskAssignee("Sam", initials = "SM")
                )
            ),
            TaskItem(
                id = "task-4",
                title = "Pick up artisan sourdough & oats",
                description = "Boulangerie Saint-Germain on 4th Ave.",
                category = "Shared Errands",
                categoryId = "cat-errand",
                dueTime = "06:30 PM",
                priority = TaskPriority.MEDIUM,
                voiceBriefQuote = "Get the seeded one if warm!"
            )
        )

        fun defaultCompletedTasks(): List<TaskItem> = listOf(
            TaskItem(
                id = "done-1",
                title = "Morning Espresso Meditation",
                description = "Quiet 10 min breathwork with dark roast",
                category = "Daily Ritual",
                categoryId = "cat-ritual",
                dueTime = "08:00 AM",
                completed = true,
                priority = TaskPriority.LOW
            ),
            TaskItem(
                id = "done-2",
                title = "Send Invoice to Studio Nord",
                description = "Sprint 13 sign-off deliverables",
                category = "Studio Dev",
                categoryId = "cat-studio",
                dueTime = "09:15 AM",
                completed = true,
                priority = TaskPriority.HIGH
            ),
            TaskItem(
                id = "done-3",
                title = "Hydration: 4 of 4 glasses",
                description = "1.2L logged before noon",
                category = "Daily Ritual",
                categoryId = "cat-ritual",
                dueTime = "10:30 AM",
                completed = true,
                priority = TaskPriority.LOW
            ),
            TaskItem(
                id = "done-4",
                title = "Sync Dog Walk with Maya",
                description = "Sunny loop along the waterfront park",
                category = "Shared Errands",
                categoryId = "cat-errand",
                dueTime = "11:00 AM",
                completed = true,
                priority = TaskPriority.MEDIUM
            )
        )

        fun defaultReminders(): List<ReminderEvent> = listOf(
            ReminderEvent(
                id = "rem-1",
                title = "Dentist & Oral Wellness Checkup",
                location = "Dr. Turner's Clinic • Bring updated allergy & nightguard notes.",
                time = "11:30 AM",
                category = "Health",
                source = "google-cal",
                partnerNote = "Maya will send a voice ping 15m prior to departure"
            ),
            ReminderEvent(
                id = "rem-2",
                title = "Pick up Artisan Sourdough & Rolled Oats",
                location = "Golden Wheat Bakery • Maya added 2 items to the bag.",
                time = "01:00 PM",
                category = "Shared Errand",
                source = "shared",
                partnerNote = "\"Remember seeded sourdough!\" Voice memo from Maya • 0:14"
            ),
            ReminderEvent(
                id = "rem-3",
                title = "Studio Architecture Review & Sprint Retro",
                location = "Google Meet room linked • Presenting the bento layout components.",
                time = "03:15 PM",
                category = "Studio Review",
                source = "onereflection"
            ),
            ReminderEvent(
                id = "rem-4",
                title = "Sunset Walk & Balcony Plant Watering",
                location = "20 minutes offline reflection • Hydrate Monstera and ferns.",
                time = "05:45 PM",
                category = "Daily Habit",
                source = "onereflection"
            )
        )

        fun roundTwoDecimals(value: Double): Double = (value * 100.0).roundToInt() / 100.0
    }
}
