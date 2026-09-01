package ru.school21.tictactoe.presentation.ui.games

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.school21.tictactoe.TicTacToeApplication
import ru.school21.tictactoe.databinding.ActivityGamesListBinding
import ru.school21.tictactoe.domain.repository.AuthRepository
import ru.school21.tictactoe.domain.repository.GameRepository
import ru.school21.tictactoe.domain.repository.UserRepository
import ru.school21.tictactoe.presentation.ui.adapter.GamesAdapter
import ru.school21.tictactoe.presentation.ui.auth.LoginActivity
import ru.school21.tictactoe.presentation.viewmodel.GamesListViewModel
import javax.inject.Inject

class GamesListActivity : AppCompatActivity() {

    @Inject lateinit var gameRepository: GameRepository
    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var authRepository: AuthRepository

    private lateinit var binding: ActivityGamesListBinding
    private lateinit var viewModel: GamesListViewModel
    private val adapter = GamesAdapter { item -> viewModel.joinGame(item.gameId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as TicTacToeApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityGamesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = GamesListViewModel(gameRepository, userRepository, authRepository)

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter
        binding.btnRefresh.setOnClickListener { viewModel.refresh() }
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }
        binding.btnCreateGame.setOnClickListener {
            startActivity(Intent(this, CreateGameActivity::class.java))
        }
        binding.btnLogout.setOnClickListener { viewModel.logout() }

        viewModel.state.observe(this) { state ->
            binding.swipeRefresh.isRefreshing = state is GamesListViewModel.State.Loading
            when (state) {
                is GamesListViewModel.State.Content -> adapter.submit(state.games)
                is GamesListViewModel.State.Error ->
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                is GamesListViewModel.State.Unauthorized -> navigateToLogin()
                else -> Unit
            }
        }

        viewModel.event.observe(this) { event ->
            when (event) {
                is GamesListViewModel.Event.OpenGame ->
                    startActivity(Intent(this, GameActivity::class.java)
                        .putExtra(GameActivity.EXTRA_GAME_ID, event.gameId))
                is GamesListViewModel.Event.Error ->
                    Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show()
                is GamesListViewModel.Event.LoggedOut -> navigateToLogin()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}