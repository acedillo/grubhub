package com.grubhub.challenge.di

import android.content.Context
import com.grubhub.challenge.network.factory.FrinkiacApiFactory
import com.grubhub.challenge.data.ChallengeDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Provides classes that must be configured
 */
@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Provides
    fun provideApi(@ApplicationContext context: Context) = FrinkiacApiFactory.getInstance(context)

    @Provides
    fun provideDb(@ApplicationContext context: Context) = ChallengeDb.getInstance(context)
}