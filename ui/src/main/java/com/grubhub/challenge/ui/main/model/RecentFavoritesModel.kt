package com.grubhub.challenge.ui.main.model

import android.database.ContentObserver
import com.grubhub.challenge.service.domain.IFavoriteService
import com.grubhub.challenge.ui.databinding.FragmentRecentFavoritesBinding
import com.grubhub.challenge.ui.main.intent.RecentFavoriteIntents
import com.grubhub.mvi.model.BaseModel
import javax.inject.Inject

class RecentFavoritesModel @Inject constructor(
    private val service: IFavoriteService
) : BaseModel<RecentFavoriteIntents, RecentFavoritesState>() {

    private var binding: FragmentRecentFavoritesBinding? = null
    private lateinit var observer: ContentObserver

    /**
     * This is the sole way MviViews should interact with their view models. The publish call is the
     * conduit through which a view issues it's intended actions or "intents". Based on the type
     * of intent we receive, we route to the appropriate flow of logic.
     */
    override fun publish(intent: RecentFavoriteIntents) {
        when (intent) {
            is RecentFavoriteIntents.BindingIntent -> bind(intent)
        }
    }

    /**
     * This method is invoked when an MviView first registers with the model (this is handled by the
     * framework). This is a good spot to kick off initialization logic.
     */
    override fun onFirstViewRegistered() {
        observer = service.observeFavorites { favorites ->
            val state = RecentFavoritesState(favorites)
            dispatchStates(state)
        }
    }

    /**
     * This method is invoked when all MviViews have unregistered from the model (this is handled by
     * the framework). If conditions setup in [onFirstViewRegistered] require tear down, this is
     * the ideal place to handle them.
     */
    override fun onAllViewsUnregistered() {
        service.stopObservation(observer)
    }

    /**
     * This method is called in the same flow that invokes the MviView's render method. This is an
     * alternate strategy for updating the UI using Android's data binding. Here we use our reference
     * to the view's binding passed to the model via a BindingIntent and set it's state variable
     * to refresh the UI.
     */
    override fun bindData(state: RecentFavoritesState) {
        binding?.state = state
    }

    /**
     * Here we respond to a BindingIntent setting our local, private reference to the MviView's
     * view binding.
     */
    private fun bind(intent: RecentFavoriteIntents.BindingIntent) {
        binding = intent.binding
    }

}