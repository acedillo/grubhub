package com.grubhub.challenge.network.factory

import android.content.Context
import com.grubhub.challenge.network.FrinkiacApi
import com.grubhub.challenge.network.R
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Provides a configured Frinkiac API instance
 */
object FrinkiacApiFactory {
    fun getInstance(context: Context): FrinkiacApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(ChuckInterceptor(context))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(context.getString(R.string.base_url))
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(FrinkiacApi::class.java)
    }
}