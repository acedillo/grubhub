package com.grubhub.challenge.ui.main.model

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grubhub.challenge.service.model.Meme
import com.grubhub.challenge.ui.main.view.RecentFavoritesAdapter
import com.grubhub.mvi.model.MviState
import kotlinx.parcelize.Parcelize

/**
 * Visual state data used for recent favorites section contained in the main view (as a fragment)
 */
@Parcelize
data class RecentFavoritesState(
    val memes: List<Meme>
) : MviState {

    object Adapters {
        /**
         * Most data binding asks can be accomplished right in the xml, however sometimes the task
         * requires a bit more wire-up. For those types of tasks, we can either rely on the MviView's
         * render call to manipulate the UI with the supplied state, or we can create a custom
         * binding adapter to handle the extra work. Here we are accessing a recyclerview and
         * passing a new data set to it's adapter.
         */
        @JvmStatic
        @BindingAdapter("set_recent_favorites")
        fun bindAdapterData(view: RecyclerView, state: RecentFavoritesState?) {
            state?.also {
                (view.adapter as RecentFavoritesAdapter).update(it.memes)
            }
        }
    }

}