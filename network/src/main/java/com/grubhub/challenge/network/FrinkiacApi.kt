package com.grubhub.challenge.network

import com.grubhub.challenge.network.models.response.CaptionResult
import com.grubhub.challenge.network.models.response.SearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FrinkiacApi {
    @GET("/api/search")
    fun search(@Query("q") searchString: String) : Call<List<SearchResult>>

    @GET("/api/random")
    fun random() : Call<CaptionResult>

    @GET("/api/caption")
    fun caption(@Query("e") episode: String, @Query("t") timestamp: Long)
}