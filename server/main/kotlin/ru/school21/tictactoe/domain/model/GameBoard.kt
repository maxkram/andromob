package ru.school21.tictactoe.domain.model

data class GameBoard(val cells: Array<IntArray>) {

    fun status(): GameStatus {
        if (hasWinner(Cell.HUMAN)) return GameStatus.HUMAN_WON
        if (hasWinner(Cell.COMPUTER)) return GameStatus.COMPUTER_WON
        if (hasEmptyCells()) return GameStatus.IN_PROGRESS
        return GameStatus.DRAW
    }

    private fun hasWinner(player: Int): Boolean {
        for (i in 0..2) {
            if (cells[i][0] == player && cells[i][1] == player && cells[i][2] == player) return true
            if (cells[0][i] == player && cells[1][i] == player && cells[2][i] == player) return true
        }
        return (cells[0][0] == player && cells[1][1] == player && cells[2][2] == player) ||
            (cells[0][2] == player && cells[1][1] == player && cells[2][0] == player)
    }

    private fun hasEmptyCells(): Boolean =
        cells.any { row -> row.any { it == Cell.EMPTY } }
}