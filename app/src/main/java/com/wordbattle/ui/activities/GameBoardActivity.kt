package com.wordbattle.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.R
import com.wordbattle.data.firebase.FirebaseManager
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.ActivityGameBoardBinding
import com.wordbattle.game.logic.GameLogic
import com.wordbattle.game.logic.PlacementResult
import com.wordbattle.game.models.*
import com.wordbattle.ui.adapters.BoardAdapter
import com.wordbattle.ui.adapters.PlayerHeaderAdapter
import com.wordbattle.ui.adapters.RackAdapter
import com.wordbattle.utils.Utils
import kotlinx.coroutines.*

class GameBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBoardBinding
    private lateinit var userPrefs: UserPrefsManager
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var gameLogic: GameLogic

    private var gameState: GameState? = null
    private var currentPlayerId: String = ""
    private var gameId: String = ""
    private var databaseListener: DatabaseListener? = null

    private lateinit var boardAdapter: BoardAdapter
    private lateinit var rackAdapter: RackAdapter

    private var selectedLetter: String? = null
    private var isProcessingMove: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPrefsManager(this)
        firebaseManager = FirebaseManager()
        gameLogic = GameLogic()

        gameId = intent.getStringExtra(EXTRA_GAME_ID) ?: generateGameId()
        currentPlayerId = userPrefs.currentUserId

        setupBoard()
        setupRack()
        setupPlayersHeader()

        loadGameState()
    }

    private fun setupBoard() {
        boardAdapter = BoardAdapter(15, 15) { row, col ->
            onCellClicked(row, col)
        }
        binding.rvBoard.apply {
            layoutManager = GridLayoutManager(this@GameBoardActivity, 15)
            adapter = boardAdapter
        }
    }

    private fun setupRack() {
        rackAdapter = RackAdapter { letter ->
            onLetterTapped(letter)
        }
        binding.scrollRack.childView.apply {
            layoutManager = LinearLayoutManager(this@GameBoardActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = rackAdapter
        }
        updateRack()
    }

    private fun setupPlayersHeader() {
        // Setup player headers
        updatePlayerHeaders()
    }

    private fun loadGameState() {
        // For demo, create a local game
        if (intent.hasExtra(EXTRA_MODE)) {
            val modeName = intent.getStringExtra(EXTRA_MODE) ?: "COMPUTER"
            val mode = try {
                GameMode.valueOf(modeName)
            } catch (e: Exception) {
                GameMode.COMPUTER
            }
            createLocalGame(mode)
        } else {
            // Load from Firebase
            loadGameFromFirebase()
        }
    }

    private fun createLocalGame(mode: GameMode) {
        val players = mutableListOf<Player>()
        val user = userPrefs.getCurrentUser()

        players.add(Player(
            id = user.id,
            name = user.name,
            type = PlayerType.HUMAN_LOCAL,
            turnOrder = 0,
            avatarColor = user.avatarColor
        ))

        if (mode == GameMode.COMPUTER) {
            players.add(Player(
                id = "computer",
                name = "Computer",
                type = PlayerType.COMPUTER,
                turnOrder = 1,
                avatarColor = 0xFF5B1E8C.toLong()
            ))
        }

        gameState = GameState(
            gameId = gameId,
            mode = mode,
            players = players,
            currentTurnPlayerId = players.first().id,
            status = GameStatus.IN_PROGRESS
        )

        updateUI()
        startGameLoop()
    }

    private fun loadGameFromFirebase() {
        databaseListener = firebaseManager.observeGame(gameId) { state ->
            gameState = state
            updateUI()
        }
    }

    private fun updateUI() {
        gameState?.let { state ->
            updateBoard(state)
            updatePlayerHeaders()
            updateTurnIndicator(state)
            updateRack()
        }
    }

    private fun updateBoard(state: GameState) {
        val board = state.board
        boardAdapter.updateBoard(board.cells)
    }

    private fun updatePlayerHeaders() {
        gameState?.let { state ->
            state.players.forEachIndexed { index, player ->
                when (index) {
                    0 -> {
                        binding.tvPlayer1Name.text = player.name
                        binding.tvPlayer1Score.text = player.score.toString()
                        binding.indicator1.visibility = if (player.id == state.currentTurnPlayerId) View.VISIBLE else View.INVISIBLE
                        binding.player1Header.alpha = if (player.id == state.currentTurnPlayerId) 1.0f else 0.6f
                    }
                    1 -> {
                        binding.player2Header.visibility = View.VISIBLE
                        binding.tvPlayer2Name.text = player.name
                        binding.tvPlayer2Score.text = player.score.toString()
                        binding.indicator2.visibility = if (player.id == state.currentTurnPlayerId) View.VISIBLE else View.INVISIBLE
                        binding.player2Header.alpha = if (player.id == state.currentTurnPlayerId) 1.0f else 0.6f
                    }
                    2 -> {
                        binding.player3Header.visibility = View.VISIBLE
                        binding.tvPlayer3Name.text = player.name
                        binding.tvPlayer3Score.text = player.score.toString()
                        binding.indicator3.visibility = if (player.id == state.currentTurnPlayerId) View.VISIBLE else View.INVISIBLE
                    }
                    3 -> {
                        binding.player4Header.visibility = View.VISIBLE
                        binding.tvPlayer4Name.text = player.name
                        binding.tvPlayer4Score.text = player.score.toString()
                        binding.indicator4.visibility = if (player.id == state.currentTurnPlayerId) View.VISIBLE else View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun updateTurnIndicator(state: GameState) {
        val currentPlayer = state.players.find { it.id == state.currentTurnPlayerId }
        val isMyTurn = state.currentTurnPlayerId == currentPlayerId

        if (isMyTurn) {
            binding.tvTurnIndicator.text = getString(R.string.your_turn)
            binding.tvTurnIndicator.visibility = View.VISIBLE
        } else if (currentPlayer != null) {
            binding.tvTurnIndicator.text = getString(R.string.waiting_for, currentPlayer.name)
            binding.tvTurnIndicator.visibility = View.VISIBLE
        } else {
            binding.tvTurnIndicator.visibility = View.GONE
        }

        // Enable/disable rack based on turn
        rackAdapter.setInteractive(isMyTurn && !isProcessingMove)
    }

    private fun updateRack() {
        // Always show full alphabet for gameplay
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toList()
        rackAdapter.submitList(letters.map { RackItem(it, selectedLetter == it) })
    }

    private fun onLetterTapped(letter: String) {
        if (gameState?.currentTurnPlayerId != currentPlayerId || isProcessingMove) {
            return
        }

        selectedLetter = if (selectedLetter == letter) null else letter
        updateRack()
    }

    private fun onCellClicked(row: Int, col: Int) {
        if (gameState?.currentTurnPlayerId != currentPlayerId || isProcessingMove) {
            return
        }

        val letter = selectedLetter ?: return

        val state = gameState ?: return
        val result = gameLogic.placeLetter(state, currentPlayerId, row, col, letter)

        if (result.isValid) {
            isProcessingMove = true

            // Update the board visually
            boardAdapter.placeLetter(row, col, letter, currentPlayerId)

            // Apply the move
            applyMove(row, col, letter, result)

            // Reset selection
            selectedLetter = null
            updateRack()
        }
    }

    private fun applyMove(row: Int, col: Int, letter: String, result: PlacementResult) {
        val state = gameState ?: return

        if (result.points > 0) {
            // Update player score
            val updatedPlayers = state.players.map { player ->
                if (player.id == currentPlayerId) {
                    player.copy(score = player.score + result.points)
                } else {
                    player
                }
            }

            // Add used word
            val usedWord = UsedWord(
                word = result.wordsFound.firstOrNull() ?: "",
                scoredByPlayerId = currentPlayerId,
                cellsInvolved = listOf(CellPosition(row, col))
            )

            val updatedState = state.copy(
                players = updatedPlayers,
                usedWords = state.usedWords + usedWord,
                currentTurnPlayerId = getNextPlayer(state)
            )

            gameState = updatedState

            // Show success toast
            Utils.showToast(
                this,
                getString(R.string.points_earned, result.points) + " " +
                getString(R.string.new_word, result.wordsFound.first()),
                com.wordbattle.utils.ToastType.SUCCESS
            )
        } else if (result.isDuplicate) {
            // Show warning toast
            Utils.showToast(
                this,
                getString(R.string.word_already_used),
                com.wordbattle.utils.ToastType.WARNING
            )

            // Still pass the turn
            val updatedState = state.copy(
                currentTurnPlayerId = getNextPlayer(state)
            )
            gameState = updatedState
        } else {
            // No word formed, just pass turn
            val updatedState = state.copy(
                currentTurnPlayerId = getNextPlayer(state)
            )
            gameState = updatedState
        }

        updateUI()
        checkWinCondition()

        // Process next player turn after delay
        if (gameState?.status == GameStatus.IN_PROGRESS) {
            isProcessingMove = false
            processNextTurn()
        }
    }

    private fun getNextPlayer(state: GameState): String {
        val players = state.players
        val currentIndex = players.indexOfFirst { it.id == state.currentTurnPlayerId }
        val nextIndex = (currentIndex + 1) % players.size
        return players[nextIndex].id
    }

    private fun processNextTurn() {
        val state = gameState ?: return
        val currentPlayer = state.players.find { it.id == state.currentTurnPlayerId }

        when (currentPlayer?.type) {
            PlayerType.COMPUTER -> {
                isProcessingMove = true
                Handler(Looper.getMainLooper()).postDelayed({
                    executeComputerMove()
                }, 1000)
            }
            PlayerType.HUMAN_LOCAL, PlayerType.HUMAN_ONLINE -> {
                // Wait for human input
            }
            null -> {}
        }
    }

    private fun executeComputerMove() {
        val state = gameState ?: return
        val computerPlayer = state.players.find { it.type == PlayerType.COMPUTER } ?: return

        val bestMove = gameLogic.findBestAIMove(state)

        bestMove?.let { move ->
            val result = gameLogic.placeLetter(state, computerPlayer.id, move.row, move.col, move.letter)
            
            boardAdapter.placeLetter(move.row, move.col, move.letter, computerPlayer.id)
            applyMove(move.row, move.col, move.letter, result)
        } ?: run {
            // Fallback: random move
            processNextTurn()
        }
    }

    private fun checkWinCondition() {
        val state = gameState ?: return
        val targetScore = GameConstants.TARGET_SCORE

        val winners = state.players.filter { it.score >= targetScore }

        if (winners.isNotEmpty()) {
            val updatedPlayers = state.players.map { player ->
                if (winners.any { it.id == player.id }) {
                    val winnerIndex = winners.indexOfFirst { it.id == player.id }
                    player.copy(rank = winnerIndex + 1)
                } else {
                    player
                }
            }

            val lastUnranked = state.players.find { it.rank == null }
            val finalPlayers = if (lastUnranked != null) {
                updatedPlayers.map { player ->
                    if (player.id == lastUnranked.id) {
                        player.copy(rank = updatedPlayers.size)
                    } else {
                        player
                    }
                }
            } else {
                updatedPlayers
            }

            gameState = state.copy(
                players = finalPlayers,
                status = GameStatus.FINISHED
            )

            updateUI()
            
            // Navigate to results
            Handler(Looper.getMainLooper()).postDelayed({
                navigateToResults()
            }, 1500)
        }
    }

    private fun navigateToResults() {
        val intent = Intent(this, ResultsActivity::class.java).apply {
            putExtra(ResultsActivity.EXTRA_GAME_ID, gameId)
            putExtra(ResultsActivity.EXTRA_PLAYERS, gameState?.players)
        }
        startActivity(intent)
        finish()
    }

    private fun startGameLoop() {
        // Check if current player is computer and process
        if (gameState?.status == GameStatus.IN_PROGRESS) {
            processNextTurn()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseListener?.remove()
    }

    companion object {
        const val EXTRA_GAME_ID = "game_id"
        const val EXTRA_MODE = "mode"

        private fun generateGameId(): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            return (1..8).map { chars.random() }.joinToString("")
        }
    }
}

// Handler for delayed execution
private val Handler = android.os.Handler(Looper.getMainLooper())
