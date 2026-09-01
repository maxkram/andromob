package ru.school21.tictactoe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthRequestDto(
    @SerializedName("login")
    val login: String,

    @SerializedName("password")
    val password: String
)

data class SignUpRequestDto(
    @SerializedName("login")
    val login: String,

    @SerializedName("password")
    val password: String
)

data class SignInResponseDto(
    @SerializedName("uuid")
    val userId: String,

    @SerializedName("login")
    val login: String
)