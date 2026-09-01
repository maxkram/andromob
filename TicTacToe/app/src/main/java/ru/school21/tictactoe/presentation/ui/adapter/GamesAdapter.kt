package ru.school21.tictactoe.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.school21.tictactoe.databinding.ItemGameBinding
import ru.school21.tictactoe.presentation.model.GameItemViewData

class GamesAdapter(
    private val onClick: (GameItemViewData) -> Unit
) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

    private val items = mutableListOf<GameItemViewData>()

    fun submit(list: List<GameItemViewData>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) =
        holder.bind(items[position])

    override fun getItemCount() = items.size

    inner class GameViewHolder(private val binding: ItemGameBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GameItemViewData) {
            binding.tvGameId.text = "Game: ${item.gameId}"
            binding.tvCreator.text = "Creator: ${item.creatorLogin}"
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}