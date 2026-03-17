package com.example.lab7

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab7.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "test_channel",
                "Test Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val settingsDataStore = SettingsDataStore(this)

        enableEdgeToEdge()
        setContent {
            val darkMode by settingsDataStore.darkModeFlow.collectAsState(initial = false)
            Lab7Theme(darkTheme = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PreferencesScreen()
                }
            }
        }
    }
}

fun sendTestNotification(context: Context, phoneNumber: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
    }

    val notification = NotificationCompat.Builder(context, "test_channel")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Test Notification")
        .setContentText("Push notifications to $phoneNumber look like this")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    try {
        NotificationManagerCompat.from(context).notify(1, notification)
    } catch (e: SecurityException) {
        // Ignored
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ── Data sources ─────────────────────────────────────
    val preferencesManager = remember { PreferencesManager(context) }
    val settingsDataStore = remember { SettingsDataStore(context) }

    // ── SharedPreferences state ──────────────────────────
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var storedName by remember { mutableStateOf("") }
    var storedEmail by remember { mutableStateOf("") }
    var showSavedBanner by remember { mutableStateOf(false) }

    // ── DataStore state ──────────────────────────────────
    val darkMode by settingsDataStore.darkModeFlow.collectAsState(initial = false)
    val notifications by settingsDataStore.notificationsFlow.collectAsState(initial = true)
    val phoneNumber by settingsDataStore.phoneNumberFlow.collectAsState(initial = "")

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            sendTestNotification(context, phoneNumber)
        }
    }

    // Load SharedPreferences on first composition
    LaunchedEffect(Unit) {
        storedName = preferencesManager.getName()
        storedEmail = preferencesManager.getEmail()
        nameInput = storedName
        emailInput = storedEmail
    }

    // Auto-dismiss saved banner
    LaunchedEffect(showSavedBanner) {
        if (showSavedBanner) {
            kotlinx.coroutines.delay(2000)
            showSavedBanner = false
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PinkGradientStart, PinkGradientMid, PinkGradientEnd)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "My Preferences",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Save your data locally and access it anytime",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Saved banner ────────────────────────────
            AnimatedVisibility(
                visible = showSavedBanner,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = PinkPrimaryContainer
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = PinkPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Profile saved successfully!",
                            style = MaterialTheme.typography.labelLarge,
                            color = PinkOnPrimaryContainer
                        )
                    }
                }
            }

            // ══════════════════════════════════════════════
            // SHARED PREFERENCES SECTION
            // ══════════════════════════════════════════════
            SectionHeader(
                icon = Icons.Rounded.Storage,
                title = "SharedPreferences",
                subtitle = "User profile stored with classic Android storage"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = PinkPrimary.copy(alpha = 0.15f),
                        spotColor = PinkPrimary.copy(alpha = 0.2f)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Name input
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Full Name") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = null,
                                tint = PinkPrimary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkPrimary,
                            unfocusedBorderColor = PinkOutlineVariant,
                            cursorColor = PinkPrimary,
                            focusedLabelColor = PinkPrimary
                        ),
                        singleLine = true
                    )

                    // Email input
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Email,
                                contentDescription = null,
                                tint = PinkPrimary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkPrimary,
                            unfocusedBorderColor = PinkOutlineVariant,
                            cursorColor = PinkPrimary,
                            focusedLabelColor = PinkPrimary
                        ),
                        singleLine = true
                    )

                    // Save button
                    Button(
                        onClick = {
                            preferencesManager.saveName(nameInput)
                            preferencesManager.saveEmail(emailInput)
                            storedName = nameInput
                            storedEmail = emailInput
                            showSavedBanner = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinkPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Icon(
                            Icons.Rounded.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Save Profile",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        )
                    }

                    // ── Stored values display ────────────
                    if (storedName.isNotBlank() || storedEmail.isNotBlank()) {
                        HorizontalDivider(
                            color = PinkOutlineVariant.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            "Currently Stored",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (storedName.isNotBlank()) {
                            StoredValueChip(
                                icon = Icons.Outlined.Person,
                                label = "Name",
                                value = storedName
                            )
                        }

                        if (storedEmail.isNotBlank()) {
                            StoredValueChip(
                                icon = Icons.Outlined.Email,
                                label = "Email",
                                value = storedEmail
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ══════════════════════════════════════════════
            // DATASTORE SECTION
            // ══════════════════════════════════════════════
            SectionHeader(
                icon = Icons.Rounded.Settings,
                title = "DataStore",
                subtitle = "App settings powered by modern Jetpack DataStore"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = PinkPrimary.copy(alpha = 0.15f),
                        spotColor = PinkPrimary.copy(alpha = 0.2f)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Dark Mode Toggle
                    SettingsToggleRow(
                        icon = Icons.Outlined.DarkMode,
                        title = "Dark Mode",
                        description = "Switch to a darker theme",
                        checked = darkMode,
                        onCheckedChange = { enabled ->
                            scope.launch { settingsDataStore.saveDarkMode(enabled) }
                        }
                    )

                    HorizontalDivider(
                        color = PinkOutlineVariant.copy(alpha = 0.35f),
                        thickness = 1.dp
                    )

                    // Notifications Toggle
                    SettingsToggleRow(
                        icon = Icons.Outlined.Notifications,
                        title = "Notifications",
                        description = "Receive push alerts",
                        checked = notifications,
                        onCheckedChange = { enabled ->
                            scope.launch { settingsDataStore.saveNotifications(enabled) }
                        }
                    )

                    HorizontalDivider(
                        color = PinkOutlineVariant.copy(alpha = 0.35f),
                        thickness = 1.dp
                    )

                    // Phone Number input
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { scope.launch { settingsDataStore.savePhoneNumber(it) } },
                        label = { Text("Phone Number") },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Phone,
                                contentDescription = null,
                                tint = PinkPrimary
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PinkPrimary,
                            unfocusedBorderColor = PinkOutlineVariant,
                            cursorColor = PinkPrimary,
                            focusedLabelColor = PinkPrimary
                        ),
                        singleLine = true
                    )

                    // Test Push Notification button
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    sendTestNotification(context, phoneNumber)
                                }
                            } else {
                                sendTestNotification(context, phoneNumber)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinkPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Icon(
                            Icons.Rounded.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Test Push Notification",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }

            // ── Status indicators ────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PinkSurfaceVariant.copy(alpha = 0.6f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatusBadge(
                        label = "Dark Mode",
                        active = darkMode
                    )
                    StatusBadge(
                        label = "Notifications",
                        active = notifications
                    )
                }
            }

            // ── Footer ──────────────────────────────────
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Data persists across app restarts ✨",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ══════════════════════════════════════════════════════════
// COMPOSABLE COMPONENTS
// ══════════════════════════════════════════════════════════

@Composable
fun SectionHeader(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(PinkGradientStart, PinkGradientMid)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StoredValueChip(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PinkPrimaryContainer.copy(alpha = 0.5f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PinkPrimary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            "$label: ",
            style = MaterialTheme.typography.labelMedium,
            color = PinkOnPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = PinkOnPrimaryContainer
        )
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = PinkPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PinkPrimary,
                uncheckedThumbColor = PinkOutline,
                uncheckedTrackColor = PinkOutlineVariant.copy(alpha = 0.5f),
                uncheckedBorderColor = PinkOutline
            )
        )
    }
}

@Composable
fun StatusBadge(label: String, active: Boolean) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (active) 1f else 0.5f,
        animationSpec = tween(durationMillis = 400),
        label = "badgeAlpha"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    if (active) PinkPrimary.copy(alpha = animatedAlpha)
                    else PinkOutlineVariant.copy(alpha = 0.6f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (active) "ON" else "OFF",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                ),
                color = if (active) Color.White else PinkOnSurfaceVariant
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}