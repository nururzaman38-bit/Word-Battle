package com.wordbattle.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPrefs: UserPrefsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPrefs = UserPrefsManager(requireContext())
        setupProfile()
        setupSettings()
        setupLogout()
    }

    private fun setupProfile() {
        val user = userPrefs.getCurrentUser()

        binding.tvName.text = user.name
        binding.tvLevelBadge.text = user.level.toString()
        binding.tvGamesPlayed.text = user.gamesPlayed.toString()
        binding.tvWins.text = user.wins.toString()

        val winRate = if (user.gamesPlayed > 0) {
            ((user.wins.toFloat() / user.gamesPlayed) * 100).toInt()
        } else 0
        binding.tvWinRate.text = "$winRate%"

        binding.ivAvatar.setAvatarColor(user.avatarColor)
    }

    private fun setupSettings() {
        binding.switchSound.isChecked = userPrefs.soundEnabled
        binding.switchNotifications.isChecked = userPrefs.notificationsEnabled

        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            userPrefs.soundEnabled = isChecked
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            userPrefs.notificationsEnabled = isChecked
        }
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            userPrefs.resetUser()
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(requireContext(), com.wordbattle.ui.activities.MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
