package ru.school21.tictactoe.presentation.mapper

import ru.school21.tictactoe.domain.model.Cell
import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.domain.model.GameStatus
import ru.school21.tictactoe.presentation.model.GameViewData

object ViewDataMapper {

    fun toGameViewData(
        game: Game,
        currentUserId: String,
        playerXLogin: String?,
        playerOLogin: String?,
        currentPlayerLogin: String?,
        allowMoveWhenWaiting: Boolean = false
    ): GameViewData {
        // Игра против компьютера: сервер ждёт первый ход игрока
        val waitingFirstMove = game.status == GameStatus.WAITING_FOR_PLAYERS &&
                allowMoveWhenWaiting &&
                game.playerXId == currentUserId &&
                game.playerOId == null

        val isMyTurn = (game.status == GameStatus.IN_PROGRESS &&
                game.currentPlayerId == currentUserId) || waitingFirstMove

        val mySymbol = when (currentUserId) {
            game.playerXId -> Cell.X
            game.playerOId -> Cell.O
            else -> Cell.EMPTY
        }

        val statusText = when {
            waitingFirstMove -> "Your turn"
            game.status == GameStatus.WAITING_FOR_PLAYERS -> "Waiting for players"
            game.status == GameStatus.IN_PROGRESS ->
                if (isMyTurn) "Your turn"
                else "Opponent ${currentPlayerLogin ?: ""} is making a move"
            game.status == GameStatus.DRAW -> "Draw"
            game.status == GameStatus.WIN_X ->
                if (game.playerXId == currentUserId) "Victory" else "Defeat"
            game.status == GameStatus.WIN_O ->
                if (game.playerOId == currentUserId) "Victory" else "Defeat"
            else -> "Unknown"
        }

        return GameViewData(
            id = game.id,
            playerXLogin = playerXLogin,
            playerOLogin = playerOLogin,
            currentPlayerLogin = currentPlayerLogin,
            statusText = statusText,
            board = game.board,
            isMyTurn = isMyTurn,
            isBoardEnabled = isMyTurn,
            mySymbol = mySymbol
        )
    }
}