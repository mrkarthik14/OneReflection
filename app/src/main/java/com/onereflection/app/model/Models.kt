package com.onereflection.app.model

enum class ScreenId(val subtitle: String, val navLabel: String, val badge: String? = null) {
    HOME("Home", "Home Dashboard"),
    ALARMS("Alarms", "Wake-up & Shared Alarms", "07:30"),
    TASKS("Tasks & Shared Flows", "Tasks & Shared Flows", "4"),
    CHATS("Chats & Circles", "Chats & Circles", "Live"),
    REMINDERS("Reminders & Calendar Planner", "Moments & Reminders"),
    HABITS("Daily Rituals & Habits", "Daily Rituals & Habits"),
    QUIET_BREAKTHROUGH("Bedtime Quiet Hours", "Bedtime Quiet Hours (Breakthrough)", "01:24 AM"),
    PEOPLE("Inner Circle & Key", "People & Sync Key (QR Code)"),
    PROFILE("Profile & Harmony", "Profile & Harmony")
}

enum class ChimeType {
    TIBETAN, NORDIC, SOLFEGGIO, WATER, CLICK, PIANO, RIVER
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

data class TaskCategory(
    val id: String,
    val name: String,
    val color: CategoryColor,
    val isDefault: Boolean = false
)

data class TaskAssignee(
    val name: String,
    val avatar: String? = null,
    val initials: String? = null
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
    val subtasksDone: Int? = null,
    val subtasksTotal: Int? = null,
    val voiceBriefQuote: String? = null,
    val voiceBriefDuration: String? = null,
    val assignees: List<TaskAssignee> = emptyList(),
    val codeBranch: String? = null,
    val streak: Int? = null
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
    val chimeSound: String = "tibetan",
    val isBreakthrough: Boolean = false
)

data class HabitItem(
    val id: String,
    val title: String,
    val category: String,
    val currentValue: Double,
    val targetValue: Double,
    val unit: String,
    val completedToday: Boolean,
    val streakDays: Int,
    val partnerSynced: String? = null,
    val colorTheme: String = "yellow",
    val lastLogTime: String? = null
)

data class MemberPermissions(
    val voiceAlarms: Boolean = true,
    val sharedTasks: Boolean = true,
    val rhythmPings: Boolean = true,
    val emergencyBreakthrough: Boolean = true,
    val gentleVoiceWhispers: Boolean = true,
    val wakeUpAudioQueue: Boolean = true
)

data class CircleMember(
    val id: String,
    val name: String,
    val handle: String,
    val role: String,
    val status: String,
    val avatar: String,
    val isOnline: Boolean = true,
    val isAwake: Boolean = true,
    val permissions: MemberPermissions = MemberPermissions(),
    val sensitivity: String = "gentle"
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

data class QuietHoursConfig(
    val bedtimeMinutes: Int = 22 * 60 + 30,
    val wakeMinutes: Int = 7 * 60,
    val syncWithPartner: Boolean = true,
    val audioVibe: String = "tibetan",
    val mutedHaptics: Boolean = true
) {
    fun formattedBedtime(): String = formatMinutes(bedtimeMinutes)
    fun formattedWake(): String = formatMinutes(wakeMinutes)

    companion object {
        fun formatMinutes(totalMins: Int): String {
            val normalized = ((totalMins % (24 * 60)) + (24 * 60)) % (24 * 60)
            val h = normalized / 60
            val m = normalized % 60
            return "%02d:%02d".format(h, m)
        }
    }
}

data class UserProfile(
    val name: String = "Elena Vance",
    val handle: String = "@elenavance",
    val avatar: String = AVATAR_ELENA,
    val joinedDate: String = "Nov 2023",
    val syncScore: Double = 8.9,
    val audioPreset: String = "studio",
    val alarmGradualFade: Boolean = true,
    val oledPureBlackMode: Boolean = false
)

const val AVATAR_ELENA = "https://lh3.googleusercontent.com/aida-public/AB6AXuBE7jpIocCISc-CQYfgAnahVZmLt7VB9UJKlTFKJX9JEBM0KeqWHrnY0EOlX7Xr016w-StCO-DfHViu0fGfhqLc5xXymxW-Idb1KIskGYbnQtfZt9R24CzmTiiG97t1oqT6BRybyaDZrINztAvlimWlhHUhwdR_IyfwE_j6uA1V6l9K1-IiXMA6jRa5-JRXxNhlmHpahWf3zorVKk7seurAn3RnMPBzb_UQpUlVYYBvZMwd1oKmMCq0"
const val AVATAR_MAYA = "https://lh3.googleusercontent.com/aida-public/AB6AXuAeRiOrr_zqXvsgQ0Cu1UQj4u4aoeq6dDkLsBuixBUJk8W2ymfgRh_05Db4L0Ky7oZTx64gcF6rxpX5RGLEdURakheLCoY84n8bYOVFcXiRPjAWC4EW9DqkrXulpggz54YOB0PuTWhaPKLm625zB-NsP2IA8pgHPBctYQJUTk5m0XczJrLOH4fJNM5uG1lfRSK-zaqt2UaENwrtFJ1RuZ1nnnUaU0dDM7cRbtDZoso7MSCXduqu5rSr"
const val AVATAR_LEO = "https://lh3.googleusercontent.com/aida-public/AB6AXuB-p-Cg99Ksi_ssT1yEzjo7dZ74bX9c286fmjBrB3JDt9C2-RVpWDUyQS3BcXmbfuc_5a3KPKxqZQ6HMO9VrNvDKyO_cDtWwfK8dUOgy7u-ooaZG5Oqa4SKmjHSsIEeLJKvVrTv2jg4kbeGT94tWbu4b9PvkRK2IieztVYz6V_0FOwskVywC5epxeiRXKV9YTW6j7Azw-udLYcHbZl6lbAB_u_ruM4C-5pOAs3oT2TtxmP9_QheqmgU"
const val AVATAR_SOFIA = "https://lh3.googleusercontent.com/aida-public/AB6AXuCYk_21N695Jts_2y9t9DTRLnqBKYt37JiJRzq1NIv0wmakZo9kLQtkH6vCfxtcSLGR9nDpaUP93NGFX-UDhJG5T_e0PVL7nt_w8niC5mp9N_a2qPlrje-kCSNx0H5uymVl_VPTzeODHeDotNL1q3m9lrveFP7tMaYabytMVrSx117nqmfZg3JNVXmAf02qSYvj_V8UqYymiuasWbegnQAywv2jec0DFhoSdfKvTlpFL7f0fHQG4704"
