package com.wordbattle.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.R
import com.wordbattle.databinding.ItemSlotBinding

class PlayerSlotAdapter(
    private val onReadyToggle: (Int, Boolean) -> Unit
) : ListAdapter<PlayerSlotItem, PlayerSlotAdapter.ViewHolder>(PlayerSlotDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSlotBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemSlotBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlayerSlotItem) {
            binding.apply {
                tvSlotNumber.text = (item.slotIndex + 1).toString()

                if (item.playerId.isNotEmpty()) {
                    ivAvatar.visibility = android.view.View.VISIBLE
                    tvName.visibility = android.view.View.VISIBLE
                    badgeType.visibility = android.view.View.VISIBLE
                    toggleReady.visibility = android.view.View.VISIBLE

                    tvName.text = item.playerName
                    badgeType.text = item.type

                    // Set avatar color based on type
                    val avatarColor = when (item.type) {
                        "Local" -> 0xFFFF4E4E.toLong()
                        "Online" -> 0xFF00C9A7.toLong()
                        else -> 0xFF5B1E8C.toLong()
                    }
                    ivAvatar.setAvatarColor(avatarColor)

                    // Toggle button state
                    toggleReady.isChecked = item.isReady
                    toggleReady.setOnCheckedChangeListener { _, isChecked ->
                        onReadyToggle(item.slotIndex, isChecked)
                    }
                } else {
                    ivAvatar.visibility = android.view.View.GONE
                    tvName.visibility = android.view.View.GONE
                    badgeType.visibility = android.view.View.GONE
                    toggleReady.visibility = android.view.View.GONE

                    tvSlotNumber.text = "${item.slotIndex + 1}"
                }
            }
        }
    }

    class PlayerSlotDiffCallback : DiffUtil.ItemCallback<PlayerSlotItem>() {
        override fun areItemsTheSame(oldItem: PlayerSlotItem, newItem: PlayerSlotItem): Boolean {
            return oldItem.slotIndex == newItem.slotIndex
        }

        override fun areContentsTheSame(oldItem: PlayerSlotItem, newItem: PlayerSlotItem): Boolean {
            return oldItem == newItem
        }
    }
}
