package com.revelations2026.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import com.revelations2026.R
import com.revelations2026.data.RegistrationData

/**
 * Event Selection Screen Activity
 * Allows user to select group and number of events
 */
class EventSelectionActivity : AppCompatActivity() {

    // UI Components
    private lateinit var spinnerGroup: Spinner
    private lateinit var chipGroupEvents: ChipGroup
    private lateinit var sliderEvents: Slider
    private lateinit var tvEventCount: TextView
    private lateinit var tvSelectedGroup: TextView
    private lateinit var btnContinue: MaterialButton
    private lateinit var btnBack: MaterialButton
    private lateinit var cardGroupInfo: MaterialCardView
    private lateinit var tvGroupDescription: TextView

    // Data
    private lateinit var registrationData: RegistrationData
    private var selectedGroup: String = ""
    private var numberOfEvents: Int = 1

    // Group data
    private val groupList = listOf(
        "Select a Group",
        "Technical Events",
        "Cultural Events",
        "Sports Events",
        "Literary Events",
        "Art & Design Events",
        "Gaming Events"
    )

    private val groupDescriptions = mapOf(
        "Technical Events" to "Coding, Hackathons, Robotics, Tech Quiz",
        "Cultural Events" to "Dance, Music, Drama, Fashion Show",
        "Sports Events" to "Cricket, Football, Basketball, Athletics",
        "Literary Events" to "Debate, Elocution, Creative Writing, Poetry",
        "Art & Design Events" to "Painting, Photography, Poster Making",
        "Gaming Events" to "BGMI, Valorant, FIFA, Chess"
    )

    private val maxEventsByGroup = mapOf(
        "Technical Events" to 4,
        "Cultural Events" to 5,
        "Sports Events" to 3,
        "Literary Events" to 4,
        "Art & Design Events" to 3,
        "Gaming Events" to 4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_selection)

        // Get registration data from previous screen
        registrationData = intent.getParcelableExtra(
            RegistrationActivity.EXTRA_REGISTRATION_DATA
        ) ?: RegistrationData()

