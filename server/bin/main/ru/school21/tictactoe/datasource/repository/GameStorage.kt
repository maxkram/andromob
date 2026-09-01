package ru.school21.tictactoe.datasource.repository

import ru.school21.tictactoe.datasource.model.Game
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class GameStorage {
    private val games = ConcurrentHashMap<UUID, Game>()

    fun save(game: Game) { games[game.id] = game }
    fun findById(id: UUID): Game? = games[id]
    fun existsById(id: UUID): Boolean = games.containsKey(id)
    fun deleteById(id: UUID) { games.remove(id) }
}