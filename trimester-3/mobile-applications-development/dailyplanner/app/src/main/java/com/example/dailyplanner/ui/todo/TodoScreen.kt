package com.example.dailyplanner.ui.todo

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.dailyplanner.data.entity.TodoEntity
import com.example.dailyplanner.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(viewModel: TodoViewModel = viewModel()) {
    val todos by viewModel.todos.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
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
                text = "To-Do List",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))

            val completedCount = todos.count { it.isCompleted }
            Text(
                text = "$completedCount of ${todos.size} tasks completed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Category chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(TodoViewModel.categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(category, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (todos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.TaskAlt,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            if (selectedCategory == "All") "No tasks yet" else "No $selectedCategory tasks",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(todos, key = { it.id }) { todo ->
                        TodoCard(
                            todo = todo,
                            onToggle = { viewModel.toggleComplete(todo) },
                            onDelete = { viewModel.deleteTodo(todo) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTodoDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, desc, cat, pri, due ->
                viewModel.addTodo(title, desc, cat, pri, due)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TodoCard(
    todo: TodoEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val alpha by animateFloatAsState(if (todo.isCompleted) 0.6f else 1f, label = "alpha")
    val priorityColor = when (todo.priority) {
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(priorityColor)
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Checkbox
            IconButton(onClick = onToggle, modifier = Modifier.size(32.dp)) {
                Icon(
                    if (todo.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = "Toggle",
                    tint = if (todo.isCompleted) SuccessGreen else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    todo.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            todo.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    if (todo.dueDate != null) {
                        val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
                        Text(
                            sdf.format(Date(todo.dueDate)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, Long?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Personal") }
    var priority by remember { mutableStateOf("Medium") }

    val editableCategories = TodoViewModel.categories.filter { it != "All" }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Task", style = MaterialTheme.typography.titleLarge) },
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
                    label = { Text("Description (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Category", style = MaterialTheme.typography.labelLarge)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(editableCategories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                Text("Priority", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TodoViewModel.priorities.forEach { p ->
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
                onClick = { if (title.isNotBlank()) onAdd(title, description, category, priority, null) },
                enabled = title.isNotBlank()
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
