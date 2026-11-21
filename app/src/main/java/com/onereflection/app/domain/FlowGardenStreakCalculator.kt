package com.onereflection.app.domain

import com.onereflection.app.model.MindfulHabit
import kotlin.math.ceil

enum class StreakDayStatus {
    ACHIEVED,
    TODAY_ACHIEVED,
    TODAY_PENDING,
    MISSED,
    UPCOMING
}

data class StreakDayNode(
    val dayIndex: Int,
    val shortLabel: String,
    val status: StreakDayStatus,
    val completedCount: Int,
    val totalCount: Int
)

/**
 * Snapshot of the user's consecutive daily habit goal streak for the Flow Garden dashboard.
 */
data class DailyStreakSummary(
    val currentStreakDays: Int,
    val bestStreakDays: Int,
    val todayGoalAchieved: Boolean,
    val isPerfectDay: Boolean,
    val completedHabitsToday: Int,
    val totalHabitsCount: Int,
    val dailyGoalTarget: Int,
    val habitsRemainingForStreak: Int,
    val consecutiveWeekDays: Int,
    val nextMilestoneDays: Int,
    val milestoneProgress: Float,
    val weekNodes: List<StreakDayNode>
)

private val STREAK_MILESTONES = listOf(7, 14, 21, 30, 60, 100, 180, 365)
private val DAY_LABELS = listOf("M", "T", "W", "T", "F", "S", "S")

/**
 * Computes the user's live consecutive daily habit streak from tracked rituals and weekly history.
 *
 * A day counts as goal-achieved when at least [dailyGoalTarget] habits (50% of active habits,
 * rounded up) are completed. Completing today's goal immediately extends [currentStreakDays]
 * by +1 and lights up today's node in the 7-day consecutive chain.
 */
fun calculateDailyStreakSummary(
    habits: List<MindfulHabit>,
    selectedDayIndex: Int = 3
): DailyStreakSummary {
    val safeTodayIdx = selectedDayIndex.coerceIn(0, 6)
    val totalHabits = habits.size
    val dailyGoalTarget = if (totalHabits == 0) 1 else ceil(totalHabits * 0.5).toInt().coerceAtLeast(1)
    val completedToday = habits.count { it.isCompleted }
    val todayGoalAchieved = totalHabits > 0 && completedToday >= dailyGoalTarget
    val isPerfectDay = totalHabits > 0 && completedToday == totalHabits
    val remaining = (dailyGoalTarget - completedToday).coerceAtLeast(0)

    // Evaluate each day in the 7-day week window
    val weekNodes = (0 until 7).map { dayIdx ->
        val dayCompletedCount = if (dayIdx == safeTodayIdx) {
            completedToday
        } else {
            habits.count { habit -> habit.weeklyHistory.getOrElse(dayIdx) { false } }
        }
        val metGoal = totalHabits > 0 && dayCompletedCount >= dailyGoalTarget
        val status = when {
            dayIdx == safeTodayIdx && metGoal -> StreakDayStatus.TODAY_ACHIEVED
            dayIdx == safeTodayIdx -> StreakDayStatus.TODAY_PENDING
            dayIdx > safeTodayIdx -> StreakDayStatus.UPCOMING
            metGoal -> StreakDayStatus.ACHIEVED
            else -> StreakDayStatus.MISSED
        }
        StreakDayNode(
            dayIndex = dayIdx,
            shortLabel = DAY_LABELS[dayIdx],
            status = status,
            completedCount = dayCompletedCount,
            totalCount = totalHabits
        )
    }

    // Count consecutive achieved days leading up to yesterday in this week
    var consecutiveBeforeToday = 0
    for (idx in (safeTodayIdx - 1) downTo 0) {
        if (weekNodes[idx].status == StreakDayStatus.ACHIEVED) {
            consecutiveBeforeToday++
        } else {
            break
        }
    }

    // Anchor multi-week streak continuity when prior days this week remained unbroken
    val establishedCarryDays = if (totalHabits > 0 && consecutiveBeforeToday > 0) {
        val sortedStreaks = habits.map { it.streakDays }.sortedDescending()
        val medianTopStreak = sortedStreaks.take(dailyGoalTarget).lastOrNull() ?: 0
        (medianTopStreak - 1).coerceAtLeast(consecutiveBeforeToday)
    } else {
        0
    }

    val currentStreakDays = establishedCarryDays + if (todayGoalAchieved) 1 else 0
    val consecutiveWeekDays = consecutiveBeforeToday + if (todayGoalAchieved) 1 else 0
    val bestStreakDays = maxOf(
        currentStreakDays,
        habits.maxOfOrNull { it.streakDays } ?: 0
    )

    val nextMilestone = STREAK_MILESTONES.firstOrNull { it > currentStreakDays }
        ?: (currentStreakDays + 30)
    val prevMilestone = STREAK_MILESTONES.lastOrNull { it <= currentStreakDays } ?: 0
    val milestoneSpan = (nextMilestone - prevMilestone).coerceAtLeast(1)
    val milestoneProgress = ((currentStreakDays - prevMilestone).toFloat() / milestoneSpan.toFloat())
        .coerceIn(0f, 1f)

    return DailyStreakSummary(
        currentStreakDays = currentStreakDays,
        bestStreakDays = bestStreakDays,
        todayGoalAchieved = todayGoalAchieved,
        isPerfectDay = isPerfectDay,
        completedHabitsToday = completedToday,
        totalHabitsCount = totalHabits,
        dailyGoalTarget = dailyGoalTarget,
        habitsRemainingForStreak = remaining,
        consecutiveWeekDays = consecutiveWeekDays,
        nextMilestoneDays = nextMilestone,
        milestoneProgress = milestoneProgress,
        weekNodes = weekNodes
    )
}
