package ru.school21.tictactoe.di
import ru.school21.tictactoe.data.remote.AuthInterceptor
import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.school21.tictactoe.data.local.AppDatabase
import ru.school21.tictactoe.data.local.dao.CurrentUserDao
import ru.school21.tictactoe.data.local.dao.GameDao
import ru.school21.tictactoe.data.local.dao.UserDao
import ru.school21.tictactoe.data.remote.ApiClient
import ru.school21.tictactoe.data.remote.ApiService
import javax.inject.Singleton

@Module
abstract class AppModule {
    
    @Binds
    abstract fun bindContext(application: Application): Context
    
    companion object {
        
        @Provides
        @Singleton
        fun provideDatabase(application: Application): AppDatabase {
            return AppDatabase.getInstance(application)
        }
        
        @Provides
        @Singleton
        fun provideUserDao(database: AppDatabase): UserDao {
            return database.userDao()
        }
        
        @Provides
        @Singleton
        fun provideGameDao(database: AppDatabase): GameDao {
            return database.gameDao()
        }
        
        @Provides
        @Singleton
        fun provideCurrentUserDao(database: AppDatabase): CurrentUserDao {
            return database.currentUserDao()
        }

        @Provides
        @Singleton
        fun provideAuthInterceptor(currentUserDao: CurrentUserDao): AuthInterceptor {
            return AuthInterceptor(currentUserDao)
        }

        @Provides
        @Singleton
        fun provideApiService(authInterceptor: AuthInterceptor): ApiService {
            return ApiClient.create(authInterceptor)
        }
    }
}