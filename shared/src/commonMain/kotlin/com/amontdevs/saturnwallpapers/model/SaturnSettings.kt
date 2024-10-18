package com.amontdevs.saturnwallpapers.model

data class SaturnSettings(
    val isDailyWallpaperActivated: Boolean = false,
    val isDownloadOverCellularActivated: Boolean = false,
    val mediaQuality: MediaQuality = MediaQuality.HIGH,
    val wallpaperScreen: WallpaperScreen = WallpaperScreen.ALL,
    val dataMaxAge: DataMaxAge = DataMaxAge.TWO_WEEKS,
    val defaultSaturnPhoto: DefaultSaturnPhoto = DefaultSaturnPhoto.RANDOM
)