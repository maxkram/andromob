package ru.school21.tictactoe.domain.model

import java.util.UUID

data class Game(
    val id: UUID,
    val board: GameBoard
)