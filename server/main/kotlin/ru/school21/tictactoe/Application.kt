package ru.school21.tictactoe

import io.ktor.server.application.Application
import ru.school21.tictactoe.web.module.configureTicTacToe

fun Application.module() {
    configureTicTacToe()
}