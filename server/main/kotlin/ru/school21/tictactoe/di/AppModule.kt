package ru.school21.tictactoe.di

import org.koin.dsl.module
import ru.school21.tictactoe.datasource.mapper.GameDataSourceMapper
import ru.school21.tictactoe.datasource.repository.GameRepository
import ru.school21.tictactoe.datasource.repository.GameStorage
import ru.school21.tictactoe.datasource.repository.InMemoryGameRepository
import ru.school21.tictactoe.domain.service.GameService
import ru.school21.tictactoe.domain.service.MinimaxMoveSelector
import ru.school21.tictactoe.domain.service.TicTacToeGameService
import ru.school21.tictactoe.web.mapper.GameWebMapper

val appModule = module {
    single { GameStorage() }
    single<GameRepository> { InMemoryGameRepository(get()) }
    single { GameDataSourceMapper() }
    single { MinimaxMoveSelector() }
    single<GameService> { TicTacToeGameService(get(), get(), get()) }
    single { GameWebMapper() }
}