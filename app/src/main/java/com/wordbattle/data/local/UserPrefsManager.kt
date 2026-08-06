package com.wordbattle.data.local

import android.content.Context
import android.content.SharedPreferences
import com.wordbattle.game.models.User
import com.wordbattle.game.models.PlayerType
import java.util.UUID

class UserPrefsManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("word_battle_prefs", Context.MODE_PRIVATE)
    
    var currentUserId: String
        get() = prefs.getString(KEY_USER_ID, null) ?: createNewUser()
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()
    
    var currentUserName: String
        get() = prefs.getString(KEY_USER_NAME, DEFAULT_USER_NAME) ?: DEFAULT_USER_NAME
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()
    
    var userLevel: Int
        get() = prefs.getInt(KEY_USER_LEVEL, 1)
        set(value) = prefs.edit().putInt(KEY_USER_LEVEL, value).apply()
    
    var userCoins: Int
        get() = prefs.getInt(KEY_USER_COINS, 0)
        set(value) = prefs.edit().putInt(KEY_USER_COINS, value).apply()
    
    var userGems: Int
        get() = prefs.getInt(KEY_USER_GEMS, 0)
        set(value) = prefs.edit().putInt(KEY_USER_GEMS, value).apply()
    
    var userGamesPlayed: Int
        get() = prefs.getInt(KEY_USER_GAMES_PLAYED, 0)
        set(value) = prefs.edit().putInt(KEY_USER_GAMES_PLAYED, value).apply()
    
    var userWins: Int
        get() = prefs.getInt(KEY_USER_WINS, 0)
        set(value) = prefs.edit().putInt(KEY_USER_WINS, value).apply()
    
    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()
    
    var musicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC_ENABLED, value).apply()
    
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()
    
    var userAvatarColor: Long
        get() = prefs.getLong(KEY_USER_AVATAR_COLOR, 0)
        set(value) = prefs.edit().putLong(KEY_USER_AVATAR_COLOR, value).apply()
    
    fun getCurrentUser(): User {
        return User(
            id = currentUserId,
            name = currentUserName,
            avatarColor = userAvatarColor,
            level = userLevel,
            coins = userCoins,
            gems = userGems,
            gamesPlayed = userGamesPlayed,
            wins = userWins
        )
    }
    
    fun updateUser(user: User) {
        currentUserId = user.id
        currentUserName = user.name
        userLevel = user.level
        userCoins = user.coins
        userGems = user.gems
        userGamesPlayed = user.gamesPlayed
        userWins = user.wins
        userAvatarColor = user.avatarColor
    }
    
    fun incrementGamesPlayed() {
        userGamesPlayed = userGamesPlayed + 1
    }
    
    fun incrementWins() {
        userWins = userWins + 1
    }
    
    fun resetUser() {
        prefs.edit().clear().apply()
    }
    
    private fun createNewUser(): String {
        val userId = UUID.randomUUID().toString()
        currentUserId = userId
        return userId
    }
    
    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_LEVEL = "user_level"
        private const val KEY_USER_COINS = "user_coins"
        private const val KEY_USER_GEMS = "user_gems"
        private const val KEY_USER_GAMES_PLAYED = "user_games_played"
        private const val KEY_USER_WINS = "user_wins"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_USER_AVATAR_COLOR = "user_avatar_color"
        
        private const val DEFAULT_USER_NAME = "Player"
    }
}
