package com.example.cia3

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cia3.ui.TaskManagerApp
import com.example.cia3.ui.theme.Cia3Theme
import com.example.cia3.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    private var isAuthenticated by mutableStateOf(false)

    // Launcher for the device credential (PIN/pattern/biometric) screen
    private val authLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                isAuthenticated = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val darkModeOption by settingsViewModel.darkMode.collectAsState()
            val accentColor by settingsViewModel.accentColor.collectAsState()
            val fontSize by settingsViewModel.fontSize.collectAsState()
            val appLockEnabled by settingsViewModel.appLockEnabled.collectAsState()

            val isDark = when (darkModeOption) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            Cia3Theme(
                darkTheme = isDark,
                accentColor = accentColor,
                fontSizeIndex = fontSize
            ) {
                if (appLockEnabled && !isAuthenticated) {
                    // Lock screen
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Task Manager is Locked",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Authenticate to access your tasks",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(onClick = { promptDeviceAuth() }) {
                                Text("Unlock")
                            }
                        }
                    }
                } else {
                    TaskManagerApp(settingsViewModel = settingsViewModel)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Re-lock when leaving app
        isAuthenticated = false
    }

    private fun promptDeviceAuth() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (keyguardManager.isDeviceSecure) {
            @Suppress("DEPRECATION")
            val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                "Unlock Task Manager",
                "Authenticate using your device PIN, pattern, or biometric"
            )
            if (intent != null) {
                authLauncher.launch(intent)
            } else {
                // Fallback: if intent is null, auto-unlock
                isAuthenticated = true
            }
        } else {
            // No device lock screen set up — auto-unlock
            isAuthenticated = true
        }
    }
}