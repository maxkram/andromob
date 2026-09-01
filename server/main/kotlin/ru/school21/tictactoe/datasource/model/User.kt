package ru.school21.tictactoe.datasource.model

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val uuid = varchar("uuid", 36)
    val login = varchar("login", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 64)

    override val primaryKey = PrimaryKey(uuid)
}