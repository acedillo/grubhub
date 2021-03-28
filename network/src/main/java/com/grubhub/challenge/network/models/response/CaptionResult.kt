package com.grubhub.challenge.network.models.response

import com.grubhub.challenge.network.models.nested.Episode
import com.grubhub.challenge.network.models.nested.Frame
import com.grubhub.challenge.network.models.nested.Subtitle
import com.squareup.moshi.Json

data class CaptionResult(
    @field:Json(name = "Episode") val episode: Episode,
    @field:Json(name = "Frame") val frame: Frame,
    @field:Json(name = "Subtitles") val subtitles: List<Subtitle>
)