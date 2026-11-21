package com.onereflection.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.onereflection.app.model.TaskItem
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.LocalElementalPalette
import com.onereflection.app.ui.theme.MonoFontFamily
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private enum class LeafShapeKind {
    LANCE,
    BROAD_CANOPY,
    FERN_SPRAY,
    OVAL
}

private data class BranchNodeSpec(
    val id: String,
    val label: String,
    val subtitle: String,
    val t: Float,                // position along main bezier branch (0.16..0.88)
    val isUpper: Boolean,
    val isCompletedLeaf: Boolean, // true = veined green leaf, false = rose/terracotta bud (started/active)
    val leafKind: LeafShapeKind,
    val stepIndex: Int,
    val categoryName: String
)

/**
 * Interactive Botanical Herbarium Growing-Branch Visualization for Task Completion Journey.
 *
 * Shows which tasks were started (terracotta buds) and which tasks were completed in chronological
 * order (veined botanical leaves sprouting along a tapered woody-to-green Bezier vine with a curly
 * tendril tip). Tapping any category bud sprouts & zooms into a sub-branch (FIG. 1 -> FIG. 2),
 * exactly matching the organic herbarium animation reference.
 */
@Composable
fun BotanicalTaskJourneyCard(
    activeTasks: List<TaskItem>,
    completedTasks: List<TaskItem>,
    oledMode: Boolean,
    modifier: Modifier = Modifier
) {
    var focusedCategory by remember { mutableStateOf<String?>(null) }
    var selectedNodeId by remember { mutableStateOf<String?>(null) }
    var replaySeed by remember { mutableIntStateOf(0) }
    var fullscreenOpen by remember { mutableStateOf(false) }

    val palette = LocalElementalPalette.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("botanical_task_journey_card"),
        shape = RoundedCornerShape(28.dp),
        color = if (oledMode) Color(0xFF101512) else Color(0xFFF7F4EC),
        border = BorderStroke(
            1.dp,
            if (oledMode) Color(0xFF2B3A30) else Color(0xFFE0D8C8)
        ),
        shadowElevation = if (oledMode) 0.dp else 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            BotanicalBranchStage(
                activeTasks = activeTasks,
                completedTasks = completedTasks,
                focusedCategory = focusedCategory,
                selectedNodeId = selectedNodeId,
                replaySeed = replaySeed,
                oledMode = oledMode,
                stageHeight = 236.dp,
                onSelectNode = { node ->
                    selectedNodeId = node.id
                    if (!node.isCompletedLeaf && focusedCategory == null) {
                        focusedCategory = node.categoryName
                        replaySeed += 1
                    }
                },
                onBackToRoot = {
                    focusedCategory = null
                    selectedNodeId = null
                    replaySeed += 1
                },
                onReplay = { replaySeed += 1 },
                onExpandFullscreen = { fullscreenOpen = true }
            )

            // Selected Node Botanical Footnote Strip
            val allNodes = remember(activeTasks, completedTasks, focusedCategory) {
                buildBranchNodes(activeTasks, completedTasks, focusedCategory)
            }
            val selectedNode = allNodes.find { it.id == selectedNodeId }
                ?: allNodes.lastOrNull { it.isCompletedLeaf }
                ?: allNodes.firstOrNull()

            AnimatedVisibility(visible = selectedNode != null) {
                if (selectedNode != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (oledMode) Color(0xFF161E19) else Color(0xFFEFECE2)
                            )
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (selectedNode.isCompletedLeaf) {
                                    "COMPLETED #${selectedNode.stepIndex}  ·  ${selectedNode.categoryName.uppercase()}"
                                } else {
                                    "STARTED BUD  ·  TAP TO SPROUT ${selectedNode.categoryName.uppercase()}"
                                },
                                fontFamily = MonoFontFamily,
                                fontSize = 9.sp,
                                letterSpacing = 0.8.sp,
                                color = if (selectedNode.isCompletedLeaf) {
                                    if (oledMode) Color(0xFF8CD97A) else Color(0xFF477836)
                                } else {
                                    if (oledMode) Color(0xFFE89A8A) else Color(0xFFA85447)
                                }
                            )
                            Text(
                                text = "${selectedNode.label} — ${selectedNode.subtitle}",
                                fontFamily = BodyFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (oledMode) Color(0xFFE7EDE8) else Color(0xFF2B2D28)
                            )
                        }
                        if (focusedCategory != null) {
                            Surface(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        focusedCategory = null
                                        selectedNodeId = null
                                        replaySeed += 1
                                    },
                                shape = CircleShape,
                                color = palette.surfaceTint
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to root", tint = palette.primary, modifier = Modifier.size(12.dp))
                                    Text("FIG. 1", fontFamily = MonoFontFamily, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = palette.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (fullscreenOpen) {
        Dialog(
            onDismissRequest = { fullscreenOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                shape = RoundedCornerShape(30.dp),
                color = if (oledMode) Color(0xFF0E1310) else Color(0xFFF7F4EC),
                border = BorderStroke(1.dp, if (oledMode) Color(0xFF2E3F34) else Color(0xFFDCD3C2))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BOTANICAL COMPLETION HERBARIUM",
                            fontFamily = MonoFontFamily,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = if (oledMode) Color(0xFF9FB4A5) else Color(0xFF7B766B)
                        )
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("botanical_fullscreen_close")
                                .clip(CircleShape)
                                .background(if (oledMode) Color(0xFF1D2721) else Color(0xFFEBE6D9))
                                .clickable { fullscreenOpen = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Close full plate", tint = if (oledMode) Color.White else Color(0xFF2A2B26), modifier = Modifier.size(17.dp))
                        }
                    }

                    BotanicalBranchStage(
                        activeTasks = activeTasks,
                        completedTasks = completedTasks,
                        focusedCategory = focusedCategory,
                        selectedNodeId = selectedNodeId,
                        replaySeed = replaySeed,
                        oledMode = oledMode,
                        stageHeight = 440.dp,
                        onSelectNode = { node ->
                            selectedNodeId = node.id
                            if (!node.isCompletedLeaf && focusedCategory == null) {
                                focusedCategory = node.categoryName
                                replaySeed += 1
                            }
                        },
                        onBackToRoot = {
                            focusedCategory = null
                            selectedNodeId = null
                            replaySeed += 1
                        },
                        onReplay = { replaySeed += 1 },
                        onExpandFullscreen = null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BotanicalBranchStage(
    activeTasks: List<TaskItem>,
    completedTasks: List<TaskItem>,
    focusedCategory: String?,
    selectedNodeId: String?,
    replaySeed: Int,
    oledMode: Boolean,
    stageHeight: Dp,
    onSelectNode: (BranchNodeSpec) -> Unit,
    onBackToRoot: () -> Unit,
    onReplay: () -> Unit,
    onExpandFullscreen: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val nodes = remember(activeTasks, completedTasks, focusedCategory) {
        buildBranchNodes(activeTasks, completedTasks, focusedCategory)
    }

    // Organic sequential growth animation: retriggers smoothly whenever a new task completes,
    // a category bud sprouts, or the user taps Replay!
    val growthProgress = remember { Animatable(0f) }
    LaunchedEffect(completedTasks.size, focusedCategory, replaySeed) {
        growthProgress.snapTo(0f)
        growthProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1350, easing = FastOutSlowInEasing)
        )
    }

    // Gentle living botanical breeze sway on leaves & tendril
    val wind = rememberInfiniteTransition(label = "botanical_wind")
    val windPhase by wind.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wind_phase"
    )

    val textMeasurer = rememberTextMeasurer()
    var hitTargets by remember { mutableStateOf<List<Pair<BranchNodeSpec, Offset>>>(emptyList()) }

    val figCode = if (focusedCategory == null) "FIG. 1" else "FIG. 2"
    val figTitle = focusedCategory ?: "Flow Garden"
    val paperTop = if (oledMode) Color(0xFF131915) else Color(0xFFFAF8F2)
    val paperBottom = if (oledMode) Color(0xFF0D120F) else Color(0xFFF1ECE1)
    val inkColor = if (oledMode) Color(0xFFE6ECE7) else Color(0xFF31332C)
    val mutedInk = if (oledMode) Color(0xFF8EA194) else Color(0xFF7D796E)
    val barkColor = if (oledMode) Color(0xFF8D7B64) else Color(0xFF5A4E3C)
    val shootColor = if (oledMode) Color(0xFF79C46A) else Color(0xFF5D8749)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(stageHeight)
            .background(Brush.radialGradient(colors = listOf(paperTop, paperBottom)))
    ) {
        // Botanical Plate Header: "FIG. 1  Flow Garden" (exact herbarium typography!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(enabled = focusedCategory != null) { onBackToRoot() }
            ) {
                Text(
                    text = figCode,
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = mutedInk,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = figTitle,
                    fontFamily = DisplayFontFamily,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    fontSize = 19.sp,
                    color = inkColor
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("botanical_replay_button")
                        .clip(CircleShape)
                        .background(if (oledMode) Color(0xFF1D2721) else Color(0xFFEBE5D8))
                        .clickable { onReplay() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Replay,
                        contentDescription = "Replay branch growth",
                        tint = inkColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
                if (onExpandFullscreen != null) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("botanical_expand_button")
                            .clip(CircleShape)
                            .background(if (oledMode) Color(0xFF1D2721) else Color(0xFFEBE5D8))
                            .clickable { onExpandFullscreen() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.OpenInFull,
                            contentDescription = "Expand botanical plate",
                            tint = inkColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Interactive Botanical Growing-Vine Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(nodes) {
                    detectTapGestures { tapOffset ->
                        val nearest = hitTargets.minByOrNull { (_, pos) ->
                            hypot(pos.x - tapOffset.x, pos.y - tapOffset.y)
                        }
                        if (nearest != null) {
                            val dist = hypot(nearest.second.x - tapOffset.x, nearest.second.y - tapOffset.y)
                            if (dist <= 44.dp.toPx()) {
                                onSelectNode(nearest.first)
                            }
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val g = growthProgress.value

            // Graceful organic Bezier curve across the plate (matching FIG. 1 / FIG. 3 graceful arc!)
            val p0 = Offset(w * 0.08f, if (focusedCategory == null) h * 0.67f else h * 0.80f)
            val p1 = Offset(w * 0.34f, if (focusedCategory == null) h * 0.56f else h * 0.52f)
            val p2 = Offset(w * 0.66f, if (focusedCategory == null) h * 0.52f else h * 0.42f)
            val p3 = Offset(w * 0.88f, if (focusedCategory == null) h * 0.50f else h * 0.30f)

            // 1. Draw Organic Tapered Branch from Root (thicker woody bark) -> Tip (delicate green shoot)
            val segments = 46
            val maxSeg = (segments * g).toInt().coerceAtLeast(1)
            for (i in 0 until maxSeg) {
                val t0 = i.toFloat() / segments.toFloat()
                val t1 = ((i + 1).toFloat() / segments.toFloat()).coerceAtMost(g)
                val a = cubicBezierPoint(p0, p1, p2, p3, t0)
                val b = cubicBezierPoint(p0, p1, p2, p3, t1)
                val strokeW = (4.6f * (1f - t0 * 0.78f)).dp.toPx()
                val segColor = androidx.compose.ui.graphics.lerp(barkColor, shootColor, (t0 * 1.15f).coerceIn(0f, 1f))
                drawLine(
                    color = segColor,
                    start = a,
                    end = b,
                    strokeWidth = strokeW,
                    cap = StrokeCap.Round
                )
            }

            // 2. Curling Vine Tendril at the Growing Tip (exact curly spiral from the video!)
            if (g > 0.86f) {
                val tendrilReveal = ((g - 0.86f) / 0.14f).coerceIn(0f, 1f)
                val tip = cubicBezierPoint(p0, p1, p2, p3, 1f)
                val sway = sin(windPhase) * 2.5.dp.toPx()
                val tendrilPath = Path().apply {
                    moveTo(tip.x, tip.y)
                    cubicTo(
                        tip.x + 14.dp.toPx() * tendrilReveal,
                        tip.y - 4.dp.toPx() * tendrilReveal + sway,
                        tip.x + 18.dp.toPx() * tendrilReveal,
                        tip.y - 18.dp.toPx() * tendrilReveal + sway,
                        tip.x + 10.dp.toPx() * tendrilReveal,
                        tip.y - 20.dp.toPx() * tendrilReveal + sway
                    )
                    cubicTo(
                        tip.x + 4.dp.toPx() * tendrilReveal,
                        tip.y - 20.dp.toPx() * tendrilReveal + sway,
                        tip.x + 5.dp.toPx() * tendrilReveal,
                        tip.y - 13.dp.toPx() * tendrilReveal + sway,
                        tip.x + 10.dp.toPx() * tendrilReveal,
                        tip.y - 13.dp.toPx() * tendrilReveal + sway
                    )
                }
                drawPath(
                    path = tendrilPath,
                    color = shootColor.copy(alpha = 0.85f * tendrilReveal),
                    style = Stroke(width = 1.1.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // 3. Delicate micro-sprout accent leaves along the vine (just like the tiny shoots in the video)
            val microPositions = listOf(0.26f to true, 0.48f to false, 0.68f to true, 0.82f to false)
            microPositions.forEachIndexed { idx, (mt, up) ->
                if (g > mt + 0.05f) {
                    val mBloom = ((g - mt - 0.05f) / 0.12f).coerceIn(0f, 1f)
                    val pt = cubicBezierPoint(p0, p1, p2, p3, mt)
                    val swayDeg = sin(windPhase + idx * 1.4f) * 3.5f
                    drawBotanicalLeaf(
                        origin = pt,
                        angleDeg = (if (up) -52f else 54f) + swayDeg,
                        lengthPx = 11.dp.toPx() * mBloom,
                        widthPx = 4.5.dp.toPx() * mBloom,
                        fillColor = if (oledMode) Color(0xFF72B862) else Color(0xFF9AC27D),
                        veinColor = if (oledMode) Color(0xFF426B3A) else Color(0xFF5D8749),
                        alpha = 0.78f * mBloom
                    )
                }
            }

            // 4. Task Nodes (Chronological Completed Veined Leaves + Active/Started Terracotta Buds!)
            val computedHits = mutableListOf<Pair<BranchNodeSpec, Offset>>()
            nodes.forEachIndexed { idx, node ->
                val nodeBloom = ((g - node.t * 0.82f) / 0.22f).coerceIn(0f, 1f)
                if (nodeBloom <= 0.01f) return@forEachIndexed

                val anchor = cubicBezierPoint(p0, p1, p2, p3, node.t)
                val stemLen = (if (node.isCompletedLeaf) 30.dp else 24.dp).toPx() * nodeBloom
                val swayDeg = sin(windPhase + idx * 1.1f) * 2.8f
                val stemAngleDeg = (if (node.isUpper) -56f else 52f) + (if (idx % 2 == 0) 6f else -5f) + swayDeg
                val stemAngleRad = stemAngleDeg * PI / 180.0
                val tip = Offset(
                    x = anchor.x + (cos(stemAngleRad) * stemLen).toFloat(),
                    y = anchor.y + (sin(stemAngleRad) * stemLen).toFloat()
                )
                val ctrl = Offset(
                    x = (anchor.x + tip.x) / 2f - (if (node.isUpper) 4.dp.toPx() else -4.dp.toPx()),
                    y = (anchor.y + tip.y) / 2f
                )

                // Graceful curved petiole/stem
                val stemPath = Path().apply {
                    moveTo(anchor.x, anchor.y)
                    quadraticTo(ctrl.x, ctrl.y, tip.x, tip.y)
                }
                drawPath(
                    path = stemPath,
                    color = shootColor.copy(alpha = 0.9f * nodeBloom),
                    style = Stroke(width = 1.4.dp.toPx(), cap = StrokeCap.Round)
                )

                val isSelected = selectedNodeId == node.id

                if (node.isCompletedLeaf) {
                    val leafFill = when (node.leafKind) {
                        LeafShapeKind.BROAD_CANOPY -> if (oledMode) Color(0xFF8BE07B) else Color(0xFF8CC46A)
                        LeafShapeKind.FERN_SPRAY -> if (oledMode) Color(0xFF66BA7E) else Color(0xFF5D9668)
                        else -> if (oledMode) Color(0xFF72C963) else Color(0xFF689E52)
                    }
                    val leafVein = if (oledMode) Color(0xFF284D24) else Color(0xFF3E6930)
                    val leafLen = (if (node.leafKind == LeafShapeKind.BROAD_CANOPY) 26.dp else 22.dp).toPx() * nodeBloom
                    val leafWid = (if (node.leafKind == LeafShapeKind.BROAD_CANOPY) 17.dp else 10.dp).toPx() * nodeBloom

                    if (isSelected) {
                        drawCircle(
                            color = leafFill.copy(alpha = 0.28f),
                            radius = 19.dp.toPx(),
                            center = tip
                        )
                    }
                    drawBotanicalLeaf(
                        origin = tip,
                        angleDeg = stemAngleDeg,
                        lengthPx = leafLen,
                        widthPx = leafWid,
                        fillColor = leafFill,
                        veinColor = leafVein,
                        alpha = nodeBloom
                    )
                } else {
                    // Started / Active Task Category Rose-Terracotta Bud (exact bud style from FIG. 1!)
                    val budFill = if (oledMode) Color(0xFFD98779) else Color(0xFFB86C5E)
                    val budHighlight = if (oledMode) Color(0xFFF2B6AA) else Color(0xFFD99588)
                    if (isSelected) {
                        drawCircle(
                            color = budFill.copy(alpha = 0.30f),
                            radius = 15.dp.toPx(),
                            center = tip
                        )
                    }
                    rotate(degrees = stemAngleDeg, pivot = tip) {
                        drawOval(
                            brush = Brush.linearGradient(listOf(budHighlight, budFill)),
                            topLeft = Offset(tip.x - 2.dp.toPx(), tip.y - 5.dp.toPx() * nodeBloom),
                            size = Size(13.dp.toPx() * nodeBloom, 10.dp.toPx() * nodeBloom)
                        )
                        // Tiny green sepal calyx at bud base
                        drawLine(
                            color = shootColor,
                            start = Offset(tip.x - 1.dp.toPx(), tip.y),
                            end = Offset(tip.x + 4.dp.toPx(), tip.y - 3.dp.toPx()),
                            strokeWidth = 1.2.dp.toPx()
                        )
                        drawLine(
                            color = shootColor,
                            start = Offset(tip.x - 1.dp.toPx(), tip.y),
                            end = Offset(tip.x + 4.dp.toPx(), tip.y + 3.dp.toPx()),
                            strokeWidth = 1.2.dp.toPx()
                        )
                    }
                }

                // Crisp Herbarium Label beside each leaf/bud (truncated cleanly so zero overlap!)
                val shortLabel = if (node.label.length > 16) node.label.take(15).trim() + "…" else node.label
                val prefix = if (node.isCompletedLeaf) "#${node.stepIndex} " else ""
                val textLayout = textMeasurer.measure(
                    text = "$prefix$shortLabel",
                    style = TextStyle(
                        fontFamily = BodyFontFamily,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = inkColor.copy(alpha = (if (isSelected) 1f else 0.84f) * nodeBloom)
                    )
                )

                val labelX = (tip.x + (if (node.t > 0.72f) -textLayout.size.width * 0.65f else 6.dp.toPx()))
                    .coerceIn(10.dp.toPx(), (w - textLayout.size.width - 10.dp.toPx()).coerceAtLeast(10.dp.toPx()))
                val labelY = if (node.isUpper) {
                    (tip.y - textLayout.size.height - 10.dp.toPx()).coerceAtLeast(38.dp.toPx())
                } else {
                    (tip.y + 10.dp.toPx()).coerceAtMost(h - textLayout.size.height - 8.dp.toPx())
                }

                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(labelX, labelY)
                )

                computedHits.add(node to tip)
            }
            hitTargets = computedHits
        }
    }
}

private fun DrawScope.drawBotanicalLeaf(
    origin: Offset,
    angleDeg: Float,
    lengthPx: Float,
    widthPx: Float,
    fillColor: Color,
    veinColor: Color,
    alpha: Float
) {
    if (lengthPx <= 1f || widthPx <= 1f) return
    rotate(degrees = angleDeg, pivot = origin) {
        val leafPath = Path().apply {
            moveTo(origin.x, origin.y)
            cubicTo(
                origin.x + lengthPx * 0.30f, origin.y - widthPx * 0.65f,
                origin.x + lengthPx * 0.75f, origin.y - widthPx * 0.48f,
                origin.x + lengthPx, origin.y
            )
            cubicTo(
                origin.x + lengthPx * 0.75f, origin.y + widthPx * 0.48f,
                origin.x + lengthPx * 0.30f, origin.y + widthPx * 0.65f,
                origin.x, origin.y
            )
            close()
        }
        drawPath(path = leafPath, color = fillColor.copy(alpha = 0.92f * alpha))
        drawPath(path = leafPath, color = veinColor.copy(alpha = 0.55f * alpha), style = Stroke(width = 0.9.dp.toPx()))

        // Central midrib vein
        drawLine(
            color = veinColor.copy(alpha = 0.68f * alpha),
            start = origin,
            end = Offset(origin.x + lengthPx * 0.88f, origin.y),
            strokeWidth = 0.9.dp.toPx(),
            cap = StrokeCap.Round
        )
        // Delicate lateral veins
        for (f in listOf(0.32f, 0.55f, 0.74f)) {
            val vx = origin.x + lengthPx * f
            val vw = widthPx * (0.36f - f * 0.14f)
            drawLine(
                color = veinColor.copy(alpha = 0.45f * alpha),
                start = Offset(vx, origin.y),
                end = Offset(vx + lengthPx * 0.12f, origin.y - vw),
                strokeWidth = 0.65.dp.toPx()
            )
            drawLine(
                color = veinColor.copy(alpha = 0.45f * alpha),
                start = Offset(vx, origin.y),
                end = Offset(vx + lengthPx * 0.12f, origin.y + vw),
                strokeWidth = 0.65.dp.toPx()
            )
        }
    }
}

private fun cubicBezierPoint(
    p0: Offset,
    p1: Offset,
    p2: Offset,
    p3: Offset,
    t: Float
): Offset {
    val u = 1f - t
    val tt = t * t
    val uu = u * u
    val uuu = uu * u
    val ttt = tt * t
    return Offset(
        x = uuu * p0.x + 3f * uu * t * p1.x + 3f * u * tt * p2.x + ttt * p3.x,
        y = uuu * p0.y + 3f * uu * t * p1.y + 3f * u * tt * p2.y + ttt * p3.y
    )
}

private fun buildBranchNodes(
    activeTasks: List<TaskItem>,
    completedTasks: List<TaskItem>,
    focusedCategory: String?
): List<BranchNodeSpec> {
    val filteredDone = if (focusedCategory == null) completedTasks else completedTasks.filter { it.category == focusedCategory }
    val filteredActive = if (focusedCategory == null) activeTasks else activeTasks.filter { it.category == focusedCategory }

    // Combine chronological completed leaves (first started/completed -> latest completed) + up to 2 active buds
    val combined = mutableListOf<Pair<TaskItem, Boolean>>()
    filteredDone.takeLast(4).forEach { combined.add(it to true) }
    filteredActive.take(2).forEach { combined.add(it to false) }

    if (combined.isEmpty()) return emptyList()

    val count = combined.size
    val leafKinds = listOf(
        LeafShapeKind.LANCE,
        LeafShapeKind.OVAL,
        LeafShapeKind.FERN_SPRAY,
        LeafShapeKind.BROAD_CANOPY
    )
    var doneCounter = 0
    return combined.mapIndexed { idx, (task, isDone) ->
        if (isDone) doneCounter += 1
        val t = if (count == 1) 0.52f else 0.18f + (idx.toFloat() / (count - 1).toFloat()) * 0.68f
        BranchNodeSpec(
            id = task.id,
            label = task.title,
            subtitle = if (isDone) "Completed at ${task.dueTime}" else "In progress · Due ${task.dueTime}",
            t = t,
            isUpper = idx % 2 == 0,
            isCompletedLeaf = isDone,
            leafKind = leafKinds[idx % leafKinds.size],
            stepIndex = if (isDone) doneCounter else idx + 1,
            categoryName = task.category
        )
    }
}
