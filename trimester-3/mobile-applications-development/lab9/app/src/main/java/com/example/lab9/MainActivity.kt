package com.example.lab9

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.lab9.ui.navigation.PortalNavigation
import com.example.lab9.ui.theme.PortalTheme
import com.example.lab9.ui.viewmodel.SyncViewModel
import com.example.lab9.ui.viewmodel.SyncViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: SyncViewModel by viewModels {
        SyncViewModelFactory(applicationContext)
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled silently */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Trigger initial sync if no data yet
        viewModel.triggerManualSync()

        setContent {
            PortalTheme {
                PortalNavigation(viewModel = viewModel)
            }
        }
    }
}