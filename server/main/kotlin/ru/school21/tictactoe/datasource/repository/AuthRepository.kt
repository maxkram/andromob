package ru.school21.tictactoe.datasource.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.school21.tictactoe.datasource.model.Users
import ru.school21.tictactoe.domain.service.AuthService
import java.security.MessageDigest
import java.util.UUID

class AuthRepository : AuthService {

    override suspend fun register(login: String, password: String): Boolean = transaction {
        val exists = Users
            .select { Users.login eq login }
            .count() > 0

        if (exists) {
            false
        } else {
            Users.insert {
                it[uuid] = UUID.randomUUID().toString()
                it[Users.login] = login
                it[passwordHash] = sha256(password)
            }
            true
        }
    }

    override suspend fun login(credentials: String): String? = transaction {
        val separator = credentials.indexOf(':')
        if (separator <= 0) return@transaction null

        val login = credentials.substring(0, separator)
        val password = credentials.substring(separator + 1)

        Users
            .select { Users.login eq login }
            .singleOrNull()
            ?.let { row ->
                if (row[Users.passwordHash] == sha256(password)) {
                    row[Users.uuid]
                } else {
                    null
                }
            }
    }

    private fun sha256(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
}