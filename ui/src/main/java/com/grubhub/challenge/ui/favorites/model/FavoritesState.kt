package com.grubhub.challenge.ui.favorites.model

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grubhub.challenge.service.model.Meme
import com.grubhub.challenge.ui.favorites.view.FavoritesAdapter
import com.grubhub.mvi.model.MviState
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavoritesState(
        val memes: List<Meme>
) : MviState {

    object Adapters {
//        @JvmStatic
//        @BindingAdapter("set_favorites")
        fun bindRecyclerview(view: RecyclerView, state: FavoritesState?) {
            state?.also {
                (view.adapter as FavoritesAdapter).update(it.memes)
            }
        }
    }

}