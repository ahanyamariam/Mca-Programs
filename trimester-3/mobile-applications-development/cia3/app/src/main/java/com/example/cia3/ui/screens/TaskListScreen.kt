package com.example.cia3.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cia3.data.Task
import com.example.cia3.viewmodel.TaskViewModel

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onEditTask: (Task) -> Unit,
    userName: String = "",
    hideDescriptions: Boolean = false,
    confirmDelete: Boolean = true,
    sortOrder: Int = 0,
    viewFilter: Int = 0
) {
    val rawTasks by viewModel.allTasks.collectAsState()

    // Apply sorting
    val sortedTasks = when (sortOrder) {
        1 -> rawTasks.sortedBy { it.dueDate }
        2 -> rawTasks.sortedBy { it.title.lowercase() }
        else -> rawTasks
    }

    // Apply filtering
    val tasks = when (viewFilter) {
        1 -> sortedTasks.filter { it.dueDate.isNotBlank() }
        2 -> sortedTasks.filter {
            if (it.dueDate.isBlank()) false
            else {
                try {
                    val parts = it.dueDate.split("/")
                    val d = parts[0].toInt(); val m = parts[1].toInt(); val y = parts[2].toInt()
                    val dueMs = java.util.GregorianCalendar(y, m - 1, d).timeInMillis
                    dueMs < System.currentTimeMillis()
                } catch (_: Exception) { false }
            }
        }
        else -> sortedTasks
    }

    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    if (taskToDelete != null && confirmDelete) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete \"${taskToDelete!!.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(taskToDelete!!)
                    taskToDelete = null
                }) { Text("Delete", color = MaterialTheme.colorScheme.primary) }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (userName.isNotBlank()) {
            Text(
                text = "Hello, $userName!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.TaskAlt,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Tasks Yet",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap the + button to add your first task",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = tasks,
                    key = { _, task -> task.taskId }
                ) { index, task ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(300, delayMillis = index * 50)) +
                                slideInVertically(
                                    initialOffsetY = { it / 2 },
                                    animationSpec = tween(300, delayMillis = index * 50)
                                ),
                        exit = fadeOut()
                    ) {
                        TaskCard(
                            task = task,
                            hideDescription = hideDescriptions,
                            onEdit = { onEditTask(task) },
                            onDelete = {
                                if (confirmDelete) {
                                    taskToDelete = task
                                } else {
                                    viewModel.deleteTask(task)
                                }
                            }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    hideDescription: Boolean = false,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDescription by remember { mutableStateOf(!hideDescription) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .then(
                    if (hideDescription) Modifier.clickable { showDescription = !showDescription }
                    else Modifier
                )
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit task",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete task",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (task.description.isNotBlank() && showDescription) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (task.description.isNotBlank() && hideDescription && !showDescription) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tap to reveal description",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }

            if (task.dueDate.isNotBlank() || task.dueTime.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (task.dueDate.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = task.dueDate,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    if (task.dueTime.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = task.dueTime,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}
