package com.grubhub.challenge.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.grubhub.challenge.ui.databinding.FragmentRecentFavoritesBinding
import com.grubhub.challenge.ui.main.intent.RecentFavoriteIntents
import com.grubhub.challenge.ui.main.model.RecentFavoritesModel
import com.grubhub.challenge.ui.main.model.RecentFavoritesState
import com.grubhub.mvi.view.MviFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecentFavoritesFragment : MviFragment<RecentFavoritesState, RecentFavoriteIntents>() {

    @Inject
    lateinit var viewModel: RecentFavoritesModel
    lateinit var binding: FragmentRecentFavoritesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentFavoritesBinding.inflate(inflater, container, false)
        viewModel.publish(RecentFavoriteIntents.BindingIntent(binding))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recentGrid.adapter = RecentFavoritesAdapter()
        binding.recentGrid.layoutManager = GridLayoutManager(context, 2)
    }

    /**
     * There are two main ways our MVI framework allows us to update our views. The render call within
     * the MviView (MviActivity, MviFragment, MviDialogFragment), and view the bindData call in the
     * BaseModel implementation. We can leverage Android's data binding in this fashion to keep ultra
     * clean activities and fragments.
     */
    override fun render(state: RecentFavoritesState) {
        /* No op - see the view model's dataBind method */
    }

    override fun getModel() = viewModel

    override fun getStateType() = RecentFavoritesState::class

}