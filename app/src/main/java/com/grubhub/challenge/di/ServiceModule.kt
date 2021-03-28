package com.grubhub.challenge.di

import com.grubhub.challenge.service.domain.IFavoriteService
import com.grubhub.challenge.service.domain.IMemeService
import com.grubhub.challenge.service.domain.impl.FavoritesService
import com.grubhub.challenge.service.domain.impl.MemeService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

/**
 * Binds interfaces to their concrete implementations.
 * This module is for the mapping the service layer of the application.
 */
@Module
@InstallIn(ApplicationComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun bindMemeService(service: MemeService): IMemeService

    @Binds
    abstract fun bindFavoriteService(service: FavoritesService): IFavoriteService
}