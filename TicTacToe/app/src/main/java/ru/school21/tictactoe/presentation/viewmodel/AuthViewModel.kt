package ru.school21.tictactoe.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
import ru.school21.tictactoe.domain.repository.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        data class Error(val message: String) : State()
    }

    private val _state = MutableLiveData<State>(State.Idle)
    val state: LiveData<State> = _state

    private val disposables = CompositeDisposable()

    fun login(login: String, password: String) {
        if (login.isBlank() || password.isBlank()) {
            _state.value = State.Error("Login and password must not be empty")
            return
        }
        _state.value = State.Loading
        disposables.add(
            authRepository.login(login, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _state.value = State.Success },
                    { e -> _state.value = State.Error(errorMessage(e)) }
                )
        )
    }

    fun resetState() { _state.value = State.Idle }

    private fun errorMessage(e: Throwable): String {
        return when (e) {
            is HttpException -> when (e.code()) {
                401 -> "Invalid login or password"
                else -> "Server error: ${e.code()}"
            }
            is java.io.IOException -> "No connection to server"
            else -> e.message ?: "Unknown error"
        }
    }

    override fun onCleared() = disposables.clear()
}