package ru.school21.tictactoe.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_user")
data class CurrentUserEntity(
    @PrimaryKey
    val id: String,

    val login: String,

    val password: String
)