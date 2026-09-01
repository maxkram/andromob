package ru.school21.tictactoe.domain.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import ru.school21.tictactoe.domain.model.User

interface AuthRepository {
    fun register(login: String, password: String): Completable
    fun login(login: String, password: String): Single<User>
    fun logout(): Completable
    fun getCurrentUser(): Maybe<User>   // было Single<User?>
}