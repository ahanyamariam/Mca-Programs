package com.revelations2026.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.revelations2026.R
import com.revelations2026.data.RegistrationData
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ConfirmationActivity : AppCompatActivity() {

    // UI Components - Summary View
    private lateinit var cardSummary: MaterialCardView
    private lateinit var tvName: TextView
    private lateinit var tvMobile: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvCollege: TextView
    private lateinit var tvGroup: TextView
    private lateinit var tvEventCount: TextView
    private lateinit var btnEdit: MaterialButton
    private lateinit var btnConfirm: MaterialButton

    // UI Components - Success View
    private lateinit var successContainer: LinearLayout

    private lateinit var ivMochi: ImageView
    private lateinit var ivSuccessCheck: ImageView
    private lateinit var tvSuccessTitle: TextView
    private lateinit var tvSuccessMessage: TextView
    private lateinit var tvRegistrationId: TextView
    private lateinit var tvRegistrationDate: TextView
    private lateinit var btnNewRegistration: MaterialButton
    private lateinit var btnShare: MaterialButton
    private lateinit var konfettiView: KonfettiView

    // Data
    private lateinit var registrationData: RegistrationData
    private var registrationId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        // Get registration data
        registrationData = intent.getParcelableExtra(
            RegistrationActivity.EXTRA_REGISTRATION_DATA
        ) ?: RegistrationData()

        initializeViews()
        displaySummary()
        setupClickListeners()
        setupToolbar()
    }

    /**
     * Initialize all view references
     */
    private fun initializeViews() {
        // Summary views
        cardSummary = findViewById(R.id.card_summary)
        tvName = findViewById(R.id.tv_summary_name)
        tvMobile = findViewById(R.id.tv_summary_mobile)
        tvEmail = findViewById(R.id.tv_summary_email)
        tvCollege = findViewById(R.id.tv_summary_college)
        tvGroup = findViewById(R.id.tv_summary_group)
        tvEventCount = findViewById(R.id.tv_summary_events)
        btnEdit = findViewById(R.id.btn_edit)
        btnConfirm = findViewById(R.id.btn_confirm)
        ivMochi = findViewById(R.id.iv_mochi)

        // Success views
        successContainer = findViewById(R.id.success_container)
        ivSuccessCheck = findViewById(R.id.iv_success_check)
        tvSuccessTitle = findViewById(R.id.tv_success_title)
        tvSuccessMessage = findViewById(R.id.tv_success_message)
        tvRegistrationId = findViewById(R.id.tv_registration_id)
        tvRegistrationDate = findViewById(R.id.tv_registration_date)
        btnNewRegistration = findViewById(R.id.btn_new_registration)
        btnShare = findViewById(R.id.btn_share)
        konfettiView = findViewById(R.id.konfetti_view)

        // Initial state
        successContainer.visibility = View.GONE
    }

    /**
     * Setup toolbar
     */
    private fun setupToolbar() {
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.confirmation_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Display registration summary
     */
    private fun displaySummary() {
        tvName.text = registrationData.name
        tvMobile.text = "+91 ${registrationData.mobileNumber}"
        tvEmail.text = registrationData.emailId
        tvCollege.text = registrationData.collegeName
        tvGroup.text = registrationData.groupName
        tvEventCount.text = "${registrationData.numberOfEvents} Event${
            if (registrationData.numberOfEvents > 1) "s" else ""
        }"
    }

    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        btnEdit.setOnClickListener {
            showEditConfirmationDialog()
        }

        btnConfirm.setOnClickListener {
            showConfirmationDialog()
        }

        btnNewRegistration.setOnClickListener {
            startNewRegistration()
        }

        btnShare.setOnClickListener {
            shareRegistrationDetails()
        }
    }

    /**
     * Show edit confirmation dialog
     */
    private fun showEditConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.edit_registration))
            .setMessage(getString(R.string.edit_confirmation_message))
            .setPositiveButton(getString(R.string.yes_edit)) { _, _ ->
                // Go back to registration screen
                val intent = Intent(this, RegistrationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    /**
     * Show final confirmation dialog
     */
    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_registration))
            .setMessage(getString(R.string.final_confirmation_message))
            .setIcon(R.drawable.ic_check_circle)
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                processRegistration()
            }
            .setNegativeButton(getString(R.string.review_again), null)
            .show()
    }

    /**
     * Process the registration
     */
    private fun processRegistration() {
        // Generate registration ID
        registrationId = registrationData.generateRegistrationId()

        // Simulate processing with a small delay
        btnConfirm.isEnabled = false
        btnConfirm.text = getString(R.string.processing)

        btnConfirm.postDelayed({
            showSuccessState()
        }, 1500)
    }

    /**
     * Show success state
     */
    private fun showSuccessState() {
        // Hide summary, show success
        cardSummary.animate()
            .alpha(0f)
            .translationY(-50f)
            .setDuration(300)
            .withEndAction {
                cardSummary.visibility = View.GONE
                findViewById<View>(R.id.header_container).visibility = View.GONE
                findViewById<View>(R.id.button_container).visibility = View.GONE

                successContainer.visibility = View.VISIBLE
                animateSuccessState()
            }
            .start()

        // Set registration details
        tvRegistrationId.text = registrationId
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        tvRegistrationDate.text = dateFormat.format(Date())

        // Disable back navigation
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    /**
     * Animate success state elements
     */
    /**
     * Animate success state elements with MOCHI!
     */
    private fun animateSuccessState() {
        // Reset initial state
        ivMochi.scaleX = 0f
        ivMochi.scaleY = 0f
        ivMochi.alpha = 0f
        ivSuccessCheck.scaleX = 0f
        ivSuccessCheck.scaleY = 0f
        tvSuccessTitle.alpha = 0f
        tvSuccessMessage.alpha = 0f

        // 🎉 Animate Mochi image - bounce in with wiggle!
        val mochiScaleX = ObjectAnimator.ofFloat(ivMochi, "scaleX", 0f, 1.2f, 0.9f, 1.05f, 1f)
        val mochiScaleY = ObjectAnimator.ofFloat(ivMochi, "scaleY", 0f, 1.2f, 0.9f, 1.05f, 1f)
        val mochiAlpha = ObjectAnimator.ofFloat(ivMochi, "alpha", 0f, 1f)
        val mochiRotation = ObjectAnimator.ofFloat(ivMochi, "rotation", 0f, -15f, 15f, -10f, 10f, -5f, 5f, 0f)

        val mochiAnimSet = AnimatorSet().apply {
            playTogether(mochiScaleX, mochiScaleY, mochiAlpha)
            duration = 800
            interpolator = OvershootInterpolator(1.5f)
        }

        mochiRotation.apply {
            duration = 1200
            startDelay = 300
        }

        // Animate check icon (appears after mochi)
        val scaleX = ObjectAnimator.ofFloat(ivSuccessCheck, "scaleX", 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(ivSuccessCheck, "scaleY", 0f, 1f)
        val checkAnim = AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 500
            startDelay = 500
            interpolator = OvershootInterpolator(2f)
        }

        // Animate text
        val titleAlpha = ObjectAnimator.ofFloat(tvSuccessTitle, "alpha", 0f, 1f).apply {
            startDelay = 700
            duration = 400
        }

        val messageAlpha = ObjectAnimator.ofFloat(tvSuccessMessage, "alpha", 0f, 1f).apply {
            startDelay = 900
            duration = 400
        }

        // Start all animations
        mochiAnimSet.start()
        mochiRotation.start()
        checkAnim.start()
        titleAlpha.start()
        messageAlpha.start()

        // Show konfetti after mochi appears
        ivMochi.postDelayed({
            showKonfetti()
        }, 600)

        // Show toast
        Toast.makeText(
            this,
            getString(R.string.registration_successful),
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Show celebration konfetti
     */
    private fun showKonfetti() {
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xFFE91E63.toInt(), 0xFFF06292.toInt(),
                0xFFFF4081.toInt(), 0xFFFFD700.toInt()),
            position = Position.Relative(0.5, 0.3),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        )
        konfettiView.start(party)
    }

    /**
     * Start new registration
     */
    private fun startNewRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Share registration details
     */
    private fun shareRegistrationDetails() {
        val shareText = buildString {
            appendLine("🎉 Revelations 2026 Registration 🎉")
            appendLine()
            appendLine("Registration ID: $registrationId")
            appendLine("Name: ${registrationData.name}")
            appendLine("College: ${registrationData.collegeName}")
            appendLine("Group: ${registrationData.groupName}")
            appendLine("Events: ${registrationData.numberOfEvents}")
            appendLine()
            appendLine("See you at Revelations 2026! 🚀")
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Revelations 2026 Registration")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (successContainer.visibility == View.VISIBLE) {
            // If success is showing, go to registration
            startNewRegistration()
        } else {
            super.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}