package ru.school21.tictactoe.web.route

import io.ktor.server.routing.Route

fun Route.gameRoute() {
    // The current game API is implemented in ApplicationModule.kt:
    // GET  /games
    // POST /games
    // GET  /games/{uuid}
    // POST /games/{uuid}/move
}