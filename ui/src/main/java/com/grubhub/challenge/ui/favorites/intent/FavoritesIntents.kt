package com.grubhub.challenge.ui.favorites.intent

import com.grubhub.challenge.ui.databinding.ActivityFavoritesBinding
import com.grubhub.mvi.intent.MviIntent

/**
 * A collection of systematic or user driven actions to be processed by the model.
 */
sealed class FavoritesIntents : MviIntent {
    /**
     * An intent to delegate rendering responsibility to the model directly
     *
     * @param binding The MviView's view binding
     */
    data class BindingIntent(val binding: ActivityFavoritesBinding) : FavoritesIntents()
}