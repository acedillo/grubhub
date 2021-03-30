package com.grubhub.challenge.ui.main.view

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import com.bumptech.glide.Glide
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
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
        if(state.episode.isBlank() && state.imageUrl.isBlank()){
            binding.error.visibility = VISIBLE
            return
        }

        binding.error.visibility = GONE
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

        binding.memeImage.setOnClickListener {
            val bitmapDrawable = binding.memeImage.drawable as BitmapDrawable
            val photo = SharePhoto.Builder().setBitmap(bitmapDrawable.bitmap).build()
            val content = SharePhotoContent.Builder().addPhoto(photo).build()
            ShareDialog.show(this, content)

        }
    }

}