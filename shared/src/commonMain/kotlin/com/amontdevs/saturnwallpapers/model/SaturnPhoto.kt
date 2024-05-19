package com.amontdevs.saturnwallpapers.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity
data class SaturnPhoto(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    var title: String = "",
    var description: String = "",
    var authors: String = "",
    var mediaType: String = "",
    var regularUrl: String = "",
    var highDefinitionUrl: String = "",
    var regularPath: String = "",
    var highDefinitionPath: String = "",
    var videoUrl: String = "",
    var isFavorite: Boolean = false
)
