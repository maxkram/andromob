package ru.school21.tictactoe.domain.service

import ru.school21.tictactoe.domain.model.Cell
import ru.school21.tictactoe.domain.model.GameBoard

object BoardValidator {

    private const val BOARD_SIZE = 3

    fun hasValidShapeAndValues(board: GameBoard?): Boolean {
        val cells = board?.cells ?: return false
        if (cells.size != BOARD_SIZE) return false
        for (row in cells) {
            if (row.size != BOARD_SIZE) return false
            for (cell in row) {
                if (cell != Cell.EMPTY && cell != Cell.HUMAN && cell != Cell.COMPUTER)
                    return false
            }
        }
        return true
    }
}