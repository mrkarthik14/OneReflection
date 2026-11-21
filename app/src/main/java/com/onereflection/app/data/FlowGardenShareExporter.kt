package com.onereflection.app.data

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.onereflection.app.R
import com.onereflection.app.domain.FlowGardenGrowthState
import com.onereflection.app.domain.PlantGrowthStage
import com.onereflection.app.model.ElementalTheme
import com.onereflection.app.ui.theme.ElementalThemePalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class FlowGardenShareSnapshot(
    val growth: Float,
    val stage: PlantGrowthStage,
    val growthState: FlowGardenGrowthState,
    val theme: ElementalTheme,
    val palette: ElementalThemePalette,
    val periodLabel: String,
    val periodSubtitle: String,
    val isMonthly: Boolean,
    val taskCount: Int,
    val gardenCount: Int,
    val circleCount: Int
)

/**
 * Renders a high-resolution 1080x1350 branded PNG card of the user's current Flow Garden tree
 * growth stage harmonized with their active [ElementalThemePalette], writes it into app cache,
 * and launches the native Android Sharesheet via [FileProvider] with zero storage permissions.
 */
object FlowGardenShareExporter {

    private const val EXPORT_WIDTH = 1080
    private const val EXPORT_HEIGHT = 1350
    private const val CACHE_SUBDIR = "shared_garden"

