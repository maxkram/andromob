package ru.school21.tictactoe.data.mapper

import com.google.gson.Gson
import ru.school21.tictactoe.data.local.entity.CurrentUserEntity
import ru.school21.tictactoe.data.local.entity.GameEntity
import ru.school21.tictactoe.data.local.entity.UserEntity
import ru.school21.tictactoe.data.remote.dto.GameDto
import ru.school21.tictactoe.data.remote.dto.UserDto
import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.domain.model.GameStatus
import ru.school21.tictactoe.domain.model.User

object UserMapper {

    fun toDomain(dto: UserDto, password: String = ""): User {
        return User(
            id = dto.id,
            login = dto.login,
            password = password
        )
    }

    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            login = entity.login,
            password = entity.password
        )
    }

    fun toEntity(user: User): UserEntity {
        return UserEntity(
            id = user.id,
            login = user.login,
            password = user.password
        )
    }

    fun toCurrentUserEntity(user: User): CurrentUserEntity {
        return CurrentUserEntity(
            id = user.id,
            login = user.login,
            password = user.password
        )
    }
}
