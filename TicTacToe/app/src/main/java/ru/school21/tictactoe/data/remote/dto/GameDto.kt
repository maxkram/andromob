package ru.school21.tictactoe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GameDto(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("state") val state: String,
    @SerializedName("currentTurn") val currentTurn: String?,
    @SerializedName("winner") val winner: String?,
    @SerializedName("board") val board: List<List<String>>,
    @SerializedName("players") val players: List<PlayerDto> = emptyList()
)

data class PlayerDto(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("login") val login: String,
    @SerializedName("mark") val mark: String
)

data class CreateGameRequestDto(
    @SerializedName("againstComputer") val againstComputer: Boolean
)

data class MoveRequestDto(
    @SerializedName("row") val row: Int,
    @SerializedName("col") val col: Int
)