package ru.school21.tictactoe.web.module

import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin
import ru.school21.tictactoe.di.appModule

fun Application.configureKoin() {
    install(Koin) {
        modules(appModule)
    }
}