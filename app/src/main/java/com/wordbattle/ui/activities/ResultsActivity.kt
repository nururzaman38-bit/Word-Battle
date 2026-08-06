package com.wordbattle.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.R
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.ActivityResultsBinding
import com.wordbattle.game.models.Player
import com.wordbattle.ui.adapters.RankingAdapter
import com.wordbattle.utils.Utils

class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private lateinit var userPrefs: UserPrefsManager

    private var players: List<Player> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPrefsManager(this)

        players = intent.getSerializableExtra(EXTRA_PLAYERS) as? List<Player> ?: emptyList()
        val gameId = intent.getStringExtra(EXTRA_GAME_ID) ?: ""

        setupRankings()
        setupButtons()
        updateStats()
    }

    private fun setupRankings() {
        // Sort players by rank
        val sortedPlayers = players.sortedBy { it.rank ?: Int.MAX_VALUE }

        val adapter = RankingAdapter(sortedPlayers)
        binding.rvRankings.apply {
            layoutManager = LinearLayoutManager(this@ResultsActivity)
            adapter = this@ResultsActivity.rvRankings.adapter
        }
        binding.rvRankings.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnPlayAgain.setOnClickListener {
            // Return to home with same settings
            finishAffinity()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnHome.setOnClickListener {
            finishAffinity()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun updateStats() {
        val winner = players.sortedBy { it.rank ?: Int.MAX_VALUE }.firstOrNull()
        winner?.let {
            Utils.showToast(
                this,
                getString(R.string.player_won, it.name),
                com.wordbattle.utils.ToastType.SUCCESS
            )
        }
    }

    companion object {
        const val EXTRA_GAME_ID = "game_id"
        const val EXTRA_PLAYERS = "players"
    }
}
