package com.wordbattle.game.models

object GameConstants {
    const val BOARD_ROWS = 15
    const val BOARD_COLS = 15
    const val TARGET_SCORE = 100
    const val MIN_WORD_LENGTH = 2
    const val MAX_PLAYERS = 4
    const val DEFAULT_USER_ID = "default_user"
    const val DEFAULT_USER_NAME = "Player"
    
    // Firebase paths
    const val PATH_ROOMS = "rooms"
    const val PATH_GAMES = "games"
    const val PATH_USERS = "users"
    const val PATH_LEADERBOARD = "leaderboard"
}
