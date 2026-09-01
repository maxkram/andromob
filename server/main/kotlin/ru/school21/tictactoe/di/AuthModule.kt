package ru.school21.tictactoe.di

import ru.school21.tictactoe.datasource.repository.AuthRepository
import ru.school21.tictactoe.domain.service.AuthService
import org.koin.dsl.module

val authModule = module {
    single<AuthService> { AuthRepository() }
}