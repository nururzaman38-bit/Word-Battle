package com.wordbattle.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wordbattle.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Profile is handled by fragment in MainActivity
        finish()
    }
}
