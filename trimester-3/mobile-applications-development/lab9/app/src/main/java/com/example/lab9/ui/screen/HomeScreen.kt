package com.example.lab9.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab9.data.local.entity.CourseEntity
import com.example.lab9.data.local.entity.SyncMetaEntity
import com.example.lab9.ui.theme.*
import com.example.lab9.ui.viewmodel.SyncViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.work.WorkInfo

@Composable
fun HomeScreen(viewModel: SyncViewModel) {
    val courses by viewModel.courses.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val syncMeta by viewModel.syncMeta.collectAsState()
    val workerState by viewModel.workerState.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val todayDay = remember {
        SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date())
    }
    val todayCourses = courses.filter { it.dayOfWeek.equals(todayDay, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Gradient Header
        GradientHeader(syncMeta = syncMeta, workerState = workerState, isSyncing = isSyncing)

        Spacer(modifier = Modifier.height(20.dp))

        // Stats Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.MenuBook,
                label = "Courses",
                value = courses.size.toString(),
                iconColor = IndigoBright
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Notifications,
                label = "Unread",
                value = unreadCount.toString(),
                iconColor = if (unreadCount > 0) GoldAccent else SuccessGreen
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Today,
                label = "Today",
                value = todayCourses.size.toString(),
                iconColor = TealAccent
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Last Sync Card
        SyncStatusCard(syncMeta = syncMeta, isSyncing = isSyncing)

        Spacer(modifier = Modifier.height(20.dp))

        // Today's Schedule
        if (todayCourses.isNotEmpty()) {
            SectionHeader(title = "Today's Schedule", icon = Icons.Default.Schedule)
            todayCourses.forEach { course ->
                TodayCourseItem(course = course)
            }
        } else {
            EmptyDayCard()
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun GradientHeader(
    syncMeta: SyncMetaEntity?,
    workerState: WorkInfo.State?,
    isSyncing: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A237E), Color(0xFF283593), NavyDeep)
                )
            )
            .padding(top = 48.dp, bottom = 32.dp, start = 20.dp, end = 20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "UniPortal",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Student Dashboard",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                LiveSyncDot(isActive = isSyncing || workerState == WorkInfo.State.RUNNING)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = getCurrentGreeting(),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun LiveSyncDot(isActive: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(
                if (isActive) SuccessGreen.copy(alpha = alpha)
                else TextSecondary.copy(alpha = 0.5f)
            )
    )
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextPrimary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
fun SyncStatusCard(syncMeta: SyncMetaEntity?, isSyncing: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = null,
                tint = if (isSyncing) TealAccent else IndigoBright,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isSyncing) "Syncing now…" else "Last Sync",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Text(
                    text = formatSyncTime(syncMeta?.lastSyncTimestamp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
            syncMeta?.let { meta ->
                val (statusColor, statusText) = when (meta.syncStatus) {
                    "SUCCESS" -> SuccessGreen to "OK"
                    "FAILED" -> ErrorRed to "Failed"
                    "IN_PROGRESS" -> TealAccent to "Running"
                    else -> TextSecondary to "Never"
                }
                Chip(text = statusText, color = statusColor)
            }
        }
    }
}

@Composable
fun Chip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = IndigoBright, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun TodayCourseItem(course: CourseEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(course.color))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(course.title, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 14.sp)
                Text("${course.startTime} – ${course.endTime}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(course.room.substringBefore("(").trim(), style = MaterialTheme.typography.labelSmall, color = TealAccent)
                Text(course.code, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
    }
}

@Composable
fun EmptyDayCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.WbSunny, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("No classes today!", fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text("Enjoy your day off 🎉", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

private fun getCurrentGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greet = when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
    val sdf = SimpleDateFormat("EEEE, d MMMM", Locale.ENGLISH)
    return "$greet · ${sdf.format(Date())}"
}

fun formatSyncTime(timestamp: Long?): String {
    if (timestamp == null || timestamp == 0L) return "Never synced"
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
    return sdf.format(Date(timestamp))
}
