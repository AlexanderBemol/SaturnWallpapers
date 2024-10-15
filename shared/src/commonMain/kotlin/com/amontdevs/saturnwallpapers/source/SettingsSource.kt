package com.amontdevs.saturnwallpapers.source
import com.amontdevs.saturnwallpapers.model.DataMaxAge
import com.amontdevs.saturnwallpapers.model.DefaultSaturnPhoto
import com.amontdevs.saturnwallpapers.model.MediaQuality
import com.amontdevs.saturnwallpapers.model.SaturnSettings
import com.amontdevs.saturnwallpapers.model.UserStatus
import com.amontdevs.saturnwallpapers.model.WallpaperScreen
import com.russhwolf.settings.Settings

interface ISettingsSource {
    fun getUserStatus(): UserStatus
    fun setUserStatus(userStatus: UserStatus)
    fun getSettings(): SaturnSettings
    fun saveSettings(saturnSettings: SaturnSettings)
}

class SettingsSource(
    private val settings: Settings
): ISettingsSource {

    override fun getUserStatus() = UserStatus(
        userOnboarded = settings.getBoolean(ONBOARDED_KEY, false),
        alreadyPopulated = settings.getBoolean(POPULATED_KEY, false)
    )

    override fun setUserStatus(userStatus: UserStatus) {
        settings.putBoolean(ONBOARDED_KEY, userStatus.userOnboarded)
        settings.putBoolean(POPULATED_KEY, userStatus.alreadyPopulated)
    }

    override fun getSettings() = SaturnSettings(
        isDailyWallpaperActivated =
            settings.getBoolean(DAILY_WALLPAPER_UPDATER_KEY, false),
        mediaQuality = MediaQuality.fromInt(settings.getInt(MEDIA_QUALITY_KEY, 0)),
        wallpaperScreen = WallpaperScreen.fromInt(settings.getInt(WALLPAPER_SCREEN_KEY, 0)),
        isDownloadOverCellularActivated = settings.getBoolean(DOWNLOAD_OVER_CELLULAR_KEY, false),
        dataMaxAge = DataMaxAge.fromInt(settings.getInt(DATA_MAX_AGE_KEY, 0)),
        defaultSaturnPhoto = DefaultSaturnPhoto.fromInt(settings.getInt(DEFAULT_SATURN_PHOTO_KEY, 0))
    )

    override fun saveSettings(saturnSettings: SaturnSettings) {
        saturnSettings.let {
            settings.putBoolean(DAILY_WALLPAPER_UPDATER_KEY, it.isDailyWallpaperActivated)
            settings.putInt(MEDIA_QUALITY_KEY, it.mediaQuality.id)
            settings.putBoolean(DOWNLOAD_OVER_CELLULAR_KEY, it.isDownloadOverCellularActivated)
            settings.putInt(WALLPAPER_SCREEN_KEY, it.wallpaperScreen.id)
            settings.putInt(DATA_MAX_AGE_KEY, it.dataMaxAge.id)
            settings.putInt(DEFAULT_SATURN_PHOTO_KEY, it.defaultSaturnPhoto.id)
        }
    }

    companion object {
        const val POPULATED_KEY = "POPULATED"
        const val ONBOARDED_KEY = "ONBOARDED"
        const val DAILY_WALLPAPER_UPDATER_KEY = "DAILY_WALLPAPER_UPDATER"
        const val MEDIA_QUALITY_KEY = "MEDIA_QUALITY"
        const val DOWNLOAD_OVER_CELLULAR_KEY = "DOWNLOAD_OVER_CELLULAR"
        const val WALLPAPER_SCREEN_KEY = "WALLPAPER_SCREEN"
        const val DATA_MAX_AGE_KEY = "MAX_AGE"
        const val DEFAULT_SATURN_PHOTO_KEY = "DEFAULT_SATURN_PHOTO"
    }
}