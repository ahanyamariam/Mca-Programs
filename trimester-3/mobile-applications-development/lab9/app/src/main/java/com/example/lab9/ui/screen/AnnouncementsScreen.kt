package com.example.lab9.ui.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab9.data.local.entity.AnnouncementEntity
import com.example.lab9.ui.theme.*
import com.example.lab9.ui.viewmodel.SyncViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(viewModel: SyncViewModel) {
    val announcements by viewModel.announcements.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    PullToRefreshBox(
        isRefreshing = isSyncing,
        onRefresh = { viewModel.triggerManualSync() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavySurface)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column {
                        Text(
                            text = "Announcements",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        val unread = announcements.count { !it.isRead }
                        Text(
                            text = if (unread > 0) "$unread unread · ${announcements.size} total" else "${announcements.size} announcements",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (unread > 0) GoldAccent else TextSecondary
                        )
                    }
                }
            }

            if (announcements.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.NotificationsOff, contentDescription = null, tint = IndigoBright, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No announcements yet", color = TextSecondary)
                            Text("Pull to sync", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(announcements, key = { it.id }) { announcement ->
                    AnnouncementCard(
                        announcement = announcement,
                        onRead = { viewModel.markAnnouncementRead(announcement.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementCard(announcement: AnnouncementEntity, onRead: () -> Unit) {
    val priorityColor = when (announcement.priority) {
        "HIGH" -> ErrorRed
        "LOW" -> SuccessGreen
        else -> IndigoBright
    }
    val isUnread = !announcement.isRead

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { if (isUnread) onRead() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnread) NavySurface else NavyCard
        ),
        border = if (isUnread) BorderStroke(1.dp, IndigoAccent.copy(alpha = 0.5f)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Priority indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(priorityColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF5C6BC0).copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(announcement.courseCode, color = IndigoBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                if (isUnread) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("NEW", color = GoldAccent, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        formatRelativeTime(announcement.postedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = announcement.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = announcement.courseTitle,
                style = MaterialTheme.typography.labelSmall,
                color = IndigoBright
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = announcement.body,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
            if (isUnread) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Tap to mark as read",
                    style = MaterialTheme.typography.labelSmall,
                    color = IndigoBright.copy(alpha = 0.6f)
                )
            }
        }
    }
}

private fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val hours = diff / (1000 * 60 * 60)
    val days = hours / 24
    return when {
        hours < 1 -> "Just now"
        hours < 24 -> "${hours}h ago"
        days == 1L -> "Yesterday"
        else -> "${days}d ago"
    }
}
