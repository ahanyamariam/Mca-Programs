package com.example.cia3.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cia3.data.Task
import com.example.cia3.viewmodel.TaskViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    taskToEdit: Task? = null,
    onNavigateBack: () -> Unit
) {
    val isEditing = taskToEdit != null

    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var dueDate by remember { mutableStateOf(taskToEdit?.dueDate ?: "") }
    var dueTime by remember { mutableStateOf(taskToEdit?.dueTime ?: "") }

    var titleError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Task" else "Add Task",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title
            Text(
                text = "Title",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = false
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter task title") },
                isError = titleError,
                supportingText = if (titleError) {
                    { Text("Title is required") }
                } else null,
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Description
            Text(
                text = "Description",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                placeholder = { Text("Enter task description") },
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Due Date & Time side by side
            Text(
                text = "Due Date & Time",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                // Date field
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Select date") },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        dueDate = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Pick date",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Time field
                OutlinedTextField(
                    value = dueTime,
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Select time") },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val calendar = Calendar.getInstance()
                                TimePickerDialog(
                                    context,
                                    { _, hourOfDay, minute ->
                                        dueTime = "%02d:%02d".format(hourOfDay, minute)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    true // 24-hour format
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Pick time",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Save button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                        return@Button
                    }

                    if (isEditing) {
                        viewModel.updateTask(
                            taskToEdit!!.copy(
                                title = title.trim(),
                                description = description.trim(),
                                dueDate = dueDate.trim(),
                                dueTime = dueTime.trim()
                            )
                        )
                    } else {
                        viewModel.insertTask(
                            title = title.trim(),
                            description = description.trim(),
                            dueDate = dueDate.trim(),
                            dueTime = dueTime.trim()
                        )
                    }
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = if (isEditing) "Update Task" else "Save Task",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
