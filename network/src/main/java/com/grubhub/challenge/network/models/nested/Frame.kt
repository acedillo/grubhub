package com.grubhub.challenge.network.models.nested

import com.squareup.moshi.Json

data class Frame(
    @field:Json(name = "Timestamp") val timestamp: Long
)