package com.onereflection.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.model.AvatarUrls
import com.onereflection.app.model.ChimeType
import com.onereflection.app.model.OneReflectionUiState
import com.onereflection.app.model.ScreenId
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.components.OneReflectionLogoMark
import com.onereflection.app.ui.theme.BodyFontFamily
import com.onereflection.app.ui.theme.DisplayFontFamily
import com.onereflection.app.ui.theme.MonoFontFamily
import com.onereflection.app.ui.theme.OneReflectionColors

@Composable
fun PeopleScreen(
    uiState: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onOpenVoiceStudio: () -> Unit,
    onOpenBedtimeSync: () -> Unit,
    onPlayChime: (ChimeType) -> Unit
) {
    BackHandler { onNavigate(ScreenId.HOME) }

    val context = LocalContext.current
    var pendingAccepted by remember { mutableStateOf(false) }
    var pendingDismissed by remember { mutableStateOf(false) }

    val oledMode = uiState.oledMode
    val cardBg = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.SurfaceCard
    val borderColor = if (oledMode) OneReflectionColors.OledBorder else OneReflectionColors.HairlineLight
    val syncKey = "REFLECTION-MY-8924"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 148.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Editorial Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "TRUSTED INNER CIRCLE & SYNC KEY",
                    fontFamily = MonoFontFamily,
                    fontSize = 10.5.sp,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Better together.",
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 31.sp,
                    letterSpacing = (-0.6).sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Signature Obsidian & Gold Sync Key Card with Custom Brand Logo Center Emblem
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("people_sync_key_card"),
                shape = RoundedCornerShape(30.dp),
                color = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.InkPrimary,
                border = BorderStroke(1.dp, OneReflectionColors.GoldLuminous.copy(alpha = 0.35f)),
                shadowElevation = if (oledMode) 0.dp else 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PRIVATE SANCTUARY KEY",
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                letterSpacing = 1.sp,
                                color = OneReflectionColors.GoldLuminous
                            )
                            Text(
                                text = "Elena Vance",
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = OneReflectionColors.GoldLuminous.copy(alpha = 0.16f)
                        ) {
                            Text(
                                text = "E2E VERIFIED",
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = OneReflectionColors.GoldLuminous,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    // Crisp Warm-Alabaster QR Frame with OneReflection Logo Mark Centered
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = OneReflectionColors.SurfaceWarm,
                        border = BorderStroke(1.5.dp, OneReflectionColors.GoldLuminous.copy(alpha = 0.55f))
                    ) {
                        Box(
                            modifier = Modifier
                                .size(176.dp)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            SanctuaryQrPattern(modifier = Modifier.fillMaxSize())
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(OneReflectionColors.SurfaceWarm)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                OneReflectionLogoMark(size = 36.dp, cornerRadius = 11.dp, showGoldRing = true)
                            }
                        }
                    }

                    Text(
                        text = syncKey,
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.2.sp,
                        color = OneReflectionColors.GoldLuminous
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("people_copy_key_button")
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    cm?.setPrimaryClip(ClipData.newPlainText("OneReflection Sync Key", syncKey))
                                    onPlayChime(ChimeType.CLICK)
                                    Toast.makeText(context, "Sync key copied", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = OneReflectionColors.Terracotta
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 11.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.ContentCopy, contentDescription = "Copy key", tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Copy Key", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("people_share_key_button")
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    onPlayChime(ChimeType.TIBETAN)
                                    Toast.makeText(context, "Invite link ready to share", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 11.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Share, contentDescription = "Share invite", tint = Color.White, modifier = Modifier.size(15.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Share", fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .size(42.dp)
                                .testTag("people_scan_qr_button")
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    onPlayChime(ChimeType.CLICK)
                                    Toast.makeText(context, "Ready to pair new member", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(14.dp),
                            color = OneReflectionColors.GoldLuminous.copy(alpha = 0.2f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan key", tint = OneReflectionColors.GoldLuminous, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Pending Request Card
        if (!pendingDismissed) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = if (oledMode) OneReflectionColors.OledSurface else OneReflectionColors.HoneyMist,
                    border = BorderStroke(1.dp, OneReflectionColors.BurnishedGold.copy(alpha = 0.28f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AvatarImage(
                            url = null,
                            fallbackInitials = "JV",
                            contentDescription = "Julian Vance",
                            size = 44.dp,
                            fallbackBg = OneReflectionColors.InkPrimary,
                            fallbackText = OneReflectionColors.GoldLuminous
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (pendingAccepted) "CIRCLE MEMBER ADDED" else "PENDING INVITE",
                                fontFamily = MonoFontFamily,
                                fontSize = 9.5.sp,
                                color = if (oledMode) OneReflectionColors.GoldLuminous else OneReflectionColors.HoneyInk
                            )
                            Text(
                                text = "Julian Vance",
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Brother • Wants to share wake alarms",
                                fontFamily = BodyFontFamily,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (!pendingAccepted) {
                            Surface(
                                modifier = Modifier
                                    .testTag("people_accept_julian")
                                    .clip(CircleShape)
                                    .clickable {
                                        pendingAccepted = true
                                        onPlayChime(ChimeType.SOLFEGGIO)
                                    },
                                shape = CircleShape,
                                color = OneReflectionColors.SageDeep
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = "Accept", tint = Color.White, modifier = Modifier.size(14.dp))
                                    Text("Accept", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Inner Circle Members List
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "TRUSTED CIRCLE (3)",
                    fontFamily = MonoFontFamily,
                    fontSize = 10.5.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                CircleMemberEditorialCard(
                    name = "Maya R.",
                    role = "Partner • Bedtime Sync & Breakthrough",
                    avatarUrl = AvatarUrls.MAYA,
                    initials = "MR",
                    statusBadge = "SYNCED",
                    cardBg = cardBg,
                    borderColor = borderColor,
                    oledMode = oledMode,
                    onVoiceClick = onOpenVoiceStudio,
                    onSyncClick = onOpenBedtimeSync
                )
                CircleMemberEditorialCard(
                    name = "Leo Chen",
                    role = "Studio Co-Lead • Shared Flows",
                    avatarUrl = AvatarUrls.LEO,
                    initials = "LC",
                    statusBadge = "ACTIVE",
                    cardBg = cardBg,
                    borderColor = borderColor,
                    oledMode = oledMode,
                    onVoiceClick = onOpenVoiceStudio,
                    onSyncClick = onOpenBedtimeSync
                )
                CircleMemberEditorialCard(
                    name = "Sofia Miller",
                    role = "Wellness Circle • Daily Rituals",
                    avatarUrl = AvatarUrls.SOFIA,
                    initials = "SM",
                    statusBadge = "7D STREAK",
                    cardBg = cardBg,
                    borderColor = borderColor,
                    oledMode = oledMode,
                    onVoiceClick = onOpenVoiceStudio,
                    onSyncClick = onOpenBedtimeSync
                )
            }
        }
    }
}

@Composable
private fun CircleMemberEditorialCard(
    name: String,
    role: String,
    avatarUrl: String,
    initials: String,
    statusBadge: String,
    cardBg: Color,
    borderColor: Color,
    oledMode: Boolean,
    onVoiceClick: () -> Unit,
    onSyncClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = cardBg,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AvatarImage(
                url = avatarUrl,
                fallbackInitials = initials,
                contentDescription = name,
                size = 46.dp,
                ringColor = OneReflectionColors.Terracotta.copy(alpha = 0.5f),
                ringWidth = 1.5.dp
            )

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = name,
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Surface(
                        shape = CircleShape,
                        color = if (oledMode) Color(0xFF142722) else OneReflectionColors.SageMist
                    ) {
                        Text(
                            text = statusBadge,
                            fontFamily = MonoFontFamily,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (oledMode) OneReflectionColors.SageLight else OneReflectionColors.SageDeep,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = role,
                    fontFamily = BodyFontFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.PeachMist)
                        .clickable { onVoiceClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = "Send voice note to $name", tint = OneReflectionColors.Terracotta, modifier = Modifier.size(18.dp))
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (oledMode) OneReflectionColors.OledSurfaceElevated else OneReflectionColors.IndigoMist)
                        .clickable { onSyncClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Bedtime, contentDescription = "Sync bedtime with $name", tint = if (oledMode) OneReflectionColors.IndigoLight else OneReflectionColors.IndigoDeep, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun SanctuaryQrPattern(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cells = 11
        val cellSize = size.minDimension / cells
        val darkInk = OneReflectionColors.InkPrimary
        val accentInk = OneReflectionColors.Terracotta

        fun drawFinder(col: Int, row: Int) {
            drawRoundRect(
                color = darkInk,
                topLeft = Offset(col * cellSize, row * cellSize),
                size = Size(cellSize * 3f, cellSize * 3f),
                cornerRadius = CornerRadius(cellSize * 0.45f, cellSize * 0.45f)
            )
            drawRoundRect(
                color = OneReflectionColors.SurfaceWarm,
                topLeft = Offset((col + 0.55f) * cellSize, (row + 0.55f) * cellSize),
                size = Size(cellSize * 1.9f, cellSize * 1.9f),
                cornerRadius = CornerRadius(cellSize * 0.25f, cellSize * 0.25f)
            )
            drawRoundRect(
                color = accentInk,
                topLeft = Offset((col + 1f) * cellSize, (row + 1f) * cellSize),
                size = Size(cellSize, cellSize),
                cornerRadius = CornerRadius(cellSize * 0.2f, cellSize * 0.2f)
            )
        }

        drawFinder(0, 0)
        drawFinder(8, 0)
        drawFinder(0, 8)

        for (r in 0 until cells) {
            for (c in 0 until cells) {
                val inTopLeft = r < 4 && c < 4
                val inTopRight = r < 4 && c >= 7
                val inBottomLeft = r >= 7 && c < 4
                val inCenter = r in 4..6 && c in 4..6
                if (!inTopLeft && !inTopRight && !inBottomLeft && !inCenter) {
                    if ((r * 7 + c * 11 + r * c) % 3 != 0) {
                        drawRoundRect(
                            color = darkInk,
                            topLeft = Offset(c * cellSize + cellSize * 0.14f, r * cellSize + cellSize * 0.14f),
                            size = Size(cellSize * 0.72f, cellSize * 0.72f),
                            cornerRadius = CornerRadius(cellSize * 0.24f, cellSize * 0.24f)
                        )
                    }
                }
            }
        }
    }
}
