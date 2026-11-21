package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.model.CategoryColor
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ScreenId
import com.onereflection.app.model.TaskCategory
import com.onereflection.app.model.TaskItem
import com.onereflection.app.model.TaskPriority
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.BotanicalTaskJourneyCard
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors
import com.onereflection.app.ui.theme.categoryCardBackground
import com.onereflection.app.ui.theme.categoryPreviewColor
import com.onereflection.app.ui.theme.priorityBadgeColors
import kotlin.math.roundToInt

@Composable
fun TasksScreen(
    uiState: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onToggleTask: (String) -> Unit,
    onUpdateTaskProgress: (String, Int, Boolean) -> Unit,
    onScheduleTaskForNextDay: (String) -> Unit,
    onAddTask: (String, String, TaskPriority) -> Unit,
    onCyclePriority: (String) -> Unit,
    onAddCategory: (String, CategoryColor) -> Unit,
    onUpdateCategory: (String, String, CategoryColor) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onPlayChime: (ChimeType) -> Unit
) {
    BackHandler { onNavigate(ScreenId.HOME) }

    var selectedCategoryId by remember { mutableStateOf("all") }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showCategoryManager by remember { mutableStateOf(false) }
    var showCompleted by remember { mutableStateOf(false) }

    val oledMode = uiState.oledMode
    val palette = LocalElementalPalette.current
    val borderColor = palette.border
    val filteredTasks = remember(uiState.tasks, selectedCategoryId) {
        if (selectedCategoryId == "all") uiState.tasks
        else uiState.tasks.filter { it.categoryId == selectedCategoryId }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 164.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Editorial Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SHARED FLOWS & LISTS",
                        fontFamily = MonoFontFamily,
                        fontSize = 10.5.sp,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tasks & Flows.",
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 31.sp,
                        letterSpacing = (-0.6).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .testTag("tasks_open_great_tree_button")
                            .clip(CircleShape)
                            .clickable { onNavigate(ScreenId.SANCTUARY_TREE) },
                        shape = CircleShape,
                        color = palette.surfaceTint,
                        border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Park,
                                contentDescription = "Open Great Tree Harvest",
                                tint = palette.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Tree",
                                fontFamily = BodyFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.primary
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .testTag("tasks_categories_button")
                            .clip(CircleShape)
                            .clickable { showCategoryManager = true },
                        shape = CircleShape,
                        color = palette.surfaceCard,
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = "Manage Categories",
                                tint = palette.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Lists",
                                fontFamily = BodyFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // Botanical Completion Journey Visualization (grows live as tasks are completed!)
        item {
            BotanicalTaskJourneyCard(
                activeTasks = uiState.tasks,
                completedTasks = uiState.completedTasks,
                oledMode = oledMode
            )
        }

        // Category Filter Pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val allSelected = selectedCategoryId == "all"
                Surface(
                    modifier = Modifier
                        .testTag("tasks_filter_all")
                        .clip(CircleShape)
                        .clickable { selectedCategoryId = "all" },
                    shape = CircleShape,
                    color = when {
                        allSelected && oledMode -> OneReflectionColors.TerracottaLight
                        allSelected -> OneReflectionColors.InkPrimary
                        oledMode -> OneReflectionColors.OledSurface
                        else -> OneReflectionColors.SurfaceCard
                    },
                    border = BorderStroke(1.dp, if (allSelected) Color.Transparent else borderColor)
                ) {
                    Text(
                        text = "All (${uiState.tasks.size})",
                        fontFamily = BodyFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = when {
                            allSelected && oledMode -> OneReflectionColors.InkPrimary
                            allSelected -> Color.White
                            else -> MaterialTheme.colorScheme.onBackground
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                    )
                }

                uiState.categories.forEach { cat ->
                    val selected = selectedCategoryId == cat.id
                    Surface(
                        modifier = Modifier
                            .testTag("tasks_filter_${cat.id}")
                            .clip(CircleShape)
                            .clickable { selectedCategoryId = cat.id },
                        shape = CircleShape,
                        color = when {
                            selected && oledMode -> OneReflectionColors.TerracottaLight
                            selected -> OneReflectionColors.InkPrimary
                            oledMode -> OneReflectionColors.OledSurface
                            else -> OneReflectionColors.SurfaceCard
                        },
                        border = BorderStroke(1.dp, if (selected) Color.Transparent else borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(categoryPreviewColor(cat.color))
                            )
                            Text(
                                text = cat.name,
                                fontFamily = BodyFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.5.sp,
                                color = when {
                                    selected && oledMode -> OneReflectionColors.InkPrimary
                                    selected -> Color.White
                                    else -> MaterialTheme.colorScheme.onBackground
                                }
                            )
                        }
                    }
                }
            }
        }

        // Quick Creation Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tasks_add_button")
                        .clip(RoundedCornerShape(22.dp))
                        .clickable { showAddTaskDialog = true },
                    shape = RoundedCornerShape(22.dp),
                    color = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.InkPrimary,
                    border = BorderStroke(1.dp, if (oledMode) OneReflectionColors.GoldLuminous.copy(alpha = 0.3f) else Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 14.dp, horizontal = 18.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "New Shared Task",
                            tint = OneReflectionColors.GoldLuminous,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "New Shared Task",
                            fontFamily = BodyFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("tasks_voice_brief_button")
                        .clip(CircleShape)
                        .clickable { onOpenVoiceStudio() },
                    shape = CircleShape,
                    color = if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.PeachMist,
                    border = BorderStroke(1.dp, OneReflectionColors.Terracotta.copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Add voice task brief",
                            tint = if (oledMode) OneReflectionColors.TerracottaLight else OneReflectionColors.Terracotta,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Active Editorial Task Cards
        items(filteredTasks, key = { it.id }) { task ->
            val catColor = uiState.categories.find { it.id == task.categoryId }?.color ?: CategoryColor.SLATE
            TaskEditorialCard(
                task = task,
                catColor = catColor,
                oledMode = oledMode,
                onToggle = { onToggleTask(task.id) },
                onUpdateProgress = { pct, notify -> onUpdateTaskProgress(task.id, pct, notify) },
                onScheduleNextDay = { onScheduleTaskForNextDay(task.id) },
                onCyclePriority = { onCyclePriority(task.id) },
                onPlayVoice = { onPlayChime(ChimeType.SOLFEGGIO) }
            )
        }

        // Completed Tasks Accordion
        if (uiState.completedTasks.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tasks_completed_toggle")
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { showCompleted = !showCompleted },
                        shape = RoundedCornerShape(18.dp),
                        color = palette.surfaceCard,
                        border = BorderStroke(1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "COMPLETED FLOWS (${uiState.completedTasks.size})",
                                fontFamily = MonoFontFamily,
                                fontSize = 10.5.sp,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = if (showCompleted) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = "Toggle completed tasks",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    AnimatedVisibility(visible = showCompleted) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            uiState.completedTasks.forEach { task ->
                                val catColor = uiState.categories.find { it.id == task.categoryId }?.color ?: CategoryColor.SLATE
                                TaskEditorialCard(
                                    task = task,
                                    catColor = catColor,
                                    oledMode = oledMode,
                                    onToggle = { onToggleTask(task.id) },
                                    onUpdateProgress = { pct, notify -> onUpdateTaskProgress(task.id, pct, notify) },
                                    onScheduleNextDay = { onScheduleTaskForNextDay(task.id) },
                                    onCyclePriority = { onCyclePriority(task.id) },
                                    onPlayVoice = { onPlayChime(ChimeType.CLICK) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            categories = uiState.categories,
            onDismiss = { showAddTaskDialog = false },
            onCreate = { title, catId, priority ->
                onAddTask(title, catId, priority)
                showAddTaskDialog = false
            }
        )
    }

    if (showCategoryManager) {
        CategoryManagerDialog(
            categories = uiState.categories,
            onDismiss = { showCategoryManager = false },
            onAdd = onAddCategory,
            onDelete = onDeleteCategory
        )
    }
}

@Composable
private fun TaskEditorialCard(
    task: TaskItem,
    catColor: CategoryColor,
    oledMode: Boolean,
    onToggle: () -> Unit,
    onUpdateProgress: (Int, Boolean) -> Unit,
    onScheduleNextDay: () -> Unit,
    onCyclePriority: () -> Unit,
    onPlayVoice: () -> Unit
) {
    val palette = LocalElementalPalette.current
    val accent = categoryPreviewColor(catColor)
    val tintBg = categoryCardBackground(catColor, oledMode)
    val (prioBg, prioInk) = priorityBadgeColors(task.priority)
    val borderColor = palette.border

    var sliderDraft by remember(task.id, task.progressPercent) {
        mutableFloatStateOf(task.progressPercent.toFloat())
    }
    val shownPercent = sliderDraft.roundToInt().coerceIn(0, 100)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (task.completed) 0.68f else 1f)
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(24.dp),
        color = palette.surfaceCard.copy(alpha = 0.96f),
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (!oledMode && !task.completed) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tactile Circle Checkbox
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .size(26.dp)
                        .testTag("task_check_${task.id}")
                        .clip(CircleShape)
                        .background(if (task.completed) palette.primary else Color.Transparent)
                        .border(2.dp, palette.primary, CircleShape)
                        .clickable { onToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    if (task.completed) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Completed",
                            tint = palette.onPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(accent)
                            )
                            Text(
                                text = task.category.uppercase(),
                                fontFamily = MonoFontFamily,
                                fontSize = 10.sp,
                                letterSpacing = 0.9.sp,
                                color = palette.primary
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .testTag("task_priority_${task.id}")
                                .clip(CircleShape)
                                .clickable { onCyclePriority() },
                            shape = CircleShape,
                            color = if (oledMode) palette.surfaceElevated else prioBg
                        ) {
                            Text(
                                text = task.priority.label,
                                fontFamily = MonoFontFamily,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (oledMode) palette.primaryBright else prioInk,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = task.title,
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            fontFamily = BodyFontFamily,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Interactive Progress Slider + Quick 50% Chip + Complete Next Day Control
            if (!task.completed) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = palette.surfaceElevated.copy(alpha = 0.72f),
                    border = BorderStroke(1.dp, palette.border.copy(alpha = 0.7f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PROGRESS · $shownPercent%",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                letterSpacing = 0.7.sp,
                                color = palette.primary
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .testTag("task_half_chip_${task.id}")
                                        .clip(CircleShape)
                                        .clickable {
                                            sliderDraft = 50f
                                            onUpdateProgress(50, true)
                                        },
                                    shape = CircleShape,
                                    color = if (shownPercent == 50) palette.primary else palette.surfaceCard,
                                    border = BorderStroke(1.dp, palette.border)
                                ) {
                                    Text(
                                        text = "50%",
                                        fontFamily = MonoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = if (shownPercent == 50) palette.onPrimary else MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    modifier = Modifier
                                        .testTag("task_next_day_${task.id}")
                                        .clip(CircleShape)
                                        .clickable { onScheduleNextDay() },
                                    shape = CircleShape,
                                    color = if (task.carryOverNextDay) palette.primary else palette.surfaceTint,
                                    border = BorderStroke(1.dp, palette.primary.copy(alpha = 0.35f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.EventRepeat,
                                            contentDescription = "Complete remaining task next day",
                                            tint = if (task.carryOverNextDay) palette.onPrimary else palette.primary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = if (task.carryOverNextDay) "Tomorrow ✓" else "Next Day",
                                            fontFamily = BodyFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.5.sp,
                                            color = if (task.carryOverNextDay) palette.onPrimary else palette.primary
                                        )
                                    }
                                }
                            }
                        }

                        Slider(
                            value = sliderDraft,
                            onValueChange = { raw ->
                                val snapped = ((raw / 5f).roundToInt() * 5).coerceIn(0, 100)
                                sliderDraft = snapped.toFloat()
                                onUpdateProgress(snapped, false)
                            },
                            onValueChangeFinished = {
                                val finalPct = sliderDraft.roundToInt().coerceIn(0, 100)
                                onUpdateProgress(finalPct, true)
                            },
                            valueRange = 0f..100f,
                            colors = SliderDefaults.colors(
                                thumbColor = palette.primary,
                                activeTrackColor = palette.primary,
                                inactiveTrackColor = palette.surfaceCard
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("task_progress_slider_${task.id}")
                        )
                    }
                }
            }

            // Per-User Completion Percentages Breakdown
            if (task.assignees.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    task.assignees.forEach { person ->
                        val userPct = if (task.completed) 100 else person.completionPercent
                        Surface(
                            shape = CircleShape,
                            color = palette.surfaceElevated.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, palette.border)
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 4.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AvatarImage(
                                    url = person.avatar,
                                    fallbackInitials = person.initials ?: person.name.take(2).uppercase(),
                                    contentDescription = "${person.name} $userPct% completed",
                                    size = 24.dp,
                                    ringColor = palette.primary,
                                    ringWidth = 1.2.dp
                                )
                                Text(
                                    text = person.name,
                                    fontFamily = BodyFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "$userPct%",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp,
                                    color = palette.primary
                                )
                            }
                        }
                    }
                }
            }

            if (!task.voiceBriefQuote.isNullOrBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onPlayVoice() },
                    shape = RoundedCornerShape(16.dp),
                    color = if (oledMode) palette.surfaceElevated else tintBg
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = "Play voice brief",
                            tint = palette.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "\"${task.voiceBriefQuote}\"",
                            fontFamily = DisplayFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = task.voiceBriefDuration ?: "0:12",
                            fontFamily = MonoFontFamily,
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Due time",
                        tint = if (task.carryOverNextDay) palette.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = task.dueTime,
                        fontFamily = MonoFontFamily,
                        fontSize = 11.5.sp,
                        fontWeight = if (task.carryOverNextDay) FontWeight.Bold else FontWeight.Normal,
                        color = if (task.carryOverNextDay) palette.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = if (task.completed) "HARVESTED 100%" else "${100 - shownPercent}% LEFT",
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp,
                    color = palette.primary
                )
            }
        }
    }
}

