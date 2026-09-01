package ru.school21.tictactoe.web.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.domain.model.GameBoard
import ru.school21.tictactoe.domain.service.GameNotFoundException
import ru.school21.tictactoe.domain.service.GameService
import ru.school21.tictactoe.domain.service.InvalidGameException
import ru.school21.tictactoe.web.mapper.GameWebMapper
import ru.school21.tictactoe.web.model.ErrorResponse
import ru.school21.tictactoe.web.model.MoveRequest
import java.util.UUID

fun Route.gameRoute() {
    val gameService by inject<GameService>()
    val webMapper by inject<GameWebMapper>()

    post("/game/{id}") {
        try {
            val id = UUID.fromString(call.parameters["id"])
            val request = call.receive<MoveRequest>()

            val gameAfterHumanMove = Game(
                id = id,
                board = GameBoard(request.board.map { it.toIntArray() }.toTypedArray())
            )

            val updatedGame = gameService.getNextMove(gameAfterHumanMove)
            call.respond(HttpStatusCode.OK, webMapper.toResponse(updatedGame))
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid request"))
        } catch (e: InvalidGameException) {
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(e.message ?: "Invalid game"))
        } catch (e: GameNotFoundException) {
            call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message ?: "Game not found"))
        }
    }
}