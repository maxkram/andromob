package ru.school21.tictactoe.datasource.mapper

import ru.school21.tictactoe.datasource.model.Game as DsGame
import ru.school21.tictactoe.datasource.model.GameBoard as DsGameBoard
import ru.school21.tictactoe.domain.model.Game as DomainGame
import ru.school21.tictactoe.domain.model.GameBoard as DomainGameBoard

class GameDataSourceMapper {

    fun toDataSource(game: DomainGame): DsGame =
        DsGame(game.id, DsGameBoard(copyCells(game.board.cells)))

    fun toDomain(game: DsGame): DomainGame =
        DomainGame(game.id, DomainGameBoard(copyCells(game.board.cells)))

    private fun copyCells(source: Array<IntArray>): Array<IntArray> =
        Array(source.size) { row -> source[row].clone() }
}