package ru.school21.tictactoe.datasource.model

import org.jetbrains.exposed.sql.Table

object Games : Table("games") {
    val uuid = varchar("uuid", 36)
    val state = varchar("state", 30)
    val currentTurn = varchar("current_turn", 36).nullable()
    val winner = varchar("winner", 36).nullable()
    val board = varchar("board", 100)
    val players = varchar("players", 1000)

    override val primaryKey = PrimaryKey(uuid)
}