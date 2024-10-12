package com.amontdevs.saturnwallpapers.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import saturnwallpapers.shared.generated.resources.Res
import saturnwallpapers.shared.generated.resources.settings_daily_wallpaper_description
import saturnwallpapers.shared.generated.resources.settings_daily_wallpaper_title
import saturnwallpapers.shared.generated.resources.settings_default_photo_description
import saturnwallpapers.shared.generated.resources.settings_default_photo_option_random
import saturnwallpapers.shared.generated.resources.settings_default_photo_option_random_favorites
import saturnwallpapers.shared.generated.resources.settings_default_photo_title
import saturnwallpapers.shared.generated.resources.settings_download_over_cellular_description
import saturnwallpapers.shared.generated.resources.settings_download_over_cellular_title
import saturnwallpapers.shared.generated.resources.settings_max_age_description
import saturnwallpapers.shared.generated.resources.settings_max_age_option_one_month
import saturnwallpapers.shared.generated.resources.settings_max_age_option_one_year
import saturnwallpapers.shared.generated.resources.settings_max_age_option_six_months
import saturnwallpapers.shared.generated.resources.settings_max_age_option_three_months
import saturnwallpapers.shared.generated.resources.settings_max_age_title
import saturnwallpapers.shared.generated.resources.settings_quality_description
import saturnwallpapers.shared.generated.resources.settings_quality_option_high
import saturnwallpapers.shared.generated.resources.settings_quality_option_medium
import saturnwallpapers.shared.generated.resources.settings_quality_title
import saturnwallpapers.shared.generated.resources.settings_title
import saturnwallpapers.shared.generated.resources.settings_wallpaper_screen_description
import saturnwallpapers.shared.generated.resources.settings_wallpaper_screen_option_both
import saturnwallpapers.shared.generated.resources.settings_wallpaper_screen_option_home
import saturnwallpapers.shared.generated.resources.settings_wallpaper_screen_option_lock
import saturnwallpapers.shared.generated.resources.settings_wallpaper_screen_title

object Settings {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsTitle() = if (LocalInspectionMode.current) "Website"
        else stringResource(Res.string.settings_title)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsDailyWallpaperTitle() = if (LocalInspectionMode.current) "Daily wallpaper updater"
        else stringResource(Res.string.settings_daily_wallpaper_title)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsDownloadOverCellularTitle() = if (LocalInspectionMode.current) "Download over cellular"
        else stringResource(Res.string.settings_download_over_cellular_title)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsDownloadOverCellularDescription() = if (LocalInspectionMode.current) "When activated, the app is going to download images when the device is not connected to a WiFi network, be careful, images can be huge."
        else stringResource(Res.string.settings_download_over_cellular_description)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsDailyWallpaperDescription() =
        if (LocalInspectionMode.current) "When activated, the app is going to automatically update the wallpaper everyday"
        else stringResource(Res.string.settings_daily_wallpaper_description)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsScreenTitle() = if (LocalInspectionMode.current) "Wallpaper screen"
        else stringResource(Res.string.settings_wallpaper_screen_title)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsScreenDescription() =
        if (LocalInspectionMode.current) "Define the screen where the wallpaper is going to automatically be set when the service is activated"
        else stringResource(Res.string.settings_wallpaper_screen_description)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsScreenOptionLock() = if (LocalInspectionMode.current) "Lock screen"
        else stringResource(Res.string.settings_wallpaper_screen_option_lock)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsScreenOptionHome() = if (LocalInspectionMode.current) "Home screen"
        else stringResource(Res.string.settings_wallpaper_screen_option_home)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsScreenOptionBoth() = if (LocalInspectionMode.current) "Both screens"
        else stringResource(Res.string.settings_wallpaper_screen_option_both)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsQualityTitle() = if (LocalInspectionMode.current) "Quality"
        else stringResource(Res.string.settings_quality_title)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsQualityDescription() =
        if (LocalInspectionMode.current) "Define the resolution of the images, the high quality requires more storage and network data"
        else stringResource(Res.string.settings_quality_description)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsQualityOptionNormal() = if (LocalInspectionMode.current) "Normal"
        else stringResource(Res.string.settings_quality_option_medium)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsQualityOptionHigh() = if (LocalInspectionMode.current) "High"
        else stringResource(Res.string.settings_quality_option_high)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsMaxAgeTitle() = if (LocalInspectionMode.current) "Max age of images"
        else stringResource(Res.string.settings_max_age_title)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsMaxAgeDescription() =
        if (LocalInspectionMode.current) "Define the maximum of time the images are going to be stored, after that, if a record is older (and is not a favorite) is going to be deleted."
        else stringResource(Res.string.settings_max_age_description)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsMaxAgeOptionOneMonth() = if (LocalInspectionMode.current) "One month"
        else stringResource(Res.string.settings_max_age_option_one_month)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsMaxAgeOptionThreeMonths() = if (LocalInspectionMode.current) "Three months"
        else stringResource(Res.string.settings_max_age_option_three_months)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsMaxAgeOptionSixMonths() = if (LocalInspectionMode.current) "Six months"
        else stringResource(Res.string.settings_max_age_option_six_months)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsMaxAgeOptionOneYear() = if (LocalInspectionMode.current) "One year"
        else stringResource(Res.string.settings_max_age_option_one_year)


    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsDefaultPhotoTitle() = if (LocalInspectionMode.current) "Default photo"
        else stringResource(Res.string.settings_default_photo_title)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsDefaultPhotoDescription() =
        if (LocalInspectionMode.current) "For some days, an image can not be retrieved, define if the app is going to take a random photo between your favorites, or a default one instead"
        else stringResource(Res.string.settings_default_photo_description)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsDefaultPhotoOptionRandom() = if (LocalInspectionMode.current) "Random"
        else stringResource(Res.string.settings_default_photo_option_random)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettingsDefaultPhotoOptionRandomBetweenFavorites() =
        if (LocalInspectionMode.current) "Random between favorites"
        else stringResource(Res.string.settings_default_photo_option_random_favorites)

}