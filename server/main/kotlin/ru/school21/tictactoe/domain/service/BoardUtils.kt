package ru.school21.tictactoe.domain.service

import ru.school21.tictactoe.domain.model.Cell
import ru.school21.tictactoe.domain.model.GameBoard

object BoardUtils {

    fun copy(board: GameBoard): GameBoard =
        GameBoard(Array(board.cells.size) { row -> board.cells[row].clone() })

    fun emptyCells(board: GameBoard): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (row in board.cells.indices)
            for (col in board.cells[row].indices)
                if (board.cells[row][col] == Cell.EMPTY)
                    result.add(row to col)
        return result
    }
}