package com.grubhub.challenge.service.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Meme(
    val imageUrl: String,
    val titleOfEpisode: String
) : Parcelable