package com.example.cia3.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        // Appearance
        val DARK_MODE = intPreferencesKey("dark_mode") // 0=system, 1=light, 2=dark
        val ACCENT_COLOR = intPreferencesKey("accent_color") // 0=pink, 1=blue, 2=green, 3=red, 4=purple
        val FONT_SIZE = intPreferencesKey("font_size") // 0=small, 1=medium, 2=large

        // Tasks
        val SORT_ORDER = intPreferencesKey("sort_order") // 0=creation, 1=dueDate, 2=title
        val VIEW_FILTER = intPreferencesKey("view_filter") // 0=all, 1=pending, 2=overdue
        val CONFIRM_DELETE = booleanPreferencesKey("confirm_delete")

        // Profile
        val USER_NAME = stringPreferencesKey("user_name")
        val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")

        // Security
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val HIDE_DESCRIPTIONS = booleanPreferencesKey("hide_descriptions")
    }

    // ── Readers ──

    val darkMode: Flow<Int> = context.dataStore.data.map { it[DARK_MODE] ?: 0 }
    val accentColor: Flow<Int> = context.dataStore.data.map { it[ACCENT_COLOR] ?: 0 }
    val fontSize: Flow<Int> = context.dataStore.data.map { it[FONT_SIZE] ?: 1 }

    val sortOrder: Flow<Int> = context.dataStore.data.map { it[SORT_ORDER] ?: 0 }
    val viewFilter: Flow<Int> = context.dataStore.data.map { it[VIEW_FILTER] ?: 0 }
    val confirmDelete: Flow<Boolean> = context.dataStore.data.map { it[CONFIRM_DELETE] ?: true }

    val userName: Flow<String> = context.dataStore.data.map { it[USER_NAME] ?: "" }
    val profileImageUri: Flow<String> = context.dataStore.data.map { it[PROFILE_IMAGE_URI] ?: "" }

    val appLockEnabled: Flow<Boolean> = context.dataStore.data.map { it[APP_LOCK_ENABLED] ?: false }
    val hideDescriptions: Flow<Boolean> = context.dataStore.data.map { it[HIDE_DESCRIPTIONS] ?: false }

    // ── Writers ──

    suspend fun setDarkMode(value: Int) { context.dataStore.edit { it[DARK_MODE] = value } }
    suspend fun setAccentColor(value: Int) { context.dataStore.edit { it[ACCENT_COLOR] = value } }
    suspend fun setFontSize(value: Int) { context.dataStore.edit { it[FONT_SIZE] = value } }

    suspend fun setSortOrder(value: Int) { context.dataStore.edit { it[SORT_ORDER] = value } }
    suspend fun setViewFilter(value: Int) { context.dataStore.edit { it[VIEW_FILTER] = value } }
    suspend fun setConfirmDelete(value: Boolean) { context.dataStore.edit { it[CONFIRM_DELETE] = value } }

    suspend fun setUserName(value: String) { context.dataStore.edit { it[USER_NAME] = value } }
    suspend fun setProfileImageUri(value: String) { context.dataStore.edit { it[PROFILE_IMAGE_URI] = value } }

    suspend fun setAppLockEnabled(value: Boolean) { context.dataStore.edit { it[APP_LOCK_ENABLED] = value } }
    suspend fun setHideDescriptions(value: Boolean) { context.dataStore.edit { it[HIDE_DESCRIPTIONS] = value } }
}
