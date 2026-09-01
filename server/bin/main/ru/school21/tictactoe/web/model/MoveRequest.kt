package ru.school21.tictactoe.web.model

import kotlinx.serialization.Serializable

@Serializable
data class MoveRequest(val board: List<List<Int>>)