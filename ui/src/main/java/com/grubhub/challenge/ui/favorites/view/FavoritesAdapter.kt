package com.grubhub.challenge.ui.favorites.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.grubhub.challenge.service.model.Meme
import com.grubhub.challenge.ui.databinding.CellFavoriteBinding


class FavoritesAdapter : RecyclerView.Adapter<FavoritesAdapter.Holder>() {

    private var data = listOf<Meme>()

    fun update(memes: List<Meme>) {
        data = memes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CellFavoriteBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = data.size

    inner class Holder(private val binding: CellFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val meme = data[position]
            Glide.with(itemView)
                    .load(meme.imageUrl)
                    .into(binding.memeThumb)
        }
    }

}