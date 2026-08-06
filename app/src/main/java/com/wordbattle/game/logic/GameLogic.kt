package com.wordbattle.game.logic

import com.wordbattle.game.models.*
import kotlin.random.Random

data class PlacementResult(
    val isValid: Boolean,
    val points: Int = 0,
    val wordsFound: List<String> = emptyList(),
    val isDuplicate: Boolean = false,
    val isNotWord: Boolean = false
)

data class AIMove(
    val row: Int,
    val col: Int,
    val letter: String,
    val points: Int,
    val wordsFormed: List<String>
)

class GameLogic {

    fun placeLetter(
        gameState: GameState,
        playerId: String,
        row: Int,
        col: Int,
        letter: String
    ): PlacementResult {
        val board = gameState.board
        
        // Validate cell is within bounds
        if (row < 0 || row >= board.rows || col < 0 || col >= board.cols) {
            return PlacementResult(false)
        }
        
        // Validate cell is empty
        if (board.cells[row][col].letter != null) {
            return PlacementResult(false)
        }
        
        val letterUpper = letter.uppercase()
        
        // Create a temporary board with the new letter
        val newBoard = createBoardWithLetter(gameState.board, row, col, letterUpper, playerId)
        
        // Find all words formed
        val wordsFound = findAllWords(newBoard, row, col)
            .map { it.uppercase() }
            .filter { it.length >= GameConstants.MIN_WORD_LENGTH }
        
        if (wordsFound.isEmpty()) {
            // No valid word formed - placement is allowed but scores 0
            return PlacementResult(
                isValid = true,
                points = 0,
                wordsFound = emptyList()
            )
        }
        
        // Check for duplicates and score
        val globalUsedWords = gameState.usedWords
        val scoredWords = mutableListOf<String>()
        val isDuplicate = mutableListOf<String>()
        var totalPoints = 0
        
        for (word in wordsFound) {
            val wordKey = word.uppercase()
            
            // Check if already used in this game
            if (globalUsedWords.any { it.word.uppercase() == wordKey }) {
                isDuplicate.add(word)
            } else if (WordDictionary.isValidWord(word)) {
                scoredWords.add(word)
                totalPoints += word.length
            }
            // If not in dictionary, skip without feedback
        }
        
        if (scoredWords.isEmpty() && isDuplicate.isNotEmpty()) {
            // All words are duplicates
            return PlacementResult(
                isValid = true,
                points = 0,
                wordsFound = isDuplicate,
                isDuplicate = true
            )
        }
        
        return PlacementResult(
            isValid = true,
            points = totalPoints,
            wordsFound = scoredWords,
            isDuplicate = isDuplicate.isNotEmpty()
        )
    }
    
    private fun createBoardWithLetter(
        board: BoardState,
        row: Int,
        col: Int,
        letter: String,
        playerId: String
    ): BoardState {
        val newCells = Array(board.rows) { boardRow ->
            Array(board.cols) { boardCol ->
                board.cells[boardRow][boardCol]
            }
        }
        newCells[row][col] = Cell(row, col, letter, playerId)
        return BoardState(board.rows, board.cols, newCells)
    }
    
    private fun findAllWords(board: BoardState, centerRow: Int, centerCol: Int): List<String> {
        val words = mutableListOf<String>()
        
        // Check horizontal word
        val horizontalWord = getHorizontalWord(board, centerRow, centerCol)
        if (horizontalWord.isNotEmpty()) {
            words.add(horizontalWord)
        }
        
        // Check vertical word
        val verticalWord = getVerticalWord(board, centerRow, centerCol)
        if (verticalWord.isNotEmpty()) {
            words.add(verticalWord)
        }
        
        return words
    }
    
    private fun getHorizontalWord(board: BoardState, row: Int, col: Int): String {
        val startCol = findWordStartHorizontal(board, row, col)
        val endCol = findWordEndHorizontal(board, row, col)
        
        if (endCol - startCol + 1 < GameConstants.MIN_WORD_LENGTH) {
            return ""
        }
        
        val chars = mutableListOf<Char>()
        for (c in startCol..endCol) {
            board.cells[row][c].letter?.let { chars.add(it[0]) }
        }
        return chars.joinToString("")
    }
    
