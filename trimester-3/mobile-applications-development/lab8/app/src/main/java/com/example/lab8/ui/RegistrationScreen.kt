package com.example.lab8.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Build
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: AttendeeViewModel,
    onNavigateToManagement: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var smsPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        smsPermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "Permission Granted! Click Register again to send SMS.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "SMS Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Registration", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onNavigateToManagement) {
                        Icon(imageVector = Icons.Default.List, contentDescription = "Management Panel", tint = MaterialTheme.colorScheme.onPrimary)
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Add Attendee",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank() && age.isNotBlank() && phone.isNotBlank()) {
                                val ageInt = age.toIntOrNull()
                                if (ageInt != null) {
                                    if (smsPermissionGranted) {
                                        viewModel.addAttendee(name, ageInt, phone)
                                        sendSms(phone, "Welcome to the AI Conference, $name!", context)
                                        name = ""
                                        age = ""
                                        phone = ""
                                        Toast.makeText(context, "Attendee Registered!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.SEND_SMS)
                                    }
                                } else {
                                    Toast.makeText(context, "Invalid age", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun sendSms(phoneNumber: String, message: String, context: Context) {
    try {
        // Clean phone number (remove spaces, dashes, etc.)
        val cleanedPhone = phoneNumber.filter { it.isDigit() || it == '+' }
        
        if (cleanedPhone.isBlank()) {
            Toast.makeText(context, "Invalid phone number", Toast.LENGTH_SHORT).show()
            return
        }

        val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java) ?: @Suppress("DEPRECATION") SmsManager.getDefault()
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }
        
        val parts = smsManager.divideMessage(message)
        if (parts.size > 1) {
            smsManager.sendMultipartTextMessage(cleanedPhone, null, parts, null, null)
        } else {
            smsManager.sendTextMessage(cleanedPhone, null, message, null, null)
        }
        
        Toast.makeText(context, "SMS Sent to $cleanedPhone", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to send SMS: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}
