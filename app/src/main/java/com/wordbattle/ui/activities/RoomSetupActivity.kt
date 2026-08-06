package com.wordbattle.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.wordbattle.R
import com.wordbattle.data.firebase.FirebaseManager
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.ActivityRoomSetupBinding
import com.wordbattle.game.models.Player
import com.wordbattle.game.models.PlayerType
import com.wordbattle.game.models.Room
import com.wordbattle.ui.adapters.PlayerSlotAdapter
import com.wordbattle.utils.Utils
import kotlinx.coroutines.*

class RoomSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomSetupBinding
    private lateinit var userPrefs: UserPrefsManager
    private lateinit var firebaseManager: FirebaseManager

    private var room: Room? = null
    private var playerSlotAdapter: PlayerSlotAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPrefsManager(this)
        firebaseManager = FirebaseManager()

        setupRoomId()
        setupPlayersList()
        setupButtons()
    }

    private fun setupRoomId() {
        // Generate random room ID
        val roomId = generateRoomId()
        val passcode = (1000..9999).random().toString()

        // Display room ID as tiles
        binding.layoutRoomId.removeAllViews()
        roomId.forEach { char ->
            val tile = layoutInflater.inflate(R.layout.item_letter_tile, binding.layoutRoomId, false) as android.widget.FrameLayout
            val tileView = tile.findViewById<com.wordbattle.ui.components.LetterTileView>(R.id.tile_view)
            tileView?.setLetter(char.toString())
            tileView?.setGold(true)
            binding.layoutRoomId.addView(tile)
        }

        binding.tvPasscode.text = passcode

        // Store room info
        room = Room(
            roomId = roomId,
            passcode = passcode,
            hostPlayerId = userPrefs.currentUserId,
            totalSlots = 4,
            localSlotsCount = 2,
            onlineSlotsCount = 2,
            slots = List(4) { index ->
                com.wordbattle.game.models.RoomSlot(
                    slotIndex = index,
                    filledBy = if (index == 0) {
                        Player(
                            id = userPrefs.currentUserId,
                            name = userPrefs.currentUserName,
                            type = PlayerType.HUMAN_LOCAL
                        )
                    } else null
                )
            }
        )
    }

    private fun setupPlayersList() {
        playerSlotAdapter = PlayerSlotAdapter(
            onReadyToggle = { slotIndex, isReady ->
                // Handle ready toggle
            }
        )
        binding.rvPlayers.apply {
            layoutManager = LinearLayoutManager(this@RoomSetupActivity)
            adapter = playerSlotAdapter
        }
        updatePlayersList()
    }

    private fun updatePlayersList() {
        val slots = room?.slots ?: return
        val user = userPrefs.getCurrentUser()

        val players = slots.mapIndexed { index, slot ->
            PlayerSlotItem(
                slotIndex = index,
                playerName = slot.filledBy?.name ?: "Empty",
                playerId = slot.filledBy?.id ?: "",
                isHost = slot.filledBy?.id == user.id,
                isReady = slot.isReady,
                type = slot.filledBy?.let {
                    if (index < room?.localSlotsCount ?: 2) "Local" else "Online"
                } ?: "Empty"
            )
        }

        playerSlotAdapter?.submitList(players)

        // Check if all online players are ready
        val allOnlineReady = slots.filter { 
            it.filledBy?.let { index -> 
                index >= room?.localSlotsCount ?: 2 
            } == true 
        }.all { it.isReady }

        binding.btnStart.isEnabled = allOnlineReady && slots.any { !it.filledBy.isNullOrEmpty() }
    }

    private fun setupButtons() {
        binding.btnShare.setOnClickListener {
            shareRoom()
        }

        binding.btnStart.setOnClickListener {
            startGame()
        }
    }

    private fun shareRoom() {
        val roomId = room?.roomId ?: return
        val passcode = room?.passcode ?: return

        val shareText = "Join my Word Battle room!\n\nRoom ID: $roomId\nPasscode: $passcode\n\nDownload Word Battle to play!"

        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_room)))
        } catch (e: Exception) {
            Utils.showToast(this, "Unable to share", com.wordbattle.utils.ToastType.ERROR)
        }
    }

    private fun startGame() {
        // Create game state and redirect to lobby
        val gameStateId = generateGameId()

        val players = room?.slots?.mapNotNull { slot ->
            slot.filledBy
        } ?: return

        val gameState = com.wordbattle.game.models.GameState(
            gameId = gameId,
            mode = com.wordbattle.game.models.GameMode.MIXED_ONLINE,
            players = players.mapIndexed { index, player ->
                player.copy(turnOrder = index)
            },
            currentTurnPlayerId = players.first().id,
            status = com.wordbattle.game.models.GameStatus.LOBBY
        )

        // Save game state to Firebase
        val db = Firebase.database
        db.getReference("games/$gameStateId").setValue(gameState)

        // Update room with game state ID
        room?.let { currentRoom ->
            db.getReference("rooms/${currentRoom.roomId}").child("gameStateId")
                .setValue(gameStateId)
        }

        // Navigate to lobby
        val intent = Intent(this, LobbyActivity::class.java).apply {
            putExtra(LobbyActivity.EXTRA_ROOM_ID, room?.roomId)
            putExtra(LobbyActivity.EXTRA_GAME_ID, gameStateId)
        }
        startActivity(intent)
        finish()
    }

    private fun generateRoomId(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..4).map { chars.random() }.joinToString("")
    }

    private fun generateGameId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8).map { chars.random() }.joinToString("")
    }
}

data class PlayerSlotItem(
    val slotIndex: Int,
    val playerName: String,
    val playerId: String,
    val isHost: Boolean,
    val isReady: Boolean,
    val type: String
)