        initializeViews()
        setupSpinner()
        setupEventSlider()
        setupQuickSelectChips()
        setupClickListeners()
        setupToolbar()
    }

    /**
     * Initialize all view references
     */
    private fun initializeViews() {
        spinnerGroup = findViewById(R.id.spinner_group)
        chipGroupEvents = findViewById(R.id.chip_group_quick_select)
        sliderEvents = findViewById(R.id.slider_events)
        tvEventCount = findViewById(R.id.tv_event_count)
        tvSelectedGroup = findViewById(R.id.tv_selected_group)
        btnContinue = findViewById(R.id.btn_continue)
        btnBack = findViewById(R.id.btn_back)
        cardGroupInfo = findViewById(R.id.card_group_info)
        tvGroupDescription = findViewById(R.id.tv_group_description)

        // Initial state
        btnContinue.isEnabled = false
        cardGroupInfo.visibility = View.GONE
    }

    /**
     * Setup toolbar
     */
    private fun setupToolbar() {
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.event_selection_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Setup group selection spinner
     */
    private fun setupSpinner() {
        val adapter = object : ArrayAdapter<String>(
            this,
            R.layout.item_spinner,
            groupList
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0 // Disable first item (hint)
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(resources.getColor(R.color.text_hint, theme))
                } else {
                    view.setTextColor(resources.getColor(R.color.text_primary, theme))
                }
                return view
            }
        }

        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        spinnerGroup.adapter = adapter

        spinnerGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    selectedGroup = groupList[position]
                    onGroupSelected(selectedGroup)
                } else {
                    selectedGroup = ""
                    cardGroupInfo.visibility = View.GONE
                    updateContinueButton()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedGroup = ""
            }
        }
    }

    /**
     * Handle group selection
     */
    private fun onGroupSelected(group: String) {
        // Update selected group display
        tvSelectedGroup.text = group

        // Show group info card
        cardGroupInfo.visibility = View.VISIBLE
        tvGroupDescription.text = groupDescriptions[group] ?: ""

        // Update slider max value based on group
        val maxEvents = maxEventsByGroup[group] ?: 4
        sliderEvents.valueTo = maxEvents.toFloat()
        sliderEvents.value = 1f
        numberOfEvents = 1
        tvEventCount.text = "1"

        // Update quick select chips
        updateQuickSelectChips(maxEvents)

        // Animate card entry
        cardGroupInfo.alpha = 0f
        cardGroupInfo.animate()
            .alpha(1f)
            .setDuration(300)
            .start()

        updateContinueButton()

        Toast.makeText(
            this,
            "Selected: $group",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Setup event count slider
     */
    private fun setupEventSlider() {
        sliderEvents.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                numberOfEvents = value.toInt()
                tvEventCount.text = numberOfEvents.toString()

                // Update chip selection
                updateChipSelection(numberOfEvents)
            }
        }

        sliderEvents.setLabelFormatter { value ->
            "${value.toInt()} Event${if (value > 1) "s" else ""}"
        }
    }

    /**
     * Setup quick select chips
     */
    private fun setupQuickSelectChips() {
        chipGroupEvents.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip = findViewById<Chip>(checkedIds[0])
                val count = chip?.text?.toString()?.toIntOrNull()
                if (count != null) {
                    numberOfEvents = count
                    sliderEvents.value = count.toFloat()
                    tvEventCount.text = count.toString()
                }
            }
        }
    }

    /**
     * Update quick select chips based on max events
     */
    private fun updateQuickSelectChips(maxEvents: Int) {
        chipGroupEvents.removeAllViews()

        for (i in 1..maxEvents) {
            val chip = Chip(this).apply {
                text = i.toString()
                isCheckable = true
                isCheckedIconVisible = false
                chipBackgroundColor = resources.getColorStateList(
                    R.color.chip_background_selector, theme
                )
                setTextColor(resources.getColorStateList(
                    R.color.chip_text_selector, theme
                ))
                chipStrokeColor = resources.getColorStateList(
                    R.color.chip_stroke_selector, theme
                )
                chipStrokeWidth = 2f
            }
            chipGroupEvents.addView(chip)

            // Select first chip by default
            if (i == 1) chip.isChecked = true
        }
    }

    /**
     * Update chip selection based on slider value
     */
    private fun updateChipSelection(value: Int) {
        for (i in 0 until chipGroupEvents.childCount) {
            val chip = chipGroupEvents.getChildAt(i) as? Chip
            if (chip?.text?.toString()?.toIntOrNull() == value) {
                chip.isChecked = true
            }
        }
    }

    /**
     * Update continue button state
     */
    private fun updateContinueButton() {
        val isValid = selectedGroup.isNotEmpty() && numberOfEvents > 0
        btnContinue.isEnabled = isValid
        btnContinue.alpha = if (isValid) 1.0f else 0.5f
    }

    /**
     * Setup click listeners
     */
    private fun setupClickListeners() {
        btnContinue.setOnClickListener {
            if (validateSelection()) {
                // Update registration data
                registrationData.groupName = selectedGroup
                registrationData.numberOfEvents = numberOfEvents

                Toast.makeText(
                    this,
                    getString(R.string.event_selection_saved),
                    Toast.LENGTH_SHORT
                ).show()

                navigateToConfirmation()
            }
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    /**
     * Validate selection
     */
    private fun validateSelection(): Boolean {
        if (selectedGroup.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.please_select_group),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (numberOfEvents < 1) {
            Toast.makeText(
                this,
                getString(R.string.please_select_events),
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    /**
     * Navigate to Confirmation Screen
     */
    private fun navigateToConfirmation() {
        val intent = Intent(this, ConfirmationActivity::class.java).apply {
            putExtra(RegistrationActivity.EXTRA_REGISTRATION_DATA, registrationData)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}