package com.example.lab9.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.WorkInfo
import com.example.lab9.ui.theme.*
import com.example.lab9.ui.viewmodel.SyncViewModel
import java.util.concurrent.TimeUnit

@Composable
fun SettingsScreen(viewModel: SyncViewModel) {
    val syncMeta by viewModel.syncMeta.collectAsState()
    val workerState by viewModel.workerState.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavySurface)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Sync configuration & info",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Manual Sync Section
        SectionTitle("Manual Sync")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Sync Now", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("Fetch latest data immediately", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    SyncButton(isSyncing = isSyncing, onClick = { viewModel.triggerManualSync() })
                }
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = IndigoAccent.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(icon = Icons.Default.History, label = "Last Sync", value = formatSyncTime(syncMeta?.lastSyncTimestamp))
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.CheckCircle,
                    label = "Status",
                    value = syncMeta?.syncStatus ?: "NEVER",
                    valueColor = when (syncMeta?.syncStatus) {
                        "SUCCESS" -> SuccessGreen
                        "FAILED" -> ErrorRed
                        "IN_PROGRESS" -> TealAccent
                        else -> TextSecondary
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = Icons.Default.Storage, label = "Courses Synced", value = "${syncMeta?.totalCoursesSync ?: 0}")
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = Icons.Default.Campaign, label = "Announcements Synced", value = "${syncMeta?.totalAnnouncementsSync ?: 0}")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // WorkManager Status Section
        SectionTitle("WorkManager")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow(
                    icon = Icons.Default.Loop,
                    label = "Task State",
                    value = workerState?.name ?: "Loading…",
                    valueColor = when (workerState) {
                        WorkInfo.State.RUNNING -> TealAccent
                        WorkInfo.State.ENQUEUED -> IndigoBright
                        WorkInfo.State.SUCCEEDED -> SuccessGreen
                        WorkInfo.State.FAILED -> ErrorRed
                        else -> TextSecondary
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = Icons.Default.Timer, label = "Sync Interval", value = "Every 6 hours")
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = Icons.Default.Wifi, label = "Requires Network", value = "Yes (Connected)")
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = Icons.Default.Refresh, label = "Retry Policy", value = "Exponential Backoff")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About Section
        SectionTitle("About")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("UniPortal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                        Text("Student Information System", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = IndigoAccent.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(icon = Icons.Default.Code, label = "Version", value = "1.0.0")
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = Icons.Default.Android, label = "Tech Stack", value = "Compose + WorkManager + Room")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SyncButton(isSyncing: Boolean, onClick: () -> Unit) {
    val rotation by rememberInfiniteTransition(label = "rotate").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)),
        label = "rotateAnim"
    )
    Button(
        onClick = onClick,
        enabled = !isSyncing,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = IndigoAccent)
    ) {
        Icon(
            imageVector = Icons.Default.Sync,
            contentDescription = null,
            modifier = if (isSyncing) Modifier.rotate(rotation) else Modifier
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(if (isSyncing) "Syncing…" else "Sync Now")
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = IndigoBright,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String, valueColor: Color = TextPrimary) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodySmall, color = valueColor, fontWeight = FontWeight.Medium)
    }
}
