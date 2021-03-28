package com.grubhub.challenge.service.domain.impl

import android.content.Context
import com.grubhub.challenge.network.FrinkiacApi
import com.grubhub.challenge.network.models.response.CaptionResult
import com.grubhub.challenge.service.R
import com.grubhub.challenge.service.domain.IMemeService
import com.grubhub.challenge.service.model.Meme
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MemeService @Inject constructor(
    @ApplicationContext context: Context,
    private val api: FrinkiacApi
) : BaseDomainService(context), IMemeService {

    /**
     * Fetches a random meme from the Frinkiac API. This method may return null
     *
     * @return A [Meme] object if the network operation was successful, otherwise null
     */
    override suspend fun getRandomMeme(): Meme? {
        var meme: Meme? = null
        try {
            val random = fetchRandom()

            val url = context.getString(
                R.string.meme_img_format,
                random.episode.key,
                random.frame.timestamp,
                "VGhpcyBpcyBvbmx5IGEgdGVzdA==" // Caption text encoded
            )
            meme = Meme(url, random.episode.title)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return meme
    }

    private suspend fun fetchRandom() = suspendCoroutine<CaptionResult> { continuation ->
        api.random().enqueue(object : Callback<CaptionResult> {
            override fun onResponse(call: Call<CaptionResult>, response: Response<CaptionResult>) {
                response.body()?.let { continuation.resume(it) }
                    ?: onFailure(call, IOException("Failed network request with code: ${response.code()}"))
            }

            override fun onFailure(call: Call<CaptionResult>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }

}