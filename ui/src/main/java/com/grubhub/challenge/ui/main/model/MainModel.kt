package com.grubhub.challenge.ui.main.model

import android.content.Intent
import com.grubhub.challenge.service.domain.IFavoriteService
import com.grubhub.challenge.service.domain.IMemeService
import com.grubhub.challenge.ui.favorites.view.FavoritesActivity
import com.grubhub.challenge.ui.main.intent.MainIntents
import com.grubhub.mvi.model.BaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MainModel @Inject constructor(
    private val memeService: IMemeService,
    private val favoriteService: IFavoriteService
): BaseModel<MainIntents, MainState>(), CoroutineScope {

    /**
     * This is the sole way MviViews should interact with their view models. The publish call is the
     * conduit through which a view issues it's intended actions or "intents". Based on the type
     * of intent we receive, we route to the appropriate flow of logic.
     */
    override fun publish(intent: MainIntents) {
        when (intent) {
            is MainIntents.RefreshIntent -> fetchRandomMeme()
            is MainIntents.FavoriteIntent -> favorite()
            is MainIntents.ViewAllFavoritesIntent -> viewAllFavorites()
        }
    }

    /**
     * This method is invoked when an MviView first registers with the model (this is handled by the
     * framework). This is a good spot to kick off initialization logic.
     */
    override fun onFirstViewRegistered() {
        if (getLastState<MainState>() == null) fetchRandomMeme()
    }

    /**
     * Leverage the IMemeService implementation to fetch a random meme from the Frinkiac API. With
     * the details provided by the service we scaffold a new state and dispatch it to update the
     * MviView's UI.
     */
    private fun fetchRandomMeme() {
        launch {
            memeService.getRandomMeme()?.also { meme ->
                val state = MainState(
                    meme.imageUrl,
                    meme.titleOfEpisode
                )

                withContext(Dispatchers.Main) { dispatchStates(state) }
            }
        }
    }

    /**
     * A user has published their intent to favorite the current meme. We know exactly what that is
     * from the current state so we retrieve that from the cache.
     */
    private fun favorite() {
        launch {
            getLastState<MainState>()?.also { lastState ->
                if (lastState.isFavorite) {
                    // We've already favorited this - halt here
                    return@launch
                }

                favoriteService.addFavorite(lastState.imageUrl, lastState.episode)
                val newState = lastState.copy(isFavorite = true)
                withContext(Dispatchers.Main) { dispatchStates(newState) }
            }
        }
    }

    /**
     * Launch a new activity to display all of the meme's we have "favorited"
     */
    private fun viewAllFavorites() {
        Intent(context, FavoritesActivity::class.java).apply { activity.startActivity(this) }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

}