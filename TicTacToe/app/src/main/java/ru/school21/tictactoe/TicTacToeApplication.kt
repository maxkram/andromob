package ru.school21.tictactoe

import android.app.Application
import ru.school21.tictactoe.di.AppComponent
import ru.school21.tictactoe.di.DaggerAppComponent

class TicTacToeApplication : Application() {
    
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .build()
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: TicTacToeApplication
            private set
    }
}