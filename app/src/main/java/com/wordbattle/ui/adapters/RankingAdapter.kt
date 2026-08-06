package com.wordbattle.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.R
import com.wordbattle.databinding.ItemRankingBinding
import com.wordbattle.game.models.Player

class RankingAdapter(
    private val players: List<Player>
) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(players[position], position)
    }

    override fun getItemCount(): Int = players.size

    inner class ViewHolder(
        private val binding: ItemRankingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player, position: Int) {
            binding.apply {
                tvRank.text = when (player.rank) {
                    1 -> "🥇"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> "#${position + 1}"
                }

                tvName.text = player.name
                tvScore.text = "${player.score} points"

                ivAvatar.setAvatarColor(player.avatarColor)

                // Highlight current user if needed
                // badgeYou.visibility = if (player.id == currentUserId) View.VISIBLE else View.GONE
            }
        }
    }
}
