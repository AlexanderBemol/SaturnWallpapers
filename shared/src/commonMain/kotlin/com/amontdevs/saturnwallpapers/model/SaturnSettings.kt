package com.amontdevs.saturnwallpapers.model

data class SaturnSettings(
    val isDailyWallpaperActivated: Boolean = false,
    val mediaQuality: MediaQuality = MediaQuality.NORMAL,
    val wallpaperScreen: WallpaperScreen = WallpaperScreen.ALL,
    val dataMaxAge: DataMaxAge = DataMaxAge.ONE_MONTH,
    val defaultSaturnPhoto: DefaultSaturnPhoto = DefaultSaturnPhoto.RANDOM
)