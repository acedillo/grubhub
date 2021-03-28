package com.grubhub.challenge.di

import com.grubhub.challenge.data.repository.IFavoriteRepository
import com.grubhub.challenge.data.repository.impl.FavoriteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

/**
 * Binds interfaces to their concrete implementations.
 * This module is for the mapping the data layer of the application.
 */
@Module
@InstallIn(ApplicationComponent::class)
abstract class DataModule {
    @Binds
    abstract fun bindIFavoriteRepository(repo: FavoriteRepository): IFavoriteRepository
}