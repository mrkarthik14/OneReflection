package com.onereflection.app.domain

import com.onereflection.app.model.MindfulHabit
import kotlin.math.roundToInt

/**
 * Canonical growth stages for the OneReflection Flow Garden plant visualization.
 */
enum class PlantGrowthStage(
    val displayName: String,
    val badgeLabel: String,
    val minScore: Float,
    val summary: String
) {
    SEEDLING(
        displayName = "Seedling",
        badgeLabel = "STAGE 1 · SEEDLING",
        minScore = 0.0f,
        summary = "First roots taking hold from your daily rituals."
    ),
    SPROUT(
        displayName = "Sprout",
        badgeLabel = "STAGE 2 · SPROUT",
        minScore = 0.35f,
        summary = "Fresh stems and leaves unfurling from steady daily habits."
    ),
    MATURE_TREE(
        displayName = "Mature Tree",
        badgeLabel = "STAGE 3 · MATURE TREE",
        minScore = 0.72f,
        summary = "A full sanctuary canopy thriving on consistent daily completion."
    );

    fun nextStage(): PlantGrowthStage? = when (this) {
        SEEDLING -> SPROUT
        SPROUT -> MATURE_TREE
        MATURE_TREE -> null
    }
}

/**
 * Calculated snapshot of the Flow Garden's growth derived from daily habit completion data.
 */
data class FlowGardenGrowthState(
    val stage: PlantGrowthStage,
    val growthScore: Float,
    val todayCompletionRatio: Float,
    val weeklyConsistencyRatio: Float,
    val completedHabitsToday: Int,
    val totalHabitsCount: Int,
    val averageStreakDays: Int,
    val stageProgress: Float,
    val nextStage: PlantGrowthStage?,
    val percentToNextStage: Int
) {
    val growthPercent: Int
        get() = (growthScore * 100f).roundToInt().coerceIn(0, 100)

    val todayCompletionPercent: Int
        get() = (todayCompletionRatio * 100f).roundToInt().coerceIn(0, 100)

    val weeklyConsistencyPercent: Int
        get() = (weeklyConsistencyRatio * 100f).roundToInt().coerceIn(0, 100)
}

/**
 * Pure stage resolver mapping any normalized growth score (0f..1f) into
 * Seedling, Sprout, or Mature Tree.
 */
fun determinePlantGrowthStage(growthScore: Float): PlantGrowthStage {
    val clamped = growthScore.coerceIn(0f, 1f)
    return when {
        clamped >= PlantGrowthStage.MATURE_TREE.minScore -> PlantGrowthStage.MATURE_TREE
        clamped >= PlantGrowthStage.SPROUT.minScore -> PlantGrowthStage.SPROUT
        else -> PlantGrowthStage.SEEDLING
    }
}

/**
 * Core Flow Garden growth engine.
 *
 * Aggregates daily habit completion progress, 7-day habit consistency history,
 * hydration ritual progress, and active streaks (with an optional gentle harmony
 * boost for completed tasks and monthly harvests) into a deterministic
 * [FlowGardenGrowthState] that drives the visualization page.
 */
fun calculateFlowGardenGrowth(
    habits: List<MindfulHabit>,
    hydrationLiters: Double = 0.0,
    hydrationGoalLiters: Double = 2.5,
    completedTasksCount: Int = 0,
    totalTasksCount: Int = 0,
    periodDays: Int = 7
): FlowGardenGrowthState {
    val hydrationRatio = if (hydrationGoalLiters > 0.0) {
        (hydrationLiters / hydrationGoalLiters).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    val totalHabits = habits.size
    val completedToday = habits.count { it.isCompleted }

    val avgHabitProgress = if (totalHabits > 0) {
        habits.sumOf { it.progress.toDouble() }.toFloat() / totalHabits.toFloat()
    } else {
        0f
    }

    // Daily completion combines all tracked habits (85%) with the hydration sanctuary ritual (15%)
    val todayCompletionRatio = if (totalHabits > 0) {
        (avgHabitProgress * 0.85f + hydrationRatio * 0.15f).coerceIn(0f, 1f)
    } else {
        hydrationRatio
    }

    // 7-day rolling consistency computed from each habit's daily completion history
    val totalHistorySlots = habits.sumOf { it.weeklyHistory.size }
    val completedHistorySlots = habits.sumOf { habit -> habit.weeklyHistory.count { it } }
    val weeklyConsistencyRatio = if (totalHistorySlots > 0) {
        (completedHistorySlots.toFloat() / totalHistorySlots.toFloat()).coerceIn(0f, 1f)
    } else {
        todayCompletionRatio
    }

    val avgStreak = if (totalHabits > 0) {
        (habits.sumOf { it.streakDays }.toFloat() / totalHabits.toFloat()).roundToInt()
    } else {
        0
    }
    val streakVitality = (avgStreak / 14f).coerceIn(0f, 1f)

    val taskCompletionRatio = if (totalTasksCount > 0) {
        (completedTasksCount.toFloat() / totalTasksCount.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    // Core weighted vitality formula centered on daily habit completions + weekly consistency
    val baseScore = if (totalHabits == 0 && hydrationRatio <= 0f) {
        0.06f
    } else {
        val raw = (todayCompletionRatio * 0.56f) +
            (weeklyConsistencyRatio * 0.28f) +
            (streakVitality * 0.10f) +
            (taskCompletionRatio * 0.06f)
        // Monthly grand harvest gently rewards accumulated 30-day ritual depth
        val periodBonus = if (periodDays >= 30) 0.08f * weeklyConsistencyRatio else 0f
        (raw + periodBonus).coerceIn(0.06f, 1f)
    }

    val stage = determinePlantGrowthStage(baseScore)
    val nextStage = stage.nextStage()

    val stageProgress = when (stage) {
        PlantGrowthStage.SEEDLING -> {
            (baseScore / PlantGrowthStage.SPROUT.minScore).coerceIn(0f, 1f)
        }
        PlantGrowthStage.SPROUT -> {
            val span = PlantGrowthStage.MATURE_TREE.minScore - PlantGrowthStage.SPROUT.minScore
            ((baseScore - PlantGrowthStage.SPROUT.minScore) / span).coerceIn(0f, 1f)
        }
        PlantGrowthStage.MATURE_TREE -> 1f
    }

    val percentToNext = if (nextStage != null) {
        ((nextStage.minScore - baseScore) * 100f).roundToInt().coerceAtLeast(1)
    } else {
        0
    }

    return FlowGardenGrowthState(
        stage = stage,
        growthScore = baseScore,
        todayCompletionRatio = todayCompletionRatio,
        weeklyConsistencyRatio = weeklyConsistencyRatio,
        completedHabitsToday = completedToday,
        totalHabitsCount = totalHabits,
        averageStreakDays = avgStreak,
        stageProgress = stageProgress,
        nextStage = nextStage,
        percentToNextStage = percentToNext
    )
}
