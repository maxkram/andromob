package ru.school21.tictactoe.data.mapper

import com.google.gson.Gson
import ru.school21.tictactoe.data.local.entity.GameEntity
import ru.school21.tictactoe.data.remote.dto.GameDto
import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.domain.model.GameStatus

object GameMapper {

    private val gson = Gson()

    fun toDomain(dto: GameDto): Game {
        val x = dto.players.firstOrNull { it.mark.equals("X", true) }
        val o = dto.players.firstOrNull { it.mark.equals("O", true) }
        val (status, currentId) = parseState(dto)
        return Game(
            id = dto.uuid,
            playerXId = x?.uuid,
            playerOId = o?.uuid,
            playerXLogin = x?.login,
            playerOLogin = o?.login,
            currentPlayerId = currentId,
            status = status,
            board = dto.board.map { row -> row.map { cellToInt(it) }.toIntArray() }.toTypedArray(),
            vsComputer = dto.players.any { it.uuid == "computer" }
        )
    }

    private fun cellToInt(cell: String): Int = when (cell.uppercase()) {
        "X" -> 1
        "O" -> 2
        else -> 0
    }

    private fun parseState(dto: GameDto): Pair<GameStatus, String?> {
        return when (dto.state) {
            "WAITING_FOR_PLAYERS" -> GameStatus.WAITING_FOR_PLAYERS to null
            "TURN" -> GameStatus.IN_PROGRESS to dto.currentTurn
            "DRAW" -> GameStatus.DRAW to null
            "WIN" -> {
                val winnerMark = dto.players.firstOrNull { it.uuid == dto.winner }?.mark
                when {
                    winnerMark.equals("X", true) -> GameStatus.WIN_X to null
                    winnerMark.equals("O", true) -> GameStatus.WIN_O to null
                    else -> GameStatus.UNKNOWN to null
                }
            }
            else -> GameStatus.UNKNOWN to null
        }
    }

    fun toEntity(game: Game): GameEntity = GameEntity(
        id = game.id,
        playerXId = game.playerXId,
        playerOId = game.playerOId,
        playerXLogin = game.playerXLogin,
        playerOLogin = game.playerOLogin,
        currentPlayerId = game.currentPlayerId,
        status = game.status.name,
        board = gson.toJson(game.board),
        vsComputer = game.vsComputer
    )

    fun toDomain(entity: GameEntity): Game = Game(
        id = entity.id,
        playerXId = entity.playerXId,
        playerOId = entity.playerOId,
        playerXLogin = entity.playerXLogin,
        playerOLogin = entity.playerOLogin,
        currentPlayerId = entity.currentPlayerId,
        status = try {
            GameStatus.valueOf(entity.status)
        } catch (e: Exception) {
            GameStatus.UNKNOWN
        },
        board = gson.fromJson(entity.board, Array<IntArray>::class.java),
        vsComputer = entity.vsComputer
    )
}