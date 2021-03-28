package com.grubhub.challenge.network.models.nested

import com.squareup.moshi.Json

data class Subtitle(
    @field:Json(name = "Content") val content: String
)
