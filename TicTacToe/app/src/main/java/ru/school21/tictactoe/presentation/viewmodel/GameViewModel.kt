package ru.school21.tictactoe.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
import ru.school21.tictactoe.domain.model.Game
import ru.school21.tictactoe.domain.repository.AuthRepository
import ru.school21.tictactoe.domain.repository.GameRepository
import ru.school21.tictactoe.presentation.mapper.ViewDataMapper
import ru.school21.tictactoe.presentation.model.GameViewData
import java.util.concurrent.TimeUnit

class GameViewModel(
    private val gameRepository: GameRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class Event {
        data class Error(val message: String) : Event()
        object Unauthorized : Event()
    }

    private val _game = MutableLiveData<GameViewData>()
    val game: LiveData<GameViewData> = _game

    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> = _event

    private val disposables = CompositeDisposable()

    private var gameId: String = ""
    private var currentUserId: String = ""
    private var vsComputerHint: Boolean = false
    private var lastGame: Game? = null

    fun start(gameId: String, vsComputer: Boolean = false) {
        this.gameId = gameId
        this.vsComputerHint = vsComputer
        disposables.add(
            authRepository.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { user ->
                        currentUserId = user.id
                        startPolling()
                    },
                    { _event.value = Event.Unauthorized },
                    { _event.value = Event.Unauthorized }
                )
        )
    }

    private fun startPolling() {
        disposables.add(
            Observable.interval(0, 1, TimeUnit.SECONDS)
                .flatMapSingle {
                    gameRepository.getGame(gameId)
                        .doOnError { e -> handleError(e) }
                        .onErrorResumeNext(Single.never())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ g -> update(g) }, { })
        )
    }

    private fun update(g: Game) {
        lastGame = g
        _game.value = ViewDataMapper.toGameViewData(
            g, currentUserId,
            playerXLogin = g.playerXLogin,
            playerOLogin = g.playerOLogin,
            currentPlayerLogin = when (g.currentPlayerId) {
                g.playerXId -> g.playerXLogin
                g.playerOId -> g.playerOLogin
                else -> null
            },
            allowMoveWhenWaiting = vsComputerHint
        )
    }

    fun makeMove(row: Int, col: Int) {
        val viewData = _game.value ?: return
        if (!viewData.isBoardEnabled) return
        val current = lastGame ?: return
        if (current.board[row][col] != 0) return

        disposables.add(
            gameRepository.makeMove(gameId, row, col)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ g -> update(g) }, { e -> handleError(e) })
        )
    }

    private fun handleError(e: Throwable) {
        if ((e as? HttpException)?.code() == 401) {
            _event.postValue(Event.Unauthorized)
        } else {
            _event.postValue(Event.Error(e.message ?: "Network error"))
        }
    }

    override fun onCleared() = disposables.clear()
}