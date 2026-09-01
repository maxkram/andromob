package ru.school21.tictactoe.web.module

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import ru.school21.tictactoe.web.route.gameRoute

fun Application.configureRouting() {
    routing {
        gameRoute()
    }
}