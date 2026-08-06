package com.wordbattle.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.R
import com.wordbattle.databinding.ItemFriendRowBinding
import com.wordbattle.game.models.Player

class FriendAdapter(
    private val friends: List<FriendData>,
    private val currentUserId: String,
    private val onInviteClick: (FriendData) -> Unit
) : ListAdapter<FriendData, FriendAdapter.ViewHolder>(FriendDiffCallback()) {

    private var filteredFriends: List<FriendData> = friends

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFriendRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredFriends[position])
    }

    override fun getItemCount(): Int = filteredFriends.size

    fun filter(query: String) {
        filteredFriends = if (query.isEmpty()) {
            friends
        } else {
            friends.filter { it.name.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ItemFriendRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: FriendData) {
            binding.apply {
                tvName.text = friend.name
                tvScore.text = "${friend.score} points"

                ivAvatar.setAvatarColor(friend.avatarColor)

                // Status badge
                badgeStatus.text = if (friend.isOnline) "Online" else "Offline"
                badgeStatus.setTextColor(
                    ContextCompat.getColor(root.context, if (friend.isOnline) R.color.teal else R.color.gray_muted)
                )
                badgeStatus.background.setTint(
                    ContextCompat.getColor(root.context, if (friend.isOnline) R.color.teal else R.color.gray_muted)
                )

                // Invite button
                btnInvite.isEnabled = friend.isOnline
                btnInvite.alpha = if (friend.isOnline) 1.0f else 0.5f
                btnInvite.setOnClickListener {
                    onInviteClick(friend)
                }
            }
        }
    }

    class FriendDiffCallback : DiffUtil.ItemCallback<FriendData>() {
        override fun areItemsTheSame(oldItem: FriendData, newItem: FriendData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FriendData, newItem: FriendData): Boolean {
            return oldItem == newItem
        }
    }

    data class FriendData(
        val id: String,
        val name: String,
        val isOnline: Boolean,
        val score: Int,
        val avatarColor: Long
    )
}
