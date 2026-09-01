package ru.school21.tictactoe

import io.ktor.server.application.Application
import ru.school21.tictactoe.web.module.configureKoin
import ru.school21.tictactoe.web.module.configureRouting
import ru.school21.tictactoe.web.module.configureSerialization

fun Application.module() {
    configureKoin()
    configureSerialization()
    configureRouting()
}