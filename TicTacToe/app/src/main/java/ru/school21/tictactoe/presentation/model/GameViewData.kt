package ru.school21.tictactoe.presentation.model

data class GameViewData(
    val id: String,
    val playerXLogin: String?,
    val playerOLogin: String?,
    val currentPlayerLogin: String?,
    val statusText: String,
    val board: Array<IntArray>,
    val isMyTurn: Boolean,
    val isBoardEnabled: Boolean,
    val mySymbol: Int // Cell.X or Cell.O
)
data class GameItemViewData(
    val gameId: String,
    val creatorLogin: String
)
