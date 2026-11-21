package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.R
import com.onereflection.app.data.FlowGardenShareExporter
import com.onereflection.app.data.FlowGardenShareSnapshot
import com.onereflection.app.domain.PlantGrowthStage
import com.onereflection.app.domain.calculateFlowGardenGrowth
import com.onereflection.app.domain.determinePlantGrowthStage
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.components.FlowGardenDailyStreakCard
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.SanctuaryEase
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private enum class HarvestPeriod(val label: String, val daysCount: Int, val subtitle: String) {
    WEEKLY("End of Week", 7, "7-Day Living Harvest"),
    MONTHLY("End of Month", 30, "30-Day Grand Canopy")
}

private enum class HarvestSourceKind(val label: String, val icon: ImageVector) {
    ALL("All", Icons.Filled.Park),
    TASKS("Tasks", Icons.Filled.CheckCircle),
    FLOW_GARDENS("Flow Gardens", Icons.Filled.Eco),
    CHATS_CIRCLES("Chats & Circles", Icons.Filled.ChatBubble)
}

private data class HarvestNodeItem(
    val id: String,
    val title: String,
    val detail: String,
    val kind: HarvestSourceKind,
    val percent: Int,
    val dayNumber: Int
)

@Composable
fun SanctuaryTreeScreen(
    uiState: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onOpenThemeSettings: () -> Unit,
    onPlayChime: (ChimeType) -> Unit,
    onShowNotification: (String, String, String) -> Unit = { _, _, _ -> }
) {
    BackHandler { onNavigate(ScreenId.HOME) }

    val palette = LocalElementalPalette.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val shareChooserTitle = stringResource(R.string.share_tree_chooser_title)
    var isExportingShare by remember { mutableStateOf(false) }

    var period by remember { mutableStateOf(HarvestPeriod.WEEKLY) }
    var selectedFilter by remember { mutableStateOf(HarvestSourceKind.ALL) }

    // Collect all Flow Gardens (Habits), Completed & In-Progress Tasks, and Completed Chats & Circles
    val collectedItems = remember(uiState.completedTasks, uiState.tasks, uiState.habits, uiState.alarms, period) {
        val list = mutableListOf<HarvestNodeItem>()

        // 1. Completed Tasks & To-Do Lists
        uiState.completedTasks.forEachIndexed { idx, task ->
            list.add(
                HarvestNodeItem(
                    id = "tree-done-${task.id}",
                    title = task.title,
                    detail = "${task.category} · Completed 100%",
                    kind = HarvestSourceKind.TASKS,
                    percent = 100,
                    dayNumber = (idx + 1).coerceAtMost(period.daysCount)
                )
            )
        }
        // Partially progressed active tasks sprout as growing buds
        uiState.tasks.filter { it.progressPercent > 0 }.forEachIndexed { idx, task ->
            list.add(
                HarvestNodeItem(
                    id = "tree-prog-${task.id}",
                    title = task.title,
                    detail = if (task.carryOverNextDay) {
                        "${task.progressPercent}% grown · Continuing tomorrow"
                    } else {
                        "${task.progressPercent}% grown · Active sprout"
                    },
                    kind = HarvestSourceKind.TASKS,
                    percent = task.progressPercent,
                    dayNumber = ((uiState.completedTasks.size + idx + 1) % period.daysCount) + 1
                )
            )
        }

        // 2. Flow Gardens (Habits & Rituals)
        uiState.habits.forEachIndexed { idx, habit ->
            val pct = (habit.progress * 100f).roundToInt()
            list.add(
                HarvestNodeItem(
                    id = "tree-habit-${habit.id}",
                    title = habit.title,
                    detail = "Flow Garden · ${habit.streakDays}d streak · $pct%",
                    kind = HarvestSourceKind.FLOW_GARDENS,
                    percent = pct,
                    dayNumber = ((idx * 2 + 2) % period.daysCount) + 1
                )
            )
        }

        // 3. Completed Chats & Circles
        list.add(
            HarvestNodeItem(
                id = "tree-chat-maya",
                title = "Maya R. · Morning Voice Circle",
                detail = "Completed 7 voice reflections & sunrise sync",
                kind = HarvestSourceKind.CHATS_CIRCLES,
                percent = 100,
                dayNumber = 3
            )
        )
        list.add(
            HarvestNodeItem(
                id = "tree-chat-leo",
                title = "Design Studio Circle Sync",
                detail = "Completed Q3 flow critique & voice notes",
                kind = HarvestSourceKind.CHATS_CIRCLES,
                percent = 100,
                dayNumber = 5
            )
        )
        list.add(
            HarvestNodeItem(
                id = "tree-chat-sofia",
                title = "Sofia Miller · Wellness Circle",
                detail = "Shared 7-day hydration ritual celebration",
                kind = HarvestSourceKind.CHATS_CIRCLES,
                percent = 100,
                dayNumber = 6
            )
        )

        if (period == HarvestPeriod.MONTHLY) {
            list.add(
                HarvestNodeItem(
                    id = "tree-month-1",
                    title = "Week 1 Sanctuary Flow Garden",
                    detail = "28 rituals & 14 shared tasks harvested",
                    kind = HarvestSourceKind.FLOW_GARDENS,
                    percent = 100,
                    dayNumber = 7
                )
            )
            list.add(
                HarvestNodeItem(
                    id = "tree-month-2",
                    title = "Week 2 Inner Circle Retrospectives",
                    detail = "12 completed circle voice threads",
                    kind = HarvestSourceKind.CHATS_CIRCLES,
                    percent = 100,
                    dayNumber = 14
                )
            )
            list.add(
                HarvestNodeItem(
                    id = "tree-month-3",
                    title = "Week 3 Studio & Home Milestones",
                    detail = "19 to-do lists completed to 100%",
                    kind = HarvestSourceKind.TASKS,
                    percent = 100,
                    dayNumber = 21
                )
            )
            list.add(
                HarvestNodeItem(
                    id = "tree-month-4",
                    title = "Week 4 Mindful Bedtime Canopy",
                    detail = "Monthly circle sleep harmony unlocked",
                    kind = HarvestSourceKind.CHATS_CIRCLES,
                    percent = 100,
                    dayNumber = 28
                )
            )
        }
        list
    }

    val taskHarvestCount = collectedItems.count { it.kind == HarvestSourceKind.TASKS }
    val gardenHarvestCount = collectedItems.count { it.kind == HarvestSourceKind.FLOW_GARDENS }
    val circleHarvestCount = collectedItems.count { it.kind == HarvestSourceKind.CHATS_CIRCLES }

    // Core Flow Garden Growth Calculation hooked directly to daily habit completion data
    val growthState = remember(
        uiState.habits,
        uiState.hydrationLiters,
        uiState.completedTasks.size,
        uiState.tasks.size,
        period
    ) {
        calculateFlowGardenGrowth(
            habits = uiState.habits,
            hydrationLiters = uiState.hydrationLiters,
            completedTasksCount = uiState.completedTasks.size,
            totalTasksCount = uiState.tasks.size + uiState.completedTasks.size,
            periodDays = period.daysCount
        )
    }

    val targetMaturity = growthState.growthScore

    val growthAnim = remember { Animatable(0.08f) }
    var isManualScrub by remember { mutableStateOf(false) }
    var manualGrowth by remember { mutableFloatStateOf(targetMaturity) }
    var replayTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(period, targetMaturity, replayTrigger) {
        isManualScrub = false
        growthAnim.snapTo(0.06f)
        growthAnim.animateTo(
            targetValue = targetMaturity,
            animationSpec = tween(durationMillis = 1900, easing = SanctuaryEase)
        )
        manualGrowth = targetMaturity
    }

    val displayedGrowth = if (isManualScrub) manualGrowth else growthAnim.value
    val smoothGrowth by animateFloatAsState(
        targetValue = displayedGrowth,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "tree_growth_smooth"
    )

    val displayedStage = determinePlantGrowthStage(smoothGrowth)
    val growthStageTitle = displayedStage.displayName

    val currentDayDisplayed = ((smoothGrowth * period.daysCount).roundToInt()).coerceIn(1, period.daysCount)

    val visibleItems = remember(collectedItems, selectedFilter) {
        if (selectedFilter == HarvestSourceKind.ALL) collectedItems
        else collectedItems.filter { it.kind == selectedFilter }
    }

    val handleShareTree: () -> Unit = {
        if (!isExportingShare) {
            isExportingShare = true
            onPlayChime(ChimeType.SOLFEGGIO)
            val snapshot = FlowGardenShareSnapshot(
                growth = smoothGrowth,
                stage = displayedStage,
                growthState = growthState,
                theme = uiState.activeElementalTheme,
                palette = palette,
                periodLabel = period.label,
                periodSubtitle = period.subtitle,
                isMonthly = period == HarvestPeriod.MONTHLY,
                taskCount = taskHarvestCount,
                gardenCount = gardenHarvestCount,
                circleCount = circleHarvestCount
            )
            scope.launch {
                val result = FlowGardenShareExporter.exportAndShare(
                    context = context,
                    snapshot = snapshot,
                    chooserTitle = shareChooserTitle
                )
                isExportingShare = false
                result.onSuccess {
                    onShowNotification(
                        "Flow Garden Ready to Share",
                        "Exported your ${displayedStage.displayName} (${(smoothGrowth * 100f).roundToInt()}%) poster.",
                        displayedStage.displayName.uppercase()
                    )
                }.onFailure {
                    onShowNotification(
                        "Unable to Share Image",
                        "Please ensure a messaging or social app is available to receive images.",
                        "ERROR"
                    )
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 164.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header with Great Tree Title + Share & Page Theme Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "${period.subtitle.uppercase()} · ${uiState.activeElementalTheme.displayName.uppercase()}",
                        fontFamily = MonoFontFamily,
                        fontSize = 10.sp,
                        letterSpacing = 1.1.sp,
                        color = palette.primary
                    )
                    Text(
                        text = "The Great Tree.",
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 29.sp,
                        letterSpacing = (-0.6).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .testTag("tree_share_button")
                            .clip(CircleShape)
                            .clickable(enabled = !isExportingShare) { handleShareTree() },
                        shape = CircleShape,
                        color = palette.primary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if (isExportingShare) {
                                CircularProgressIndicator(
                                    color = palette.onPrimary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = stringResource(R.string.cd_share_flow_garden_tree),
                                    tint = palette.onPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Text(
                                text = stringResource(R.string.share_tree_button),
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = palette.onPrimary
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .testTag("tree_customize_page_theme")
                            .clip(CircleShape)
                            .clickable { onOpenThemeSettings() },
                        shape = CircleShape,
                        color = palette.surfaceTint,
                        border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Palette,
                                contentDescription = "Customize Great Tree page theme",
                                tint = palette.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Theme",
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = palette.primary
                            )
                        }
                    }
                }
            }
        }

        // 2. Weekly vs Monthly Harvest Toggle
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = palette.surfaceCard.copy(alpha = 0.94f),
                border = BorderStroke(1.dp, palette.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HarvestPeriod.entries.forEach { option ->
                        val selected = period == option
                        val bgCol by animateColorAsState(
                            targetValue = if (selected) palette.primary else Color.Transparent,
                            animationSpec = tween(320, easing = SanctuaryEase),
                            label = "harvest_period_bg"
                        )
                        val txtCol by animateColorAsState(
                            targetValue = if (selected) palette.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            animationSpec = tween(320, easing = SanctuaryEase),
                            label = "harvest_period_txt"
                        )
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tree_period_${option.name.lowercase()}")
                                .clip(RoundedCornerShape(17.dp))
                                .clickable {
                                    if (period != option) {
                                        period = option
                                        onPlayChime(ChimeType.SOLFEGGIO)
                                    }
                                },
                            shape = RoundedCornerShape(17.dp),
                            color = bgCol
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 11.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option.label,
                                    fontFamily = BodyFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = txtCol
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2b. Prominent Daily Habit Goal Streak Counter Card
        item {
            FlowGardenDailyStreakCard(
                streak = uiState.dailyStreak,
                onActionClick = { onNavigate(ScreenId.HABITS) },
                actionLabel = if (uiState.dailyStreak.todayGoalAchieved) "Rituals" else "Log Ritual"
            )
        }

        // 3. Living Great Tree Sanctuary Canvas Card (Starts as Small Plant -> Grows into Full Tree!)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sanctuary_great_tree_card"),
                shape = RoundedCornerShape(30.dp),
                color = palette.surfaceCard.copy(alpha = 0.96f),
                border = BorderStroke(1.2.dp, palette.border),
                shadowElevation = (8f - 5f * palette.darkProgress).dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Stage header + Replay from small plant button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "${displayedStage.badgeLabel} · ${(smoothGrowth * 100f).roundToInt()}% VITALITY",
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                letterSpacing = 0.9.sp,
                                color = palette.primary
                            )
                            Text(
                                text = growthStageTitle,
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = displayedStage.summary,
                                fontFamily = BodyFontFamily,
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(Modifier.width(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("tree_card_share_button")
                                    .clip(CircleShape)
                                    .clickable(enabled = !isExportingShare) { handleShareTree() },
                                shape = CircleShape,
                                color = palette.surfaceTint,
                                border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.35f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (isExportingShare) {
                                        CircularProgressIndicator(
                                            color = palette.primary,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.Share,
                                            contentDescription = stringResource(R.string.cd_share_flow_garden_tree),
                                            tint = palette.primary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .testTag("tree_replay_growth_button")
                                    .clip(CircleShape)
                                    .clickable {
                                        onPlayChime(ChimeType.PIANO)
                                        replayTrigger++
                                    },
                                shape = CircleShape,
                                color = palette.primary
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PlayArrow,
                                        contentDescription = "Replay growth from small plant",
                                        tint = palette.onPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Grow",
                                        fontFamily = BodyFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = palette.onPrimary
                                    )
                                }
                            }
                        }
                    }

                    // 3-Stage Flow Garden Progression Strip (Seedling -> Sprout -> Mature Tree)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PlantGrowthStage.entries.forEach { stageOption ->
                            val isCurrent = displayedStage == stageOption
                            val isReached = displayedStage.ordinal >= stageOption.ordinal
                            val previewScore = when (stageOption) {
                                PlantGrowthStage.SEEDLING -> 0.18f
                                PlantGrowthStage.SPROUT -> 0.54f
                                PlantGrowthStage.MATURE_TREE -> maxOf(0.86f, targetMaturity)
                            }
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("tree_stage_pill_${stageOption.name.lowercase()}")
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        onPlayChime(ChimeType.CLICK)
                                        isManualScrub = true
                                        manualGrowth = previewScore
                                    },
                                shape = RoundedCornerShape(14.dp),
                                color = when {
                                    isCurrent -> palette.primary
                                    isReached -> palette.surfaceTint
                                    else -> palette.surfaceElevated.copy(alpha = 0.65f)
                                },
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isCurrent) Color.Transparent else palette.border
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = stageOption.displayName,
                                        fontFamily = BodyFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = when {
                                            isCurrent -> palette.onPrimary
                                            isReached -> palette.primary
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                    Text(
                                        text = when (stageOption) {
                                            PlantGrowthStage.SEEDLING -> "0–34%"
                                            PlantGrowthStage.SPROUT -> "35–71%"
                                            PlantGrowthStage.MATURE_TREE -> "72–100%"
                                        },
                                        fontFamily = MonoFontFamily,
                                        fontSize = 8.5.sp,
                                        color = if (isCurrent) palette.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Living Great Tree Canvas
                    LivingGreatTreeCanvas(
                        growth = smoothGrowth,
                        stage = displayedStage,
                        isMonthly = period == HarvestPeriod.MONTHLY,
                        highlightedKind = selectedFilter,
                        taskCount = taskHarvestCount,
                        gardenCount = gardenHarvestCount,
                        circleCount = circleHarvestCount,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(310.dp)
                            .clip(RoundedCornerShape(24.dp))
                    )

                    // Live Habit-Driven Growth Formula Breakdown Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = palette.surfaceElevated.copy(alpha = 0.78f),
                        border = BorderStroke(1.dp, palette.border)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 13.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "DAILY HABITS ${growthState.todayCompletionPercent}% · 7D RHYTHM ${growthState.weeklyConsistencyPercent}%",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp,
                                    color = palette.primary
                                )
                                Text(
                                    text = if (growthState.nextStage != null) {
                                        "${growthState.completedHabitsToday}/${growthState.totalHabitsCount} rituals done · +${growthState.percentToNextStage}% to ${growthState.nextStage.displayName}"
                                    } else {
                                        "${growthState.completedHabitsToday}/${growthState.totalHabitsCount} rituals done · Mature Tree thriving!"
                                    },
                                    fontFamily = BodyFontFamily,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .testTag("tree_log_habits_cta")
                                    .clip(CircleShape)
                                    .clickable { onNavigate(ScreenId.HABITS) },
                                shape = CircleShape,
                                color = palette.surfaceTint,
                                border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = "Log Habits",
                                    fontFamily = BodyFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = palette.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Interactive Growth Timeline Scrubber (Seedling -> Sprout -> Mature Tree)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Seedling",
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Sprout",
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                color = palette.primary
                            )
                            Text(
                                text = "Mature Tree",
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Slider(
                            value = smoothGrowth,
                            onValueChange = { next ->
                                isManualScrub = true
                                manualGrowth = next.coerceIn(0.06f, 1f)
                            },
                            onValueChangeFinished = {
                                scope.launch {
                                    growthAnim.snapTo(manualGrowth)
                                }
                            },
                            valueRange = 0.06f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = palette.primary,
                                activeTrackColor = palette.primary,
                                inactiveTrackColor = palette.surfaceElevated
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tree_growth_slider")
                        )
                    }

                    // Harvest Triad Summary Pills (Tasks, Flow Gardens, Chats & Circles)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TreeStatTile(
                            count = taskHarvestCount,
                            label = "Tasks",
                            color = palette.primary,
                            modifier = Modifier.weight(1f)
                        )
                        TreeStatTile(
                            count = gardenHarvestCount,
                            label = "Gardens",
                            color = palette.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        TreeStatTile(
                            count = circleHarvestCount,
                            label = "Circles",
                            color = palette.glowCore,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 4. Filter Pills to highlight branches/fruits on the tree & filter harvest items
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HarvestSourceKind.entries.forEach { kind ->
                    val selected = selectedFilter == kind
                    Surface(
                        modifier = Modifier
                            .testTag("tree_filter_${kind.name.lowercase()}")
                            .clip(CircleShape)
                            .clickable {
                                selectedFilter = kind
                                onPlayChime(ChimeType.CLICK)
                            },
                        shape = CircleShape,
                        color = if (selected) palette.primary else palette.surfaceCard,
                        border = BorderStroke(1.dp, if (selected) Color.Transparent else palette.border)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = kind.icon,
                                contentDescription = kind.label,
                                tint = if (selected) palette.onPrimary else palette.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = kind.label,
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (selected) palette.onPrimary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // 5. Harvest Ledger Cards (Every collected Flow Garden, Task, and Completed Chat/Circle)
        items(visibleItems, key = { it.id }) { item ->
            val kindColor = when (item.kind) {
                HarvestSourceKind.TASKS -> palette.primary
                HarvestSourceKind.FLOW_GARDENS -> palette.secondary
                HarvestSourceKind.CHATS_CIRCLES -> palette.glowCore
                HarvestSourceKind.ALL -> palette.primary
            }
            val unlocked = item.dayNumber <= currentDayDisplayed
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tree_harvest_item_${item.id}"),
                shape = RoundedCornerShape(22.dp),
                color = palette.surfaceCard.copy(alpha = if (unlocked) 0.96f else 0.62f),
                border = BorderStroke(1.dp, if (unlocked) kindColor.copy(alpha = 0.35f) else palette.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(kindColor.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.kind.icon,
                            contentDescription = item.kind.label,
                            tint = kindColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = item.title,
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = item.detail,
                            fontFamily = BodyFontFamily,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = palette.surfaceElevated,
                        border = BorderStroke(1.dp, palette.border)
                    ) {
                        Text(
                            text = "Day ${item.dayNumber}",
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = kindColor,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TreeStatTile(
    count: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val palette = LocalElementalPalette.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = palette.surfaceElevated.copy(alpha = 0.82f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.30f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "$count",
                fontFamily = MonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = color
            )
            Text(
                text = label.uppercase(),
                fontFamily = MonoFontFamily,
                fontSize = 9.sp,
                letterSpacing = 0.7.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LivingGreatTreeCanvas(
    growth: Float,
    stage: PlantGrowthStage,
    isMonthly: Boolean,
    highlightedKind: HarvestSourceKind,
    taskCount: Int,
    gardenCount: Int,
    circleCount: Int,
    modifier: Modifier = Modifier
) {
    val palette = LocalElementalPalette.current
    val infinite = rememberInfiniteTransition(label = "great_tree_sway")
    val sway by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tree_wind_sway"
    )
    val shimmer by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tree_shimmer"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val groundY = h * 0.87f
        val baseX = w * 0.5f

        // 1. Living Atmospheric Backdrop harmonized with active ElementalTheme & Light/Dark
        drawRect(
            brush = Brush.verticalGradient(
                0.0f to palette.heroStart.copy(alpha = 0.88f),
                0.62f to palette.heroEnd.copy(alpha = 0.94f),
                1.0f to palette.pageBgBottom.copy(alpha = 0.96f)
            )
        )

        // Radiant Canopy Moon / Sun Halo behind crown
        val haloCenter = Offset(baseX, h * (0.64f - growth * 0.28f))
        val haloRadius = w * (0.28f + growth * 0.34f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    palette.glowCore.copy(alpha = 0.42f * growth.coerceAtLeast(0.25f)),
                    palette.glowOuter.copy(alpha = 0.16f),
                    Color.Transparent
                ),
                center = haloCenter,
                radius = haloRadius
            ),
            radius = haloRadius,
            center = haloCenter
        )

        // 2. Mossy Sanctuary Hill / Soil Mound
        val hillPath = Path().apply {
            moveTo(0f, h)
            lineTo(0f, groundY + 14.dp.toPx())
            quadraticTo(baseX, groundY - 20.dp.toPx(), w, groundY + 14.dp.toPx())
            lineTo(w, h)
            close()
        }
        drawPath(
            path = hillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    lerp(palette.heroStart, palette.primary, 0.25f),
                    palette.heroEnd
                ),
                startY = groundY - 18.dp.toPx(),
                endY = h
            )
        )

        // 3. Organic Trunk & Roots (grows smoothly from a tiny green sprout stem -> sturdy great trunk)
        val trunkHeight = h * (0.14f + growth * (if (isMonthly) 0.50f else 0.44f))
        val trunkTopX = baseX + sway * (4f + growth * 7f).dp.toPx()
        val trunkTopY = groundY - trunkHeight
        val baseWidth = (3.5f + growth * (if (isMonthly) 20f else 15f)).dp.toPx()
        val topWidth = (2.0f + growth * 6f).dp.toPx()

        val barkLight = lerp(Color(0xFF7B5642), palette.primaryBright, 0.22f)
        val barkDark = lerp(Color(0xFF3E271E), palette.heroStart, 0.35f)
        val sproutGreen = lerp(Color(0xFF63D496), palette.secondary, 0.35f)
        val trunkColorStart = if (growth < 0.28f) lerp(sproutGreen, barkLight, growth / 0.28f) else barkLight
        val trunkColorEnd = if (growth < 0.28f) lerp(sproutGreen.copy(alpha = 0.85f), barkDark, growth / 0.28f) else barkDark

        // Ground roots unfurling as tree matures
        if (growth > 0.26f) {
            val rootProgress = ((growth - 0.26f) / 0.74f).coerceIn(0f, 1f)
            val rootSpan = (22f + rootProgress * 44f).dp.toPx()
            val leftRoot = Path().apply {
                moveTo(baseX - baseWidth * 0.3f, groundY - 4.dp.toPx())
                quadraticTo(baseX - rootSpan * 0.5f, groundY + 4.dp.toPx(), baseX - rootSpan, groundY + 12.dp.toPx())
            }
            val rightRoot = Path().apply {
                moveTo(baseX + baseWidth * 0.3f, groundY - 4.dp.toPx())
                quadraticTo(baseX + rootSpan * 0.5f, groundY + 4.dp.toPx(), baseX + rootSpan, groundY + 12.dp.toPx())
            }
            drawPath(leftRoot, trunkColorEnd, style = Stroke(width = (3f + rootProgress * 5f).dp.toPx(), cap = StrokeCap.Round))
            drawPath(rightRoot, trunkColorEnd, style = Stroke(width = (3f + rootProgress * 5f).dp.toPx(), cap = StrokeCap.Round))
        }

        // Tapered Main Trunk Path
        val trunkPath = Path().apply {
            moveTo(baseX - baseWidth * 0.55f, groundY + 4.dp.toPx())
            cubicTo(
                baseX - baseWidth * 0.35f, groundY - trunkHeight * 0.45f,
                trunkTopX - topWidth * 0.6f, trunkTopY + trunkHeight * 0.28f,
                trunkTopX - topWidth * 0.5f, trunkTopY
            )
            lineTo(trunkTopX + topWidth * 0.5f, trunkTopY)
            cubicTo(
                trunkTopX + topWidth * 0.6f, trunkTopY + trunkHeight * 0.28f,
                baseX + baseWidth * 0.35f, groundY - trunkHeight * 0.45f,
                baseX + baseWidth * 0.55f, groundY + 4.dp.toPx()
            )
            close()
        }
        drawPath(
            path = trunkPath,
            brush = Brush.verticalGradient(
                colors = listOf(trunkColorStart, trunkColorEnd),
                startY = trunkTopY,
                endY = groundY
            )
        )

        // Tiny Seedling Cotyledon Leaves (prominent in Seedling, tapering cleanly into Sprout)
        val seedlingAlpha = (1f - ((growth - 0.24f) / 0.26f).coerceIn(0f, 1f)).coerceIn(0f, 1f)
        if (seedlingAlpha > 0.02f) {
            val leafSpan = (18f + growth * 26f).dp.toPx()
            val leftLeaf = Path().apply {
                moveTo(trunkTopX, trunkTopY + 4.dp.toPx())
                quadraticTo(trunkTopX - leafSpan, trunkTopY - leafSpan * 0.85f, trunkTopX - leafSpan * 1.15f, trunkTopY + 2.dp.toPx())
                quadraticTo(trunkTopX - leafSpan * 0.5f, trunkTopY + 11.dp.toPx(), trunkTopX, trunkTopY + 4.dp.toPx())
            }
            val rightLeaf = Path().apply {
                moveTo(trunkTopX, trunkTopY + 4.dp.toPx())
                quadraticTo(trunkTopX + leafSpan, trunkTopY - leafSpan * 0.85f, trunkTopX + leafSpan * 1.15f, trunkTopY + 2.dp.toPx())
                quadraticTo(trunkTopX + leafSpan * 0.5f, trunkTopY + 11.dp.toPx(), trunkTopX, trunkTopY + 4.dp.toPx())
            }
            drawPath(leftLeaf, palette.primaryBright.copy(alpha = 0.94f * seedlingAlpha))
            drawPath(rightLeaf, palette.secondary.copy(alpha = 0.94f * seedlingAlpha))
        }

        // 4. Primary & Secondary Branches unfolding cleanly across Sprout (0.35+) and Mature Tree (0.72+)
        data class BranchSpec(val startFrac: Float, val angleDeg: Float, val lengthFrac: Float, val unlockAt: Float)
        val branches = listOf(
            BranchSpec(0.54f, -48f, 0.30f, 0.33f),
            BranchSpec(0.58f, 46f, 0.30f, 0.37f),
            BranchSpec(0.72f, -34f, 0.34f, 0.48f),
            BranchSpec(0.75f, 36f, 0.34f, 0.52f),
            BranchSpec(0.88f, -22f, 0.28f, 0.64f),
            BranchSpec(0.90f, 22f, 0.28f, 0.68f),
            BranchSpec(0.96f, 0f, 0.24f, 0.72f)
        )

        val canopyAnchors = mutableListOf<Offset>()
        branches.forEachIndexed { idx, spec ->
            if (growth > spec.unlockAt) {
                val localG = ((growth - spec.unlockAt) / (1f - spec.unlockAt)).coerceIn(0f, 1f)
                val sx = baseX + (trunkTopX - baseX) * spec.startFrac
                val sy = groundY - trunkHeight * spec.startFrac
                val len = h * spec.lengthFrac * localG * (if (isMonthly || stage == PlantGrowthStage.MATURE_TREE) 1.12f else 1.0f)
                val rad = ((spec.angleDeg - 90f) + sway * 2.5f) * (PI / 180.0)
                val ex = sx + (cos(rad) * len).toFloat()
                val ey = sy + (sin(rad) * len).toFloat()
                val ctrlX = (sx + ex) * 0.5f + (if (spec.angleDeg < 0) -10f else 10f).dp.toPx() * localG
                val ctrlY = (sy + ey) * 0.5f - 8.dp.toPx() * localG

                val bPath = Path().apply {
                    moveTo(sx, sy)
                    quadraticTo(ctrlX, ctrlY, ex, ey)
                }
                drawPath(
                    path = bPath,
                    color = barkLight.copy(alpha = 0.94f),
                    style = Stroke(
                        width = (2.2f + (1f - idx * 0.08f) * 4.5f * localG).dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
                canopyAnchors.add(Offset(ex, ey))
                if (localG > 0.45f) {
                    canopyAnchors.add(Offset(ctrlX, ctrlY))
                }
            }
        }

        // 5. Lush Layered Canopy Clouds harmonized to the active Elemental Theme
        if (growth > 0.30f) {
            val canopyG = ((growth - 0.30f) / 0.70f).coerceIn(0f, 1f)
            canopyAnchors.forEachIndexed { idx, anchor ->
                val baseR = (20f + canopyG * (if (isMonthly) 32f else 26f) + (idx % 3) * 6f).dp.toPx()
                val pulseR = baseR * (0.96f + 0.04f * sin(shimmer + idx))
                val c1 = if (idx % 2 == 0) palette.primary else palette.secondary
                val c2 = palette.primaryBright
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            c2.copy(alpha = 0.58f * canopyG),
                            c1.copy(alpha = 0.46f * canopyG),
                            palette.heroStart.copy(alpha = 0.14f * canopyG),
                            Color.Transparent
                        ),
                        center = anchor,
                        radius = pulseR
                    ),
                    radius = pulseR,
                    center = anchor
                )
            }
        }

        // 6. Harvest Fruit / Blossom / Lantern Nodes representing Tasks, Flow Gardens, and Chats & Circles
        if (growth > 0.36f && canopyAnchors.isNotEmpty()) {
            val nodeG = ((growth - 0.36f) / 0.64f).coerceIn(0f, 1f)
            val totalNodes = (taskCount + gardenCount + circleCount).coerceIn(6, 22)
            for (i in 0 until totalNodes) {
                val anchor = canopyAnchors[i % canopyAnchors.size]
                val ang = (i * 137.5f) * (PI / 180.0)
                val dist = (10f + (i % 4) * 9f).dp.toPx() * nodeG
                val nx = anchor.x + (cos(ang) * dist).toFloat() + sway * 2.dp.toPx()
                val ny = anchor.y + (sin(ang) * dist).toFloat()

                val nodeKind = when {
                    i < taskCount -> HarvestSourceKind.TASKS
                    i < taskCount + gardenCount -> HarvestSourceKind.FLOW_GARDENS
                    else -> HarvestSourceKind.CHATS_CIRCLES
                }
                val isEmphasized = highlightedKind == HarvestSourceKind.ALL || highlightedKind == nodeKind
                val nodeAlpha = (if (isEmphasized) 0.96f else 0.24f) * nodeG

                val nodeColor = when (nodeKind) {
                    HarvestSourceKind.TASKS -> palette.primaryBright
                    HarvestSourceKind.FLOW_GARDENS -> palette.secondary
                    HarvestSourceKind.CHATS_CIRCLES -> Color(0xFFFFE599)
                    HarvestSourceKind.ALL -> palette.primaryBright
                }
                val nr = (3.8f + (if (isEmphasized) 1.6f else 0f) + (if (isMonthly) 0.8f else 0f)).dp.toPx() * nodeG
                val glowR = nr * (2.6f + 0.35f * sin(shimmer + i * 0.9f))

                // Soft glowing fruit/blossom halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(nodeColor.copy(alpha = 0.55f * nodeAlpha), Color.Transparent),
                        center = Offset(nx, ny),
                        radius = glowR
                    ),
                    radius = glowR,
                    center = Offset(nx, ny)
                )
                drawCircle(
                    color = nodeColor.copy(alpha = nodeAlpha),
                    radius = nr,
                    center = Offset(nx, ny)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.72f * nodeAlpha),
                    radius = nr * 0.36f,
                    center = Offset(nx - nr * 0.25f, ny - nr * 0.25f)
                )
            }
        }

        // 7. Gently Floating Fireflies / Falling Petals around the Great Tree
        for (p in 0 until 10) {
            val phase = (shimmer * 0.16f + p * 0.11f) % 1f
            val px = w * (0.14f + (p * 0.082f) % 0.72f) + sin(shimmer + p * 1.4f) * 14.dp.toPx()
            val py = h * (0.18f + phase * 0.64f)
            val pa = sin(phase * PI).toFloat().coerceIn(0f, 1f) * 0.65f * growth.coerceAtLeast(0.3f)
            drawCircle(
                color = palette.primaryBright.copy(alpha = pa),
                radius = (2.0f + (p % 2) * 1.1f).dp.toPx(),
                center = Offset(px, py)
            )
        }
    }
}
