package com.wordbattle.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.R
import com.wordbattle.databinding.ItemSlotAssignmentBinding

class SlotAssignmentAdapter(
    private val playerCount: Int,
    private val assignments: MutableList<Boolean>,
    private val onToggle: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<SlotAssignmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSlotAssignmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, assignments[position] if position < assignments.size else false)
    }

    override fun getItemCount(): Int = playerCount

    fun updateAssignments(newAssignments: MutableList<Boolean>) {
        assignments.clear()
        assignments.addAll(newAssignments)
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ItemSlotAssignmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(index: Int, isOnline: Boolean) {
            binding.apply {
                // Enable toggle only for non-first slots
                val canToggle = index > 0
                toggleType.isEnabled = canToggle
                toggleType.alpha = if (canToggle) 1.0f else 0.5f

                tvSlotNumber.text = "Player ${index + 1}"

                // Set initial state
                toggleType.isChecked = isOnline
                updateTypeBadge(isOnline)

                toggleType.setOnCheckedChangeListener { _, isChecked ->
                    onToggle(index, isChecked)
                }
            }
        }

        private fun updateTypeBadge(isOnline: Boolean) {
            val context = binding.root.context
            binding.badgeType.text = if (isOnline) "Online" else "Local"
            binding.badgeType.setBackgroundColor(
                ContextCompat.getColor(context, if (isOnline) R.color.blue else R.color.gold)
            )
        }
    }
}
