package ru.school21.tictactoe.web.model

import kotlinx.serialization.Serializable
import ru.school21.tictactoe.domain.model.GameStatus

@Serializable
data class GameResponse(
    val id: String,
    val board: List<List<Int>>,
    val status: GameStatus
)
