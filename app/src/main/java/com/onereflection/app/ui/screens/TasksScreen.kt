package com.onereflection.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.model.*
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.theme.BrandColors
import com.onereflection.app.ui.theme.toThemeStyle
import com.onereflection.app.viewmodel.OneReflectionUiState

@Composable
fun TasksScreen(
    state: OneReflectionUiState,
    onBack: () -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onToggleTask: (String) -> Unit,
    onAddTask: (title: String, categoryId: String, priority: TaskPriority) -> Unit,
    onCyclePriority: (String) -> Unit,
    onAddCategory: (String, CategoryColor) -> Unit,
    onUpdateCategory: (String, String, CategoryColor) -> Unit,
    onDeleteCategory: (String) -> Unit
) {
    BackHandler(onBack = onBack)

    val oledMode = state.oledMode
    var selectedCategoryId by remember { mutableStateOf("all") }
    var sortMode by remember { mutableStateOf(TaskSortMode.PRIORITY_DESC) }
    var activeDay by remember { mutableIntStateOf(16) }
    var completedOpen by remember { mutableStateOf(true) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showCategoryModal by remember { mutableStateOf(false) }

    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark
    val textSecondary = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
    val cardBorder = if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.06f)

    val filteredAndSorted = remember(state.tasks, selectedCategoryId, sortMode) {
        val filtered = if (selectedCategoryId == "all") {
            state.tasks
        } else {
            state.tasks.filter { it.categoryId == selectedCategoryId }
        }
        when (sortMode) {
            TaskSortMode.PRIORITY_DESC -> filtered.sortedWith(compareByDescending<TaskItem> { it.priority.weight }.thenBy { it.dueTime })
            TaskSortMode.PRIORITY_ASC -> filtered.sortedWith(compareBy<TaskItem> { it.priority.weight }.thenBy { it.dueTime })
            TaskSortMode.TIME -> filtered.sortedBy { it.dueTime }
            TaskSortMode.DEFAULT -> filtered
        }
    }

    val days = remember { listOf("Mon" to 14, "Tue" to 15, "Wed" to 16, "Thu" to 17, "Fri" to 18, "Sat" to 19) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Sync Score
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BrandColors.ButterYellow)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(Icons.Default.Celebration, contentDescription = null, tint = BrandColors.ButterYellowText, modifier = Modifier.size(14.dp))
                        Text("SYNCHRONIZED CIRCLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.ButterYellowText)
                    }
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Stars,
                            contentDescription = null,
                            tint = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum,
                            modifier = Modifier.size(15.dp)
                        )
                        Text("8.8 Sync Score", style = MaterialTheme.typography.labelSmall, color = textPrimary)
                    }
                }
                Column {
                    Text("Tasks & Shared Flows", style = MaterialTheme.typography.headlineMedium, color = textPrimary)
                    Text("Conquer your day alongside your circle.", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                }
            }
        }

        // 2. Weekly Calendar Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                days.forEach { (name, num) ->
                    val isActive = num == activeDay
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                when {
                                    isActive -> BrandColors.ButterYellow
                                    oledMode -> BrandColors.OledCard
                                    else -> BrandColors.SurfaceContainerLow
                                }
                            )
                            .clickable {
                                ChimePlayer.play(ChimeType.CLICK)
                                activeDay = num
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isActive) BrandColors.ButterYellowText else textSecondary)
                        Text(num.toString(), style = MaterialTheme.typography.titleMedium, color = if (isActive) BrandColors.ButterYellowText else textPrimary)
                        if (isActive) {
                            Box(modifier = Modifier.padding(top = 2.dp).size(5.dp).clip(CircleShape).background(BrandColors.ButterYellowText))
                        }
                    }
                }
            }
        }

        // 3. Dynamic Category Filter Chips + Tags Manager
        item {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterPill(
                    label = "All",
                    count = state.tasks.size,
                    dotColor = null,
                    selected = selectedCategoryId == "all",
                    oledMode = oledMode,
                    onClick = {
                        ChimePlayer.play(ChimeType.CLICK)
                        selectedCategoryId = "all"
                    }
                )
                state.categories.forEach { cat ->
                    val count = state.tasks.count { it.categoryId == cat.id }
                    FilterPill(
                        label = cat.name,
                        count = count,
                        dotColor = cat.color.toThemeStyle().preview,
                        selected = selectedCategoryId == cat.id,
                        oledMode = oledMode,
                        onClick = {
                            ChimePlayer.play(ChimeType.CLICK)
                            selectedCategoryId = cat.id
                        }
                    )
                }
                Surface(
                    onClick = {
                        ChimePlayer.play(ChimeType.CLICK)
                        showCategoryModal = true
                    },
                    shape = CircleShape,
                    color = BrandColors.PinkFixed,
                    modifier = Modifier.height(36.dp).testTag("tasks_manage_tags_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null, tint = BrandColors.PrimaryPlum, modifier = Modifier.size(15.dp))
                        Text("Tags", style = MaterialTheme.typography.labelMedium, color = BrandColors.PrimaryPlum)
                    }
                }
            }
        }

        // 4. Sort Controller Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FLOWS (${filteredAndSorted.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = textSecondary
                )
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    SortButton("High ↓", sortMode == TaskSortMode.PRIORITY_DESC, oledMode) { sortMode = TaskSortMode.PRIORITY_DESC }
                    SortButton("Low ↑", sortMode == TaskSortMode.PRIORITY_ASC, oledMode) { sortMode = TaskSortMode.PRIORITY_ASC }
                    SortButton("Time", sortMode == TaskSortMode.TIME, oledMode) { sortMode = TaskSortMode.TIME }
                }
            }
        }

        // 5. Bento Tasks Deck
        if (filteredAndSorted.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, tint = textSecondary, modifier = Modifier.size(32.dp))
                        Text("No tasks in this view", style = MaterialTheme.typography.titleSmall, color = textPrimary)
                        Text("Tap + Add New Task below to create one", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                    }
                }
            }
        } else {
            items(filteredAndSorted, key = { it.id }) { task ->
                val cat = state.categories.find { it.id == task.categoryId }
                val style = (cat?.color ?: CategoryColor.SLATE).toThemeStyle()
                val cardBg = if (oledMode) BrandColors.OledCard else style.cardBgLight
                val cardText = if (oledMode) Color.White else BrandColors.OnSurfaceDark

                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = cardBg,
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (oledMode) Color(0xFF27272A) else style.badgeBg)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = cat?.name ?: task.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (oledMode) Color.White else style.badgeText
                                    )
                                }

                                PriorityBadge(
                                    priority = task.priority,
                                    onClick = { onCyclePriority(task.id) }
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (oledMode) Color(0xFF27272A) else Color.White.copy(alpha = 0.85f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = textSecondary, modifier = Modifier.size(13.dp))
                                Text(task.dueTime, style = MaterialTheme.typography.labelSmall, color = if (oledMode) Color.White else BrandColors.OnSurfaceDark)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 10.dp)) {
                                Text(task.title, style = MaterialTheme.typography.titleLarge, color = cardText)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = task.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
                                )
                            }

                            Surface(
                                onClick = { onToggleTask(task.id) },
                                shape = CircleShape,
                                color = if (oledMode) Color(0xFF27272A) else Color.White,
                                modifier = Modifier.size(36.dp).testTag("task_complete_${task.id}")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Circle,
                                        contentDescription = "Mark task done",
                                        tint = BrandColors.OutlineVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        if (task.subtasksDone != null || task.voiceBriefQuote != null || task.codeBranch != null || task.assignees.isNotEmpty() || task.streak != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (task.subtasksDone != null && task.subtasksTotal != null) {
                                    TaskMetaPill("${task.subtasksDone}/${task.subtasksTotal} subtasks", oledMode)
                                }
                                if (task.voiceBriefQuote != null) {
                                    Surface(
                                        onClick = { ChimePlayer.play(ChimeType.TIBETAN) },
                                        shape = CircleShape,
                                        color = if (oledMode) Color(0xFF27272A) else Color.White.copy(alpha = 0.9f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.PlayCircle,
                                                contentDescription = null,
                                                tint = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = task.voiceBriefDuration?.let { "Voice: $it" } ?: "Voice Note",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (oledMode) Color.White else BrandColors.OnSurfaceDark
                                            )
                                        }
                                    }
                                }
                                if (task.codeBranch != null) {
                                    TaskMetaPill(task.codeBranch, oledMode)
                                }
                                if (task.streak != null) {
                                    TaskMetaPill("${task.streak}d streak", oledMode)
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                if (task.assignees.isNotEmpty()) {
                                    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                                        task.assignees.forEach { assignee ->
                                            if (assignee.avatar != null) {
                                                AvatarImage(
                                                    url = assignee.avatar,
                                                    initials = assignee.name.take(2),
                                                    contentDescription = assignee.name,
                                                    size = 28.dp
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(CircleShape)
                                                        .background(BrandColors.LavenderContainer)
                                                        .border(2.dp, Color.White, CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = assignee.initials ?: assignee.name.take(2),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = BrandColors.LavenderText
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Completed Today Accordion
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                ChimePlayer.play(ChimeType.CLICK)
                                completedOpen = !completedOpen
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (oledMode) Color(0xFF27272A) else BrandColors.SurfaceContainerHighest),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.completedTasks.size.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textPrimary
                                )
                            }
                            Text("Completed Today", style = MaterialTheme.typography.titleMedium, color = textPrimary)
                        }
                        Icon(
                            imageVector = if (completedOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle completed list",
                            tint = textSecondary
                        )
                    }

                    AnimatedVisibility(visible = completedOpen) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.completedTasks.forEach { done ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (oledMode) BrandColors.OledBlack else Color.White.copy(alpha = 0.85f))
                                        .clickable { onToggleTask(done.id) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Restore task",
                                            tint = if (oledMode) BrandColors.ButterYellow else BrandColors.SecondaryGold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = done.title,
                                            style = MaterialTheme.typography.bodySmall,
                                            textDecoration = TextDecoration.LineThrough,
                                            color = textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(done.dueTime, fontSize = 10.sp, color = textSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Bottom Add Task + Voice Button Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        ChimePlayer.play(ChimeType.CLICK)
                        showAddTaskDialog = true
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandColors.OnSurfaceDark,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("tasks_add_btn")
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Task", style = MaterialTheme.typography.titleSmall)
                }

                Surface(
                    onClick = {
                        ChimePlayer.play(ChimeType.TIBETAN)
                        onOpenVoiceStudio()
                    },
                    shape = CircleShape,
                    color = BrandColors.ButterYellow,
                    modifier = Modifier.size(52.dp).testTag("tasks_voice_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice to Task",
                            tint = BrandColors.ButterYellowText,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            categories = state.categories,
            oledMode = oledMode,
            onDismiss = { showAddTaskDialog = false },
            onSave = { title, catId, prio ->
                onAddTask(title, catId, prio)
                showAddTaskDialog = false
            }
        )
    }

    if (showCategoryModal) {
        CategoryManagerDialog(
            categories = state.categories,
            oledMode = oledMode,
            onDismiss = { showCategoryModal = false },
            onAdd = onAddCategory,
            onUpdate = onUpdateCategory,
            onDelete = onDeleteCategory
        )
    }
}

@Composable
private fun FilterPill(
    label: String,
    count: Int,
    dotColor: Color?,
    selected: Boolean,
    oledMode: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = when {
            selected -> BrandColors.OnSurfaceDark
            oledMode -> BrandColors.OledCard
            else -> BrandColors.SurfaceContainerHigh
        },
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (dotColor != null) {
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(dotColor))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) Color.White else if (oledMode) Color(0xFFD4D4D8) else BrandColors.OnSurfaceDark
            )
            Text(
                text = count.toString(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White.copy(alpha = 0.8f) else BrandColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun SortButton(label: String, selected: Boolean, oledMode: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) BrandColors.OnSurfaceDark else Color.Transparent)
            .clickable {
                ChimePlayer.play(ChimeType.CLICK)
                onClick()
            }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
        )
    }
}

