package ru.school21.tictactoe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.domain.model.User

interface GameRepository {
    fun getGames(): Single<List<Game>>
    fun createGame(vsComputer: Boolean): Single<Game>
    fun getGame(gameId: String): Single<Game>
    fun joinGame(gameId: String): Single<Game>
    fun makeMove(gameId: String, row: Int, col: Int): Single<Game>
    fun getCachedGames(): Single<List<Game>>
}