@Composable
private fun AddTaskDialog(
    categories: List<TaskCategory>,
    onDismiss: () -> Unit,
    onCreate: (String, String, TaskPriority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCatId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "cat-home") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Shared Flow",
                fontFamily = DisplayFontFamily,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Category", fontFamily = MonoFontFamily, fontSize = 10.sp)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val selected = selectedCatId == cat.id
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { selectedCatId = cat.id },
                            shape = CircleShape,
                            color = if (selected) OneReflectionColors.Terracotta else OneReflectionColors.SurfaceCardLow
                        ) {
                            Text(
                                text = cat.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else OneReflectionColors.InkPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Text("Priority", fontFamily = MonoFontFamily, fontSize = 10.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TaskPriority.entries.forEach { prio ->
                        val selected = selectedPriority == prio
                        Surface(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { selectedPriority = prio },
                            shape = CircleShape,
                            color = if (selected) OneReflectionColors.InkPrimary else OneReflectionColors.SurfaceCardLow
                        ) {
                            Text(
                                text = prio.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Color.White else OneReflectionColors.InkPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onCreate(title, selectedCatId, selectedPriority) },
                colors = ButtonDefaults.buttonColors(containerColor = OneReflectionColors.Terracotta)
            ) {
                Text("Create", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun CategoryManagerDialog(
    categories: List<TaskCategory>,
    onDismiss: () -> Unit,
    onAdd: (String, CategoryColor) -> Unit,
    onDelete: (String) -> Unit
) {
    var newName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(CategoryColor.MATCHA) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Manage Categories",
                fontFamily = DisplayFontFamily,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                categories.forEach { cat ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(categoryPreviewColor(cat.color))
                            )
                            Text(cat.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        if (categories.size > 1) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete category",
                                tint = OneReflectionColors.DangerInk,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { onDelete(cat.id) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New category name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryColor.entries.take(5).forEach { col ->
                        val active = selectedColor == col
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(categoryPreviewColor(col))
                                .border(
                                    width = if (active) 2.dp else 0.dp,
                                    color = OneReflectionColors.InkPrimary,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = col }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newName.isNotBlank()) {
                        onAdd(newName, selectedColor)
                        newName = ""
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OneReflectionColors.Terracotta)
            ) {
                Text(if (newName.isNotBlank()) "Add" else "Done", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Close")
            }
        }
    )
}
