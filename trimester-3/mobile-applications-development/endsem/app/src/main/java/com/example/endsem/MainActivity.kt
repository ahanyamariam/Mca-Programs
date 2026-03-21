package com.example.endsem

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.endsem.ui.navigation.MovieExplorerApp
import com.example.endsem.ui.theme.EndsemTheme
import com.example.endsem.viewmodel.MovieViewModel
import com.example.endsem.viewmodel.MovieViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MovieViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
        // Permission result handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request notification permission for Android 13+
        requestNotificationPermission()

        // Initialize ViewModel
        val app = application as MovieExplorerApp
        val factory = MovieViewModelFactory(app.repository, application)
        viewModel = ViewModelProvider(this, factory)[MovieViewModel::class.java]

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            EndsemTheme(darkTheme = isDarkMode) {
                MovieExplorerApp(viewModel = viewModel)
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}
