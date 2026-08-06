package com.wordbattle.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.R
import com.wordbattle.data.firebase.FirebaseManager
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.ActivityAssignmentBinding
import com.wordbattle.game.models.Player
import com.wordbattle.game.models.PlayerType
import com.wordbattle.game.models.Room
import com.wordbattle.ui.adapters.SlotAssignmentAdapter
import com.wordbattle.utils.Utils

class AssignmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssignmentBinding
    private lateinit var userPrefs: UserPrefsManager
    private lateinit var firebaseManager: FirebaseManager

    private var playerCount: Int = 2
    private var slotAdapter: SlotAssignmentAdapter? = null
    private var slotAssignments: MutableList<Boolean> = mutableListOf() // true = online, false = local

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPrefsManager(this)
        firebaseManager = FirebaseManager()

        playerCount = intent.getIntExtra(EXTRA_PLAYER_COUNT, 2)

        slotAssignments = MutableList(playerCount) { index -> index == 0 } // First slot is always local

        setupSlots()
        setupConfirmButton()
    }

    private fun setupSlots() {
        slotAdapter = SlotAssignmentAdapter(
            playerCount = playerCount,
            assignments = slotAssignments,
            onToggle = { index, isOnline ->
                if (index > 0) { // Can't change first slot (self)
                    slotAssignments[index] = isOnline
                    slotAdapter?.updateAssignments(slotAssignments)
                }
            }
        )

        binding.rvSlots.apply {
            layoutManager = LinearLayoutManager(this@AssignmentActivity)
            adapter = slotAdapter
        }
    }

    private fun setupConfirmButton() {
        binding.btnConfirm.setOnClickListener {
            val hasOnlineSlots = slotAssignments.any { it }
            val allLocal = !hasOnlineSlots

            if (allLocal) {
                // Start game directly - pass and play
                startLocalGame()
            } else {
                // Create online room
                createOnlineRoom()
            }
        }
    }

    private fun startLocalGame() {
        // Create local game with specified player count
        val players = mutableListOf<Player>()

        val user = userPrefs.getCurrentUser()
        players.add(Player(
            id = user.id,
            name = user.name,
            type = PlayerType.HUMAN_LOCAL,
            turnOrder = 0,
            avatarColor = user.avatarColor
        ))

        // Add additional local players
        for (i in 1 until playerCount) {
            players.add(Player(
                id = "local_$i",
                name = "Player ${i + 1}",
                type = PlayerType.HUMAN_LOCAL,
                turnOrder = i,
                avatarColor = com.wordbattle.utils.Utils.getRandomAvatarColor()
            ))
        }

        val gameState = com.wordbattle.game.models.GameState(
            gameId = generateGameId(),
            mode = com.wordbattle.game.models.GameMode.LOCAL,
            players = players,
            currentTurnPlayerId = players.first().id,
            status = com.wordbattle.game.models.GameStatus.IN_PROGRESS
        )

        // Navigate to game board
        val intent = Intent(this, GameBoardActivity::class.java).apply {
            putExtra(GameBoardActivity.EXTRA_GAME_ID, gameState.gameId)
        }
        startActivity(intent)
        finish()
    }

    private fun createOnlineRoom() {
        // Generate room
        val roomId = generateRoomId()
        val passcode = (1000..9999).random().toString()

        val localSlots = slotAssignments.count { !it }
        val onlineSlots = slotAssignments.count { it }

        val slots = (0 until playerCount).map { index ->
            com.wordbattle.game.models.RoomSlot(
                slotIndex = index,
                filledBy = if (index == 0) {
                    Player(
                        id = userPrefs.currentUserId,
                        name = userPrefs.currentUserName,
                        type = PlayerType.HUMAN_LOCAL
                    )
                } else null,
                isReady = false
            )
        }

        val room = Room(
            roomId = roomId,
            passcode = passcode,
            hostPlayerId = userPrefs.currentUserId,
            totalSlots = playerCount,
            localSlotsCount = localSlots,
            onlineSlotsCount = onlineSlots,
            slots = slots
        )

        // Save room to Firebase
        firebaseManager.createRoom(
            hostPlayerId = userPrefs.currentUserId,
            hostPlayerName = userPrefs.currentUserName,
            totalSlots = playerCount,
            localSlotsCount = localSlots,
            onlineSlotsCount = onlineSlots
        )

        // Navigate to room setup
        val intent = Intent(this, RoomSetupActivity::class.java).apply {
            putExtra(RoomSetupActivity.EXTRA_ROOM_ID, roomId)
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

    companion object {
        const val EXTRA_PLAYER_COUNT = "player_count"
    }
}
