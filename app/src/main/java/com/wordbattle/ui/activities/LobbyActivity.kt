package com.wordbattle.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.R
import com.wordbattle.data.firebase.FirebaseManager
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.ActivityLobbyBinding
import com.wordbattle.game.models.Player
import com.wordbattle.game.models.Room
import com.wordbattle.ui.adapters.PlayerSlotAdapter
import com.wordbattle.utils.Utils
import kotlinx.coroutines.*

class LobbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLobbyBinding
    private lateinit var userPrefs: UserPrefsManager
    private lateinit var firebaseManager: FirebaseManager

    private var roomId: String = ""
    private var gameId: String = ""
    private var currentPlayerName: String = ""
    private var playerSlotAdapter: PlayerSlotAdapter? = null
    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPrefsManager(this)
        firebaseManager = FirebaseManager()

        roomId = intent.getStringExtra(EXTRA_ROOM_ID) ?: ""
        gameId = intent.getStringExtra(EXTRA_GAME_ID) ?: ""
        currentPlayerName = intent.getStringExtra(EXTRA_PLAYER_NAME) ?: userPrefs.currentUserName

        setupPlayersList()
        setupReadyButton()
        observeRoom()
    }

    private fun setupPlayersList() {
        playerSlotAdapter = PlayerSlotAdapter { slotIndex, readyState ->
            if (readyState && !isReady) {
                toggleReady()
            }
        }
        binding.rvPlayers.apply {
            layoutManager = LinearLayoutManager(this@LobbyActivity)
            adapter = playerSlotAdapter
        }
    }

    private fun setupReadyButton() {
        binding.btnReady.setOnClickListener {
            toggleReady()
        }
    }

    private fun toggleReady() {
        isReady = !isReady
        binding.btnReady.text = if (isReady) "Ready ✓" else getString(R.string.ready)
        binding.btnReady.setBackgroundColor(
            getColor(if (isReady) R.color.teal else R.color.gold)
        )

        // Update Firebase
        firebaseManager.makePlayerReady(roomId, userPrefs.currentUserId)

        Utils.showToast(
            this,
            if (isReady) "Ready!" else "Not ready",
            if (isReady) com.wordbattle.utils.ToastType.SUCCESS else com.wordbattle.utils.ToastType.DEFAULT
        )
    }

    private fun observeRoom() {
        firebaseManager.observeRoom(roomId) { room ->
            updateUI(room)
        }
    }

    private fun updateUI(room: Room) {
        val slots = room.slots
        val user = userPrefs.getCurrentUser()

        val playerItems = slots.mapIndexed { index, slot ->
            PlayerSlotItem(
                slotIndex = index,
                playerName = slot.filledBy?.name ?: "Empty",
                playerId = slot.filledBy?.id ?: "",
                isHost = slot.filledBy?.id == user.id,
                isReady = slot.isReady,
                type = slot.filledBy?.let {
                    if (index < room.localSlotsCount) "Local" else "Online"
                } ?: "Empty"
            )
        }

        playerSlotAdapter?.submitList(playerItems)

        // Check if all online players are ready
        val allOnlineReady = slots.filter { 
            it.filledBy?.let { playerSlotIndex -> 
                playerSlotIndex >= room.localSlotsCount 
            } == true 
        }.all { it.isReady }

        // Enable start button if all ready
        binding.btnReady.isEnabled = !isReady
    }

    override fun onResume() {
        super.onResume()
        // Check if game has started
        if (gameId.isNotEmpty()) {
            firebaseManager.observeGame(gameId) { gameState ->
                if (gameState.status == com.wordbattle.game.models.GameStatus.IN_PROGRESS) {
                    navigateToGameBoard(gameState.gameId)
                }
            }
        }
    }

    private fun navigateToGameBoard(gameStateId: String) {
        val intent = Intent(this, GameBoardActivity::class.java).apply {
            putExtra(GameBoardActivity.EXTRA_GAME_ID, gameStateId)
        }
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_ROOM_ID = "room_id"
        const val EXTRA_GAME_ID = "game_id"
        const val EXTRA_PLAYER_NAME = "player_name"
    }
}
