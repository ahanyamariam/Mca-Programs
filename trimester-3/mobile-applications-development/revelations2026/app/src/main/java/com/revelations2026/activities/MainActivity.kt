package com.revelations2026.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        

        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)

        finish()
    }
}
