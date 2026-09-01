package ru.school21.tictactoe.web.model

import kotlinx.serialization.Serializable

@Serializable
data class MoveRequest(
    val row: Int,
    val col: Int
)