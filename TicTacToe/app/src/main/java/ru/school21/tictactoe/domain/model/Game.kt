package ru.school21.tictactoe.domain.model

data class Game(
    val id: String,
    val playerXId: String?,
    val playerOId: String?,
    val playerXLogin: String? = null,
    val playerOLogin: String? = null,
    val currentPlayerId: String?,
    val status: GameStatus,
    val board: Array<IntArray>,
    val vsComputer: Boolean
)
