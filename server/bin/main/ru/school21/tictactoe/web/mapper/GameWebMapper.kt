package ru.school21.tictactoe.web.mapper

import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.web.model.GameResponse

class GameWebMapper {
    fun toResponse(game: Game): GameResponse =
        GameResponse(
            id = game.id.toString(),
            board = game.board.cells.map { row -> row.toList() },
            status = game.board.status()
        )
}