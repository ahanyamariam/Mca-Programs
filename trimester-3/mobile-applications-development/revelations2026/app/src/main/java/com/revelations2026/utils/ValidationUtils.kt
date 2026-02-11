package com.revelations2026.utils

import android.util.Patterns

/**
 * Utility object for input validation
 */
object ValidationUtils {

    /**
     * Validate name - must be at least 2 characters
     */
    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Name is required")
            name.length < 2 -> ValidationResult(false, "Name must be at least 2 characters")
            !name.matches(Regex("^[a-zA-Z\\s]+$")) ->
                ValidationResult(false, "Name can only contain letters")
            else -> ValidationResult(true, "")
        }
    }

    /**
     * Validate mobile number - must be 10 digits
     */
    fun validateMobile(mobile: String): ValidationResult {
        return when {
            mobile.isBlank() -> ValidationResult(false, "Mobile number is required")
            mobile.length != 10 -> ValidationResult(false, "Mobile number must be 10 digits")
            !mobile.matches(Regex("^[6-9][0-9]{9}$")) ->
                ValidationResult(false, "Enter a valid Indian mobile number")
            else -> ValidationResult(true, "")
        }
    }

    /**
     * Validate email using Android's Patterns
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult(false, "Email is required")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                ValidationResult(false, "Enter a valid email address")
            else -> ValidationResult(true, "")
        }
    }

    /**
     * Validate college name
     */
    fun validateCollegeName(collegeName: String): ValidationResult {
        return when {
            collegeName.isBlank() -> ValidationResult(false, "College name is required")
            collegeName.length < 3 ->
                ValidationResult(false, "College name must be at least 3 characters")
            else -> ValidationResult(true, "")
        }
    }

    /**
     * Data class to hold validation result
     */
    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String
    )
}