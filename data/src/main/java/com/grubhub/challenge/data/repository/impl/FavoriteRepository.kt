package com.grubhub.challenge.data.repository.impl

import com.grubhub.challenge.data.entity.Favorite
import com.grubhub.challenge.data.provider.ProviderContract
import com.grubhub.challenge.data.repository.IFavoriteRepository
import com.grubhub.persistence.repository.impl.BaseRepository
import javax.inject.Inject

/**
 * With the provided data layer - this is all the code that is required to create a repository that
 * can handle CRUD functionality as well as observation and synchronization with an external source
 * of truth. @see [BaseRepository]
 */
class FavoriteRepository @Inject constructor() : BaseRepository<Favorite, Long>(ProviderContract.Favorites, Favorite), IFavoriteRepository