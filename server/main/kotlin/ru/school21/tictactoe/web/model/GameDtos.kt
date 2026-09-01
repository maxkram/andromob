package ru.school21.tictactoe.web.model

data class CreateGameRequest(
    val againstComputer: Boolean
)

data class PlayerResponse(
    val uuid: String,
    val login: String,
    val mark: String
)
