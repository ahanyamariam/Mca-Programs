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
import com.example.lab9.data.local.entity.CourseEntity
import com.example.lab9.ui.theme.*
import com.example.lab9.ui.viewmodel.SyncViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: SyncViewModel) {
    val courses by viewModel.courses.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val coursesByDay = days.associateWith { day ->
        courses.filter { it.dayOfWeek.equals(day, ignoreCase = true) }
    }.filter { it.value.isNotEmpty() }

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
                // Screen header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavySurface)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Column {
                        Text(
                            text = "Weekly Schedule",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${courses.size} courses enrolled",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            if (courses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = IndigoBright, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Pull down to sync your schedule", color = TextSecondary)
                        }
                    }
                }
            } else {
                coursesByDay.forEach { (day, dayCourses) ->
                    item(key = "header_$day") {
                        DayHeader(day = day, count = dayCourses.size)
                    }
                    items(dayCourses, key = { it.id }) { course ->
                        CourseCard(course = course)
                    }
                }
            }
        }
    }
}

@Composable
fun DayHeader(day: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(IndigoAccent.copy(alpha = 0.2f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(day, color = IndigoBright, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("$count class${if (count > 1) "es" else ""}", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.weight(1f))
        Divider(
            modifier = Modifier.weight(1f),
            color = IndigoAccent.copy(alpha = 0.2f),
            thickness = 1.dp
        )
    }
}

@Composable
fun CourseCard(course: CourseEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Color accent bar
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(70.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(course.color))
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = course.code,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(course.color),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(course.color).copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("${course.credits} cr", fontSize = 10.sp, color = Color(course.color))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(course.title, fontWeight = FontWeight.SemiBold, color = TextPrimary, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(course.instructor, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TealAccent, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(course.room, style = MaterialTheme.typography.bodySmall, color = TealAccent)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(course.startTime, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(course.endTime, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}
