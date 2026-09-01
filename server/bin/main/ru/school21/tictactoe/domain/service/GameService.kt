package ru.school21.tictactoe.domain.service

import ru.school21.tictactoe.domain.model.Game

interface GameService {
    fun getNextMove(game: Game): Game
    fun isBoardValid(currentGame: Game, savedGame: Game): Boolean
    fun isGameFinished(game: Game): Boolean
}