@Composable
private fun PriorityBadge(priority: TaskPriority, onClick: () -> Unit) {
    val (bg, fg) = when (priority) {
        TaskPriority.HIGH -> BrandColors.HighPriorityBg to BrandColors.HighPriorityText
        TaskPriority.MEDIUM -> BrandColors.ButterYellow.copy(alpha = 0.78f) to BrandColors.ButterYellowText
        TaskPriority.LOW -> BrandColors.Matcha.copy(alpha = 0.65f) to BrandColors.MatchaDark
    }
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = bg
    ) {
        Text(
            text = priority.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TaskMetaPill(text: String, oledMode: Boolean) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (oledMode) Color(0xFF27272A) else Color.White.copy(alpha = 0.9f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (oledMode) Color.White else BrandColors.OnSurfaceDark
        )
    }
}

@Composable
private fun AddTaskDialog(
    categories: List<TaskCategory>,
    oledMode: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, TaskPriority) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCatId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "cat-work") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }

    val dialogBg = if (oledMode) BrandColors.OledCard else Color.White
    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = dialogBg,
            border = BorderStroke(1.dp, if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("New Flow or Task", style = MaterialTheme.typography.titleLarge, color = textPrimary)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textPrimary)
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. Prep dinner sourdough or review designs...") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("add_task_input")
                )

                Text("SELECT TAG / CATEGORY", style = MaterialTheme.typography.labelSmall, color = BrandColors.OnSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSel = cat.id == selectedCatId
                        Surface(
                            onClick = { selectedCatId = cat.id },
                            shape = CircleShape,
                            color = if (isSel) BrandColors.OnSurfaceDark else BrandColors.SurfaceContainerLow
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(cat.color.toThemeStyle().preview))
                                Text(
                                    text = cat.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSel) Color.White else BrandColors.OnSurfaceDark
                                )
                            }
                        }
                    }
                }

                Text("PRIORITY LEVEL", style = MaterialTheme.typography.labelSmall, color = BrandColors.OnSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(TaskPriority.LOW, TaskPriority.MEDIUM, TaskPriority.HIGH).forEach { prio ->
                        val isSel = selectedPriority == prio
                        Surface(
                            onClick = { selectedPriority = prio },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSel) BrandColors.OnSurfaceDark else BrandColors.SurfaceContainerLow,
                            modifier = Modifier.weight(1f).height(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = prio.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSel) Color.White else BrandColors.OnSurfaceDark
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSave(title, selectedCatId, selectedPriority) },
                        enabled = title.isNotBlank(),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                        modifier = Modifier.weight(1f).height(46.dp).testTag("save_task_btn")
                    ) {
                        Text("Save Task", style = MaterialTheme.typography.labelLarge)
                    }
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = CircleShape,
                        modifier = Modifier.height(46.dp)
                    ) {
                        Text("Cancel", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryManagerDialog(
    categories: List<TaskCategory>,
    oledMode: Boolean,
    onDismiss: () -> Unit,
    onAdd: (String, CategoryColor) -> Unit,
    onUpdate: (String, String, CategoryColor) -> Unit,
    onDelete: (String) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(CategoryColor.PINK) }
    var editingId by remember { mutableStateOf<String?>(null) }

    val dialogBg = if (oledMode) BrandColors.OledCard else Color.White
    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = dialogBg,
            border = BorderStroke(1.dp, if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Custom Task Tags", style = MaterialTheme.typography.titleMedium, color = textPrimary)
                        Text("Persistent pastel color badges", style = MaterialTheme.typography.bodySmall, color = BrandColors.OnSurfaceVariant)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textPrimary)
                    }
                }

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    placeholder = { Text("Tag name (e.g. Deep Work, Client)...") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CategoryColor.entries.forEach { color ->
                        val isSel = color == selectedColor
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color.toThemeStyle().preview)
                                .border(
                                    width = if (isSel) 2.5.dp else 1.dp,
                                    color = if (isSel) textPrimary else Color.Black.copy(alpha = 0.1f),
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSel) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = BrandColors.OnSurfaceDark, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (editingId != null) {
                            onUpdate(editingId!!, nameInput, selectedColor)
                            editingId = null
                        } else {
                            onAdd(nameInput, selectedColor)
                        }
                        nameInput = ""
                    },
                    enabled = nameInput.isNotBlank(),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text(if (editingId != null) "Save Tag Changes" else "+ Add Custom Tag", style = MaterialTheme.typography.labelMedium)
                }

                Column(
                    modifier = Modifier.heightIn(max = 190.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(6).forEach { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (oledMode) BrandColors.OledBlack else BrandColors.SurfaceContainerLow)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(cat.color.toThemeStyle().preview))
                                Text(cat.name, style = MaterialTheme.typography.labelMedium, color = textPrimary)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        editingId = cat.id
                                        nameInput = cat.name
                                        selectedColor = cat.color
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit tag", tint = textPrimary, modifier = Modifier.size(15.dp))
                                }
                                IconButton(
                                    onClick = { onDelete(cat.id) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete tag", tint = BrandColors.HighPriorityText, modifier = Modifier.size(15.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
