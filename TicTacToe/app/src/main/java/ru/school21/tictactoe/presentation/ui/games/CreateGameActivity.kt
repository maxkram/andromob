package ru.school21.tictactoe.presentation.ui.games

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.school21.tictactoe.TicTacToeApplication
import ru.school21.tictactoe.databinding.ActivityCreateGameBinding
import ru.school21.tictactoe.domain.repository.GameRepository
import ru.school21.tictactoe.presentation.ui.auth.LoginActivity
import ru.school21.tictactoe.presentation.viewmodel.CreateGameViewModel
import javax.inject.Inject

class CreateGameActivity : AppCompatActivity() {

    @Inject
    lateinit var gameRepository: GameRepository

    private lateinit var binding: ActivityCreateGameBinding
    private lateinit var viewModel: CreateGameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as TicTacToeApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityCreateGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = CreateGameViewModel(gameRepository)

        binding.btnVsComputer.setOnClickListener {
            viewModel.create(true)
        }

        binding.btnVsPlayer.setOnClickListener {
            viewModel.create(false)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        viewModel.event.observe(this) { event ->
            when (event) {
                is CreateGameViewModel.Event.GameCreated -> {
                    startActivity(
                        Intent(this, GameActivity::class.java)
                            .putExtra(GameActivity.EXTRA_GAME_ID, event.gameId)
                            .putExtra(
                                GameActivity.EXTRA_VS_COMPUTER,
                                event.vsComputer
                            )
                    )
                    finish()
                }

                is CreateGameViewModel.Event.Error -> {
                    Toast.makeText(
                        this,
                        event.message,
                        Toast.LENGTH_LONG
                    ).show()
                }

                is CreateGameViewModel.Event.Unauthorized -> {
                    navigateToLogin()
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}