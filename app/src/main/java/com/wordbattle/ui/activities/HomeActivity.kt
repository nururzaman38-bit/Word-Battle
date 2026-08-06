package com.wordbattle.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wordbattle.R
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userPrefs: UserPrefsManager

    private var selectedMode: GameMode = GameMode.COMPUTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPrefsManager(this)
        setupTopBar()
        setupModeSelection()
        setupPlayButton()
        setupQuickActions()
    }

    private fun setupTopBar() {
        val user = userPrefs.getCurrentUser()
        binding.tvUserName.text = user.name
    }

    private fun setupModeSelection() {
        binding.cardComputer.setOnClickListener {
            selectMode(GameMode.COMPUTER, binding.cardComputer)
        }

        binding.card2player.setOnClickListener {
            selectMode(GameMode.LOCAL, binding.card2player)
        }

        binding.card3player.setOnClickListener {
            selectMode(GameMode.LOCAL, binding.card3player)
        }

        binding.card4player.setOnClickListener {
            selectMode(GameMode.LOCAL, binding.card4player)
        }

        // Default selection
        selectMode(GameMode.COMPUTER, binding.cardComputer)
    }

    private fun selectMode(mode: GameMode, card: androidx.cardview.widget.CardView) {
        selectedMode = mode

        // Reset all cards
        resetCardBorder(binding.cardComputer)
        resetCardBorder(binding.card2player)
        resetCardBorder(binding.card3player)
        resetCardBorder(binding.card4player)

        // Set selected border
        card.strokeColor = ContextCompat.getColor(this, R.color.gold)
        card.strokeWidth = 4
    }

    private fun resetCardBorder(card: androidx.cardview.widget.CardView) {
        card.strokeColor = ContextCompat.getColor(this, android.R.color.transparent)
        card.strokeWidth = 0
    }

    private fun setupPlayButton() {
        binding.btnPlay.setOnClickListener {
            when (selectedMode) {
                GameMode.COMPUTER -> {
                    // Start game immediately with computer
                    navigateToGameBoard(GameMode.COMPUTER)
                }
                GameMode.LOCAL -> {
                    // Go to assignment screen
                    val playerCount = when (selectedMode) {
                        GameMode.LOCAL -> {
                            if (binding.card2player.strokeColor == ContextCompat.getColor(this, R.color.gold)) 2
                            else if (binding.card3player.strokeColor == ContextCompat.getColor(this, R.color.gold)) 3
                            else 4
                        }
                        else -> 2
                    }
                    navigateToAssignment(playerCount)
                }
                GameMode.MIXED_ONLINE -> {
                    // Go to assignment screen
                    navigateToAssignment(4)
                }
            }
        }
    }

    private fun setupQuickActions() {
        binding.cardJoinRoom.setOnClickListener {
            navigateToJoinRoom()
        }

        binding.cardCreateRoom.setOnClickListener {
            navigateToRoomSetup()
        }
    }

    private fun navigateToGameBoard(mode: GameMode) {
        val intent = Intent(this, GameBoardActivity::class.java).apply {
            putExtra(GameBoardActivity.EXTRA_MODE, mode.name)
        }
        startActivity(intent)
    }

    private fun navigateToAssignment(playerCount: Int) {
        val intent = Intent(this, AssignmentActivity::class.java).apply {
            putExtra(AssignmentActivity.EXTRA_PLAYER_COUNT, playerCount)
        }
        startActivity(intent)
    }

    private fun navigateToJoinRoom() {
        val intent = Intent(this, JoinRoomActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToRoomSetup() {
        val intent = Intent(this, RoomSetupActivity::class.java)
        startActivity(intent)
    }
}
