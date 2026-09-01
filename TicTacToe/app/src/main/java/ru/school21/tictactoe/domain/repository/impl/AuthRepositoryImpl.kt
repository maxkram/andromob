package ru.school21.tictactoe.domain.repository.impl

import android.util.Base64
import androidx.room.EmptyResultSetException
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.school21.tictactoe.data.local.AppDatabase
import ru.school21.tictactoe.data.mapper.UserMapper
import ru.school21.tictactoe.data.remote.ApiService
import ru.school21.tictactoe.data.remote.dto.SignUpRequestDto
import ru.school21.tictactoe.domain.model.User
import ru.school21.tictactoe.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val database: AppDatabase
) : AuthRepository {

    override fun register(login: String, password: String): Completable {
        return apiService.signUp(SignUpRequestDto(login, password))
            .subscribeOn(Schedulers.io())
    }

    override fun login(login: String, password: String): Single<User> {
        val header = "Basic " + Base64.encodeToString(
            "$login:$password".toByteArray(), Base64.NO_WRAP
        )
        return apiService.signIn(header)
            .flatMap { response ->
                val user = User(response.userId, login, password)
                database.currentUserDao().getCurrent()
                    .map { it?.id ?: "" }
                    .onErrorReturnItem("")          // "" = пользователя нет (null запрещён в RxJava)
                    .flatMap { existingId ->
                        val wipe = if (existingId.isNotEmpty() && existingId != response.userId) {
                            database.gameDao().deleteAll()
                                .andThen(database.userDao().deleteAll())
                        } else {
                            Completable.complete()
                        }
                        wipe
                            .andThen(database.currentUserDao().deleteAll())
                            .andThen(database.currentUserDao().insert(UserMapper.toCurrentUserEntity(user)))
                            .andThen(database.userDao().insert(UserMapper.toEntity(user)))
                            .andThen(Single.just(user))
                    }
            }
            .subscribeOn(Schedulers.io())
    }

    override fun logout(): Completable {
        return database.currentUserDao().deleteAll()
            .andThen(database.userDao().deleteAll())
            .andThen(database.gameDao().deleteAll())
            .subscribeOn(Schedulers.io())
    }

    override fun getCurrentUser(): Maybe<User> {
        return database.currentUserDao().getCurrent()
            .filter { it != null }
            .map { User(it!!.id, it.login, it.password) }
            .onErrorComplete { e -> e is EmptyResultSetException }
            .subscribeOn(Schedulers.io())
    }
}