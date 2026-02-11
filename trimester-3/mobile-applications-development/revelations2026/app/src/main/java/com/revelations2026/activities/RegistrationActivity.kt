package com.revelations2026.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.android.material.appbar.MaterialToolbar
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.revelations2026.R
import com.revelations2026.data.RegistrationData
import com.revelations2026.utils.ValidationUtils

/**
 * Registration Screen Activity
 * Collects user's basic information
 */
class RegistrationActivity : AppCompatActivity() {
    private lateinit var toolbar: MaterialToolbar

    // UI Components - Input Fields
    private lateinit var tilName: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var tilMobile: TextInputLayout
    private lateinit var etMobile: TextInputEditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var tilCollege: TextInputLayout
    private lateinit var etCollege: TextInputEditText

    // UI Components - Buttons and Progress
    private lateinit var btnNext: Button
    private lateinit var btnClear: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgress: TextView

    // Data object
    private lateinit var registrationData: RegistrationData

    // Track form completion
    private var completedFields = 0
    private val totalFields = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize data object
        registrationData = RegistrationData()

        initializeViews()
        setupToolbar()
        setupTextWatchers()
        setupClickListeners()
        setupToolbar()
    }

    /**
     * Initialize all view references
     */
    private fun initializeViews() {
        toolbar = findViewById(R.id.toolbar)
        // Name field
        tilName = findViewById(R.id.til_name)
        etName = findViewById(R.id.et_name)

        // Mobile field
        tilMobile = findViewById(R.id.til_mobile)
        etMobile = findViewById(R.id.et_mobile)

        // Email field
        tilEmail = findViewById(R.id.til_email)
        etEmail = findViewById(R.id.et_email)

        // College field
        tilCollege = findViewById(R.id.til_college)
        etCollege = findViewById(R.id.et_college)

        // Buttons and progress
        btnNext = findViewById(R.id.btn_next)
        btnClear = findViewById(R.id.btn_clear)
        progressBar = findViewById(R.id.progress_bar)
        tvProgress = findViewById(R.id.tv_progress)

        // Initial state
        updateProgress()
    }

    /**
     * Setup toolbar with back navigation
     */
    private fun setupToolbar() {
        // Set toolbar as action bar
        setSupportActionBar(toolbar)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Handle navigation click
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Setup text watchers for real-time validation
     */
    private fun setupTextWatchers() {
        // Name field watcher
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateNameField(showError = false)
                updateProgress()
            }
        })

        // Name field focus change for error display
        etName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateNameField(showError = true)
            }
        }

        // Mobile field watcher
        etMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateMobileField(showError = false)
                updateProgress()
            }
        })

        etMobile.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateMobileField(showError = true)
            }
        }

        // Email field watcher
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateEmailField(showError = false)
                updateProgress()
            }
        })

        etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmailField(showError = true)
            }
        }

        // College field watcher
        etCollege.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateCollegeField(showError = false)
                updateProgress()
            }
        })

        etCollege.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateCollegeField(showError = true)
            }
        }
    }

    /**
     * Validate name field
     */
    private fun validateNameField(showError: Boolean): Boolean {
        val result = ValidationUtils.validateName(etName.text.toString().trim())
        if (showError && !result.isValid) {
            tilName.error = result.errorMessage
            tilName.isErrorEnabled = true
        } else {
            tilName.error = null
            tilName.isErrorEnabled = false
        }
        return result.isValid
    }

    /**
     * Validate mobile field
     */
    private fun validateMobileField(showError: Boolean): Boolean {
        val result = ValidationUtils.validateMobile(etMobile.text.toString().trim())
        if (showError && !result.isValid) {
            tilMobile.error = result.errorMessage
            tilMobile.isErrorEnabled = true
        } else {
            tilMobile.error = null
            tilMobile.isErrorEnabled = false
        }
        return result.isValid
    }

    /**
     * Validate email field
     */
    private fun validateEmailField(showError: Boolean): Boolean {
        val result = ValidationUtils.validateEmail(etEmail.text.toString().trim())
        if (showError && !result.isValid) {
            tilEmail.error = result.errorMessage
            tilEmail.isErrorEnabled = true
        } else {
            tilEmail.error = null
            tilEmail.isErrorEnabled = false
        }
        return result.isValid
    }

    /**
     * Validate college field
     */
    private fun validateCollegeField(showError: Boolean): Boolean {
        val result = ValidationUtils.validateCollegeName(etCollege.text.toString().trim())
        if (showError && !result.isValid) {
            tilCollege.error = result.errorMessage
            tilCollege.isErrorEnabled = true
        } else {
            tilCollege.error = null
            tilCollege.isErrorEnabled = false
        }
        return result.isValid
    }

    /**
     * Validate all fields
     */
    private fun validateAllFields(): Boolean {
        val isNameValid = validateNameField(showError = true)
        val isMobileValid = validateMobileField(showError = true)
        val isEmailValid = validateEmailField(showError = true)
        val isCollegeValid = validateCollegeField(showError = true)

        return isNameValid && isMobileValid && isEmailValid && isCollegeValid
    }

    /**
     * Update progress indicator
     */
    private fun updateProgress() {
        completedFields = 0

        if (ValidationUtils.validateName(etName.text.toString().trim()).isValid) completedFields++
        if (ValidationUtils.validateMobile(etMobile.text.toString().trim()).isValid) completedFields++
        if (ValidationUtils.validateEmail(etEmail.text.toString().trim()).isValid) completedFields++
        if (ValidationUtils.validateCollegeName(etCollege.text.toString().trim()).isValid) completedFields++

        // Update progress bar
        val progress = (completedFields * 100) / totalFields
        progressBar.progress = progress

        // Update progress text
        tvProgress.text = getString(R.string.progress_format, completedFields, totalFields)

        // Update button state
        btnNext.isEnabled = completedFields == totalFields
        btnNext.alpha = if (completedFields == totalFields) 1.0f else 0.5f
    }

    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        // Next button
        btnNext.setOnClickListener {
            if (validateAllFields()) {
                // Populate data object
                registrationData.apply {
                    name = etName.text.toString().trim()
                    mobileNumber = etMobile.text.toString().trim()
                    emailId = etEmail.text.toString().trim()
                    collegeName = etCollege.text.toString().trim()
                }

                // Show success toast
                Toast.makeText(
                    this,
                    getString(R.string.basic_info_saved),
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate to Event Selection
                navigateToEventSelection()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.please_fill_all_fields),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Clear button
        btnClear.setOnClickListener {
            clearAllFields()
        }
    }

    /**
     * Clear all input fields
     */
    private fun clearAllFields() {
        etName.text?.clear()
        etMobile.text?.clear()
        etEmail.text?.clear()
        etCollege.text?.clear()

        // Clear errors
        tilName.error = null
        tilMobile.error = null
        tilEmail.error = null
        tilCollege.error = null

        // Reset focus
        etName.requestFocus()

        Toast.makeText(this, getString(R.string.form_cleared), Toast.LENGTH_SHORT).show()
    }

    /**
     * Navigate to Event Selection Screen
     */
    private fun navigateToEventSelection() {
        val intent = Intent(this, EventSelectionActivity::class.java).apply {
            putExtra(EXTRA_REGISTRATION_DATA, registrationData)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
    companion object {
        const val EXTRA_REGISTRATION_DATA = "extra_registration_data"
    }
}