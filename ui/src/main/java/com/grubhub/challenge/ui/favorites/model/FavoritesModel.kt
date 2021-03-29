package com.grubhub.challenge.ui.favorites.model

import android.database.ContentObserver
import com.grubhub.challenge.service.domain.IFavoriteService
import com.grubhub.challenge.ui.databinding.ActivityFavoritesBinding
import com.grubhub.challenge.ui.favorites.intent.FavoritesIntents
import com.grubhub.challenge.ui.main.model.RecentFavoritesState
import com.grubhub.mvi.model.BaseModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class FavoritesModel @Inject constructor(
        private val favoritesService: IFavoriteService
) : BaseModel<FavoritesIntents, FavoritesState>(), CoroutineScope {

    private var binding: ActivityFavoritesBinding? = null
    private lateinit var observer: ContentObserver

    /**
     * This is the sole way MviViews should interact with their view models. The publish call is the
     * conduit through which a view issues it's intended actions or "intents". Based on the type
     * of intent we receive, we route to the appropriate flow of logic.
     */
    override fun publish(intent: FavoritesIntents) {
        when (intent) {
            is FavoritesIntents.BindingIntent -> bind(intent)
        }
    }

    /**
     * This method is invoked when an MviView first registers with the model (this is handled by the
     * framework). This is a good spot to kick off initialization logic.
     */
    override fun onFirstViewRegistered() {
        launch {
            val memes = favoritesService.fetchFavorites()
            val state = FavoritesState(memes)
            withContext(Dispatchers.Main) { dispatchStates(state) }
        }

        observer = favoritesService.observeFavorites { favorites ->
            val state = FavoritesState(favorites)
            dispatchStates(state)
        }
    }

    /**
     * Here we respond to a BindingIntent setting our local, private reference to the MviView's
     * view binding.
     */
    private fun bind(intent: FavoritesIntents.BindingIntent) {
        binding = intent.binding
    }

    override fun bindData(state: FavoritesState) {
        binding?.state = state
    }


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}