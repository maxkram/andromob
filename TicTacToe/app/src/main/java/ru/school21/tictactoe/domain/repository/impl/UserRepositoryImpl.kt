package ru.school21.tictactoe.domain.repository.impl

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.school21.tictactoe.data.local.AppDatabase
import ru.school21.tictactoe.data.mapper.GameMapper
import ru.school21.tictactoe.data.mapper.UserMapper
import ru.school21.tictactoe.data.remote.ApiService
import ru.school21.tictactoe.data.remote.dto.AuthRequestDto
import ru.school21.tictactoe.data.remote.dto.CreateGameRequestDto
import ru.school21.tictactoe.data.remote.dto.MoveRequestDto
import ru.school21.tictactoe.data.remote.dto.SignUpRequestDto
import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.domain.model.User
import ru.school21.tictactoe.domain.repository.AuthRepository
import ru.school21.tictactoe.domain.repository.GameRepository
import ru.school21.tictactoe.domain.repository.UserRepository

class UserRepositoryImpl(
    private val apiService: ApiService,
    private val database: AppDatabase
) : UserRepository {

    override fun getUser(userId: String): Single<User> {
        return apiService.getUser(userId)
            .map { dto -> UserMapper.toDomain(dto) }
            .subscribeOn(Schedulers.io())
    }

    override fun getCachedUser(userId: String): Single<User?> {
        return database.userDao().getById(userId)
            .map { entity -> entity?.let { UserMapper.toDomain(it) } }
            .subscribeOn(Schedulers.io())
    }
}