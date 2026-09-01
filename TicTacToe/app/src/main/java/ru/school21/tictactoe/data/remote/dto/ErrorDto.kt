
package ru.school21.tictactoe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ErrorDto(
    @SerializedName("message")
    val message: String
)