package com.example.lab9.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SyncViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SyncViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SyncViewModel(context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
