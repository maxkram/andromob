package ru.school21.tictactoe.domain.service

import ru.school21.tictactoe.domain.model.Cell
import ru.school21.tictactoe.domain.model.GameBoard

class MinimaxMoveSelector {

    fun selectMove(board: GameBoard): Pair<Int, Int> {
        require(BoardValidator.hasValidShapeAndValues(board)) {
            "Board has invalid shape or cell values"
        }
        val empties = BoardUtils.emptyCells(board)
        require(empties.isNotEmpty()) { "Board has no empty cells" }

        var bestScore = Int.MIN_VALUE
        var bestMove = empties.first()

        for (move in empties) {
            val nextBoard = BoardUtils.copy(board)
            nextBoard.cells[move.first][move.second] = Cell.COMPUTER
            val score = minimax(nextBoard, false, 0, Int.MIN_VALUE, Int.MAX_VALUE)
            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }
        return bestMove
    }

    private fun minimax(
        board: GameBoard,
        computerTurn: Boolean,
        depth: Int,
        alpha: Int,
        beta: Int
    ): Int {
        if (BoardRules.hasWinner(board, Cell.COMPUTER)) return 10 - depth
        if (BoardRules.hasWinner(board, Cell.HUMAN)) return depth - 10

        val empties = BoardUtils.emptyCells(board)
        if (empties.isEmpty()) return 0

        if (computerTurn) {
            var bestScore = Int.MIN_VALUE
            var a = alpha
            for (move in empties) {
                val nextBoard = BoardUtils.copy(board)
                nextBoard.cells[move.first][move.second] = Cell.COMPUTER
                val score = minimax(nextBoard, false, depth + 1, a, beta)
                bestScore = maxOf(bestScore, score)
                a = maxOf(a, bestScore)
                if (a >= beta) break
            }
            return bestScore
        } else {
            var bestScore = Int.MAX_VALUE
            var b = beta
            for (move in empties) {
                val nextBoard = BoardUtils.copy(board)
                nextBoard.cells[move.first][move.second] = Cell.HUMAN
                val score = minimax(nextBoard, true, depth + 1, alpha, b)
                bestScore = minOf(bestScore, score)
                b = minOf(b, bestScore)
                if (alpha >= b) break
            }
            return bestScore
        }
    }
}