package com.grubhub.challenge.service.domain.impl

import android.content.Context
import android.database.ContentObserver
import com.grubhub.challenge.data.entity.Favorite
import com.grubhub.challenge.data.provider.ProviderContract
import com.grubhub.challenge.data.repository.IFavoriteRepository
import com.grubhub.challenge.service.domain.IFavoriteService
import com.grubhub.challenge.service.model.Meme
import com.grubhub.persistence.repository.ISetObserver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.yield
import javax.inject.Inject

class FavoritesService @Inject constructor(
    @ApplicationContext context: Context,
    private val repo: IFavoriteRepository
) : BaseDomainService(context), IFavoriteService {

    /**
     * Adds a favorite meme to the underlying data store
     *
     * @param imageUrl The url for the meme's image
     * @param title The title of the originating episode
     */
    override suspend fun addFavorite(imageUrl: String, title: String) {
        yield()
        val favorite = Favorite(0L, imageUrl, title)
        repo.insert(context, favorite)
    }

    /**
     * Observes changes to the favorites data store and emits the updated recordset to the
     * callback code block provided. This method will return a content observer that is used to
     * administer the callback actions. A reference should be kept to this observer so it may be
     * unregistered when no longer needed.
     *
     * @param block A lambda which handles a list of [Meme] objects
     * @return A content observer registered to the uri of the favorites data type
     */
    override fun observeFavorites(block: (favorites: List<Meme>) -> Unit): ContentObserver {
        val callback = object : ISetObserver<Favorite> {
            override fun onEntitiesChanged(entities: List<Favorite>) {
                block.invoke(entities.map { Meme(it.imageUrl, it.episodeTitle) }.toList())
            }
        }

        return repo.observe(context, callback, sortOrder = "${ProviderContract.Favorites.Columns.ID} DESC LIMIT 4")
    }

    /**
     * Unregisters the provided content observer halting the execution of callbacks provided to the
     * [observeFavorites] method.
     *
     * @param observer The content observer to unregister from uri notifications
     */
    override fun stopObservation(observer: ContentObserver) {
        repo.unregister(context, observer)
    }

    /**
     * Fetches all favorited memes from the underlying data store
     *
     * @return A list of [Meme] objects
     */
    override suspend fun fetchFavorites(): List<Meme> {
        yield()
        return repo.fetchAll(context).map { Meme(it.imageUrl, it.episodeTitle) }.toList()
    }

}