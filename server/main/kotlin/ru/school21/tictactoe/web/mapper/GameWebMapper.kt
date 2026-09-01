package ru.school21.tictactoe.web.mapper

import ru.school21.tictactoe.domain.model.Cell
import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.domain.model.GameStatus
import ru.school21.tictactoe.web.model.GameResponse

class GameWebMapper {

    fun toResponse(game: Game): GameResponse {
        val gameStatus = game.board.status()

        return GameResponse(
            uuid = game.id.toString(),
            state = when (gameStatus) {
                GameStatus.IN_PROGRESS -> "TURN"
                GameStatus.DRAW -> "DRAW"
                GameStatus.HUMAN_WON,
                GameStatus.COMPUTER_WON -> "WIN"
            },
            currentTurn = null,
            winner = null,
            board = game.board.cells.map { row ->
                row.map { cell ->
                    when (cell) {
                        Cell.HUMAN -> "X"
                        Cell.COMPUTER -> "O"
                        else -> ""
                    }
                }
            },
            players = emptyList()
        )
    }
}