package com.wordbattle.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wordbattle.R
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.ActivitySetupBinding
import com.wordbattle.game.models.GameMode

class SetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetupBinding
    private lateinit var userPrefs: UserPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPrefsManager(this)

        setupPlayerCountSelection()
        setupNextButton()
    }

    private fun setupPlayerCountSelection() {
        binding.rgPlayerCount.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_2players -> {
                    // 2 players selected
                }
                R.id.rb_3players -> {
                    // 3 players selected
                }
                R.id.rb_4players -> {
                    // 4 players selected
                }
            }
        }
    }

    private fun setupNextButton() {
        binding.btnNext.setOnClickListener {
            val playerCount = when (binding.rgPlayerCount.checkedRadioButtonId) {
                R.id.rb_2players -> 2
                R.id.rb_3players -> 3
                R.id.rb_4players -> 4
                else -> 2
            }

            navigateToAssignment(playerCount)
        }
    }

    private fun navigateToAssignment(playerCount: Int) {
        val intent = Intent(this, AssignmentActivity::class.java).apply {
            putExtra(AssignmentActivity.EXTRA_PLAYER_COUNT, playerCount)
        }
        startActivity(intent)
        finish()
    }
}
