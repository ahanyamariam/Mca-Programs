package com.example.cia3.ui.screens

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cia3.ui.theme.BluePrimary
import com.example.cia3.ui.theme.GreenPrimary
import com.example.cia3.ui.theme.PinkPrimary
import com.example.cia3.ui.theme.PurplePrimary
import com.example.cia3.ui.theme.RedPrimary
import com.example.cia3.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val darkMode by settingsViewModel.darkMode.collectAsState()
    val accentColor by settingsViewModel.accentColor.collectAsState()
    val fontSize by settingsViewModel.fontSize.collectAsState()
    val sortOrder by settingsViewModel.sortOrder.collectAsState()
    val viewFilter by settingsViewModel.viewFilter.collectAsState()
    val confirmDelete by settingsViewModel.confirmDelete.collectAsState()
    val userName by settingsViewModel.userName.collectAsState()
    val profileImageUri by settingsViewModel.profileImageUri.collectAsState()
    val appLockEnabled by settingsViewModel.appLockEnabled.collectAsState()
    val hideDescriptions by settingsViewModel.hideDescriptions.collectAsState()

    var nameInput by remember(userName) { mutableStateOf(userName) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) { }
            settingsViewModel.setProfileImageUri(it.toString())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ════════════════ APPEARANCE ════════════════
            SectionHeader("Appearance")

            SettingsCard {
                SettingsLabel("Theme Mode")
                Spacer(modifier = Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    listOf("System", "Light", "Dark").forEachIndexed { index, label ->
                        SegmentedButton(
                            selected = darkMode == index,
                            onClick = { settingsViewModel.setDarkMode(index) },
                            shape = SegmentedButtonDefaults.itemShape(index, 3)
                        ) { Text(label) }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                SettingsLabel("Accent Color")
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val colors = listOf(
                        Pair(PinkPrimary, "Pink"),
                        Pair(BluePrimary, "Blue"),
                        Pair(GreenPrimary, "Green"),
                        Pair(RedPrimary, "Red"),
                        Pair(PurplePrimary, "Purple")
                    )
                    colors.forEachIndexed { index, (color, name) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .then(
                                        if (accentColor == index)
                                            Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                        else Modifier
                                    )
                                    .clickable { settingsViewModel.setAccentColor(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (accentColor == index) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                SettingsLabel("Font Size")
                Spacer(modifier = Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    listOf("Small", "Medium", "Large").forEachIndexed { index, label ->
                        SegmentedButton(
                            selected = fontSize == index,
                            onClick = { settingsViewModel.setFontSize(index) },
                            shape = SegmentedButtonDefaults.itemShape(index, 3)
                        ) { Text(label) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ════════════════ TASKS ════════════════
            SectionHeader("Tasks")

            SettingsCard {
                SettingsLabel("Default Sort Order")
                Spacer(modifier = Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    listOf("Created", "Due Date", "Title").forEachIndexed { index, label ->
                        SegmentedButton(
                            selected = sortOrder == index,
                            onClick = { settingsViewModel.setSortOrder(index) },
                            shape = SegmentedButtonDefaults.itemShape(index, 3)
                        ) { Text(label, style = MaterialTheme.typography.labelSmall) }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                SettingsLabel("Default View Filter")
                Spacer(modifier = Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    listOf("All", "Pending", "Overdue").forEachIndexed { index, label ->
                        SegmentedButton(
                            selected = viewFilter == index,
                            onClick = { settingsViewModel.setViewFilter(index) },
                            shape = SegmentedButtonDefaults.itemShape(index, 3)
                        ) { Text(label, style = MaterialTheme.typography.labelSmall) }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                SettingsToggle(
                    label = "Confirm before deleting",
                    description = "Show a confirmation dialog before deleting tasks",
                    checked = confirmDelete,
                    onCheckedChange = { settingsViewModel.setConfirmDelete(it) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ════════════════ PROFILE ════════════════
            SectionHeader("Profile")

            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { imagePickerLauncher.launch(arrayOf("image/*")) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUri.isNotBlank()) {
                            val bitmap = remember(profileImageUri) {
                                try {
                                    val uri = Uri.parse(profileImageUri)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                                        ImageDecoder.decodeBitmap(source).copy(android.graphics.Bitmap.Config.ARGB_8888, true)
                                    } else {
                                        @Suppress("DEPRECATION")
                                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                                    }
                                } catch (_: Exception) {
                                    null
                                }
                            }
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Profile picture",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = "Pick image",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = "Pick image",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Profile Picture",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "Tap to change",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))

                SettingsLabel("Your Name")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = {
                        nameInput = it
                        settingsViewModel.setUserName(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your name") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Displayed as a greeting on the home screen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ════════════════ SECURITY ════════════════
            SectionHeader("Security & Privacy")

            SettingsCard {
                SettingsToggle(
                    label = "App Lock",
                    description = "Enable PIN or biometric authentication to open the app",
                    checked = appLockEnabled,
                    onCheckedChange = { settingsViewModel.setAppLockEnabled(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                SettingsToggle(
                    label = "Hide Descriptions",
                    description = "Task descriptions are hidden until tapped",
                    checked = hideDescriptions,
                    onCheckedChange = { settingsViewModel.setHideDescriptions(it) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingsLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun SettingsToggle(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
