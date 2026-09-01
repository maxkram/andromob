package ru.school21.tictactoe.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
import ru.school21.tictactoe.domain.repository.AuthRepository

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    private val disposables = CompositeDisposable()

    fun register(login: String, password: String, repeat: String) {
        when {
            login.isBlank() || password.isBlank() ->
                _state.value = State.Error("All fields are required")
            password != repeat ->
                _state.value = State.Error("Passwords do not match")
            else -> {
                _state.value = State.Loading
                disposables.add(
                    authRepository.register(login, password)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { _state.value = State.Success },
                            { e ->
                                val msg = if (e is HttpException)
                                    "Registration failed: ${e.code()}"
                                else "No connection to server"
                                _state.value = State.Error(msg)
                            }
                        )
                )
            }
        }
    }

    override fun onCleared() = disposables.clear()
}