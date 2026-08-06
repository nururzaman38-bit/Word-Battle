package com.wordbattle.game.models

data class Player(
    val id: String,
    val name: String,
    val type: PlayerType,
    val score: Int = 0,
    val rank: Int? = null,
    val isReady: Boolean = false,
    val isConnected: Boolean = true,
    val turnOrder: Int = 0,
    val avatarColor: Int = 0
)

enum class PlayerType {
    HUMAN_LOCAL,
    HUMAN_ONLINE,
    COMPUTER
}

data class Cell(
    val row: Int,
    val col: Int,
    val letter: String? = null,
    val placedByPlayerId: String? = null
)

data class BoardState(
    val rows: Int = 15,
    val cols: Int = 15,
    val cells: Array<Array<Cell>> = Array(15) { row ->
        Array(15) { col ->
            Cell(row, col)
        }
    }
)

data class UsedWord(
    val word: String,
    val scoredByPlayerId: String,
    val cellsInvolved: List<CellPosition>
)

data class CellPosition(
    val row: Int,
    val col: Int
)

data class GameState(
    val gameId: String,
    val mode: GameMode,
    val targetScore: Int = 100,
    val board: BoardState = BoardState(),
    val players: List<Player> = emptyList(),
    val usedWords: List<UsedWord> = emptyList(),
    val currentTurnPlayerId: String? = null,
    val status: GameStatus = GameStatus.LOBBY,
    val rankingsAssigned: List<String> = emptyList()
)

enum class GameMode {
    COMPUTER,
    LOCAL,
    MIXED_ONLINE
}

enum class GameStatus {
    LOBBY,
    IN_PROGRESS,
    FINISHED
}

data class Room(
    val roomId: String,
    val passcode: String,
    val hostPlayerId: String,
    val totalSlots: Int,
    val localSlotsCount: Int,
    val onlineSlotsCount: Int,
    val slots: List<RoomSlot>,
    val gameStateId: String? = null
)

data class RoomSlot(
    val slotIndex: Int,
    val filledBy: Player? = null,
    val isReady: Boolean = false
)

data class User(
    val id: String,
    val name: String,
    val email: String? = null,
    val avatarColor: Long = 0,
    val level: Int = 1,
    val coins: Int = 0,
    val gems: Int = 0,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val isOnline: Boolean = false
)
