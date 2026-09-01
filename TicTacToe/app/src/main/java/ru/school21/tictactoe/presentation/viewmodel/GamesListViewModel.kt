package ru.school21.tictactoe.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
import ru.school21.tictactoe.domain.model.GameStatus
import ru.school21.tictactoe.domain.repository.AuthRepository
import ru.school21.tictactoe.domain.repository.GameRepository
import ru.school21.tictactoe.domain.repository.UserRepository
import ru.school21.tictactoe.presentation.model.GameItemViewData

class GamesListViewModel(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed class State {
        object Loading : State()
        data class Content(val games: List<GameItemViewData>) : State()
        data class Error(val message: String) : State()
        object Unauthorized : State()
    }

    sealed class Event {
        data class OpenGame(val gameId: String) : Event()
        data class Error(val message: String) : Event()
        object LoggedOut : Event()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> = _event

    private val disposables = CompositeDisposable()

    fun refresh() {
        _state.value = State.Loading
        disposables.add(
            gameRepository.getGames()
                .map { list -> list.filter { it.status == GameStatus.WAITING_FOR_PLAYERS } }
                .map { filtered -> filtered.map { GameItemViewData(it.id, it.playerXLogin ?: "?") } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _state.value = State.Content(it) },
                    { e ->
                        if ((e as? HttpException)?.code() == 401) {
                            _state.value = State.Unauthorized
                        } else {
                            _state.value = State.Error(e.message ?: "Failed to load games")
                        }
                    }
                )
        )
    }

    fun joinGame(gameId: String) {
        disposables.add(
            gameRepository.joinGame(gameId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _event.value = Event.OpenGame(gameId) },
                    { e ->
                        if ((e as? HttpException)?.code() == 401) {
                            _state.value = State.Unauthorized
                        } else {
                            _event.value = Event.Error(e.message ?: "Failed to join")
                        }
                    }
                )
        )
    }

    fun logout() {
        disposables.add(
            authRepository.logout()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _event.value = Event.LoggedOut }
        )
    }

    override fun onCleared() = disposables.clear()
}