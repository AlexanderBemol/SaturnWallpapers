package com.amontdevs.saturnwallpapers.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApodModel(
    val title: String?,
    val explanation: String?,
    val date: String?,
    @SerialName("hdurl") val highDefinitionUrl: String?,
    @SerialName("media_type") val mediaType: String?,
    @SerialName("copyright") val author: String?,
    @SerialName("url") val regularDefinitionUrl: String?,
    @SerialName("thumbnail_url") val thumbnailUrl: String?
)