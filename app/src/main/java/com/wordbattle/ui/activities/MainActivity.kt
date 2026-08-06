package com.wordbattle.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wordbattle.R
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.ActivityMainBinding
import com.wordbattle.ui.fragments.HomeFragment
import com.wordbattle.ui.fragments.FriendsFragment
import com.wordbattle.ui.fragments.RankFragment
import com.wordbattle.ui.fragments.ProfileFragment
import com.wordbattle.utils.Utils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPrefs: UserPrefsManager

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPrefsManager(this)
        setupTopBar()
        setupBottomNav()
        showHomeFragment()
    }

    private fun setupTopBar() {
        val user = userPrefs.getCurrentUser()

        binding.tvUserName.text = user.name
        binding.tvCoins.text = user.coins.toString()
        binding.tvGems.text = user.gems.toString()
        binding.tvLevelBadge.text = user.level.toString()

        binding.ivAvatar.setAvatarColor(user.avatarColor)

        binding.btnSettings.setOnClickListener {
            showProfileFragment()
        }
    }

    private fun setupBottomNav() {
        binding.navHome.setOnClickListener {
            showHomeFragment()
        }

        binding.navRank.setOnClickListener {
            showRankFragment()
        }

        binding.navFriends.setOnClickListener {
            showFriendsFragment()
        }

        binding.navProfile.setOnClickListener {
            showProfileFragment()
        }
    }

    private fun showHomeFragment() {
        setActiveNav(R.id.nav_home, R.id.icon_home, R.id.label_home)
        replaceFragment(HomeFragment())
    }

    private fun showRankFragment() {
        setActiveNav(R.id.nav_rank, R.id.icon_rank, R.id.label_rank)
        replaceFragment(RankFragment())
    }

    private fun showFriendsFragment() {
        setActiveNav(R.id.nav_friends, R.id.icon_friends, R.id.label_friends)
        replaceFragment(FriendsFragment())
    }

    private fun showProfileFragment() {
        setActiveNav(R.id.nav_profile, R.id.icon_profile, R.id.label_profile)
        replaceFragment(ProfileFragment())
    }

    private fun setActiveNav(navId: Int, iconId: Int, labelId: Int) {
        // Reset all nav items
        resetNav(R.id.nav_home, R.id.icon_home, R.id.label_home)
        resetNav(R.id.nav_rank, R.id.icon_rank, R.id.label_rank)
        resetNav(R.id.nav_friends, R.id.icon_friends, R.id.label_friends)
        resetNav(R.id.nav_profile, R.id.icon_profile, R.id.label_profile)

        // Activate current
        val iconView = when (navId) {
            R.id.nav_home -> binding.iconHome
            R.id.nav_rank -> binding.iconRank
            R.id.nav_friends -> binding.iconFriends
            R.id.nav_profile -> binding.iconProfile
            else -> return
        }
        val labelView = when (navId) {
            R.id.nav_home -> binding.labelHome
            R.id.nav_rank -> binding.labelRank
            R.id.nav_friends -> binding.labelFriends
            R.id.nav_profile -> binding.labelProfile
            else -> return
        }

        iconView.setColorFilter(getColor(iconId))
        labelView.setTextColor(getColor(iconId))
    }

    private fun resetNav(navId: Int, iconId: Int, labelId: Int) {
        val iconView = when (navId) {
            R.id.nav_home -> binding.iconHome
            R.id.nav_rank -> binding.iconRank
            R.id.nav_friends -> binding.iconFriends
            R.id.nav_profile -> binding.iconProfile
            else -> return
        }
        val labelView = when (navId) {
            R.id.nav_home -> binding.labelHome
            R.id.nav_rank -> binding.labelRank
            R.id.nav_friends -> binding.labelFriends
            R.id.nav_profile -> binding.labelProfile
            else -> return
        }

        iconView.setColorFilter(getColor(R.color.gray_muted))
        labelView.setTextColor(getColor(R.color.gray_muted))
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.tabContent, fragment)
            .commit()
    }

    fun navigateToGameBoard(gameId: String) {
        val intent = Intent(this, GameBoardActivity::class.java).apply {
            putExtra(EXTRA_GAME_ID, gameId)
        }
        startActivity(intent)
    }

    fun navigateToRoomSetup() {
        val intent = Intent(this, RoomSetupActivity::class.java)
        startActivity(intent)
    }

    fun navigateToJoinRoom() {
        val intent = Intent(this, JoinRoomActivity::class.java)
        startActivity(intent)
    }

    fun navigateToAssignment(playerCount: Int) {
        val intent = Intent(this, AssignmentActivity::class.java).apply {
            putExtra(EXTRA_PLAYER_COUNT, playerCount)
        }
        startActivity(intent)
    }

    companion object {
        const val EXTRA_GAME_ID = "game_id"
        const val EXTRA_PLAYER_COUNT = "player_count"
    }
}