    private fun getVerticalWord(board: BoardState, row: Int, col: Int): String {
        val startRow = findWordStartVertical(board, row, col)
        val endRow = findWordEndVertical(board, row, col)
        
        if (endRow - startRow + 1 < GameConstants.MIN_WORD_LENGTH) {
            return ""
        }
        
        val chars = mutableListOf<Char>()
        for (r in startRow..endRow) {
            board.cells[r][col].letter?.let { chars.add(it[0]) }
        }
        return chars.joinToString("")
    }
    
    private fun findWordStartHorizontal(board: BoardState, row: Int, col: Int): Int {
        var c = col
        while (c > 0 && board.cells[row][c - 1].letter != null) {
            c--
        }
        return c
    }
    
    private fun findWordEndHorizontal(board: BoardState, row: Int, col: Int): Int {
        var c = col
        while (c < board.cols - 1 && board.cells[row][c + 1].letter != null) {
            c++
        }
        return c
    }
    
    private fun findWordStartVertical(board: BoardState, row: Int, col: Int): Int {
        var r = row
        while (r > 0 && board.cells[r - 1][col].letter != null) {
            r--
        }
        return r
    }
    
    private fun findWordEndVertical(board: BoardState, row: Int, col: Int): Int {
        var r = row
        while (r < board.rows - 1 && board.cells[r + 1][col].letter != null) {
            r++
        }
        return r
    }
    
    // AI Logic
    fun findBestAIMove(gameState: GameState): AIMove? {
        val emptyCells = getEmptyCells(gameState.board)
        val validLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toList()
        
        var bestMove: AIMove? = null
        var bestScore = -1
        
        // Try to find a scoring move
        for (cell in emptyCells) {
            for (letter in validLetters) {
                val result = placeLetter(gameState, gameState.players.first { it.type == PlayerType.COMPUTER }.id, 
                    cell.row, cell.col, letter)
                
                if (result.isValid && result.points > 0) {
                    if (result.points > bestScore) {
                        bestScore = result.points
                        bestMove = AIMove(
                            row = cell.row,
                            col = cell.col,
                            letter = letter,
                            points = result.points,
                            wordsFormed = result.wordsFound
                        )
                    }
                }
            }
        }
        
        // If no scoring move, place adjacent to existing letters
        if (bestMove == null) {
            val adjacentCells = getAdjacentEmptyCells(gameState.board)
            if (adjacentCells.isNotEmpty()) {
                val randomCell = adjacentCells.random()
                val randomLetter = validLetters.random()
                bestMove = AIMove(
                    row = randomCell.row,
                    col = randomCell.col,
                    letter = randomLetter,
                    points = 0,
                    wordsFormed = emptyList()
                )
            } else if (emptyCells.isNotEmpty()) {
                // Fall back to any empty cell
                val randomCell = emptyCells.random()
                val randomLetter = validLetters.random()
                bestMove = AIMove(
                    row = randomCell.row,
                    col = randomCell.col,
                    letter = randomLetter,
                    points = 0,
                    wordsFormed = emptyList()
                )
            }
        }
        
        return bestMove
    }
    
    private fun getEmptyCells(board: BoardState): List<CellPosition> {
        val cells = mutableListOf<CellPosition>()
        for (r in 0 until board.rows) {
            for (c in 0 until board.cols) {
                if (board.cells[r][c].letter == null) {
                    cells.add(CellPosition(r, c))
                }
            }
        }
        return cells
    }
    
    private fun getAdjacentEmptyCells(board: BoardState): List<CellPosition> {
        val cells = mutableListOf<CellPosition>()
        val directions = listOf(
            Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1)
        )
        
        for (r in 0 until board.rows) {
            for (c in 0 until board.cols) {
                if (board.cells[r][c].letter == null) {
                    for ((dr, dc) in directions) {
                        val nr = r + dr
                        val nc = c + dc
                        if (nr in 0 until board.rows && nc in 0 until board.cols &&
                            board.cells[nr][nc].letter != null) {
                            cells.add(CellPosition(r, c))
                            break
                        }
                    }
                }
            }
        }
        return cells
    }
}
