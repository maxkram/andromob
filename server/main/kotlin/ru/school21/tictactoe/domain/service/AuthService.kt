package ru.school21.tictactoe.domain.service

import ru.school21.tictactoe.domain.model.User
import java.util.Base64

interface AuthService {
    suspend fun register(login: String, password: String): Boolean
    suspend fun login(credentials: String): String?
}