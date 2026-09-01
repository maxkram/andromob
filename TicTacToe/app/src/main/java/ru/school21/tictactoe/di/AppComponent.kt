package ru.school21.tictactoe.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ru.school21.tictactoe.presentation.ui.auth.LoginActivity
import ru.school21.tictactoe.presentation.ui.auth.RegisterActivity
import ru.school21.tictactoe.presentation.ui.games.GamesListActivity
import ru.school21.tictactoe.presentation.ui.games.CreateGameActivity
import ru.school21.tictactoe.presentation.ui.games.GameActivity
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {
    
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
    
    fun inject(activity: LoginActivity)
    fun inject(activity: RegisterActivity)
    fun inject(activity: GamesListActivity)
    fun inject(activity: CreateGameActivity)
    fun inject(activity: GameActivity)
}