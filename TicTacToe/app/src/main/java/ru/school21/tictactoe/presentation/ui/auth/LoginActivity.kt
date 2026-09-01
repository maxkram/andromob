package ru.school21.tictactoe.presentation.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.school21.tictactoe.TicTacToeApplication
import ru.school21.tictactoe.databinding.ActivityLoginBinding
import ru.school21.tictactoe.domain.repository.AuthRepository
import ru.school21.tictactoe.presentation.ui.games.GamesListActivity
import ru.school21.tictactoe.presentation.viewmodel.AuthViewModel
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as TicTacToeApplication)
            .appComponent
            .inject(this)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = AuthViewModel(authRepository)

        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.etLogin.text?.toString().orEmpty(),
                binding.etPassword.text?.toString().orEmpty()
            )
        }

        binding.btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.state.observe(this) { state ->
            binding.progress.visibility =
                if (state is AuthViewModel.State.Loading) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            when (state) {
                is AuthViewModel.State.Success -> {
                    val intent = Intent(this, GamesListActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }

                is AuthViewModel.State.Error -> {
                    Toast.makeText(
                        this,
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.resetState()
                }

                else -> Unit
            }
        }
    }
}