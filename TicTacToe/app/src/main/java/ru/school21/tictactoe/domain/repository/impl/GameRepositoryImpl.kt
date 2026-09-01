package ru.school21.tictactoe.domain.repository.impl

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.school21.tictactoe.data.local.AppDatabase
import ru.school21.tictactoe.data.mapper.GameMapper
import ru.school21.tictactoe.data.remote.ApiService
import ru.school21.tictactoe.data.remote.dto.CreateGameRequestDto
import ru.school21.tictactoe.data.remote.dto.MoveRequestDto
import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.domain.repository.GameRepository

class GameRepositoryImpl(
    private val apiService: ApiService,
    private val database: AppDatabase
) : GameRepository {

    override fun getGames(): Single<List<Game>> {
        return apiService.getGames()
            .map { list -> list.map { GameMapper.toDomain(it) } }
            .subscribeOn(Schedulers.io())
    }

    override fun createGame(vsComputer: Boolean): Single<Game> {
        return apiService.createGame(CreateGameRequestDto(vsComputer))
            .map { GameMapper.toDomain(it) }
            .subscribeOn(Schedulers.io())
    }

    override fun getGame(gameId: String): Single<Game> {
        return apiService.getGame(gameId)
            .map { dto ->
                val game = GameMapper.toDomain(dto)
                database.gameDao().insert(GameMapper.toEntity(game)).subscribe()
                game
            }
            .subscribeOn(Schedulers.io())
    }

    override fun joinGame(gameId: String): Single<Game> {
        return apiService.joinGame(gameId)
            .map { GameMapper.toDomain(it) }
            .subscribeOn(Schedulers.io())
    }

    override fun makeMove(gameId: String, row: Int, col: Int): Single<Game> {
        return apiService.makeMove(gameId, MoveRequestDto(row, col))
            .map { GameMapper.toDomain(it) }
            .subscribeOn(Schedulers.io())
    }

    override fun getCachedGames(): Single<List<Game>> {
        return database.gameDao().getAll()
            .map { entities -> entities.map { GameMapper.toDomain(it) } }
            .subscribeOn(Schedulers.io())
    }
}