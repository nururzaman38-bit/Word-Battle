package com.wordbattle.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wordbattle.R
import com.wordbattle.game.models.Cell
import com.wordbattle.game.models.Player
import com.wordbattle.ui.components.BoardCellView

class BoardAdapter(
    private val rows: Int,
    private val cols: Int,
    private val onCellClick: (Int, Int) -> Unit
) : RecyclerView.Adapter<BoardAdapter.CellViewHolder>() {

    private var cells: Array<Array<Cell>> = Array(rows) { row ->
        Array(cols) { col -> Cell(row, col) }
    }

    private var players: Map<String, Player> = emptyMap()
    private var scoredCells: Set<Pair<Int, Int>> = emptySet()
    private var selectedRow: Int? = null
    private var selectedCol: Int? = null

    fun updateBoard(newCells: Array<Array<Cell>>) {
        cells = newCells
        notifyDataSetChanged()
    }

    fun updatePlayers(playerList: List<Player>) {
        players = playerList.associateBy { it.id }
        notifyDataSetChanged()
    }

    fun markScoredCells(cells: Set<Pair<Int, Int>>) {
        scoredCells = cells
        notifyDataSetChanged()
    }

    fun selectCell(row: Int, col: Int, selected: Boolean) {
        if (selected) {
            selectedRow = row
            selectedCol = col
        } else {
            selectedRow = null
            selectedCol = null
        }
        notifyItemChanged(row * cols + col)
    }

    fun placeLetter(row: Int, col: Int, letter: String, playerId: String) {
        cells[row][col] = Cell(row, col, letter, playerId)
        notifyItemChanged(row * cols + col)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_board_cell, parent, false)
        return CellViewHolder(view)
    }

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        val row = position / cols
        val col = position % cols
        val cell = cells[row][col]
        val isScored = scoredCells.contains(Pair(row, col))
        val isSelected = selectedRow == row && selectedCol == col

        holder.bind(cell, isScored, isSelected, players)
    }

    override fun getItemCount(): Int = rows * cols

    inner class CellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cellView: BoardCellView = itemView.findViewById(R.id.cell_view)

        fun bind(cell: Cell, isScored: Boolean, isSelected: Boolean, players: Map<String, Player>) {
            cellView.setCell(cell)
            cellView.setScored(isScored)

            // Visual feedback for selected cell
            if (isSelected) {
                cellView.alpha = 0.8f
            } else {
                cellView.alpha = 1.0f
            }

            itemView.setOnClickListener {
                onCellClick(cell.row, cell.col)
            }
        }
    }
}
