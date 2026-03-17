package com.example.dailyplanner.ui.planner

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyplanner.data.entity.PlannerEventEntity
import com.example.dailyplanner.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(viewModel: PlannerViewModel = viewModel()) {
    val events by viewModel.events.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Event")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Daily Planner",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Plan your day, achieve your goals",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Date selector
            DateSelector(selectedDate = selectedDate, onDateSelected = { viewModel.selectDate(it) })

            Spacer(modifier = Modifier.height(16.dp))

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.EventNote,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No events planned",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            "Tap + to add your first event",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(events, key = { it.id }) { event ->
                        EventCard(
                            event = event,
                            onToggleComplete = { viewModel.toggleEventComplete(event) },
                            onDelete = { viewModel.deleteEvent(event) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEventDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, desc, start, end, priority ->
                viewModel.addEvent(title, desc, start, end, priority)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun DateSelector(selectedDate: Long, onDateSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance()
    val today = calendar.timeInMillis
    val dates = remember {
        (-3..10).map { offset ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, offset)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
    }
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

    val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedDate }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dates) { date ->
            val cal = Calendar.getInstance().apply { timeInMillis = date }
            val isSelected = cal.get(Calendar.DAY_OF_YEAR) == selectedCal.get(Calendar.DAY_OF_YEAR) &&
                    cal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR)

            val bgColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                label = "dateBg"
            )
            val textColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "dateText"
            )

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .clickable { onDateSelected(date) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    dayFormat.format(Date(date)),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    dateFormat.format(Date(date)),
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun EventCard(
    event: PlannerEventEntity,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val alpha by animateFloatAsState(if (event.isCompleted) 0.6f else 1f, label = "alpha")
    val priorityColor = when (event.priority) {
        "High" -> PriorityHigh
        "Medium" -> PriorityMedium
        else -> PriorityLow
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(priorityColor)
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Time column
            if (event.startTime.isNotBlank()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        event.startTime,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (event.endTime.isNotBlank()) {
                        Text(
                            event.endTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    event.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (event.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (event.description.isNotBlank()) {
                    Text(
                        event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Actions
            IconButton(onClick = onToggleComplete) {
                Icon(
                    if (event.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = "Toggle",
                    tint = if (event.isCompleted) SuccessGreen else MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Event", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start") },
                        placeholder = { Text("09:00") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End") },
                        placeholder = { Text("10:00") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                Text("Priority", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlannerViewModel.priorities.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onAdd(title, description, startTime, endTime, priority) },
                enabled = title.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
