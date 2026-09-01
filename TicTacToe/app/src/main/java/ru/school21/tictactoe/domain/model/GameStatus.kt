package ru.school21.tictactoe.domain.model

enum class GameStatus {
    WAITING_FOR_PLAYERS,
    IN_PROGRESS,
    DRAW,
    WIN_X,
    WIN_O,
    UNKNOWN
}

object Cell {
    const val EMPTY = 0
    const val X = 1
    const val O = 2
}