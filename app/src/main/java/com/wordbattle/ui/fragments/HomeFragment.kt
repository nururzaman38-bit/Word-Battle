package com.wordbattle.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.wordbattle.R
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.FragmentHomeBinding
import com.wordbattle.game.models.GameMode

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPrefs: UserPrefsManager
    private var selectedMode: GameMode = GameMode.COMPUTER

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPrefs = UserPrefsManager(requireContext())
        setupTopBar()
        setupModeSelection()
        setupPlayButton()
        setupQuickActions()
    }

    private fun setupTopBar() {
        binding.tvUserName.text = userPrefs.currentUserName
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

        selectMode(GameMode.COMPUTER, binding.cardComputer)
    }

    private fun selectMode(mode: GameMode, card: androidx.cardview.widget.CardView) {
        selectedMode = mode

        resetCardBorder(binding.cardComputer)
        resetCardBorder(binding.card2player)
        resetCardBorder(binding.card3player)
        resetCardBorder(binding.card4player)

        card.strokeColor = ContextCompat.getColor(requireContext(), R.color.gold)
        card.strokeWidth = 4
    }

    private fun resetCardBorder(card: androidx.cardview.widget.CardView) {
        card.strokeColor = ContextCompat.getColor(requireContext(), android.R.color.transparent)
        card.strokeWidth = 0
    }

    private fun setupPlayButton() {
        binding.btnPlay.setOnClickListener {
            val activity = requireActivity()
            when (selectedMode) {
                GameMode.COMPUTER -> {
                    activity.navigateToGameBoard(GameMode.COMPUTER)
                }
                GameMode.LOCAL -> {
                    val playerCount = determinePlayerCount()
                    activity.navigateToAssignment(playerCount)
                }
                GameMode.MIXED_ONLINE -> {
                    activity.navigateToAssignment(4)
                }
            }
        }
    }

    private fun determinePlayerCount(): Int {
        return when {
            binding.card2player.strokeColor == ContextCompat.getColor(requireContext(), R.color.gold) -> 2
            binding.card3player.strokeColor == ContextCompat.getColor(requireContext(), R.color.gold) -> 3
            else -> 4
        }
    }

    private fun setupQuickActions() {
        binding.cardJoinRoom.setOnClickListener {
            requireActivity().navigateToJoinRoom()
        }

        binding.cardCreateRoom.setOnClickListener {
            requireActivity().navigateToRoomSetup()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
