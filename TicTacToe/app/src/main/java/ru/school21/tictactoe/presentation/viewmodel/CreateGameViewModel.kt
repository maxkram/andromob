package ru.school21.tictactoe.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
import ru.school21.tictactoe.domain.repository.GameRepository

class CreateGameViewModel(private val gameRepository: GameRepository) : ViewModel() {

    sealed class Event {
        data class GameCreated(
            val gameId: String,
            val vsComputer: Boolean
        ) : Event()

        data class Error(val message: String) : Event()

        object Unauthorized : Event()
    }

    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> = _event

    private val disposables = CompositeDisposable()

    fun create(vsComputer: Boolean) {
        disposables.add(
            gameRepository.createGame(vsComputer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { game ->
                        _event.value = Event.GameCreated(
                            game.id,
                            vsComputer
                        )
                    },
                    { error ->
                        _event.value =
                            if ((error as? HttpException)?.code() == 401) {
                                Event.Unauthorized
                            } else {
                                Event.Error(
                                    error.message ?: "Failed to create game"
                                )
                            }
                    }
                )
        )
    }

    override fun onCleared() = disposables.clear()
}