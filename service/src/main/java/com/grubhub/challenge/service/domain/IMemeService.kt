package com.grubhub.challenge.service.domain

import com.grubhub.challenge.service.model.Meme

interface IMemeService {
    /**
     * Fetches a random meme from the Frinkiac API. This method may return null
     *
     * @return A [Meme] object if the network operation was successful, otherwise null
     */
    suspend fun getRandomMeme(): Meme?
}