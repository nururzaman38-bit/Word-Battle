package com.wordbattle.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.databinding.ItemRackLetterBinding

data class RackItem(
    val letter: String,
    val isSelected: Boolean = false
)

class RackAdapter(
    private val onLetterClick: (String) -> Unit
) : RecyclerView.Adapter<RackAdapter.RackViewHolder>() {

    private var items: List<RackItem> = emptyList()
    private var isInteractive: Boolean = true

    fun submitList(newItems: List<RackItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun setInteractive(interactive: Boolean) {
        isInteractive = interactive
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RackViewHolder {
        val binding = ItemRackLetterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RackViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class RackViewHolder(
        private val binding: ItemRackLetterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RackItem) {
            binding.tileView.setLetter(item.letter)
            binding.tileView.setSelected(item.isSelected)
            binding.tileView.alpha = if (isInteractive) 1.0f else 0.5f

            binding.root.setOnClickListener {
                if (isInteractive) {
                    onLetterClick(item.letter)
                }
            }
        }
    }
}
