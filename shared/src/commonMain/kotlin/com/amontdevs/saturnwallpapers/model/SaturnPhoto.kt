package com.amontdevs.saturnwallpapers.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity
data class SaturnPhoto(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    var title: String = "",
    var description: String = "",
    var authors: String = "",
    var isVideo: Boolean = false,
    var videoUrl: String = "",
    var isFavorite: Boolean = false
)
