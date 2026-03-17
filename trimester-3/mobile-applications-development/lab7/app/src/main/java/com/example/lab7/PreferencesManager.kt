package com.example.lab7

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages user profile data (name & email) via SharedPreferences.
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveName(name: String) {
        prefs.edit().putString(KEY_NAME, name).apply()
    }

    fun getName(): String {
        return prefs.getString(KEY_NAME, "") ?: ""
    }

    fun saveEmail(email: String) {
        prefs.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getEmail(): String {
        return prefs.getString(KEY_EMAIL, "") ?: ""
    }

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_NAME = "user_name"
        private const val KEY_EMAIL = "user_email"
    }
}
