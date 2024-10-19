package com.amontdevs.saturnwallpapers.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import saturnwallpapers.shared.generated.resources.Res
import saturnwallpapers.shared.generated.resources.dialog_cancel_text
import saturnwallpapers.shared.generated.resources.dialog_confirm_text
import saturnwallpapers.shared.generated.resources.settings_daily_wallpaper_description
import saturnwallpapers.shared.generated.resources.settings_daily_wallpaper_title
import saturnwallpapers.shared.generated.resources.settings_default_photo_description
import saturnwallpapers.shared.generated.resources.settings_default_photo_option_random
import saturnwallpapers.shared.generated.resources.settings_default_photo_option_random_favorites
import saturnwallpapers.shared.generated.resources.settings_default_photo_title
import saturnwallpapers.shared.generated.resources.settings_download_over_cellular_description
import saturnwallpapers.shared.generated.resources.settings_download_over_cellular_title
import saturnwallpapers.shared.generated.resources.settings_downloading
import saturnwallpapers.shared.generated.resources.settings_max_age_description
import saturnwallpapers.shared.generated.resources.settings_max_age_option_one_month
import saturnwallpapers.shared.generated.resources.settings_max_age_option_one_year
import saturnwallpapers.shared.generated.resources.settings_max_age_option_six_months
import saturnwallpapers.shared.generated.resources.settings_max_age_option_three_months
import saturnwallpapers.shared.generated.resources.settings_max_age_option_two_weeks
import saturnwallpapers.shared.generated.resources.settings_max_age_title
import saturnwallpapers.shared.generated.resources.settings_quality_confirm_dialog_high_quality_message
import saturnwallpapers.shared.generated.resources.settings_quality_confirm_dialog_regular_quality_message
import saturnwallpapers.shared.generated.resources.settings_quality_confirm_dialog_title
import saturnwallpapers.shared.generated.resources.settings_quality_confirm_high_quality_loading_title
import saturnwallpapers.shared.generated.resources.settings_quality_confirm_regular_quality_loading_title
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
    
    @Composable
    fun getSettingsTitle() = if (LocalInspectionMode.current) "Settings"
        else stringResource(Res.string.settings_title)

    @Composable
    fun getSettingsDailyWallpaperTitle() = if (LocalInspectionMode.current) "Daily wallpaper updater"
        else stringResource(Res.string.settings_daily_wallpaper_title)

    @Composable
    fun getSettingsDownloadOverCellularTitle() = if (LocalInspectionMode.current) "Download over cellular"
        else stringResource(Res.string.settings_download_over_cellular_title)

    @Composable
    fun getSettingsDownloadOverCellularDescription() = if (LocalInspectionMode.current) "When activated, the app is going to download images when the device is not connected to a WiFi network, be careful, images can be huge."
        else stringResource(Res.string.settings_download_over_cellular_description)

    @Composable
    fun getSettingsDailyWallpaperDescription() =
        if (LocalInspectionMode.current) "When activated, the app is going to automatically update the wallpaper everyday"
        else stringResource(Res.string.settings_daily_wallpaper_description)

    @Composable
    fun getSettingsScreenTitle() = if (LocalInspectionMode.current) "Wallpaper screen"
        else stringResource(Res.string.settings_wallpaper_screen_title)

    @Composable
    fun getSettingsScreenDescription() =
        if (LocalInspectionMode.current) "Define the screen where the wallpaper is going to automatically be set when the service is activated"
        else stringResource(Res.string.settings_wallpaper_screen_description)

    @Composable
    fun getSettingsScreenOptionLock() = if (LocalInspectionMode.current) "Lock screen"
        else stringResource(Res.string.settings_wallpaper_screen_option_lock)

    @Composable
    fun getSettingsScreenOptionHome() = if (LocalInspectionMode.current) "Home screen"
        else stringResource(Res.string.settings_wallpaper_screen_option_home)

    @Composable
    fun getSettingsScreenOptionBoth() = if (LocalInspectionMode.current) "Both screens"
        else stringResource(Res.string.settings_wallpaper_screen_option_both)

    @Composable
    fun getSettingsQualityTitle() = if (LocalInspectionMode.current) "Quality"
        else stringResource(Res.string.settings_quality_title)

    @Composable
    fun getSettingsQualityDescription() =
        if (LocalInspectionMode.current) "Define the resolution of the images, the high quality requires more storage and network data"
        else stringResource(Res.string.settings_quality_description)
    
    @Composable
    fun getSettingsQualityOptionNormal() = if (LocalInspectionMode.current) "Normal"
        else stringResource(Res.string.settings_quality_option_medium)

    @Composable
    fun getSettingsQualityOptionHigh() = if (LocalInspectionMode.current) "High"
        else stringResource(Res.string.settings_quality_option_high)

    @Composable
    fun getSettingsMaxAgeTitle() = if (LocalInspectionMode.current) "Max age of images"
        else stringResource(Res.string.settings_max_age_title)

    @Composable
    fun getSettingsMaxAgeDescription() =
        if (LocalInspectionMode.current) "Define the maximum of time the images are going to be stored, after that, if a record is older (and is not a favorite) is going to be deleted."
        else stringResource(Res.string.settings_max_age_description)

    @Composable
    fun getSettingsMaxAgeOptionTwoWeeks() = if (LocalInspectionMode.current) "Two weeks"
        else stringResource(Res.string.settings_max_age_option_two_weeks)

    @Composable
    fun getSettingsMaxAgeOptionOneMonth() = if (LocalInspectionMode.current) "One month"
        else stringResource(Res.string.settings_max_age_option_one_month)

    @Composable
    fun getSettingsMaxAgeOptionThreeMonths() = if (LocalInspectionMode.current) "Three months"
        else stringResource(Res.string.settings_max_age_option_three_months)

    @Composable
    fun getSettingsMaxAgeOptionSixMonths() = if (LocalInspectionMode.current) "Six months"
        else stringResource(Res.string.settings_max_age_option_six_months)

    @Composable
    fun getSettingsMaxAgeOptionOneYear() = if (LocalInspectionMode.current) "One year"
        else stringResource(Res.string.settings_max_age_option_one_year)
    
    @Composable
    fun getSettingsDefaultPhotoTitle() = if (LocalInspectionMode.current) "Default photo"
        else stringResource(Res.string.settings_default_photo_title)

    @Composable
    fun getSettingsDefaultPhotoDescription() =
        if (LocalInspectionMode.current) "For some days, an image can not be retrieved, define if the app is going to take a random photo between your favorites, or a default one instead"
        else stringResource(Res.string.settings_default_photo_description)

    @Composable
    fun getSettingsDefaultPhotoOptionRandom() = if (LocalInspectionMode.current) "Random"
        else stringResource(Res.string.settings_default_photo_option_random)

    @Composable
    fun getSettingsDefaultPhotoOptionRandomBetweenFavorites() =
        if (LocalInspectionMode.current) "Random between favorites"
        else stringResource(Res.string.settings_default_photo_option_random_favorites)

    @Composable
    fun getSettingsQualityConfirmDialogTitle() = if (LocalInspectionMode.current) "Are you sure?"
        else stringResource(Res.string.settings_quality_confirm_dialog_title)

    @Composable
    fun getSettingsQualityConfirmDialogHighQualityMessage() = if (LocalInspectionMode.current) "All the images will be downloaded at high quality, this operation can take some time depending on your internet connection, do you want to continue?"
        else stringResource(Res.string.settings_quality_confirm_dialog_high_quality_message)

    @Composable
    fun getSettingsQualityConfirmDialogRegularQualityMessage() = if (LocalInspectionMode.current) "All the high quality images are going to be removed."
        else stringResource(Res.string.settings_quality_confirm_dialog_regular_quality_message)

    @Composable
    fun getSettingsQualityConfirmHighQualityLoadingTitle() = if (LocalInspectionMode.current) "Downloading high quality images"
        else stringResource(Res.string.settings_quality_confirm_high_quality_loading_title)

    @Composable
    fun getSettingsQualityConfirmRegularQualityLoadingTitle() = if (LocalInspectionMode.current) "Removing high quality images"
        else stringResource(Res.string.settings_quality_confirm_regular_quality_loading_title)

    @Composable
    fun getDialogConfirmText() =
        if (LocalInspectionMode.current) "Confirm"
        else stringResource(Res.string.dialog_confirm_text)

    @Composable
    fun getDownloadingText() =
        if (LocalInspectionMode.current) "Downloading"
        else stringResource(Res.string.settings_downloading)

    @Composable
    fun getDialogCancelText() =
        if (LocalInspectionMode.current) "Cancel"
        else stringResource(Res.string.dialog_cancel_text)
}