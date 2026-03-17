package com.example.lab8

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab8.data.AppDatabase
import com.example.lab8.data.AttendeeRepository
import com.example.lab8.ui.AttendeeViewModel
import com.example.lab8.ui.AttendeeViewModelFactory
import com.example.lab8.ui.NavGraph
import com.example.lab8.ui.theme.Lab8Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(this)
        val repository = AttendeeRepository(database.attendeeDao())
        
        enableEdgeToEdge()
        setContent {
            Lab8Theme(dynamicColor = false) {
                val viewModel: AttendeeViewModel = viewModel(
                    factory = AttendeeViewModelFactory(repository)
                )
                NavGraph(viewModel = viewModel)
            }
        }
    }
}