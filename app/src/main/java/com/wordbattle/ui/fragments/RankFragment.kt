package com.wordbattle.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.R
import com.wordbattle.data.local.UserPrefsManager
import com.wordbattle.databinding.FragmentRankBinding
import com.wordbattle.game.models.Player
import com.wordbattle.ui.adapters.RankingAdapter

class RankFragment : Fragment() {

    private var _binding: FragmentRankBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPrefs: UserPrefsManager
    private lateinit var adapter: RankingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPrefs = UserPrefsManager(requireContext())
        setupLeaderboard()
        setupFilter()
    }

    private fun setupLeaderboard() {
        // Sample data - in real app, load from Firebase
        val samplePlayers = listOf(
            SamplePlayer("1", "John", 1250, 1),
            SamplePlayer("2", "Sarah", 980, 2),
            SamplePlayer("3", "Mike", 870, 3),
            SamplePlayer("4", "Emma", 720, 4),
            SamplePlayer("5", "Alex", 650, 5)
        )

        adapter = RankingAdapter(samplePlayers.map { player ->
            Player(
                id = player.id,
                name = player.name,
                type = com.wordbattle.game.models.PlayerType.HUMAN_ONLINE,
                score = player.score,
                rank = player.rank,
                avatarColor = com.wordbattle.utils.Utils.getRandomAvatarColor()
            )
        })

        binding.rvLeaderboard.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RankFragment.adapter
        }
    }

    private fun setupFilter() {
        binding.btnWeekly.setOnClickListener {
            binding.btnWeekly.setTextColor(resources.getColor(R.color.purple_light, null))
            binding.btnAlltime.setTextColor(resources.getColor(R.color.gray_muted, null))
            // Update leaderboard data
        }

        binding.btnAlltime.setOnClickListener {
            binding.btnAlltime.setTextColor(resources.getColor(R.color.purple_light, null))
            binding.btnWeekly.setTextColor(resources.getColor(R.color.gray_muted, null))
            // Update leaderboard data
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    data class SamplePlayer(
        val id: String,
        val name: String,
        val score: Int,
        val rank: Int
    )
}
