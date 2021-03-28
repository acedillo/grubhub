package com.grubhub.challenge.ui.main.intent

import com.grubhub.challenge.ui.databinding.FragmentRecentFavoritesBinding
import com.grubhub.mvi.intent.MviIntent

/**
 * A collection of systematic or user driven actions to be processed by the model.
 */
sealed class RecentFavoriteIntents : MviIntent {
    /**
     * An intent to delegate rendering responsibility to the model directly
     *
     * @param binding The MviView's view binding
     */
    data class BindingIntent(val binding: FragmentRecentFavoritesBinding) : RecentFavoriteIntents()
}