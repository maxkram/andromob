package ru.school21.tictactoe.domain.service

import ru.school21.tictactoe.datasource.mapper.GameDataSourceMapper
import ru.school21.tictactoe.datasource.repository.GameRepository
import ru.school21.tictactoe.domain.model.Cell
import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.domain.model.GameBoard

class TicTacToeGameService(
    private val repository: GameRepository,
    private val mapper: GameDataSourceMapper,
    private val moveSelector: MinimaxMoveSelector
) : GameService {

    override fun getNextMove(game: Game): Game {
        require(game.id != null) { "Game id must not be null" }

        val savedGame = repository.findById(game.id)?.let(mapper::toDomain)
            ?: Game(game.id, GameBoard(Array(3) { IntArray(3) }))

        if (!isBoardValid(game, savedGame))
            throw InvalidGameException("Invalid board transition")
        if (isGameFinished(game))
            throw InvalidGameException("Game is already finished")

        val boardAfterComputerMove = BoardUtils.copy(game.board)
        val move = moveSelector.selectMove(boardAfterComputerMove)
        boardAfterComputerMove.cells[move.first][move.second] = Cell.COMPUTER

        val updatedGame = Game(game.id, boardAfterComputerMove)
        repository.save(mapper.toDataSource(updatedGame))
        return updatedGame
    }

    override fun isBoardValid(currentGame: Game, savedGame: Game): Boolean =
        BoardTransitionValidator.isValidHumanMove(savedGame.board, currentGame.board)

    override fun isGameFinished(game: Game): Boolean {
        if (!BoardValidator.hasValidShapeAndValues(game.board)) return true
        return BoardRules.hasWinner(game.board, Cell.HUMAN) ||
            BoardRules.hasWinner(game.board, Cell.COMPUTER) ||
            !BoardRules.hasEmptyCell(game.board)
    }
}