package ru.school21.tictactoe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.domain.model.User

interface UserRepository {
    fun getUser(userId: String): Single<User>
    fun getCachedUser(userId: String): Single<User?>
}