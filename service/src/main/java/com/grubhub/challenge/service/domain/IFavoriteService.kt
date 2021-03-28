package com.grubhub.challenge.service.domain

import android.database.ContentObserver
import com.grubhub.challenge.service.model.Meme

interface IFavoriteService {
    /**
     * Adds a favorite meme to the underlying data store
     *
     * @param imageUrl The url for the meme's image
     * @param title The title of the originating episode
     */
    suspend fun addFavorite(imageUrl: String, title: String)

    /**
     * Observes changes to the favorites data store and emits the updated recordset to the
     * callback code block provided. This method will return a content observer that is used to
     * administer the callback actions. A reference should be kept to this observer so it may be
     * unregistered when no longer needed.
     *
     * @param block A lambda which handles a list of [Meme] objects
     * @return A content observer registered to the uri of the favorites data type
     */
    fun observeFavorites(block: (favorites: List<Meme>) -> Unit): ContentObserver

    /**
     * Unregisters the provided content observer halting the execution of callbacks provided to the
     * [observeFavorites] method.
     *
     * @param observer The content observer to unregister from uri notifications
     */
    fun stopObservation(observer: ContentObserver)

    /**
     * Fetches all favorited memes from the underlying data store
     *
     * @return A list of [Meme] objects
     */
    suspend fun fetchFavorites(): List<Meme>
}