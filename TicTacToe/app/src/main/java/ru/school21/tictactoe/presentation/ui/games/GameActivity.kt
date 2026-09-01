package ru.school21.tictactoe.presentation.ui.games

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.school21.tictactoe.R
import ru.school21.tictactoe.TicTacToeApplication
import ru.school21.tictactoe.databinding.ActivityGameBinding
import ru.school21.tictactoe.domain.repository.AuthRepository
import ru.school21.tictactoe.domain.repository.GameRepository
import ru.school21.tictactoe.presentation.ui.auth.LoginActivity
import ru.school21.tictactoe.presentation.viewmodel.GameViewModel
import javax.inject.Inject

class GameActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_GAME_ID = "extra_game_id"
        const val EXTRA_VS_COMPUTER = "extra_vs_computer"
    }

    @Inject lateinit var gameRepository: GameRepository
    @Inject lateinit var authRepository: AuthRepository

    private lateinit var binding: ActivityGameBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var cells: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as TicTacToeApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = GameViewModel(gameRepository, authRepository)

        cells = listOf(
            findViewById(R.id.btn0), findViewById(R.id.btn1), findViewById(R.id.btn2),
            findViewById(R.id.btn3), findViewById(R.id.btn4), findViewById(R.id.btn5),
            findViewById(R.id.btn6), findViewById(R.id.btn7), findViewById(R.id.btn8)
        )
        cells.forEachIndexed { index, button ->
            button.setOnClickListener { viewModel.makeMove(index / 3, index % 3) }
        }

        binding.btnBack.setOnClickListener { finish() }

        viewModel.game.observe(this) { data ->
            binding.tvGameId.text = "Game: ${data.id}"
            binding.tvPlayers.text =
                "X: ${data.playerXLogin ?: "-"}   O: ${data.playerOLogin ?: "-"}"
            binding.tvStatus.text = data.statusText
            cells.forEachIndexed { index, button ->
                val value = data.board[index / 3][index % 3]
                button.text = when (value) {
                    1 -> "X"
                    2 -> "O"
                    else -> ""
                }
                button.isEnabled = data.isBoardEnabled && value == 0
            }
        }

        viewModel.event.observe(this) { event ->
            when (event) {
                is GameViewModel.Event.Error ->
                    Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
                is GameViewModel.Event.Unauthorized -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
        }

        val gameId = intent.getStringExtra(EXTRA_GAME_ID)
        val vsComputer = intent.getBooleanExtra(EXTRA_VS_COMPUTER, false)
        if (gameId == null) {
            finish()
        } else {
            viewModel.start(gameId, vsComputer)
        }
    }
}