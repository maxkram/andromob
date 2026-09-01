package ru.school21.tictactoe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("uuid") val id: String,
    @SerializedName("login") val login: String
)