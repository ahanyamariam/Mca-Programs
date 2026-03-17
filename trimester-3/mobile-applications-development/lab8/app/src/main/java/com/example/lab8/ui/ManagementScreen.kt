package com.example.lab8.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lab8.data.Attendee

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
    viewModel: AttendeeViewModel,
    onNavigateBack: () -> Unit
) {
    val attendees by viewModel.allAttendees.collectAsState()
    var selectedAttendee by remember { mutableStateOf<Attendee?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Management Panel", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (attendees.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No attendees registered yet.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(attendees) { attendee ->
                        AttendeeItem(
                            attendee = attendee,
                            onEdit = {
                                selectedAttendee = attendee
                                showEditDialog = true
                            },
                            onDelete = { viewModel.deleteAttendee(attendee) }
                        )
                    }
                }
            }
        }

        if (showEditDialog && selectedAttendee != null) {
            EditAttendeeDialog(
                attendee = selectedAttendee!!,
                onDismiss = {
                    showEditDialog = false
                    selectedAttendee = null
                },
                onSave = { updatedAttendee ->
                    viewModel.updateAttendee(updatedAttendee)
                    showEditDialog = false
                    selectedAttendee = null
                }
            )
        }
    }
}

@Composable
fun AttendeeItem(
    attendee: Attendee,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = attendee.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Age: ${attendee.age}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Phone: ${attendee.phoneNumber}", style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAttendeeDialog(
    attendee: Attendee,
    onDismiss: () -> Unit,
    onSave: (Attendee) -> Unit
) {
    var name by remember { mutableStateOf(attendee.name) }
    var age by remember { mutableStateOf(attendee.age.toString()) }
    var phone by remember { mutableStateOf(attendee.phoneNumber) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Attendee") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val ageInt = age.toIntOrNull()
                    if (name.isNotBlank() && ageInt != null && phone.isNotBlank()) {
                        onSave(attendee.copy(name = name, age = ageInt, phoneNumber = phone))
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
