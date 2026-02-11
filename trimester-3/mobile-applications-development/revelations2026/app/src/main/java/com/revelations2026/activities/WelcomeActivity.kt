package com.revelations2026.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.revelations2026.R

class WelcomeActivity : AppCompatActivity() {

    // UI Components
    private lateinit var logoImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var subtitleTextView: TextView
    private lateinit var taglineTextView: TextView
    private lateinit var proceedButton: Button
    private lateinit var departmentImageView: ImageView

    // Handler for auto-navigation
    private val autoNavigateHandler = Handler(Looper.getMainLooper())
    private val autoNavigateRunnable = Runnable { navigateToRegistration() }

    // Auto navigate delay in milliseconds
    private val AUTO_NAVIGATE_DELAY = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        initializeViews()
        startAnimations()
        setupClickListeners()
        scheduleAutoNavigation()
    }

    /**
     * Initialize all view references
     */
    private fun initializeViews() {
        logoImageView = findViewById(R.id.iv_university_logo)
        titleTextView = findViewById(R.id.tv_title)
        subtitleTextView = findViewById(R.id.tv_subtitle)
        taglineTextView = findViewById(R.id.tv_tagline)
        proceedButton = findViewById(R.id.btn_proceed)
        departmentImageView = findViewById(R.id.iv_department)

        // Initially hide elements for animation
        logoImageView.alpha = 0f
        titleTextView.alpha = 0f
        subtitleTextView.alpha = 0f
        taglineTextView.alpha = 0f
        proceedButton.alpha = 0f
        departmentImageView.alpha = 0f

        // Scale down for bounce effect
        logoImageView.scaleX = 0f
        logoImageView.scaleY = 0f
    }

    /**
     * Start entrance animations
     */
    private fun startAnimations() {
        // Logo animation - scale up with bounce
        val logoScaleX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0f, 1f)
        val logoScaleY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0f, 1f)
        val logoAlpha = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f)

        val logoAnimSet = AnimatorSet().apply {
            playTogether(logoScaleX, logoScaleY, logoAlpha)
            duration = 800
            interpolator = OvershootInterpolator(1.5f)
        }

        // Title animation
        val titleAlpha = ObjectAnimator.ofFloat(titleTextView, "alpha", 0f, 1f)
        val titleTranslate = ObjectAnimator.ofFloat(titleTextView, "translationY", 50f, 0f)
        val titleAnimSet = AnimatorSet().apply {
            playTogether(titleAlpha, titleTranslate)
            duration = 600
            startDelay = 400
        }

        // Subtitle animation
        val subtitleAlpha = ObjectAnimator.ofFloat(subtitleTextView, "alpha", 0f, 1f)
        val subtitleTranslate = ObjectAnimator.ofFloat(subtitleTextView, "translationY", 50f, 0f)
        val subtitleAnimSet = AnimatorSet().apply {
            playTogether(subtitleAlpha, subtitleTranslate)
            duration = 600
            startDelay = 600
        }

        // Tagline animation
        val taglineAlpha = ObjectAnimator.ofFloat(taglineTextView, "alpha", 0f, 1f)
        taglineAlpha.apply {
            duration = 600
            startDelay = 800
        }

        // Department image animation
        val deptAlpha = ObjectAnimator.ofFloat(departmentImageView, "alpha", 0f, 0.3f)
        deptAlpha.apply {
            duration = 1000
            startDelay = 200
        }

        // Proceed button animation
        val buttonAlpha = ObjectAnimator.ofFloat(proceedButton, "alpha", 0f, 1f)
        val buttonScale = ObjectAnimator.ofFloat(proceedButton, "scaleX", 0.8f, 1f)
        val buttonAnimSet = AnimatorSet().apply {
            playTogether(buttonAlpha, buttonScale)
            duration = 500
            startDelay = 1200
            interpolator = AccelerateDecelerateInterpolator()
        }

        // Play all animations
        logoAnimSet.start()
        titleAnimSet.start()
        subtitleAnimSet.start()
        taglineAlpha.start()
        deptAlpha.start()
        buttonAnimSet.start()
    }

    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        proceedButton.setOnClickListener {
            // Cancel auto-navigation
            autoNavigateHandler.removeCallbacks(autoNavigateRunnable)

            // Animate button press
            it.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction { navigateToRegistration() }
                        .start()
                }
                .start()
        }

        // Allow clicking anywhere on screen to proceed
        findViewById<View>(R.id.welcome_root).setOnClickListener {
            autoNavigateHandler.removeCallbacks(autoNavigateRunnable)
            navigateToRegistration()
        }
    }

    /**
     * Schedule automatic navigation
     */
    private fun scheduleAutoNavigation() {
        autoNavigateHandler.postDelayed(autoNavigateRunnable, AUTO_NAVIGATE_DELAY)
    }

    /**
     * Navigate to Registration Screen
     */
    private fun navigateToRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)

        // Custom transition animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        // Finish this activity so user can't go back to splash
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove any pending callbacks
        autoNavigateHandler.removeCallbacks(autoNavigateRunnable)
    }
}