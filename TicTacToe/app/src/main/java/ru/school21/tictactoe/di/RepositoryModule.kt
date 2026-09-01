package ru.school21.tictactoe.di

import dagger.Module
import dagger.Provides
import ru.school21.tictactoe.data.local.AppDatabase
import ru.school21.tictactoe.data.remote.ApiService
import ru.school21.tictactoe.domain.repository.AuthRepository
import ru.school21.tictactoe.domain.repository.GameRepository
import ru.school21.tictactoe.domain.repository.UserRepository
import ru.school21.tictactoe.domain.repository.impl.AuthRepositoryImpl
import ru.school21.tictactoe.domain.repository.impl.GameRepositoryImpl
import ru.school21.tictactoe.domain.repository.impl.UserRepositoryImpl
import javax.inject.Singleton

@Module
class RepositoryModule {
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        database: AppDatabase
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, database)
    }
    
    @Provides
    @Singleton
    fun provideGameRepository(
        apiService: ApiService,
        database: AppDatabase
    ): GameRepository {
        return GameRepositoryImpl(apiService, database)
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService,
        database: AppDatabase
    ): UserRepository {
        return UserRepositoryImpl(apiService, database)
    }
}