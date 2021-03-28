package com.grubhub.challenge.network.models.nested

import com.squareup.moshi.Json

data class Episode(
    @field:Json(name = "Title") val title: String,
    @field:Json(name = "Key") val key: String,
)