package ru.school21.tictactoe.web.model

data class GameResponse(
    val uuid: String,
    val state: String,
    val currentTurn: String?,
    val winner: String?,
    val board: List<List<String>>,
    val players: List<PlayerResponse>
)