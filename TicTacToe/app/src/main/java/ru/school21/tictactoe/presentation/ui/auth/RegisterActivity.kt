package ru.school21.tictactoe.presentation.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.school21.tictactoe.TicTacToeApplication
import ru.school21.tictactoe.databinding.ActivityRegisterBinding
import ru.school21.tictactoe.domain.repository.AuthRepository
import ru.school21.tictactoe.presentation.viewmodel.RegisterViewModel
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as TicTacToeApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = RegisterViewModel(authRepository)

        binding.btnRegister.setOnClickListener {
            viewModel.register(
                binding.etLogin.text.toString(),
                binding.etPassword.text.toString(),
                binding.etPasswordRepeat.text.toString()
            )
        }

        viewModel.state.observe(this) { state ->
            binding.progress.visibility =
                if (state is RegisterViewModel.State.Loading) View.VISIBLE else View.GONE
            when (state) {
                is RegisterViewModel.State.Success -> {
                    Toast.makeText(this, "Registered! Now log in", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is RegisterViewModel.State.Error ->
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                else -> Unit
            }
        }
    }
}