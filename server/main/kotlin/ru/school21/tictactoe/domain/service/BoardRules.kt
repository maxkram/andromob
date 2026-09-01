package ru.school21.tictactoe.domain.service

import ru.school21.tictactoe.domain.model.Cell
import ru.school21.tictactoe.domain.model.GameBoard

object BoardRules {

    private const val BOARD_SIZE = 3

    fun hasWinner(board: GameBoard, player: Int): Boolean {
        val cells = board.cells
        for (i in 0 until BOARD_SIZE) {
            if (cells[i][0] == player && cells[i][1] == player && cells[i][2] == player) return true
            if (cells[0][i] == player && cells[1][i] == player && cells[2][i] == player) return true
        }
        return (cells[0][0] == player && cells[1][1] == player && cells[2][2] == player) ||
            (cells[0][2] == player && cells[1][1] == player && cells[2][0] == player)
    }

    fun hasEmptyCell(board: GameBoard): Boolean =
        board.cells.any { row -> row.any { it == Cell.EMPTY } }
}