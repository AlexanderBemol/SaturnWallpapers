package com.amontdevs.saturnwallpapers.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import saturnwallpapers.shared.generated.resources.Res
import saturnwallpapers.shared.generated.resources.details_back_button
import saturnwallpapers.shared.generated.resources.details_download_button
import saturnwallpapers.shared.generated.resources.details_favorite_button
import saturnwallpapers.shared.generated.resources.details_information_button
import saturnwallpapers.shared.generated.resources.details_play_video_button
import saturnwallpapers.shared.generated.resources.details_read_less_button
import saturnwallpapers.shared.generated.resources.details_read_more_button
import saturnwallpapers.shared.generated.resources.details_set_wallpaper_button
import saturnwallpapers.shared.generated.resources.details_website_icon

object DetailsScreen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getWebsiteIcon() = if (LocalInspectionMode.current) "Website"
        else stringResource(Res.string.details_website_icon)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getInformationButton() = if (LocalInspectionMode.current) "Information"
        else stringResource(Res.string.details_information_button)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getDownloadButton() = if (LocalInspectionMode.current) "Download"
        else stringResource(Res.string.details_download_button)


    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getFavoriteButton() = if (LocalInspectionMode.current) "Favorite"
        else stringResource(Res.string.details_favorite_button)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getBackButton() = if (LocalInspectionMode.current) "Back"
        else stringResource(Res.string.details_back_button)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSetWallpaperButton() = if (LocalInspectionMode.current) "Set wallpaper"
        else stringResource(Res.string.details_set_wallpaper_button)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getPlayVideoButton() = if (LocalInspectionMode.current) "Play video"
        else stringResource(Res.string.details_play_video_button)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getReadMoreButton() = if (LocalInspectionMode.current) "Read more"
        else stringResource(Res.string.details_read_more_button)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getReadLessButton() = if (LocalInspectionMode.current) "Read less"
        else stringResource(Res.string.details_read_less_button)

}