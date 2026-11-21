package com.onereflection.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onereflection.app.data.ChimePlayer
import com.onereflection.app.model.*
import com.onereflection.app.ui.components.AvatarImage
import com.onereflection.app.ui.theme.BrandColors
import com.onereflection.app.viewmodel.OneReflectionUiState
import kotlinx.coroutines.delay

@Composable
fun PeopleScreen(
    state: OneReflectionUiState,
    onNavigate: (ScreenId) -> Unit,
    onBack: () -> Unit,
    onOpenBedtimeSync: () -> Unit,
    onDismissPendingRequest: (Boolean) -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val oledMode = state.oledMode
    var copied by remember { mutableStateOf(false) }
    var mayaVoiceAlarms by remember { mutableStateOf(true) }
    var mayaSharedHabits by remember { mutableStateOf(true) }
    var mayaRhythmPings by remember { mutableStateOf(true) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(2200L)
            copied = false
        }
    }

    val textPrimary = if (oledMode) Color.White else BrandColors.OnSurfaceDark
    val textSecondary = if (oledMode) Color(0xFFA1A1AA) else BrandColors.OnSurfaceVariant
    val cardBorder = if (oledMode) BrandColors.OledBorder else Color.Black.copy(alpha = 0.06f)

    fun copySyncKey() {
        ChimePlayer.play(ChimeType.CLICK)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText("OneReflection Sync Key", "REFLECTION-MY-8924"))
        copied = true
    }

    fun shareSyncLink() {
        ChimePlayer.play(ChimeType.CLICK)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Join my OneReflection trusted circle with Sync Key: REFLECTION-MY-8924")
        }
        context.startActivity(Intent.createChooser(intent, "Share Sync Key"))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(BrandColors.YellowFixed)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF221B00), modifier = Modifier.size(14.dp))
                    Text("TRUSTED CIRCLE • SAFE SYNC", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF221B00))
                }
                Text("Better together.", style = MaterialTheme.typography.headlineLarge, color = textPrimary)
                Text(
                    "Invite your closest souls, share playful voice wake-ups, and sync daily habits in warm harmony.",
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondary
                )
            }
        }

        // 2. Hero Sync Key & Stylized QR Card (Candy Pink)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = BrandColors.CandyPink,
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.clip(CircleShape).background(Color.White).padding(horizontal = 12.dp, vertical = 5.dp)) {
                            Text("Your Sync Key", style = MaterialTheme.typography.labelMedium, color = BrandColors.OnSurfaceDark)
                        }
                        Box(modifier = Modifier.size(34.dp).clip(CircleShape).background(BrandColors.ButterYellow), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = BrandColors.ButterYellowText, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Stylized Canvas QR Code
                    Surface(shape = RoundedCornerShape(22.dp), color = Color.White, shadowElevation = 2.dp) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(152.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(BrandColors.SurfaceContainerLow)
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val c = BrandColors.OnSurfaceDark
                                    val s = size.minDimension / 160f
                                    fun finder(x: Float, y: Float) {
                                        drawRoundRect(c, Offset(x * s, y * s), Size(40f * s, 40f * s), CornerRadius(8f * s))
                                        drawRoundRect(Color.White, Offset((x + 8) * s, (y + 8) * s), Size(24f * s, 24f * s), CornerRadius(4f * s))
                                        drawRoundRect(c, Offset((x + 14) * s, (y + 14) * s), Size(12f * s, 12f * s), CornerRadius(2f * s))
                                    }
                                    finder(10f, 10f)
                                    finder(110f, 10f)
                                    finder(10f, 110f)
                                    val dots = listOf(
                                        Triple(68f, 22f, 5f), Triple(88f, 22f, 4f), Triple(78f, 38f, 6f),
                                        Triple(28f, 74f, 5f), Triple(46f, 82f, 6f), Triple(120f, 68f, 5f),
                                        Triple(138f, 80f, 6f), Triple(66f, 116f, 6f), Triple(86f, 120f, 4f),
                                        Triple(120f, 124f, 5f), Triple(136f, 138f, 6f)
                                    )
                                    dots.forEach { (cx, cy, r) ->
                                        drawCircle(c, radius = r * s, center = Offset(cx * s, cy * s))
                                    }
                                    drawRoundRect(c, Offset(62f * s, 52f * s), Size(10f * s, 10f * s), CornerRadius(3f * s))
                                    drawRoundRect(c, Offset(70f * s, 132f * s), Size(18f * s, 12f * s), CornerRadius(3f * s))
                                }
                                Box(
                                    modifier = Modifier.size(38.dp).clip(CircleShape).background(BrandColors.ButterYellow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Sensors, contentDescription = null, tint = BrandColors.ButterYellowText, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("SCAN TO SYNC", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.OnSurfaceVariant)
                        }
                    }

                    // Unique Code Pill
                    Surface(
                        onClick = { copySyncKey() },
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.testTag("people_copy_code_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("REFLECTION-MY-8924", style = MaterialTheme.typography.titleSmall, color = BrandColors.OnSurfaceDark)
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(BrandColors.OnSurfaceDark), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy sync key",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    if (copied) {
                        Text("Code copied to clipboard!", style = MaterialTheme.typography.labelSmall, color = BrandColors.CandyPinkDeep)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { shareSyncLink() },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                            modifier = Modifier.weight(1f).height(46.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Link", style = MaterialTheme.typography.labelMedium)
                        }
                        Button(
                            onClick = { copySyncKey() },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BrandColors.OnSurfaceDark),
                            modifier = Modifier.weight(1f).height(46.dp)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Key", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // 3. Pending Requests
        if (!state.pendingRequestDismissed) {
            item {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier.size(44.dp).clip(CircleShape).background(BrandColors.LavenderSoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("JV", style = MaterialTheme.typography.titleSmall, color = BrandColors.LavenderText)
                            }
                            Column {
                                Text("Julian Vance", style = MaterialTheme.typography.titleSmall, color = textPrimary)
                                Text("Connected with Leo Chen • Pending Request", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onDismissPendingRequest(true) },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                                modifier = Modifier.weight(1f).height(42.dp).testTag("people_accept_julian")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Accept & Sync", style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(
                                onClick = { onDismissPendingRequest(false) },
                                shape = CircleShape,
                                modifier = Modifier.height(42.dp)
                            ) {
                                Text("Dismiss", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }

        // 4. Inner Circle Members
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Your Inner Circle", style = MaterialTheme.typography.titleLarge, color = textPrimary)

                // Maya R. (Butter Yellow)
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = BrandColors.ButterYellow,
                    border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                AvatarImage(AVATAR_MAYA, "MR", "Maya R.", 48.dp)
                                Column {
                                    Text("Maya R.", style = MaterialTheme.typography.titleMedium, color = BrandColors.OnSurfaceDark)
                                    Text("Awake & synced at 06:45 AM", style = MaterialTheme.typography.bodySmall, color = BrandColors.ButterYellowText)
                                }
                            }
                            IconButton(onClick = onOpenBedtimeSync) {
                                Icon(Icons.Default.Tune, contentDescription = "Manage Maya access", tint = BrandColors.OnSurfaceDark)
                            }
                        }

                        PermissionToggleRow("Voice Alarms", Icons.Default.Mic, mayaVoiceAlarms) {
                            ChimePlayer.play(ChimeType.CLICK)
                            mayaVoiceAlarms = !mayaVoiceAlarms
                        }
                        PermissionToggleRow("Shared Habits & Tasks", Icons.Default.Checklist, mayaSharedHabits) {
                            ChimePlayer.play(ChimeType.CLICK)
                            mayaSharedHabits = !mayaSharedHabits
                        }
                        PermissionToggleRow("Rhythm Pings", Icons.Default.NotificationsActive, mayaRhythmPings) {
                            ChimePlayer.play(ChimeType.CLICK)
                            mayaRhythmPings = !mayaRhythmPings
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onNavigate(ScreenId.CHATS) },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Text("Voice Ping", style = MaterialTheme.typography.labelMedium)
                            }
                            Button(
                                onClick = onOpenBedtimeSync,
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BrandColors.OnSurfaceDark),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Text("Manage Access", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }

                // Leo Chen (Lavender Blue)
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = BrandColors.LavenderSoft,
                    border = BorderStroke(1.dp, BrandColors.TertiarySlateBlue.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AvatarImage(AVATAR_LEO, "LC", "Leo Chen", 46.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Leo Chen", style = MaterialTheme.typography.titleMedium, color = BrandColors.LavenderText)
                                Text("Focus Session • 45m left", style = MaterialTheme.typography.bodySmall, color = BrandColors.LavenderMuted)
                            }
                            Box(modifier = Modifier.clip(CircleShape).background(Color.White).padding(horizontal = 9.dp, vertical = 4.dp)) {
                                Text("Studio Sprint", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandColors.LavenderText)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onNavigate(ScreenId.TASKS) },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                                modifier = Modifier.weight(1f).height(40.dp)
                            ) {
                                Text("Sync Habit", style = MaterialTheme.typography.labelMedium)
                            }
                            Button(
                                onClick = onOpenBedtimeSync,
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BrandColors.LavenderText),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text("Permissions", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }

                // Sofia Miller
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerHigh,
                    border = BorderStroke(1.dp, cardBorder)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            AvatarImage(AVATAR_SOFIA, "SM", "Sofia Miller", 46.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Sofia Miller", style = MaterialTheme.typography.titleMedium, color = textPrimary)
                                Text("Completed Hydration Streak (7d)", style = MaterialTheme.typography.bodySmall, color = textSecondary)
                            }
                            Box(modifier = Modifier.clip(CircleShape).background(if (oledMode) Color(0xFF27272A) else Color.White).padding(horizontal = 9.dp, vertical = 4.dp)) {
                                Text("Ritual Pal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                            }
                        }
                        Button(
                            onClick = {
                                ChimePlayer.play(ChimeType.WATER)
                                onNavigate(ScreenId.CHATS)
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandColors.OnSurfaceDark, contentColor = Color.White),
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        ) {
                            Text("Send High Five", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }

        // 5. Security Footnote
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (oledMode) BrandColors.OledCard else BrandColors.SurfaceContainerLow,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "End-to-end encrypted reflection mesh",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (oledMode) BrandColors.CandyPink else BrandColors.PrimaryPlum
                    )
                    Text(
                        "You are always in complete control. Revoke any voice alarm or habit access instantly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionToggleRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(icon, contentDescription = null, tint = BrandColors.PrimaryPlum, modifier = Modifier.size(16.dp))
                Text(label, style = MaterialTheme.typography.labelMedium, color = BrandColors.OnSurfaceDark)
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (enabled) BrandColors.ButterYellow else BrandColors.SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = if (enabled) BrandColors.ButterYellowText else BrandColors.OutlineVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
