package com.wordbattle.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.wordbattle.R.layout.activity_profile)
        
        // Settings are handled by fragment in MainActivity
        finish()
    }
}
