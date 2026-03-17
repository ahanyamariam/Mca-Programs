package com.example.lab7

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property — creates the DataStore singleton
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Manages application settings (dark mode, notifications) via Preferences DataStore.
 */
class SettingsDataStore(private val context: Context) {

    // ── Keys ─────────────────────────────────────────────
    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
        val PHONE_NUMBER_KEY = androidx.datastore.preferences.core.stringPreferencesKey("phone_number")
    }

    // ── Flows (read) ─────────────────────────────────────
    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    val notificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATIONS_KEY] ?: true
        }

    val phoneNumberFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PHONE_NUMBER_KEY] ?: ""
        }

    // ── Suspend writers ──────────────────────────────────
    suspend fun saveDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun saveNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_KEY] = enabled
        }
    }

    suspend fun savePhoneNumber(number: String) {
        context.dataStore.edit { preferences ->
            preferences[PHONE_NUMBER_KEY] = number
        }
    }
}
