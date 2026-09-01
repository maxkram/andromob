package ru.school21.tictactoe.datasource.repository

import ru.school21.tictactoe.datasource.model.Game
import java.util.UUID

interface GameRepository {
    fun save(game: Game)
    fun findById(id: UUID): Game?
    fun existsById(id: UUID): Boolean
    fun deleteById(id: UUID)
}