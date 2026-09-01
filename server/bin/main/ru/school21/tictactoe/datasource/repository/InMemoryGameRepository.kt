package ru.school21.tictactoe.datasource.repository

import ru.school21.tictactoe.datasource.model.Game
import java.util.UUID

class InMemoryGameRepository(private val storage: GameStorage) : GameRepository {
    override fun save(game: Game) = storage.save(game)
    override fun findById(id: UUID): Game? = storage.findById(id)
    override fun existsById(id: UUID): Boolean = storage.existsById(id)
    override fun deleteById(id: UUID) = storage.deleteById(id)
}