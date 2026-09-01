package ru.school21.tictactoe.domain.service

import java.util.UUID

class GameNotFoundException(gameId: UUID) : RuntimeException("Game not found: $gameId")