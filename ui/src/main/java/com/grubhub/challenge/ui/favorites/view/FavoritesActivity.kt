package com.grubhub.challenge.ui.favorites.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import com.grubhub.challenge.ui.databinding.ActivityFavoritesBinding
import com.grubhub.challenge.ui.favorites.intent.FavoritesIntents
import com.grubhub.challenge.ui.favorites.model.FavoritesModel
import com.grubhub.challenge.ui.favorites.model.FavoritesState
import com.grubhub.challenge.ui.main.intent.RecentFavoriteIntents
import com.grubhub.mvi.view.MviActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesActivity : MviActivity<FavoritesState, FavoritesIntents>() {

    @Inject
    lateinit var viewModel: FavoritesModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val inflater = LayoutInflater.from(this)
        val binding = ActivityFavoritesBinding.inflate(inflater)
        with (binding.favoritesGrid) {
            layoutManager = GridLayoutManager(this@FavoritesActivity, 2)
            adapter = FavoritesAdapter()
        }
        setContentView(binding.root)
        viewModel.publish(FavoritesIntents.BindingIntent(binding))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * There are two main ways our MVI framework allows us to update our views. The render call within
     * the MviView (MviActivity, MviFragment, MviDialogFragment), and view the bindData call in the
     * BaseModel implementation. We can leverage Android's data binding in this fashion to keep ultra
     * clean activities and fragments.
     */
    override fun render(state: FavoritesState) {
        /* No op - see the view model's bindData method */
    }

    override fun getModel() = viewModel

    override fun getStateType() = FavoritesState::class

}