package com.revelations2026.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class to hold all registration information
 * Implements Parcelable for easy passing between activities
 */
@Parcelize
data class RegistrationData(
    var name: String = "",
    var mobileNumber: String = "",
    var emailId: String = "",
    var collegeName: String = "",
    var groupName: String = "",
    var numberOfEvents: Int = 0
) : Parcelable {

    /**
     * Generate a unique registration ID
     */
    fun generateRegistrationId(): String {
        val timestamp = System.currentTimeMillis()
        return "REV2026-${name.take(3).uppercase()}-$timestamp".take(20)
    }

    /**
     * Check if basic registration is complete
     */
    fun isBasicInfoComplete(): Boolean {
        return name.isNotBlank() &&
                mobileNumber.isNotBlank() &&
                emailId.isNotBlank() &&
                collegeName.isNotBlank()
    }

    /**
     * Check if event selection is complete
     */
    fun isEventInfoComplete(): Boolean {
        return groupName.isNotBlank() && numberOfEvents > 0
    }
}