    suspend fun exportAndShare(
        context: Context,
        snapshot: FlowGardenShareSnapshot,
        chooserTitle: String
    ): Result<Uri> = withContext(Dispatchers.IO) {
        runCatching {
            val bitmap = renderSharePosterBitmap(context, snapshot)
            val cacheFolder = File(context.cacheDir, CACHE_SUBDIR).apply { mkdirs() }

            // Clean up older temporary exports to keep cache lightweight
            cacheFolder.listFiles()?.sortedByDescending { it.lastModified() }?.drop(4)?.forEach { old ->
                runCatching { old.delete() }
            }

            val safeStage = snapshot.stage.name.lowercase()
            val outFile = File(cacheFolder, "flow_garden_${safeStage}_${System.currentTimeMillis()}.png")
            FileOutputStream(outFile).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 96, stream)
                stream.flush()
            }
            bitmap.recycle()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                outFile
            )

            val vitalityPct = (snapshot.growth.coerceIn(0f, 1f) * 100f).roundToInt()
            val caption = buildString {
                append("My Flow Garden reached ${snapshot.stage.displayName} ($vitalityPct% vitality) ")
                append("in ${snapshot.theme.displayName} · ${snapshot.periodLabel}!\n")
                append("Daily Habits: ${snapshot.growthState.todayCompletionPercent}% · ")
                append("7-Day Rhythm: ${snapshot.growthState.weeklyConsistencyPercent}% · ")
                append("${snapshot.taskCount + snapshot.gardenCount + snapshot.circleCount} mindful harvests in OneReflection.")
            }

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "Flow Garden · ${snapshot.stage.displayName}")
                putExtra(Intent.EXTRA_TITLE, "Flow Garden · ${snapshot.stage.displayName} ($vitalityPct%)")
                putExtra(Intent.EXTRA_TEXT, caption)
                clipData = ClipData.newRawUri("Flow Garden Tree", contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(sendIntent, chooserTitle).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            withContext(Dispatchers.Main) {
                context.startActivity(chooser)
            }

            contentUri
        }
    }

    private fun renderSharePosterBitmap(
        context: Context,
        snapshot: FlowGardenShareSnapshot
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(EXPORT_WIDTH, EXPORT_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val w = EXPORT_WIDTH.toFloat()
        val h = EXPORT_HEIGHT.toFloat()
        val palette = snapshot.palette
        val growth = snapshot.growth.coerceIn(0.06f, 1f)

        val displayTypeface = runCatching {
            ResourcesCompat.getFont(context, R.font.fraunces)
        }.getOrNull() ?: Typeface.SERIF

        val bodyTypeface = runCatching {
            ResourcesCompat.getFont(context, R.font.dm_sans)
        }.getOrNull() ?: Typeface.SANS_SERIF

        val monoTypeface = runCatching {
            ResourcesCompat.getFont(context, R.font.jetbrains_mono)
        }.getOrNull() ?: Typeface.MONOSPACE

        // 1. Full-Bleed Elemental Background Gradient
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, 0f, h,
                intArrayOf(
                    palette.heroStart.toArgb(),
                    palette.heroEnd.toArgb(),
                    lerp(palette.heroEnd, Color(0xFF060608), 0.35f).toArgb()
                ),
                floatArrayOf(0f, 0.64f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // Subtle outer botanical frame border
        val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = palette.primaryBright.copy(alpha = 0.28f).toArgb()
        }
        canvas.drawRoundRect(RectF(36f, 36f, w - 36f, h - 36f), 54f, 54f, framePaint)

        // 2. Top Editorial Header
        val eyebrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(monoTypeface, Typeface.BOLD)
            textSize = 24f
            letterSpacing = 0.14f
            color = palette.primaryBright.toArgb()
        }
        canvas.drawText(
            "ONEREFLECTION  ·  ${snapshot.theme.displayName.uppercase()}",
            76f,
            106f,
            eyebrowPaint
        )

        val periodChipText = snapshot.periodLabel.uppercase()
        val chipTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(monoTypeface, Typeface.BOLD)
            textSize = 20f
            color = palette.onPrimary.toArgb()
        }
        val chipW = chipTextPaint.measureText(periodChipText) + 48f
        val chipRect = RectF(w - 76f - chipW, 74f, w - 76f, 122f)
        val chipBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = palette.primary.toArgb()
        }
        canvas.drawRoundRect(chipRect, 24f, 24f, chipBgPaint)
        canvas.drawText(periodChipText, chipRect.left + 24f, chipRect.top + 31f, chipTextPaint)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(displayTypeface, Typeface.BOLD)
            textSize = 62f
            color = android.graphics.Color.WHITE
        }
        canvas.drawText(snapshot.stage.displayName, 76f, 186f, titlePaint)

        val vitalityPct = (growth * 100f).roundToInt()
        val subtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = bodyTypeface
            textSize = 28f
            color = Color(0xFFE6DFD9).toArgb()
        }
        canvas.drawText(
            "${snapshot.stage.badgeLabel}  ·  $vitalityPct% Vitality  ·  ${snapshot.periodSubtitle}",
            76f,
            232f,
            subtitlePaint
        )

        // 3. Framed Sanctuary Living Tree Canvas
        val treeRect = RectF(64f, 272f, w - 64f, 996f)
        canvas.save()
        val clipPath = Path().apply {
            addRoundRect(treeRect, 48f, 48f, Path.Direction.CW)
        }
        canvas.clipPath(clipPath)
        drawTreeScene(
            canvas = canvas,
            bounds = treeRect,
            growth = growth,
            stage = snapshot.stage,
            isMonthly = snapshot.isMonthly,
            palette = palette,
            taskCount = snapshot.taskCount,
            gardenCount = snapshot.gardenCount,
            circleCount = snapshot.circleCount
        )
        canvas.restore()

        val treeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = palette.primaryBright.copy(alpha = 0.42f).toArgb()
        }
        canvas.drawRoundRect(treeRect, 48f, 48f, treeBorderPaint)

        // 4. Stage Progression Bar (Seedling -> Sprout -> Mature Tree)
        val stageTop = 1028f
        val stageGap = 16f
        val stageW = (w - 128f - stageGap * 2f) / 3f
        PlantGrowthStage.entries.forEachIndexed { idx, stageOption ->
            val sx = 64f + idx * (stageW + stageGap)
            val rect = RectF(sx, stageTop, sx + stageW, stageTop + 76f)
            val isCurrent = stageOption == snapshot.stage
            val isReached = snapshot.stage.ordinal >= stageOption.ordinal
            val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = when {
                    isCurrent -> palette.primary.toArgb()
                    isReached -> palette.primary.copy(alpha = 0.25f).toArgb()
                    else -> Color.White.copy(alpha = 0.08f).toArgb()
                }
            }
            canvas.drawRoundRect(rect, 22f, 22f, pillPaint)

            val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.create(bodyTypeface, Typeface.BOLD)
                textSize = 25f
                textAlign = Paint.Align.CENTER
                color = if (isCurrent) palette.onPrimary.toArgb() else android.graphics.Color.WHITE
            }
            canvas.drawText(stageOption.displayName, rect.centerX(), rect.top + 35f, labelPaint)

            val rangeText = when (stageOption) {
                PlantGrowthStage.SEEDLING -> "0–34%"
                PlantGrowthStage.SPROUT -> "35–71%"
                PlantGrowthStage.MATURE_TREE -> "72–100%"
            }
            val rangePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = monoTypeface
                textSize = 18f
                textAlign = Paint.Align.CENTER
                color = if (isCurrent) {
                    palette.onPrimary.copy(alpha = 0.85f).toArgb()
                } else {
                    Color(0xFFC4BBB4).toArgb()
                }
            }
            canvas.drawText(rangeText, rect.centerX(), rect.top + 61f, rangePaint)
        }

        // 5. Bottom Harvest & Consistency Metrics Row
        val metricsTop = 1126f
        val metricGap = 16f
        val metricW = (w - 128f - metricGap * 2f) / 3f
        val metrics = listOf(
            Triple("${snapshot.growthState.todayCompletionPercent}%", "DAILY HABITS", palette.primaryBright),
            Triple("${snapshot.growthState.weeklyConsistencyPercent}%", "7-DAY RHYTHM", palette.secondary),
            Triple(
                "${snapshot.taskCount + snapshot.gardenCount + snapshot.circleCount}",
                "HARVEST NODES",
                Color(0xFFFFE599)
            )
        )
        metrics.forEachIndexed { idx, (value, label, accent) ->
            val mx = 64f + idx * (metricW + metricGap)
            val rect = RectF(mx, metricsTop, mx + metricW, metricsTop + 116f)
            val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.White.copy(alpha = 0.08f).toArgb()
            }
            canvas.drawRoundRect(rect, 26f, 26f, cardPaint)
            val cardStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2f
                color = accent.copy(alpha = 0.35f).toArgb()
            }
            canvas.drawRoundRect(rect, 26f, 26f, cardStroke)

            val valPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = Typeface.create(monoTypeface, Typeface.BOLD)
                textSize = 40f
                textAlign = Paint.Align.CENTER
                color = accent.toArgb()
            }
            canvas.drawText(value, rect.centerX(), rect.top + 58f, valPaint)

            val lblPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                typeface = monoTypeface
                textSize = 18f
                letterSpacing = 0.08f
                textAlign = Paint.Align.CENTER
                color = Color(0xFFD6CFC8).toArgb()
            }
            canvas.drawText(label, rect.centerX(), rect.top + 92f, lblPaint)
        }

        // 6. Subtle Editorial Footer
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = monoTypeface
            textSize = 20f
            letterSpacing = 0.1f
            textAlign = Paint.Align.CENTER
            color = Color(0xFFB5ACA4).toArgb()
        }
        canvas.drawText(
            "GROWN MINDFULLY IN ONEREFLECTION  ·  ${snapshot.theme.soundscapeTitle.uppercase()}",
            w * 0.5f,
            1294f,
            footerPaint
        )

        return bitmap
    }

    private fun drawTreeScene(
        canvas: Canvas,
        bounds: RectF,
        growth: Float,
        stage: PlantGrowthStage,
        isMonthly: Boolean,
        palette: ElementalThemePalette,
        taskCount: Int,
        gardenCount: Int,
        circleCount: Int
    ) {
        val left = bounds.left
        val top = bounds.top
        val w = bounds.width()
        val h = bounds.height()
        val scale = w / 340f
        val groundY = top + h * 0.87f
        val baseX = left + w * 0.5f

        // Sky / Atmosphere Gradient
        val skyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, top, 0f, top + h,
                intArrayOf(
                    palette.heroStart.copy(alpha = 0.94f).toArgb(),
                    palette.heroEnd.copy(alpha = 0.96f).toArgb(),
                    palette.pageBgBottom.copy(alpha = 0.96f).toArgb()
                ),
                floatArrayOf(0f, 0.62f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(bounds, skyPaint)

        // Radiant Canopy Halo
        val haloCenterY = top + h * (0.64f - growth * 0.28f)
        val haloRadius = (w * (0.28f + growth * 0.34f)).coerceAtLeast(10f)
        val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                baseX, haloCenterY, haloRadius,
                intArrayOf(
                    palette.glowCore.copy(alpha = 0.48f * growth.coerceAtLeast(0.28f)).toArgb(),
                    palette.glowOuter.copy(alpha = 0.18f).toArgb(),
                    android.graphics.Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.58f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawCircle(baseX, haloCenterY, haloRadius, haloPaint)

        // Mossy Sanctuary Hill
        val hillPath = Path().apply {
            moveTo(left, top + h)
            lineTo(left, groundY + 14f * scale)
            quadTo(baseX, groundY - 20f * scale, left + w, groundY + 14f * scale)
            lineTo(left + w, top + h)
            close()
        }
        val hillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, groundY - 18f * scale, 0f, top + h,
                lerp(palette.heroStart, palette.primary, 0.28f).toArgb(),
                palette.heroEnd.toArgb(),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(hillPath, hillPaint)

        // Trunk & Roots
        val trunkHeight = h * (0.14f + growth * (if (isMonthly) 0.50f else 0.44f))
        val trunkTopX = baseX + 2.5f * scale
        val trunkTopY = groundY - trunkHeight
        val baseWidth = (3.5f + growth * (if (isMonthly) 20f else 15f)) * scale
        val topWidth = (2.0f + growth * 6f) * scale

        val barkLight = lerp(Color(0xFF7B5642), palette.primaryBright, 0.22f)
        val barkDark = lerp(Color(0xFF3E271E), palette.heroStart, 0.35f)
        val sproutGreen = lerp(Color(0xFF63D496), palette.secondary, 0.35f)
        val trunkColorStart = if (growth < 0.28f) lerp(sproutGreen, barkLight, growth / 0.28f) else barkLight
        val trunkColorEnd = if (growth < 0.28f) lerp(sproutGreen.copy(alpha = 0.85f), barkDark, growth / 0.28f) else barkDark

        if (growth > 0.26f) {
            val rootProgress = ((growth - 0.26f) / 0.74f).coerceIn(0f, 1f)
            val rootSpan = (22f + rootProgress * 44f) * scale
            val rootPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = (3f + rootProgress * 5f) * scale
                strokeCap = Paint.Cap.ROUND
                color = trunkColorEnd.toArgb()
            }
            val leftRoot = Path().apply {
                moveTo(baseX - baseWidth * 0.3f, groundY - 4f * scale)
                quadTo(baseX - rootSpan * 0.5f, groundY + 4f * scale, baseX - rootSpan, groundY + 12f * scale)
            }
            val rightRoot = Path().apply {
                moveTo(baseX + baseWidth * 0.3f, groundY - 4f * scale)
                quadTo(baseX + rootSpan * 0.5f, groundY + 4f * scale, baseX + rootSpan, groundY + 12f * scale)
            }
            canvas.drawPath(leftRoot, rootPaint)
            canvas.drawPath(rightRoot, rootPaint)
        }

        val trunkPath = Path().apply {
            moveTo(baseX - baseWidth * 0.55f, groundY + 4f * scale)
            cubicTo(
                baseX - baseWidth * 0.35f, groundY - trunkHeight * 0.45f,
                trunkTopX - topWidth * 0.6f, trunkTopY + trunkHeight * 0.28f,
                trunkTopX - topWidth * 0.5f, trunkTopY
            )
            lineTo(trunkTopX + topWidth * 0.5f, trunkTopY)
            cubicTo(
                trunkTopX + topWidth * 0.6f, trunkTopY + trunkHeight * 0.28f,
                baseX + baseWidth * 0.35f, groundY - trunkHeight * 0.45f,
                baseX + baseWidth * 0.55f, groundY + 4f * scale
            )
            close()
        }
        val trunkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, trunkTopY, 0f, groundY,
                trunkColorStart.toArgb(),
                trunkColorEnd.toArgb(),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawPath(trunkPath, trunkPaint)

        // Seedling Cotyledon Leaves
        val seedlingAlpha = (1f - ((growth - 0.24f) / 0.26f).coerceIn(0f, 1f)).coerceIn(0f, 1f)
        if (seedlingAlpha > 0.02f) {
            val leafSpan = (18f + growth * 26f) * scale
            val leftLeaf = Path().apply {
                moveTo(trunkTopX, trunkTopY + 4f * scale)
                quadTo(trunkTopX - leafSpan, trunkTopY - leafSpan * 0.85f, trunkTopX - leafSpan * 1.15f, trunkTopY + 2f * scale)
                quadTo(trunkTopX - leafSpan * 0.5f, trunkTopY + 11f * scale, trunkTopX, trunkTopY + 4f * scale)
            }
            val rightLeaf = Path().apply {
                moveTo(trunkTopX, trunkTopY + 4f * scale)
                quadTo(trunkTopX + leafSpan, trunkTopY - leafSpan * 0.85f, trunkTopX + leafSpan * 1.15f, trunkTopY + 2f * scale)
                quadTo(trunkTopX + leafSpan * 0.5f, trunkTopY + 11f * scale, trunkTopX, trunkTopY + 4f * scale)
            }
            canvas.drawPath(
                leftLeaf,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = palette.primaryBright.copy(alpha = 0.94f * seedlingAlpha).toArgb() }
            )
            canvas.drawPath(
                rightLeaf,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = palette.secondary.copy(alpha = 0.94f * seedlingAlpha).toArgb() }
            )
        }

        // Branches
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

        val canopyAnchors = mutableListOf<Pair<Float, Float>>()
        branches.forEachIndexed { idx, spec ->
            if (growth > spec.unlockAt) {
                val localG = ((growth - spec.unlockAt) / (1f - spec.unlockAt)).coerceIn(0f, 1f)
                val sx = baseX + (trunkTopX - baseX) * spec.startFrac
                val sy = groundY - trunkHeight * spec.startFrac
                val len = h * spec.lengthFrac * localG * (if (isMonthly || stage == PlantGrowthStage.MATURE_TREE) 1.12f else 1.0f)
                val rad = (spec.angleDeg - 90f) * (PI / 180.0)
                val ex = sx + (cos(rad) * len).toFloat()
                val ey = sy + (sin(rad) * len).toFloat()
                val ctrlX = (sx + ex) * 0.5f + (if (spec.angleDeg < 0) -10f else 10f) * scale * localG
                val ctrlY = (sy + ey) * 0.5f - 8f * scale * localG

                val bPath = Path().apply {
                    moveTo(sx, sy)
                    quadTo(ctrlX, ctrlY, ex, ey)
                }
                val bPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    style = Paint.Style.STROKE
                    strokeWidth = (2.2f + (1f - idx * 0.08f) * 4.5f * localG) * scale
                    strokeCap = Paint.Cap.ROUND
                    strokeJoin = Paint.Join.ROUND
                    color = barkLight.copy(alpha = 0.94f).toArgb()
                }
                canvas.drawPath(bPath, bPaint)
                canopyAnchors.add(ex to ey)
                if (localG > 0.45f) {
                    canopyAnchors.add(ctrlX to ctrlY)
                }
            }
        }

        // Lush Layered Canopy
        if (growth > 0.30f) {
            val canopyG = ((growth - 0.30f) / 0.70f).coerceIn(0f, 1f)
            canopyAnchors.forEachIndexed { idx, (ax, ay) ->
                val r = ((20f + canopyG * (if (isMonthly) 32f else 26f) + (idx % 3) * 6f) * scale).coerceAtLeast(4f)
                val c1 = if (idx % 2 == 0) palette.primary else palette.secondary
                val c2 = palette.primaryBright
                val canopyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = RadialGradient(
                        ax, ay, r,
                        intArrayOf(
                            c2.copy(alpha = 0.62f * canopyG).toArgb(),
                            c1.copy(alpha = 0.48f * canopyG).toArgb(),
                            palette.heroStart.copy(alpha = 0.14f * canopyG).toArgb(),
                            android.graphics.Color.TRANSPARENT
                        ),
                        floatArrayOf(0f, 0.45f, 0.78f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawCircle(ax, ay, r, canopyPaint)
            }
        }

        // Harvest Fruit / Blossom / Lantern Nodes
        if (growth > 0.36f && canopyAnchors.isNotEmpty()) {
            val nodeG = ((growth - 0.36f) / 0.64f).coerceIn(0f, 1f)
            val totalNodes = (taskCount + gardenCount + circleCount).coerceIn(6, 22)
            for (i in 0 until totalNodes) {
                val (ax, ay) = canopyAnchors[i % canopyAnchors.size]
                val ang = (i * 137.5f) * (PI / 180.0)
                val dist = (10f + (i % 4) * 9f) * scale * nodeG
                val nx = ax + (cos(ang) * dist).toFloat()
                val ny = ay + (sin(ang) * dist).toFloat()

                val nodeColor = when {
                    i < taskCount -> palette.primaryBright
                    i < taskCount + gardenCount -> palette.secondary
                    else -> Color(0xFFFFE599)
                }
                val nr = (5.4f + (if (isMonthly) 0.8f else 0f)) * scale * nodeG
                val glowR = (nr * 2.8f).coerceAtLeast(4f)

                val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = RadialGradient(
                        nx, ny, glowR,
                        intArrayOf(nodeColor.copy(alpha = 0.56f * nodeG).toArgb(), android.graphics.Color.TRANSPARENT),
                        floatArrayOf(0f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
                canvas.drawCircle(nx, ny, glowR, glowPaint)
                canvas.drawCircle(
                    nx,
                    ny,
                    nr,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply { color = nodeColor.copy(alpha = 0.96f * nodeG).toArgb() }
                )
                canvas.drawCircle(
                    nx - nr * 0.25f,
                    ny - nr * 0.25f,
                    nr * 0.36f,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.White.copy(alpha = 0.72f * nodeG).toArgb() }
                )
            }
        }

        // Floating Ambient Fireflies / Petals
        for (p in 0 until 12) {
            val phase = (0.28f + p * 0.083f) % 1f
            val px = left + w * (0.12f + (p * 0.074f) % 0.76f)
            val py = top + h * (0.14f + phase * 0.66f)
            val pa = sin(phase * PI).toFloat().coerceIn(0f, 1f) * 0.70f * growth.coerceAtLeast(0.35f)
            val r = (2.2f + (p % 2) * 1.2f) * scale
            canvas.drawCircle(
                px,
                py,
                r,
                Paint(Paint.ANTI_ALIAS_FLAG).apply { color = palette.primaryBright.copy(alpha = pa).toArgb() }
            )
        }
    }
}
