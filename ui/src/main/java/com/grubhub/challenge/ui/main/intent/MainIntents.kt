package com.grubhub.challenge.ui.main.intent

import com.grubhub.mvi.intent.MviIntent

/**
 * A collection of systematic or user driven actions to be processed by the model.
 */
sealed class MainIntents : MviIntent {
    /**
     * An intent to get a new meme for display
     */
    object RefreshIntent : MainIntents()

    /**
     * An intent to persist the currently displayed meme to the underlying store
     */
    object FavoriteIntent: MainIntents()

    /**
     * An intent to view all current favorites in a new activity
     */
    object ViewAllFavoritesIntent: MainIntents()
}