package ru.school21.tictactoe.datasource.model

import java.util.UUID

data class Game(val id: UUID, val board: GameBoard)