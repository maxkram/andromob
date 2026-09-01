package ru.school21.tictactoe.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val playerXId: String?,
    val playerOId: String?,
    val playerXLogin: String? = null,
    val playerOLogin: String? = null,
    val currentPlayerId: String?,
    val status: String,
    val board: String,
    val vsComputer: Boolean
)