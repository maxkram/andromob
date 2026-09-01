package ru.school21.tictactoe.domain.service

import ru.school21.tictactoe.domain.model.Cell
import ru.school21.tictactoe.domain.model.GameBoard

object BoardTransitionValidator {

    private const val BOARD_SIZE = 3

    fun isValidHumanMove(saved: GameBoard, current: GameBoard): Boolean {
        if (!BoardValidator.hasValidShapeAndValues(saved) ||
            !BoardValidator.hasValidShapeAndValues(current)) return false

        var newHumanMoves = 0
        for (row in 0 until BOARD_SIZE) {
            for (col in 0 until BOARD_SIZE) {
                val savedCell = saved.cells[row][col]
                val currentCell = current.cells[row][col]
                if (savedCell == currentCell) continue
                if (savedCell != Cell.EMPTY || currentCell != Cell.HUMAN) return false
                newHumanMoves++
            }
        }
        return newHumanMoves == 1
    }
}