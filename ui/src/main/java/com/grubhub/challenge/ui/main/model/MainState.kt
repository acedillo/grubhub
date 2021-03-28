package com.grubhub.challenge.ui.main.model

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grubhub.challenge.ui.main.view.MainActivity
import com.grubhub.challenge.ui.main.view.RecentFavoritesAdapter
import com.grubhub.mvi.model.MviState
import kotlinx.parcelize.Parcelize

/**
 * Visual state data used for rendering the main view
 */
@Parcelize
data class MainState(
    val imageUrl: String,
    val episode: String,
    val isFavorite: Boolean = false
) : MviState