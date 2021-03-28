package com.grubhub.challenge.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.grubhub.challenge.ui.databinding.ActivityMainBinding
import com.grubhub.challenge.ui.main.intent.MainIntents
import com.grubhub.challenge.ui.main.model.MainModel
import com.grubhub.challenge.ui.main.model.MainState
import com.grubhub.mvi.view.MviActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : MviActivity<MainState, MainIntents>() {

    @Inject
    lateinit var viewModel: MainModel

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        initUI()
    }

    /**
     * This is called by the MVI framework when a new state has been prepared by the view model.
     */
    override fun render(state: MainState) {
        binding.episodeTitle.text = state.episode
        binding.favIcon.isSelected = state.isFavorite
        Glide.with(binding.memeImage)
            .load(state.imageUrl)
            .into(binding.memeImage)
    }

    override fun getModel() = viewModel

    override fun getStateType() = MainState::class

    /**
     * Wiring up the UI elements to respond to the user. Our sole pipeline for communicating with
     * the model is through the BaseModel's publish method. As suck, each interaction publishes a
     * specific user intent which may be processed by the model to calculate and dispatch a fresh
     * state.
     */
    private fun initUI() {
        binding.refreshButton.setOnClickListener {
            viewModel.publish(MainIntents.RefreshIntent)
        }

        binding.favIcon.setOnClickListener {
            viewModel.publish(MainIntents.FavoriteIntent)
        }

        binding.viewFavoritesButton.setOnClickListener {
            viewModel.publish(MainIntents.ViewAllFavoritesIntent)
        }
    }

}